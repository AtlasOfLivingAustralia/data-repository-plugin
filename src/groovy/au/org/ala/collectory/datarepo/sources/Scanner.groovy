package au.org.ala.collectory.datarepo.sources

import au.org.ala.collectory.datarepo.plugin.CandidateDataResource
import au.org.ala.collectory.datarepo.plugin.DataRepository

/**
 * A scanner scans a data repository and generates possible candidate resources.
 * <p>
 * Subclasses are responsible for implementing a scan on a specific repository
 * Subclasses should use {@link #createCandidate} to generate a semi-initialised candidate which can be over-written.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
abstract class Scanner {
    /** The name of the scanner. Override for each subclass */
    static NAME = "Abstract"
    /** The name of the scanner. Override for each subclass */
    static DESCRIPTION = "Abstract scanner which shouldn't appear in lists"

    def DataRepository repository

    /**
     * Truncate a large string to fit it into a field.
     * <p>
     * Truncated strings have an ellipsis at the end
     *
     * @param string The string
     * @param len The expected length
     *
     * @return The possible truncated string
     */
    def truncate(String string, int len) {
        if (!string || string.length() <= len)
            return string
        int idx = string.lastIndexOf((char) ' ', len - 3)
        if (idx < 0)
            idx = len - 3
        return string.substring(0, idx) + "..."
    }

    /**
     * Construct for a repository.
     * <p>
     * The data repository contains enough information to make a connection to the
     * repository at the other end.
     *
     * @param repository The data repository
     */
    Scanner(DataRepository repository) {
        this.repository = repository
    }

    /**
     * Generate a new candidate data resource.
     * <p>
     * Any useful information from the repository is copied at this point.
     *
     * @return A new candidate data resource
     */
    def createCandidate() {
        CandidateDataResource cdr = new CandidateDataResource()

        cdr.dataProvider = repository.dataProvider
        return cdr
    }

    /**
     * Scan the source for data.
     *
     * @param since Only include resources changed after this date (null for all resources)
     *
     * @return A list of possible candidate resources
     */
    abstract List<CandidateDataResource> scan(Date since);

    /**
     * Get a list of the available types of scanner, along with a name and description.
     * The returned list is a list of dictionaries with scannerClass and name properties.
     *
     * @return The list of
     */
    static List<Class<Scanner>> list() {
        [
                [scannerClass: NullScanner.class, name: "${NullScanner.NAME} - ${NullScanner.DESCRIPTION}"],
                [scannerClass: DAPScanner.class, name: "${DAPScanner.NAME} - ${DAPScanner.DESCRIPTION}"]
        ]
    }

}
