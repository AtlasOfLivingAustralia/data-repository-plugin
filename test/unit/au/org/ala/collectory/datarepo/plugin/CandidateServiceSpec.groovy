package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.DataResource
import au.org.ala.collectory.datarepo.issues.TestIssues
import au.org.ala.util.ResourceMixin
import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.PatternLayout
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import spock.lang.Specification

// Get data resource constants
import static au.org.ala.collectory.datarepo.plugin.CandidateDataResourceSpec.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(CandidateService)
@Mock([CandidateDataResource, DataResource])
@TestMixin(ControllerUnitTestMixin)
@ContextConfiguration
class CandidateServiceSpec extends Specification {
    static final USER2 = "user2@nowhere.com.xx"
    static final NAME2 = "Test Resource 2"
    static final ISSUEID1 = "2"

    def issues
    def resource

    def setup() {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n"))) // So we can see what is going on
        LogManager.rootLogger.level = Level.INFO
        LogManager.getLogger("grails.app.services.au.org.ala.collectory").level = Level.DEBUG
        LogManager.getLogger("au.org.ala.collectory").level = Level.DEBUG
        ConfigurationHolder.config.grails.serverURL = "http://localhost:8080" // Needed for DataResource public URL
        issues = new TestIssues()
        service.idGeneratorService = new TestIdGenerator()
        service.issueManagementService = new IssueManagementService(issueManagementSystem: issues)
        service.issueManagementService.transactionManager = Mock(PlatformTransactionManager) { getTransaction(_) >> Mock(TransactionStatus) }
        resource = new CandidateDataResource(
                uid: UID1,
                guid: GUID1,
                lifecycle: LIFECYCLE1,
                issueId: ISSUEID1,
                name: NAME1,
                pubDescription: PUBDESC1,
                techDescription: TECDESC1,
                address: ADDRESS1,
                state: ADDRESS1.state,
                websiteUrl: WEBSITE1,
                primaryContact: CONTACT1,
                notes: NOTES1,
                connectionParameters: CONNECTION1,
                userLastModified: USER1
        )
    }

    def cleanup() {
    }

    void testRead1() {
        when:
        def json = service.read(resource)
        then:
        json.toString(true).trim() == ResourceMixin.loadResource(this.getClass(), "candidate-1.json").trim()
    }

    void testInsert1() {
        setup:
        def json = JSON.parse(ResourceMixin.loadResource(this.getClass(), "candidate-1.json"))
        when:
        resource = service.insert(json)
        then:
        resource.uid == "cdr1"
        resource.guid == GUID1
        resource.lifecycle == LIFECYCLE1
        resource.issueId == ISSUEID1
        resource.name == NAME1
        resource.pubDescription == PUBDESC1
        resource.techDescription == TECDESC1
        resource.address != null
        resource.address.buildAddress() == ADDRESS1.buildAddress()
        resource.state == ADDRESS1.state
        resource.websiteUrl == WEBSITE1
        resource.primaryContact == CONTACT1
        resource.notes == NOTES1
        resource.dateCreated != null
        resource.lastUpdated != null
        resource.userLastModified == "Data services"
        resource.lifecycle == "New"
    }

    void testUpdate1() {
        setup:
        resource.save();
        resource = CandidateDataResource.findByUid(UID1)
        def json = JSON.parse(ResourceMixin.loadResource(this.getClass(), "candidate-2.json"))
        json.user = USER2
        when:
        service.update(resource, json)
        resource = CandidateDataResource.findByUid(UID1)
        then:
        resource.uid == UID1
        resource.guid == GUID1
        resource.lifecycle == LIFECYCLE1
        resource.name == NAME2
        resource.pubDescription == PUBDESC1
        resource.techDescription == TECDESC1
        resource.address != null
        resource.address.buildAddress() == ADDRESS1.buildAddress()
        resource.state == ADDRESS1.state
        resource.websiteUrl == WEBSITE1
        resource.primaryContact == CONTACT1
        resource.notes == NOTES1
        resource.dateCreated != null
        resource.lastUpdated != null
        resource.userLastModified == USER2
        resource.lifecycle == "New"
    }

    void testCandidateEvent1() {
        setup:
        resource.issueId = null
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.CANDIDATE_EVENT, resource)
        then:
        nr.issueId == "issue=1"
        nr.lifecycle == "Candidate"
        issues.comments.size() == 0
        issues.tags.size() == 1
        issues.states[nr.issueId] == "open"
    }

    void testAcceptEvent1() {
        setup:
        resource.lifecycle = "Candidate"
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.ACCEPT_EVENT, resource)
        def dr = nr.dataResource
        then:
        nr.lifecycle == "DataResource"
        dr != null
        dr.name == nr.name
        dr.pubDescription == nr.pubDescription
        dr.guid == nr.guid
        dr.dataProvider == nr.dataProvider
        dr.notes == nr.notes
        dr.address == nr.address
        dr.state == nr.state
        issues.comments.size() == 1
        issues.tags.size() == 0
    }

    void testAcceptEvent2() {
        setup:
        resource.lifecycle = "Candidate"
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.ACCEPT_EVENT, resource)
        nr = service.event(service.CREATED_DATA_RESOURCE_EVENT, resource)
        def dr = nr.dataResource
        then:
        nr.lifecycle == "Accepted"
        dr != null
        dr.name == nr.name
        dr.pubDescription == nr.pubDescription
        dr.guid == nr.guid
        dr.dataProvider == nr.dataProvider
        dr.notes == nr.notes
        dr.address == nr.address
        dr.state == nr.state
        issues.comments.size() == 1
        issues.tags.size() == 1
    }

    void testRejectEvent1() {
        setup:
        resource.lifecycle = "Candidate"
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.REJECT_EVENT, resource)
        def dr = nr.dataResource
        then:
        nr.lifecycle == "Rejected"
        dr == null
        issues.comments.size() == 1
        issues.tags.size() == 1
        issues.states[nr.issueId] == "closed"
    }

    void testLoadEvent1() {
        setup:
        resource.lifecycle = "Accepted"
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.LOAD_EVENT, resource)
        then:
        nr.lifecycle == "Loaded"
        issues.comments.size() == 1
        issues.tags.size() == 1
        issues.states[nr.issueId] == "closed"
    }

    void testLoadEvent2() {
        setup:
        resource.lifecycle = "Reload"
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.LOAD_EVENT, resource)
        then:
        nr.lifecycle == "Loaded"
        issues.comments.size() == 1
        issues.tags.size() == 1
        issues.states[nr.issueId] == "closed"
    }

    void testUpdateEvent1() {
        setup:
        resource.lifecycle = "Loaded"
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.UPDATE_EVENT, resource)
        then:
        nr.lifecycle == "Review"
        issues.comments.size() == 1
        issues.tags.size() == 1
        issues.states[nr.issueId] == "open"
    }

    void testReviewNoupdateEvent1() {
        setup:
        resource.lifecycle = "Review"
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.REVIEWED_NOUPDATE_EVENT, resource)
        then:
        nr.lifecycle == "Loaded"
        issues.comments.size() == 1
        issues.tags.size() == 1
        issues.states[nr.issueId] == "closed"
    }

    void testReviewUpdateEvent1() {
        setup:
        resource.lifecycle = "Review"
        resource.save()
        resource = CandidateDataResource.findByUid(UID1)
        when:
        def nr = service.event(service.REVIEWED_UPDATE_EVENT, resource)
        then:
        nr.lifecycle == "Reload"
        issues.comments.size() == 1
        issues.tags.size() == 1
    }

}
