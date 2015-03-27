package au.org.ala.collectory.datarepo.sources

import au.org.ala.collectory.datarepo.plugin.CandidateDataResource
import au.org.ala.collectory.datarepo.plugin.DataRepository
import au.org.ala.util.ISO8601Helper
import groovy.json.JsonSlurper
import groovyx.net.http.URIBuilder
import groovyx.net.http.HTTPBuilder
import org.codehaus.groovy.grails.web.mime.MimeType
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Scanner for CSIRO Data Access Portal repository
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class DAPScanner extends Scanner {
    static final Logger log = LoggerFactory.getLogger(DAPScanner.class)

    /** The name of the scanner. Override for each subclass */
    static NAME = "CSIRO DAP"
    /** The name of the scanner. Override for each subclass */
    static DESCRIPTION = "CSIRO Data Access Portal"

    /** The base connection description */
    def api
    /** The query string to use (may be null) */
    def query
    /** Show only unrestricted data */
    def unrestricted

    /**
     * Construct for a repository
     *
     * @param repository
     */

    DAPScanner(DataRepository repository) {
        super(repository)

        def slurper = new JsonSlurper()
        def connectionParameters = slurper.parseText(repository.connectionParameters)
        api = connectionParameters.api
        query = connectionParameters.query
        unrestricted = connectionParameters.unrestricted ?: true
        log.debug "Constructed DAP Scanner api=${api}, query=${query}, unrestricted=${unrestricted}"
    }

    /**
     * Scasn the DAP for updates
     *
     * @param since The date last modified
     *
     * @return An empty list
     */
    @Override
    List<CandidateDataResource> scan(Date since) {
        return atom(since)
    }

    def atom(Date since) {
        int pn = 1
        boolean found = true
        List<CandidateDataResource> candidates = []

        // Go through each page until we run out of entries
        while (found) {
            found = processPage(pn, candidates, since)
            pn++
        }
        log.debug "Found ${candidates.size()} candidates"
        return candidates
    }

    /**
     * Construct a per-page call to the
     *
     * @param page
     * @return
     */
    def pageUrl(page) {
        def qs = query
        def url = new URIBuilder(api)

        url.with {
            def q = [rpp: 100, p: page, soud: unrestricted, sb: "RECENT"]
            if (qs)
                q["q"] = qs
            query = q
        }
        return url.toString()
    }

    /**
     * Construct a builder.
     * <p>
     *     Provides a place to put additional header information, such
     *     as accepting XML.
     *     Also provides a convienient point to mock for testing purposes.
     *
     * @param url The URL for the builder
     * @param mimeTyper The mime type (defaults to XML)
     *
     * @return The resulting content
     */
    def getContent(url, mimeType = MimeType.XML) {
        def builder = new HTTPBuilder(url)

        builder.headers[HttpHeaders.ACCEPT] = mimeType?.name
        return builder.get([:])
    }

    def processPage(int page, List<CandidateDataResource> candidates, Date since) {
        def url = pageUrl(page)
        log.debug "Scanning page ${url} for changes since ${since}"
        boolean found = false
        def feed = getContent(url, MimeType.ATOM_XML)
        def entries = feed.entry?.list() ?: []

        for (entry in entries) {
            def published = ISO8601Helper.parseTimestamp(entry.published?.text())

            log.debug "Entry ${entry.id} published ${published}"
            if (since == null || published == null || published.after(since)) {
                def candidate = processEntry(entry)

                if (candidate) {
                    candidates << candidate
                    found = true
                }
            }
        }
        return found
    }

    def processEntry(entry) {
        def link = entry.link.find { l ->  l.@rel == "self" && l.@type == MimeType.XML.name  }

        log.debug "Process entry ${entry.id} link ${link?.@href}"
        if (!link)
            return null;
        def data = getContent(link.@href)
        def candidate = new CandidateDataResource()

        candidate.dataProvider = repository.dataProvider
        candidate.guid = data.id?.text().trim()
        candidate.lastModified = ISO8601Helper.parseTimestamp(data.published.text())
        candidate.lifecycle = "New"
        candidate.name = truncate(data.title?.text().trim(), 1024)
        candidate.pubDescription = data.description?.text().trim()
        candidate.techDescription = data.keywords?.text().trim()
        candidate.websiteUrl = entry.link.find { l -> l.@rel == MimeType.HTML.name }
        candidate.userLastModified = repository.name
        return candidate
    }

}
