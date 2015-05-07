package au.org.ala.collectory.datarepo.sources

import au.org.ala.collectory.Address
import au.org.ala.collectory.ProviderGroup
import au.org.ala.collectory.datarepo.plugin.CandidateDataResource
import au.org.ala.collectory.datarepo.plugin.DataRepository
import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult
import groovyx.net.http.HTTPBuilder
import org.codehaus.groovy.grails.web.mime.MimeType
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Scanner for GBIF Integrated Publishing Toolkit repository
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class IPTScanner extends Scanner {
    static final Logger log = LoggerFactory.getLogger(IPTScanner.class)

    /** The name of the scanner. Override for each subclass */
    static NAME = "GBIF IPT"
    /** The name of the scanner. Override for each subclass */
    static DESCRIPTION = "GBIF Integrated Publishing Toolkit"

    /** The base connection description */
    def rss
    /** The namne of the key filed (defaults to catalogNumber) */
    def keyName

    static final IPT = new groovy.xml.Namespace("http://ipt.gbif.org/")

    /** Source of the RSS feed */
    static final RSS_PATH = "rss.do"
    /** The form that an IPT resource reference takes */
    //static final IPT_RESOURCE_PATTERN = /^.+\/resource.do\?r=([A-Za-z0-9_]+)$/
    /** Parse RFC 822 date/times */
    static final RFC822_PARSER = new SimpleDateFormat('EEE, d MMM yyyy HH:mm:ss Z')
    /** Parse ISO 8601 date/times */
    //static final ISO8601_PARSER = new SimpleDateFormat('yyyy-MM-dd\'T\'HH:mm:ssXXX')

    def getPubDate = { item ->
        def pd = item.pubDate?.text()

        pd == null || pd.isEmpty() ? null : new Timestamp(RFC822_PARSER.parse(pd).getTime())
    }

    /** Fields that we can derive from the RSS feed */
    protected rssFields = [
            guid: { item -> item.link.text() },
            name: { item -> item.title.text() },
            pubDescription: { item -> item.description.text() },
            websiteUrl: { item -> item.link.text() },
            lastModified: getPubDate
    ]
    /** Fields that we can derive from the EML document */
    protected emlFields = [
            pubDescription: { eml -> this.collectParas(eml.dataset.abstract?.para) },
            techDescription: { eml -> this.collectParas(eml.dataset.additionalInfo?.para) },
            rights: { eml ->  this.collectParas(eml.dataset.intellectualRights?.para) ?: repository.rights },
            citation: { eml ->  eml.additionalMetadata?.metadata?.gbif?.citation?.text() ?: repository.citation },
            state: { eml -> eml.dataset.contact?.address?.administrativeArea?.text() },
            email: { eml ->  eml.dataset.contact?.electronicMailAddress?.text() },
            phone: { eml ->  eml.dataset.contact?.phone?.text() },
            address: { eml ->
                def addr = eml.dataset.contact?.address
                addr ? new Address(
                  street: addr.deliveryPoint?.text(),
                  city: addr.city?.text(),
                  state: addr.administrativeArea?.text(),
                  postcode: addr.postalCode?.text(),
                  country: addr.country?.text()
                ) : null
            }
    ]
    /** All field names */
    protected allFields = rssFields.keySet() + emlFields.keySet()

    /** Collect individual XML para elements together into a single block of text */
    protected def collectParas(GPathResult paras) {
        paras?.list().inject(null, { acc, para -> acc == null ? (para.text()?.trim() ?: "") : acc + " " + (para.text()?.trim() ?: "") })
    }

    /**
     * Construct for a repository
     *
     * @param repository
     */

    IPTScanner(DataRepository repository) {
        super(repository)

        def slurper = new JsonSlurper()
        def connectionParameters = slurper.parseText(repository.connectionParameters)
        rss = connectionParameters.rss
        keyName = connectionParameters.keyName ?: "catalogNumber"
        log.debug "Constructed IPT Scanner rss=${rss} keyName=${keyName}"
    }

    /**
     * Scasn the IPT server for updates
     *
     * @param since The date last modified
     *
     * @return An empty list
     */
    @Override
    List<CandidateDataResource> scan(Date since) {
        return rss(since)
    }

    /**
     * Construct a builder.
     * <p>
     *     Provides a place to put additional header information, such
     *     as accepting XML.
     *     Also provides a convienient point to mock for testing purposes.
     *
     * @param url The URL for the builder
     *
     * @return The resulting content
     */
    def getContent(String url) {
        def builder = new HTTPBuilder(url)

        builder.headers[HttpHeaders.ACCEPT] = MimeType.XML.name
        return builder.get([:])
    }


    /**
     * Scan an IPT data provider's RSS stream and build a set of candidiate datasets.
     *
     * @param since If not null, only return candidiates published after this date
     *
     * @return A list of (possibly new) candidates
     */
    def rss(Date since) {
        log.info("Scanning ${rss}")
        def feed = getContent(rss)
        if (feed == null)
            return []
        def items = feed.channel.item
        items = items.findAll { item -> since == null || !since.after(getPubDate(item)) }
        def candidates = items.collect { item -> this.createCandidate(item) }
        log.debug "Found ${candidates.size()} candidates"
        return candidates

    }

    /**
     * Construct a candidiate from an RSS item
     *
     * @param rssItem The RSS item
     *
     * @return A created resource matching the information provided
     */
    def createCandidate(GPathResult rssItem) {
        def candidate = new CandidateDataResource()
        def eml = rssItem.eml?.text()
        def dwca = rssItem.dwca?.text()

        candidate.dataProvider = repository.dataProvider
        candidate.lifecycle = "New"
        candidate.licenseType = repository.licenseType
        candidate.licenseVersion = repository.licenseVersion
        if (candidate.licenseType == "other")
            candidate.licenseVersion = null
        candidate.userLastModified = repository.name
        rssFields.each { name, accessor -> candidate.setProperty(name, accessor(rssItem))}
        candidate.connectionParameters =  dwca == null || dwca.isEmpty() ? null : "{ \"protocol\": \"DwCA\", \"url\": \"${dwca}\", \"automation\": true, \"termsForUniqueKey\": [ \"${keyName}\" ] }";
        if (eml != null)
            retrieveEml(candidate, eml)
        return candidate
    }

    /**
     * Retreieve Eco-informatics metadata for the dataset and put it into the candidate description.
     *
     * @param candidate The candidate
     * @param url The URL of the metadata
     *
     */
    def retrieveEml(CandidateDataResource candidate, String url) {
        log.debug("Retrieving EML from " + url)
        def eml = getContent(url)

        if (eml == null)
            return
        emlFields.each { name, accessor ->
            def val = accessor(eml)
            if (val != null)
                candidate.setProperty(name, val)
        }
    }
}
