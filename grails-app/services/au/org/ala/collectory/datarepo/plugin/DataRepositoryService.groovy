package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.IdGeneratorService
import au.org.ala.collectory.datarepo.sources.Scanner
import grails.converters.JSON
import grails.transaction.Transactional
import grails.web.JSONBuilder

import java.sql.Timestamp

/**
 * Handle changes to data repository data
 */
@Transactional
class DataRepositoryService {
    def grailsApplication
    def idGeneratorService
    def candidateService
    def messageSource


    /**
     * Generate a JSON representation of a candidiate
     *
     * @param dr The candidate data resource
     */
    def read(DataRepository dr) {
        def builder = new JSONBuilder()
        def result = builder.build {
            guid = dr.guid
            uid = dr.uid
            name = dr.name
            dataProvider = dr.dataProvider?.uid
            pubDescription = dr.pubDescription
            techDescription = dr.techDescription
            notes = dr.notes
            websiteUrl = dr.websiteUrl
            rights = dr.rights
            citation = dr.citation
            licenseType = dr.licenseType
            licenseVersion = dr.licenseVersion
            connectionParameters = dr.connectionParameters ? JSON.parse(dr.connectionParameters) : null
            scannerClass = dr.scannerClass
            lastChecked = dr.lastChecked
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
        def uid = idGeneratorService.getNextTempDataResource().replace(IdGeneratorService.IdType.tempDataResource.prefix, DataRepository.ENTITY_PREFIX)
        log.debug "Insert new instance with uid ${uid}"
        return update(new DataRepository(uid: uid), obj)
    }

    /**
     * Update an existing candidate data resource
     *
     * @param dr The data resource
     * @param obj The object that carries the update
     *
     * @return The resulting data resource
     */
    def update(DataRepository dr, obj) {
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
     * Scan a data repository for new data resources and see whether there is anything already there.
     * <p>
     * If there isn't, then create a new candidate resource.
     * If there is, it's loaded and the data has changed, then mark it as updated.
     *
     * @param dr The data repository
     * @param since When to scan from
     * @param username The user that is responsible for this
     *
     * @return A list of modified candidates
     */
    List<CandidateDataResource> scan(DataRepository dr, Date since, String username) {
        Timestamp now = new Timestamp(System.currentTimeMillis())
        Scanner scanner = dr.createScanner()
        def query = CandidateDataResource.where { dataProvider.uid == dr.dataProvider.uid }
        Map<String, CandidateDataResource> existing = query.list().collectEntries { [it.guid, it] }
        List<CandidateDataResource> candidates = []

        for (CandidateDataResource cdr: scanner.scan(since)) {
            def prev = existing.get(cdr.guid)

            log.debug "Candidiate ${cdr.guid} with existing ${prev?.uid}"
            if (prev) {
                // Have to do this to accommodate milliseconds being lost during timestamps
                Calendar cm = Calendar.instance
                Calendar pm = Calendar.instance
                pm.timeInMillis = prev.lastModified?.time ?: 0L
                cm.timeInMillis = cdr.lastModified?.time ?: now.time
                pm.set(Calendar.MILLISECOND, 0)
                cm.set(Calendar.MILLISECOND, 0)
                if (pm.before(cm)) {
                    log.debug "Loaded candidate ${prev.uid} has been modified ${pm} -> ${cm}"
                    prev.lastModified = cdr.lastModified
                    prev.userLastModified = username
                    prev.save()
                    if (prev.lifecycle == "Loaded")
                        candidateService.event(prev, CandidateService.UPDATE_EVENT)
                    candidates << prev
                }
            } else {
                cdr.userLastModified = username
                cdr = candidateService.insertNew(cdr)
                cdr = candidateService.event(CandidateService.CANDIDATE_EVENT, cdr)
                candidates << cdr
            }
        }
        dr.lastChecked = now
        dr.save(flush: true)
        return candidates
    }
}
