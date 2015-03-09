package au.org.ala.collectory.datarepo.issues
/**
 * Interface for issue management implementations.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;
 *
 * Copyright (c) 2015 CSIRO
 */
abstract class IssueManagementSystem {
    /**
     * Construct from a configuration
     *
     * @param config The configuration
     */
    IssueManagementSystem(config) {
    }

    /**
     * Create a new issue
     *
     * @param name The issue name
     * @param description A description of the issue
     * @param tags Tags associated with the issue
     *
     * @return The issue identifier
     */
    abstract String create(String name, String description, List<String> tags)

    /**
     * Comment on an issue.
     * <p>
     * Used to provide a trail of information about the issue.
     *
     * @param id The issue identifier
     * @param title The comment title
     * @param body The comment body
     */
    abstract void comment(String id, String title, String body)

    /**
     * Update the tags on an issue.
     *
     * @param id The issue identifier
     * @param tags The new list of tags
     */
    abstract void tag(String id, List<String> tag)

    /**
     * Close an issue.
     *
     * @param id The issue identifier
     */
    abstract void close(String id)

    /**
     * Re-open an issue.
     *
     * @param id The issue identifier
     */
    abstract void reopen(String id)

    /**
     * Generate a URL that can be used to refer to the issue associated
     * with an identifier.
     *
     * @param id The identifier (may be null)
     *
     * @return The public URL associated with the id, or null for no URL
     */
    abstract String publicIssueUrl(String id)
}
