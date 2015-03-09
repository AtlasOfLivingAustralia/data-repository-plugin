package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.datarepo.issues.GitHub
import au.org.ala.collectory.datarepo.issues.IssueManagementSystem
import grails.transaction.Transactional
import org.springframework.transaction.annotation.Propagation

import javax.annotation.PostConstruct

/**
 * Integrate with an issue manager.
 * <p>
 * The actual issue management system is derived from a configuration class.
 * Defined by the <code>issueManagement.type</code> configuration property
 */
@Transactional(propagation = Propagation.NOT_SUPPORTED) // No way of supporting transactions on server
class IssueManagementService {
    def grailsApplication
    def issueManagementSystem

    /**
     * Construct the issue management system if it has not been supplied
     */
    @PostConstruct
    def buildSystem() {
        if (issueManagementSystem == null) {
            def type = grailsApplication.config.issueManagement.type ?: GitHub.class.getName()
            def system = (Class<IssueManagementSystem>) Class.forName(type)

            issueManagementSystem = system.newInstance(grailsApplication.config)
        }
    }

    /**
     * Create a new issue
     *
     * @param name The name of the issue
     * @param description A description of the issue
     * @param tags Tags associated with this issue
     *
     * @return The issue identifier
     */
    String create(String name, String description, List<String> tags) {
        return issueManagementSystem.create(name, description, tags)
    }

    /**
     * Update an issue with a comment.
     *
     * @param id The issue identifier
     * @param title The comment title
     * @param body An optional comment body
     */
    void comment(String id, String title, String body = null) {
        if (id != null)
            issueManagementSystem.comment(id, title, body)
    }

    /**
     * Update the tags on an issue
     *
     * @param id The issue identifier
     * @param tag The new list of tags
     */
    void tag(String id, List<String> tags) {
        if (id != null && tags != null)
            issueManagementSystem.tag(id, tags)
    }

    /**
     * Close an issue
     *
     * @param id The issue identifier
     */
    void close(String id) {
        if (id != null)
            issueManagementSystem.close(id)
    }

    /**
     * Re-open an issue
     *
     * @param id The issue identifier
     */
    void reopen(String id) {
        if (id != null)
            issueManagementSystem.reopen(id)
    }

    /**
     * Get a public URL for an issue associated with a candidate
     *
     * @param dr The data resource
     *
     * @return The issue URL or null for none
     */
    String publicIssueUrl(CandidateDataResource dr) {
        return issueManagementSystem.publicIssueUrl(dr.issueId)
    }


}
