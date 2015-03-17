package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.DataResource
import au.org.ala.collectory.IdGeneratorService
import au.org.ala.collectory.state.StateMachine
import grails.converters.JSON
import grails.transaction.Transactional
import grails.web.JSONBuilder
import org.springframework.context.i18n.LocaleContextHolder

/**
 * Handle changes to candidate data
 */
@Transactional
class CandidateService {
    /** Event indicating that the candidate is a candidiate data resource */
    static final CANDIDATE_EVENT = "candidate"
    /** Event indicating that the candidate has been accepted */
    static final ACCEPT_EVENT = "accept"
    /** Event indicating that the candidate has been rejected */
    static final REJECT_EVENT = "reject"
    /** Event indicating that the candidate has been loaded */
    static final LOAD_EVENT = "loaded"
    /** Event indicating that new data has arrived and the candidate may need to be updated */
    static final UPDATE_EVENT = "update"
    /** Event indicating that the an error should be cleared */
    static final CLEAR_ERROR_EVENT = "clearError"
    /** Event indicating that wee have created a data resource for this candidate */
    static final CREATED_DATA_RESOURCE_EVENT = "created_data_resource"
    /** Event indicating that we have reviewed a data resource and it does not need to be updated */
    static final REVIEWED_NOUPDATE_EVENT = "reviewed_noupdate"
    /** Event indicating that we have reviewed a data resource and it needs to be updated */
    static final REVIEWED_UPDATE_EVENT = "reviewed_update"

    /** Message key for data resource created */
    static final CREATED_DATA_RESOURCE_TITLE_MESSAGE = "candidate.action.createddr.title"
    /** Message key for data resource created */
    static final CREATED_DATA_RESOURCE_BODY_MESSAGE = "candidate.action.createddr.body"
    /** Message key for data resource accepted */
    static final ACCEPT_TITLE_MESSAGE = "candidate.action.accept.title"
    /** Message key for data resource rejected */
    static final REJECT_TITLE_MESSAGE = "candidate.action.reject.title"
    /** Message key for data resource rejected */
    static final LOADED_TITLE_MESSAGE = "candidate.action.loaded.title"
    /** Message key for data resource rejected */
    static final REVIEW_TITLE_MESSAGE = "candidate.action.review.title"
    /** Message key for data resource rejected */
    static final REVIEW_NOUPDATE_TITLE_MESSAGE = "candidate.action.reviewNoupdate.title"
    /** Message key for data resource rejected */
    static final REVIEW_UPDATE_TITLE_MESSAGE = "candidate.action.reviewUpdate.title"
    /** Message key for data resource rejected */
    static final RELOAD_TITLE_MESSAGE = "candidate.action.reload.title"

    def grailsApplication
    def idGeneratorService
    def issueManagementService
    def messageSource
    def collectoryAuthService

    def stateMachine = StateMachine.make {
        state "New", {
            titleKey = "candidate.lifecycle.new"
            title = "New"
            descriptionKey = "candidate.lifecycle.new.description"
            description = "Newly created candidate data resource"
        }
        state "Candidate", {
            titleKey = "candidate.lifecycle.candidate"
            title = "Candidate"
            descriptionKey = "candidate.lifecycle.candidate.description"
            description = "A candidate data resource awaiting examination"
            tags = [ "candidate" ]
            action = { cdr -> issueManagementService.tag(cdr.issueId, tags) }
        }
        state "DataResource"
        state "Accepted", {
            titleKey = "candidate.lifecycle.accepted"
            title = "Accepted"
            descriptionKey = "candidate.lifecycle.accepted.description"
            description = "A data resource accepted and awaiting loading"
            tags = [ "accepted", "load" ]
            action = { cdr ->
                issueManagementService.comment(
                   cdr.issueId,
                   messageSource.getMessage(ACCEPT_TITLE_MESSAGE, [cdr?.dataResource?.uid] as Object[], "Accepted {0}", LocaleContextHolder.locale),
                   null
                )
                issueManagementService.tag(cdr.issueId, tags)
            }
        }
        state "Rejected", {
            titleKey = "candidate.lifecycle.rejected"
            title = "Rejected"
            descriptionKey = "candidate.lifecycle.rejected.description"
            description = "A data resource that has been rejected"
            tags = [ "rejected" ]
            action = { cdr ->
                issueManagementService.comment(
                        cdr.issueId,
                        messageSource.getMessage(REJECT_TITLE_MESSAGE, null, "Rejected", LocaleContextHolder.locale),
                        null
                )
                issueManagementService.tag(cdr.issueId, tags)
                issueManagementService.close(cdr.issueId)
            }
        }
        state "Loaded", {
            titleKey = "candidate.lifecycle.loaded"
            title = "Loaded"
            descriptionKey = "candidate.lifecycle.loaded.description"
            description = "The data resource has been loaded"
            tags = [ "loaded" ]
            action = { cdr ->
                issueManagementService.comment(
                        cdr.issueId,
                        messageSource.getMessage(LOADED_TITLE_MESSAGE, [cdr?.dataResource?.uid] as Object[], "Loaded {0}", LocaleContextHolder.locale),
                        null
                )
                issueManagementService.tag(cdr.issueId, tags)
                issueManagementService.close(cdr.issueId)
            }
        }
        state "Review", {
            titleKey = "candidate.lifecycle.review"
            title = "Review"
            descriptionKey = "candidate.lifecycle.review.description"
            description = "The data source has been updated and needs to be reviewed"
            tags = [ "review" ]
            action = { cdr ->
                issueManagementService.comment(
                        cdr.issueId,
                        messageSource.getMessage(REVIEW_TITLE_MESSAGE, [cdr?.dataResource?.uid] as Object[], "Review {0}", LocaleContextHolder.locale),
                        null
                )
                issueManagementService.reopen(cdr.issueId)
                issueManagementService.tag(cdr.issueId, tags)
            }
        }
        state "Reload", {
            titleKey = "candidate.lifecycle.reload"
            title = "Reload"
            descriptionKey = "candidate.lifecycle.reload.description"
            description = "The data source needs to be reloaded"
            tags = [ "load" ]
            action = { cdr ->
                issueManagementService.comment(
                        cdr.issueId,
                        messageSource.getMessage(RELOAD_TITLE_MESSAGE, [cdr?.dataResource?.uid] as Object[], "Awating reload of {0}", LocaleContextHolder.locale),
                        null
                )
                issueManagementService.tag(cdr.issueId, tags)
            }
        }
        state "Error", {
            titleKey = "candidate.lifecycle.error"
            title = "Error"
            descriptionKey = "candidate.lifecycle.error.description"
            description = "The candidiate data resource has entered an error state"
            tags = [ "bug" ]
            action = { cdr ->
                log.error "Candidate data resource ${cdr?.uid} entered error state"
                issueManagementService.comment(cdr.issueId, "Error. Please inform ${grailsApplication?.config?.grails?.serverURL}")
                issueManagementService.tag(cdr.issueId, tags)
            }
        }
        start "New"
        error "Error"
        event CANDIDATE_EVENT, {
            external = false
        }
        event ACCEPT_EVENT, {
            titleKey = "candidate.event.accept"
            title = "Accept"
            descriptionKey = "candidate.event.accept.description"
            description = "Accept this candidate as a data resource"
        }
        event REJECT_EVENT, {
            titleKey = "candidate.event.reject"
            title = "Reject"
            descriptionKey = "candidate.event.reject.description"
            description = "Reject this candidate as a data resource"
        }
        event LOAD_EVENT, {
            titleKey = "candidate.event.load"
            title = "Loaded"
            descriptionKey = "candidate.event.load.description"
            description = "This data resource has been loaded"
        }
        event UPDATE_EVENT, {
            external = false
        }
        event REVIEWED_NOUPDATE_EVENT, {
            titleKey = "candidate.event.reviewedNoupdate"
            title = "No Update Required"
            descriptionKey = "candidate.event.reviewedNoupdate.description"
            description = "The update has been reviewed and no update is required"
        }
        event REVIEWED_UPDATE_EVENT, {
            titleKey = "candidate.event.reviewedUpdate"
            title = "Update Required"
            descriptionKey = "candidate.event.reviewedUpdate.description"
            description = "The update has been reviewed and an update is required"
        }
        event CREATED_DATA_RESOURCE_EVENT
        event CLEAR_ERROR_EVENT, {
            titleKey = "candidate.event.clearError"
            title = "Clear Error"
            descriptionKey = "candidate.event.clearError.description"
            description = "Clear any error state associated with this candidate"
        }
        transition "New", CANDIDATE_EVENT, "Candidate", { cdr ->
            cdr.issueId = issueManagementService.create(cdr.name, cdr.pubDescription, [])
            if (cdr.issueId == null)
                log.error "Unable to create issue for candidiate ${cdr.uid}"
             log.debug "Candidiate ${cdr.uid} associated with issue ${cdr.issueId}"
        }
        transition "Candidate", ACCEPT_EVENT, "DataResource", { cdr ->
            createDataResourceAction(cdr)
        }
        transition "DataResource", CREATED_DATA_RESOURCE_EVENT, "Accepted"
        transition "Error", CLEAR_ERROR_EVENT, "Candidate", { cdr ->
            issueManagementService.comment(cdr.issueId, "Error cleared")
        }
        transition "Candidate", REJECT_EVENT, "Rejected"
        transition "Accepted", LOAD_EVENT, "Loaded"
        transition "Loaded", UPDATE_EVENT, "Review"
        transition "Review", REVIEWED_NOUPDATE_EVENT, "Loaded", { cdr ->
            issueManagementService.comment(
                    cdr.issueId,
                    messageSource.getMessage(REVIEW_NOUPDATE_TITLE_MESSAGE, null, "No update required", LocaleContextHolder.locale),
                    null
            )
        }
        transition "Review", REVIEWED_UPDATE_EVENT, "Reload", { cdr ->
            issueManagementService.comment(
                    cdr.issueId,
                    messageSource.getMessage(REVIEW_UPDATE_TITLE_MESSAGE, null, "Update required", LocaleContextHolder.locale),
                    null
            )
        }
        transition "Reload", LOAD_EVENT, "Loaded"
    }

    /**
     * Generate a JSON representation of a candidiate
     *
     * @param dr The candidate data resource
     */
    def read(CandidateDataResource dr) {
        def builder = new JSONBuilder()
        def result = builder.build {
            guid = dr.guid
            uid = dr.uid
            lifecycle = dr.lifecycle
            name = dr.name
            dataProvider = dr.dataProvider?.uid
            dataResource = dr.dataResource?.uid
            pubDescription = dr.pubDescription
            techDescription = dr.techDescription
            if (!dr.address)
                address = null
            else {
                address {
                    street = dr.address.street
                    city = dr.address.city
                    state = dr.address.state
                    postcode = dr.address.postcode
                    country = dr.address.country
                    postBox = dr.address.postBox
                }
            }
            latitude = dr.latitude
            longitude = dr.longitude
            state = dr.state
            email = dr.email
            phone = dr.phone
            websiteUrl = dr.websiteUrl
            alaPublicUrl = (grailsApplication.config.grails.serverURL ?: "") + dr.buildRelativeUrl()
            issueId = dr.issueId
            issueUrl = issueManagementService.publicIssueUrl(dr)
            primaryContact = dr.primaryContact
            notes = dr.notes
            connectionParameters = dr.connectionParameters ? JSON.parse(dr.connectionParameters) : null
            lastModified = dr.lastModified
            dateCreated = dr.dateCreated
            lastUpdated = dr.lastUpdated
            userLastModified = dr.userLastModified
        }
        return result
    }

    /**
     * Insert a new candidate data resource
     *
     * @param obj The object that carries the data resource data
     *
     * @return The new data resource
     */
    def insert(obj) {
        def uid = idGeneratorService.getNextTempDataResource().replace(IdGeneratorService.IdType.tempDataResource.prefix, CandidateDataResource.ENTITY_PREFIX)
        log.debug "Insert new instance with uid ${uid}"
        return update(new CandidateDataResource(uid: uid, lifecycle: stateMachine.start.name), obj)
    }

    /**
     * Update an existing candidate data resource
     *
     * @param dr The data resource
     * @param obj The object that carries the update
     *
     * @return The resulting data resource
     */
    def update(CandidateDataResource dr, obj) {
        log.debug "Update ${dr.uid} with ${obj}"
        dr.update(obj)
        dr.userLastModified = obj.user ?: 'Data services'
        dr.validate()
        if (!dr.hasErrors())
            dr.save(flush: true)
        else {
            dr.errors.every { err -> log.debug err }
        }
        return dr
    }

    /**
     * Process an event for a candidate.
     * <p>
     * The event is matched against the current lifecycle state
     * and any appropriate arc and action is processed.
     *
     * @param event The event
     * @param cdr The candidate data resource
     */
    def CandidateDataResource event(String event, CandidateDataResource cdr) {
        cdr.lifecycle = stateMachine.process(cdr.lifecycle, event, cdr)
        cdr.validate()
        if (!cdr.hasErrors())
            cdr.save()
        else {
            cdr.errors.every { err -> log.debug err }
        }
        return cdr
    }

    def createDataResourceAction(cdr) {
        DataResource dr = cdr.dataResource;

        if (dr == null) {
            dr = new DataResource(
                    guid: cdr.guid,
                    uid: idGeneratorService.getNextDataResourceId(),
                    name: cdr.name,
                    pubDescription: cdr.pubDescription,
                    techDescription: cdr.techDescription,
                    address: cdr.address,
                    latitude: cdr.latitude,
                    longitude: cdr.longitude,
                    state: cdr.state,
                    websiteUrl: cdr.websiteUrl,
                    email: cdr.email,
                    phone: cdr.phone,
                    notes: cdr.notes,
                    dataProvider: cdr.dataProvider,
                    connectionParameters: cdr.connectionParameters,
                    userLastModified: collectoryAuthService?.username() ?: "Candidate Service"

            )
            dr.validate()
            if (dr.hasErrors()) {
                dr.errors.every { err -> log.warn err }
                throw new IllegalStateException("Unable to create data resource due to validation errors")
            }
            dr.save()
            cdr.dataResource = DataResource.findByUid(dr.uid)
            cdr.save()
            issueManagementService.comment(
                    cdr.issueId,
                    messageSource.getMessage(CREATED_DATA_RESOURCE_TITLE_MESSAGE, [dr?.uid] as Object[], "Created data resource {0}", LocaleContextHolder.locale),
                    messageSource.getMessage(CREATED_DATA_RESOURCE_BODY_MESSAGE, [dr?.buildPublicUrl()] as Object[], "Created data resource {0}", LocaleContextHolder.locale)
            )
            log.info "Candidiate ${cdr?.uid} associated with data resource ${dr?.uid}"
        }
    }

    def List<StateMachine.Event> getEvents(CandidateDataResource cdr) {
        return stateMachine.getExternal(cdr.lifecycle)
    }
}
