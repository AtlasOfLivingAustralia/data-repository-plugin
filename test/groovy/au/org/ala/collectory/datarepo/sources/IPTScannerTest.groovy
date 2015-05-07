package au.org.ala.collectory.datarepo.sources

import au.org.ala.collectory.datarepo.plugin.CandidateDataResource
import au.org.ala.collectory.datarepo.plugin.DataRepository
import au.org.ala.util.ISO8601Helper
import au.org.ala.util.ResourceMixin
import spock.lang.Specification

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Mixin(ResourceMixin)
class IPTScannerTest extends Specification {
    // Capture closure behaviour
    DataRepository repository
    IPTScanner scanner

    def resolveRss1 = { String path ->
        if (path == "http://xxxx.oooo/ipt/rssx.do")
            return this.loadResourceXml("ipt-rss-1.xml")
        if (path == "http://xxxx.oooo/ipt/eml.do?r=csiro_ab_zooplankton_1")
            return this.loadResourceXml("ipt-eml-1.xml")
        return null
    }

    def setup() {
        repository = new DataRepository()

        repository.name = "Test Repository"
        repository.connectionParameters = '{ "rss": "http://xxxx.oooo/ipt/rssx.do" }'
        repository.scannerClass = IPTScanner.class
        scanner = repository.createScanner()
    }

    def testCreateCandidate1() {
        when:
            def entry = this.loadResourceXml("ipt-rss-1.xml").channel.item[0]
            scanner.metaClass.getContent = resolveRss1
            CandidateDataResource cdr = scanner.createCandidate(entry)
        then:
            cdr.guid == "http://xxxx.oooo/ipt/resource.do?r=csiro_ab_zooplankton_1"
            cdr.name == "CSIRO, Tropical Zooplankton Biomass and Composition, Albatross Bay - Gulf of Carpentaria, North Australia 1986-1988 - Version 3"
            cdr.pubDescription.startsWith("The biomass and species composition of tropical zooplankton in Albatross Bay, Gulf of Carpentaria")
            cdr.techDescription.startsWith("Greenwood, J.G. and Rothlisberg, P.C. (1990). Temporal and spatial variation")
            cdr.websiteUrl == "http://xxxx.oooo/ipt/resource.do?r=csiro_ab_zooplankton_1"
    }

    def testRss1() {
        when:
            Date since = ISO8601Helper.parseTimestamp("2014-01-01T00:00:00")
            scanner.metaClass.getContent = resolveRss1
            List<CandidateDataResource> cdrs = scanner.rss(since)
        then:
            cdrs.size() == 2
    }

    def testRss2() {
        when:
            Date since = ISO8601Helper.parseTimestamp("2014-10-01T00:00:00")
            scanner.metaClass.getContent = resolveRss1
            List<CandidateDataResource> cdrs = scanner.rss(since)
        then:
            cdrs.size() == 0
    }

    /*
    def testScanForReal() {
        when:
            repository.connectionParameters = '{ "rss": "http://ogc-act.csiro.au/ipt/rss.do" }'
            scanner = repository.createScanner()
            List<CandidateDataResource> cdrs = scanner.scan(null)
        then:
            cdrs.size() > 0

    }
    */


}