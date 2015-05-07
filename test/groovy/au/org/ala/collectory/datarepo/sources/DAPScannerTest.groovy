package au.org.ala.collectory.datarepo.sources

import au.org.ala.collectory.datarepo.plugin.CandidateDataResource
import au.org.ala.collectory.datarepo.plugin.DataRepository
import au.org.ala.collectory.state.StateMachine
import au.org.ala.util.ISO8601Helper
import au.org.ala.util.ResourceMixin
import groovy.mock.interceptor.MockFor
import groovyx.net.http.HTTPBuilder
import spock.lang.Specification


/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Mixin(ResourceMixin)
class DAPScannerTest extends Specification {
    // Capture closure behaviour
    DataRepository repository
    DAPScanner scanner

    def resolveAtom1 = { String path, type ->
        if (path.matches("http://ws\\.data\\.csiro\\.au/collections/\\d+"))
                return this.loadResourceXml("dap-data-1.xml")
        else if (path.contains("&p=1&"))
                return this.loadResourceXml("dap-atom-1.xml")
        return this.loadResourceXml("dap-atom-2.xml")
    }

    def resolveAtom2 = { String path, type ->
        if (path.matches("http://ws\\.data\\.csiro\\.au/collections/\\d+"))
            return this.loadResourceXml("dap-data-1.xml")
        return this.loadResourceXml("dap-atom-2.xml")
    }

    def setup() {
        repository = new DataRepository()

        repository.name = "Test Repository"
        repository.connectionParameters = '{ "api": "http://ws.data.csiro.au/collections", "query": "specimen", "unresticted": true }'
        repository.scannerClass = DAPScanner.class
        scanner = repository.createScanner()
    }

    def testFindLicence1() {
        when:
            def licence = scanner.findLicence("CC BY")
        then:
            licence != null
            licence.licence == "CC BY"
            licence.version == null
    }

    def testFindLicence2() {
        when:
        def licence = scanner.findLicence("CC-BY")
        then:
        licence != null
        licence.licence == "CC BY"
        licence.version == null
    }

    def testFindLicence3() {
        when:
        def licence = scanner.findLicence("Creative Commons Attribution")
        then:
        licence != null
        licence.licence == "CC BY"
        licence.version == null
    }

    def testFindLicence4() {
        when:
        def licence = scanner.findLicence("Creative Commons Attribution Licence")
        then:
        licence != null
        licence.licence == "CC BY"
        licence.version == null
    }

    def testFindLicence5() {
        when:
        def licence = scanner.findLicence("Creative Commons Attribution Sharealike 3.0")
        then:
        licence != null
        licence.licence == "CC BY-SA"
        licence.version == "3.0"
    }

    def testPageUrl1() {
        expect:
            scanner.pageUrl(1) == "http://ws.data.csiro.au/collections?rpp=100&p=1&soud=true&sb=RECENT&q=specimen"
            scanner.pageUrl(18) == "http://ws.data.csiro.au/collections?rpp=100&p=18&soud=true&sb=RECENT&q=specimen"
    }

    def testProcessEntry1() {
        when:
            def entry = this.loadResourceXml("dap-atom-1.xml").entry[0]
            scanner.metaClass.getContent = resolveAtom1
            CandidateDataResource cdr = scanner.processEntry(entry)
        then:
            cdr.guid == "csiro:9067"
            cdr.name == "Metallesthes specimens inspected and supporting information published in Metallesthes revision"
            cdr.pubDescription.startsWith("This data is a spreadsheet of label data of specimens from the flower beetle genus Metallesthes")
            cdr.techDescription == "Metallesthes; Scarabaeidae; Coleoptera; specimen data; statistical analysis; monthly occurrence"
            cdr.websiteUrl == "https://data.csiro.au/dap/landingpage?pid=csiro%3A9067"
    }

    def testProcessPage1() {
        when:
            Date since = ISO8601Helper.parseTimestamp("2014-01-01T00:00:00")
            scanner.metaClass.getContent = resolveAtom1
            List<CandidateDataResource> cdrs = []
            boolean found = scanner.processPage(1, cdrs, since)
        then:
            found == true
            cdrs.size() == 10
    }

    def testProcessPage2() {
        when:
            Date since = ISO8601Helper.parseTimestamp("2015-01-01T00:00:00")
            scanner.metaClass.getContent = resolveAtom1
            List<CandidateDataResource> cdrs = []
            boolean found = scanner.processPage(1, cdrs, since)
        then:
            found == false
            cdrs.size() == 0
    }

    def testProcessPage3() {
        when:
            Date since = ISO8601Helper.parseTimestamp("2010-01-01T00:00:00")
            scanner.metaClass.getContent = resolveAtom2
            List<CandidateDataResource> cdrs = []
            boolean found = scanner.processPage(1, cdrs, since)
        then:
            found == false
            cdrs.size() == 0
    }

    def testProcessPage4() {
        when:
            scanner.metaClass.getContent = resolveAtom1
            List<CandidateDataResource> cdrs = []
            boolean found = scanner.processPage(1, cdrs, null)
        then:
            found == true
            cdrs.size() == 35
    }

    def testAtom1() {
        when:
            Date since = ISO8601Helper.parseTimestamp("2014-01-01T00:00:00")
            scanner.metaClass.getContent = resolveAtom1
            List<CandidateDataResource> cdrs = []
        boolean found = scanner.processPage(1, cdrs, since)
        then:
            found == true
            cdrs.size() == 10
    }

    def testAtom2() {
        when:
            scanner.metaClass.getContent = resolveAtom1
            List<CandidateDataResource> cdrs = []
            boolean found = scanner.processPage(1, cdrs, null)
        then:
            found == true
            cdrs.size() == 35
    }

    def testAtom3() {
        when:
            Date since = ISO8601Helper.parseTimestamp("2015-01-01T00:00:00")
            scanner.metaClass.getContent = resolveAtom1
            List<CandidateDataResource> cdrs = []
            boolean found = scanner.processPage(1, cdrs, since)
        then:
            found == false
            cdrs.size() == 0
    }

    def testAtom4() {
        when:
           Date since = ISO8601Helper.parseTimestamp("2014-01-01T00:00:00")
             scanner.metaClass.getContent = resolveAtom2
            List<CandidateDataResource> cdrs = []
            boolean found = scanner.processPage(1, cdrs, since)
        then:
            found == false
            cdrs.size() == 0
    }

    /*
    def testScanForReal() {
        when:
        List<CandidateDataResource> cdrs = scanner.scan(null)
        then:
            cdrs.size() == 0

    }
    */

}