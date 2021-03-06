package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.DataController
import au.org.ala.collectory.ProviderGroup
import au.org.ala.collectory.datarepo.sources.NullScanner
import au.org.ala.util.ISO8601Helper
import grails.converters.JSON
import grails.converters.XML
import org.codehaus.groovy.grails.plugins.orm.auditable.AuditLogEvent
import org.codehaus.groovy.grails.web.servlet.HttpHeaders

/**
 * A controller that allows access to candidate data
 */
class DataRepositoryController {
    static final API_KEY_COOKIE = "ALA-API-Key"

    def dataRepositoryService
    def collectoryAuthService

    def beforeInterceptor = [action: this.&auth]

    /**
     * Endure access control.
     *
     * @return True if authorised, false otherwise
     */
    def auth() {
        if (grailsApplication.config.security.cas.bypass.toBoolean())
            return true;
        if (!collectoryAuthService?.userInRole(DataRepository.ROLE_EDITOR)) {
            response.setHeader("Content-type", "text/plain; charset=UTF-8")
            render (status: 403, text: message(code: "dataRepository.controller.error.auth", default: "You are not authorised to access this page. You do not have 'Candidate editor' rights."))
            return false
        }
    }

    def index = { }

    /**
     * Return JSON representation of the counts of data repositories
     *
     * @param groupBy - name of the property to group by
     */
    def count = {
        def list = DataRepository.list()

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
        [instanceList: DataRepository.list(params), entityType: 'DataRepository', instanceTotal: DataRepository.count()]
    }

    def show = {
        def instance = DataRepository.findByUid(params.uid)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataRepository.label', default: 'Data Repository'), params.uid])}"
            redirect(action: "list")
        } else
            [instance: instance, changes: getChanges(instance.uid)]
     }

    def create = {
        log.debug "Create new data repository"
        def instance = new DataRepository(scannerClass: NullScanner.class, name: 'Name')
        render(view: 'edit', model: [instance: instance])
    }

    def edit = {
        def instance = DataRepository.findByUid(params.uid)
        if (!instance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'dataRepository.label', default: 'Data Repository'), params.uid])}"
            redirect(action: "list")
        } else {
            [instance: instance]
        }
    }

    def save = {
        log.debug "Save with params ${params}"
        def instance = DataRepository.findByUid(params.uid)
        if (!instance)
            instance = dataRepositoryService.insert(params)
        else
            instance = dataRepositoryService.update(instance, params)
         render(view: instance.hasErrors() ? 'edit' : 'show', model: [instance: instance, changes: getChanges(instance.uid)])
    }

    def cancel = {
        log.debug "Cancelled edit"
        redirect(action: "list")
    }

    /**
     * Return headers as if GET had been called - but with no payload.
     *
     * @param uid - uid of an instance of entity
      */
    def head = {
        def instance = DataRepository.findByUid(params.uid)
        if (instance) {
            addContentLocation instance
            addLastModifiedHeader instance
        }
        render ""
    }

    def getEntity = {
        if (params.uid) {
            log.debug "Get with uid ${params.uid}"
            def instance = DataRepository.findByUid(params.uid)
            if (!instance)
                return wsError("Invalid uid ${params.uid}")
            addContentLocation instance
            addLastModifiedHeader instance
            instance = dataRepositoryService.read(instance)
            response.addHeader HttpHeaders.VARY, HttpHeaders.ACCEPT
            render instance
        } else {
            log.debug "Get list}"
            def results = DataRepository.list([sort: 'name'])
            def summaries = results.collect(summary) as JSON
            render summaries
        }

    }

    def saveEntity = {
        def dr
        def json = request.JSON
        json.user = collectoryAuthService.username()
        json.dataProvider = json.dataProvider == null ? null : [uid: json.dataProvider] // Match form update
        log.info "Update for ${params.uid} is ${json}"
        if (!params.uid) {
            dr = dataRepositoryService.insert(json)
            if (dr.hasErrors())
                return wsError(dr.errors)
            addContentLocation dr
            return render(status:201, text: "Created data repository ${dr.uid}")
        } else {
            dr = DataRepository.findByUid(params.uid)
            if (!dr)
                return wsError("Invalid uid ${params.uid}")
            dr = dataRepositoryService.update(dr, json)
            if (dr.hasErrors())
                return wsError(dr.errors)
            addContentLocation dr
            return render(status:200, text: "Updated data repository ${dr.uid}")
        }
    }

    def scan = {
        def apiKey = request.cookies.find { cookie -> cookie.name == API_KEY_COOKIE }
        def keyCheck = apiKey ? collectoryAuthService.checkApiKey(apiKey.value) : null
        def username = keyCheck?.userEmail ?: collectoryAuthService.username()
        def admin = keyCheck?.valid || collectoryAuthService.userInRole(ProviderGroup.ROLE_ADMIN)
        def since = params.since ? ISO8601Helper.parseTimestamp(params.since) : null

        log.debug "Access via apikey: ${keyCheck}, user ${username}, admin ${admin}"
        if (!admin)
            return wsError("Scan requires admin role")
        if (!params.uid)
            return wsError("Requires repository uid")
        log.debug "Scan with uid ${params.uid} format ${params.format}"
        def instance = DataRepository.findByUid(params.uid)
        if (!instance)
            return wsError("Invalid uid ${params.uid}")
        def updates = dataRepositoryService.scan(instance, since, username)
        log.info "${updates.size()} candidates to update for ${params.uid}"
        response.addHeader HttpHeaders.VARY, HttpHeaders.ACCEPT
        withFormat {
            text { render (contentType: 'text/plain', text: updates.findAll({ dr -> dr.uid != null }).collect({ dr -> dr.uid }).join("\n")) }
            xml { render updates as XML }
            '*' { render updates as JSON }
        }
    }

    private def wsError = { error -> render(status:400, text:error)}

    private def summary = { instance -> [name: instance.name, uid: instance.uid, uri: buildUri(instance)] }

    private def buildUri(instance) {
        return grailsApplication.config.grails.serverURL + "/ws/dataRepository/${instance.uid}"
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
