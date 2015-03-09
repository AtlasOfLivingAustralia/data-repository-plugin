package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.Action
import au.org.ala.collectory.ActivityLog
import au.org.ala.collectory.DataController
import grails.converters.JSON
import grails.converters.XML
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

/**
 * A controller that allows access to candidate data
 */
class CandidateController {
    def candidateService
    def collectoryAuthService
    def issueManagementService

    def beforeInterceptor = [action: this.&auth]

    /**
     * Endure access control.
     *
     * @return True if authorised, false otherwise
     */
    def auth() {
        if (grailsApplication.config.security.cas.bypass.toBoolean())
            return true;
        if (!collectoryAuthService?.userInRole(CandidateDataResource.ROLE_EDITOR)) {
            response.setHeader("Content-type", "text/plain; charset=UTF-8")
            render (status: 403, text: message(code: "candidate.controller.error.auth", default: "You are not authorised to access this page. You do not have 'Candidate editor' rights."))
            return false
        }
    }

    def index = { }

    /**
     * Return JSON representation of the counts of candidates
     *
     * @param groupBy - name of the property to group by
     */
    def count = {
        def list = CandidateDataResource.list()

        // init results with total
        def results = [total: list.size()]

        if (params.groupBy) {
            results.groupBy = params.groupBy
            def groups = [:]
            list.each {
                def value = it[params.groupBy]
                if (groups[value]) {
                    groups[value]++
                } else {
                    groups[value] = 1
                }
            }
            results.groups = groups
        }
        render(text: results as JSON, encoding: "UTF-8", contentType: "application/json")
    }


    def list = {
        if (params.message)
            flash.message = params.message
        params.max = Math.min(params.max ? params.int('max') : 50, 100)
        params.sort = params.sort ?: "name"
        // ActivityLog.log collectoryAuthService?.username(), collectoryAuthService?.isAdmin(), Action.LIST
        [instanceList: CandidateDataResource.list(params), entityType: 'CandidateDataResource', instanceTotal: CandidateDataResource.count()]
    }

    def show = {
        def instance = CandidateDataResource.findByUid(params.uid)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'candidate.label', default: 'Candidate'), params.uid])}"
            redirect(action: "list")
        } else
            [instance: instance, publicIssueUrl: issueManagementService.publicIssueUrl(instance), changes: getChanges(instance.uid), events: candidateService.getEvents(instance)]
     }

    def create = {
        log.debug "Create new candidate"
        def instance = new CandidateDataResource(lifecycle: 'New', name: 'Name')
        render(view: 'edit', model: [instance: instance])
    }

    def edit = {
        def instance = CandidateDataResource.findByUid(params.uid)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'candidate.label', default: 'Candidate'), params.uid])}"
            redirect(action: "list")
        } else {
            [instance: instance]
        }
    }

    def save = {
        log.debug "Save with params ${params}"
        def instance = CandidateDataResource.findByUid(params.uid)
        if (!instance) {
            instance = candidateService.insert(params)
            candidateService.event(candidateService.CANDIDATE_EVENT, instance)
        } else
            instance = candidateService.update(instance, params)
         render(view: instance.hasErrors() ? 'edit' : 'show', model: [instance: instance, changes: getChanges(instance.uid), events: candidateService.getEvents(instance)])
    }

    def cancel = {
        log.debug "Cancelled edit"
        redirect(action: "list")
    }

    def accept = {
        log.debug "Accept ${params.uid}"
        def instance = CandidateDataResource.findByUid(params.uid)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'candidate.label', default: 'Candidate'), params.uid])}"
            redirect(action: "list")
        } else {
            instance = candidateService.event(candidateService.ACCEPT_EVENT, instance)
            instance = candidateService.event(candidateService.CREATED_DATA_RESOURCE_EVENT, instance)
            render(view: instance.hasErrors() ? 'edit' : 'show', model: [instance: instance, changes: getChanges(instance.uid),  events: candidateService.getEvents(instance)])
        }
    }

    def clearError = {
        log.debug "Clear error on ${params.uid}"
        def instance = CandidateDataResource.findByUid(params.uid)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'candidate.label', default: 'Candidate'), params.uid])}"
            redirect(action: "list")
        } else {
            instance = candidateService.event(candidateService.CLEAR_ERROR_EVENT, instance)
            render(view: instance.hasErrors() ? 'edit' : 'show', model: [instance: instance, changes: getChanges(instance.uid),  events: candidateService.getEvents(instance)])
        }
    }

    /**
     * Return headers as if GET had been called - but with no payload.
     *
     * @param uid - uid of an instance of entity
      */
    def head = {
        def instance = CandidateDataResource.findByUid(params.uid)
        if (instance) {
            addContentLocation instance
            addLastModifiedHeader instance
        }
        render ""
    }

    def getEntity = {
        if (params.uid) {
            log.debug "Get with uid ${params.uid}"
            def instance = CandidateDataResource.findByUid(params.uid)
            if (!instance)
                return wsError("Invalid uid ${params.uid}")
            addContentLocation instance
            addLastModifiedHeader instance
            instance = candidateService.read(instance)
            response.addHeader HttpHeaders.VARY, HttpHeaders.ACCEPT
            render instance
        } else {
            log.debug "Get list}"
            def results = CandidateDataResource.list([sort: 'name'])
            def summaries = results.collect(summary) as JSON
            render summaries
        }

    }

    def saveEntity = {
        def cdr
        def json = request.JSON
        json.user = collectoryAuthService.username()
        json.dataProvider = json.dataProvider == null ? null : [uid: json.dataProvider] // Match form update
        json.dataResource = json.dataResource == null ? null : [uid: json.dataResource]
        log.info "Update for ${params.uid} is ${json}"
        if (!params.uid) {
            cdr = candidateService.insert(json)
            if (cdr.hasErrors())
                return wsError(cdr.errors)
            addContentLocation cdr
            return render(status:201, text: "Created candidate ${cdr.uid}")
        } else {
            cdr = CandidateDataResource.findByUid(params.uid)
            if (!cdr)
                return wsError("Invalid uid ${params.uid}")
            cdr = candidateService.update(cdr, json)
            if (cdr.hasErrors())
                return wsError(cdr.errors)
            addContentLocation cdr
            return render(status:200, text: "Updated candidate ${cdr.uid}")
        }
    }

    private def wsError = { error -> render(status:400, text:error)}

    private def summary = { instance -> [name: instance.name, uid: instance.uid, uri: buildUri(instance)] }

    private def buildUri(instance) {
        return grailsApplication.config.grails.serverURL + "/ws/candidate/${instance.uid}"
    }

    private def getChanges(uid) {
         return AuditLogEvent.findAllByUri(uid,[sort:'lastUpdated',order:'desc',max:10])
    }

    private def addContentLocation(instance) {
        response.addHeader HttpHeaders.CONTENT_LOCATION, buildUri(instance)
    }

    private def addLastModifiedHeader(instance) {
        response.addHeader HttpHeaders.LAST_MODIFIED, DataController.rfc1123Format.format(instance.lastUpdated)
    }

}
