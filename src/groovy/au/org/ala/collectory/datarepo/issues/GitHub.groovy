package au.org.ala.collectory.datarepo.issues

import groovy.util.logging.Log4j
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Log4j
class GitHub extends IssueManagementSystem {
    /** The MIME-type of the API */
    static final API_MIME = "application/vnd.github.v3+json"

    /** The server URL for API access */
    def api
    /** The server URL for public access */
    def pub
    /** The repository path */
    def repository
    /** The user name to use. TODO replace with OAuth2 */
    def user
    /** The password to use. TODO replace with OAuth2 */
    def password

    /**
     * Construct using a configuration bean
     *
     * @param config
     */
    GitHub(config) {
        super(config)
        api = config.issueManagement.api
        pub = config.issueManagement.pub
        repository = config.issueManagement.repository
        user = config.issueManagement.user
        password = config.issueManagement.password
    }

    /**
     * Construct a builder for a request
     *
     * @param path The path to work with
     *
     * @return The builder
     */
    def builder(path) {
        def builder = new HTTPBuilder(api + path)

        builder.auth.basic(user, password)
        builder.headers[HttpHeaders.ACCEPT] = API_MIME
        // Use pre-emptive authorisation. Github does not support 401-style authorisation
        builder.headers[HttpHeaders.AUTHORIZATION] = "Basic " + "${user}:${password}".bytes.encodeBase64().toString()
        // Also required by github. TODO replace with application name once registered
        builder.headers[HttpHeaders.USER_AGENT] = user
        builder.contentType = ContentType.JSON
        return builder
    }

    /**
     * Construct a public URL for the associated issue
     *
     * @param id The issue identifier
     *
     * @return The issue public URL
     */
    @Override
    String publicIssueUrl(String id) {
        return id == null ? null : pub + repository + "/issues/" + id
    }

    /**
     * Create a new issue
     *
     * @param title The issue title
     * @param description The issue description
     * @param tags Tags associated with the creation (null for an empty list)
     *
     * @return The identifier of the corresponding issue
     */
    @Override
    String create(String title, String description, List<String> tags) {
        def http = builder("/repos${repository}/issues")

        log.debug("Creating issue ${title} for ${http.uri}")
        http.request(Method.POST) { req ->
            body = [title: title, body: description, labels: tags == null ? [] : tags]
            response.success = { resp, json ->
                log.info "Created issue ${title} response: ${json}"
                return json.number
            }
            response.failure = { resp ->
                log.warn "Failed to create issue ${title} response: ${resp.responseBase} data: ${resp.responseData}"
                return null
            }
        }
    }

    /**
     * Comment on an issue
     *
     * @param id The issue identifier
     * @param title The comment title
     * @param body The comment body
     */
    @Override
    void comment(String id, String title, String description) {
        def http = builder("/repos${repository}/issues/${id}/comments")
        def comment = title

        if (description)
            comment = comment + "\n" + description
        log.debug("Adding comment ${comment} to issue ${id}")
        http.request(Method.POST) { req ->
            body = [body: comment]
            response.success = { resp, json ->
                log.info "Added comment to ${id} response: ${json}"
            }
            response.failure = { resp ->
                log.warn "Failed add comment to ${id} response: ${resp.responseBase} data: ${resp.responseData}"
            }
        }
    }

    /**
     * Change the tags on an issue.
     *
     * @param id The issue identifier
     * @param tags The new list of tags
     */
    @Override
    void tag(String id, List<String> tags) {
        def http = builder("/repos${repository}/issues/${id}/labels")

        log.debug("Adding tags ${tags} to issue ${id}")
        http.request(Method.PUT) { req ->
            body = tags
            response.success = { resp, json ->
                log.info "Added tags to ${id} response: ${json}"
            }
            response.failure = { resp ->
                log.warn "Failed add tags to ${id} response: ${resp.responseBase} data: ${resp.responseData}"
            }
        }
    }

    /**
     * Change the issue state
     *
     * @param id The issue identifier
     */
    void changeState(String id, String state) {
        def http = builder("/repos${repository}/issues/${id}")

        log.debug("Set state ${state} on issue ${id} - ${http.uri}")
        http.request(Method.POST) { req ->
            body = [state: state]
            response.success = { resp, json ->
                log.info "Set ${state} on ${id} response: ${json}"
            }
            response.failure = { resp ->
                log.warn "Failed to set ${state} on ${id} response: ${resp.responseBase} data: ${resp.responseData}"
            }
        }
    }

    /**
     * Close an issue.
     *
     * @param id The issue identifier
     */
    @Override
    void close(String id) {
        changeState(id, "closed")
    }

    /**
     * Re-open an issue.
     *
     * @param id The issue identifier
     */
    @Override
    void reopen(String id) {
        changeState(id, "open")
    }

}
