package au.org.ala.collectory.datarepo.sources

import au.org.ala.collectory.Address
import au.org.ala.collectory.datarepo.plugin.CandidateDataResource
import au.org.ala.collectory.datarepo.plugin.DataRepository

import java.sql.Timestamp

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class TestScanner extends Scanner {
    /** The name of the scanner */
    static NAME = "Test"
    /** The name of the scanners */
    static DESCRIPTION = "A test scanner"

    def id = 1

    TestScanner(DataRepository repository) {
        super(repository)
    }

    /**
     * Return a single new resource to play with
     * @param since
     * @return
     */
    @Override
    List<CandidateDataResource> scan(Date since) {
        List<CandidateDataResource> resources = []
        CandidateDataResource resource = createCandidate()

        resource.name = "CDR-${id}"
        resource.guid = "urn:x-test:cdr:${id}"
        // http://en.wikipedia.org/wiki/Beasley_Street
        resource.address = new Address()
        resource.address.city = "Salford"
        resource.address.country = "UK"
        resource.address.postcode = "M27 5AW"
        resource.address.street = "${id} Beasley Street"
        resource.email = "nobody@nowhere"
        resource.latitude = -1
        resource.longitude - 1
        resource.pubDescription = "A description of some sort"
        resource.connectionParameters = "http://localhost:8080/candidiate/${id}"
        resource.lastModified = new Timestamp()
        id++
        resource << resource
        return resources
    }

}
