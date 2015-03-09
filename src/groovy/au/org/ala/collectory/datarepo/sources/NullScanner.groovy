package au.org.ala.collectory.datarepo.sources

import au.org.ala.collectory.datarepo.plugin.CandidateDataResource
import au.org.ala.collectory.datarepo.plugin.DataRepository

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
class NullScanner extends Scanner {
    /** The name of the scanner. Override for each subclass */
    static NAME = "Null"
    /** The name of the scanner. Override for each subclass */
    static DESCRIPTION = "A scanner that doesn't do anything"

    /**
     * Construct for a repository
     * @param repository
     */
    NullScanner(DataRepository repository) {
        super(repository)
    }

    /**
     * A null scan returns no candidates
     *
     * @param since The date last modified
     *
     * @return An empty list
     */
    @Override
    List<CandidateDataResource> scan(Date since) {
        return []
    }
}
