package au.org.ala.collectory.datarepo.plugin

import au.org.ala.util.ResourceMixin
import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.apache.log4j.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static au.org.ala.collectory.datarepo.plugin.DataRepositorySpec.*

// Get data repository constants
/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DataRepositoryService)
@Mock([CandidateDataResource, DataRepository])
@TestMixin(ControllerUnitTestMixin)
@ContextConfiguration
class DataRepositoryServiceSpec extends Specification {
    static final USER2 = "user2@nowhere.com.xx"
    static final NAME2 = "Test repository 2"

    def repository

    def setup() {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n"))) // So we can see what is going on
        LogManager.rootLogger.level = Level.INFO
        LogManager.getLogger("grails.app.services.au.org.ala.collectory").level = Level.DEBUG
        LogManager.getLogger("au.org.ala.collectory").level = Level.DEBUG
        ConfigurationHolder.config.grails.serverURL = "http://localhost:8080" // Needed for DataRepository public URL
        service.idGeneratorService = new TestIdGenerator()
        repository = new DataRepository(
                uid: UID1,
                guid: GUID1,
                name: NAME1,
                pubDescription: PUBDESC1,
                techDescription: TECDESC1,
                notes: NOTES1,
                websiteUrl: WEBSITE1,
                rights: RIGHTS1,
                citation: CITATION1,
                licenseType: LICENSE_TYPE1,
                licenseVersion: LICENSE_VERSION1,
                userLastModified: USER1,
                lastChecked: CHECKED1,
                connectionParameters: CONNECTION1,
                scannerClass: SCANNER1
        )
    }

    def cleanup() {
    }

    void testRead1() {
        when:
        def json = service.read(repository)
        then:
        json.toString(true).trim() == ResourceMixin.loadResource(this.getClass(), "repository-1.json").trim()
    }

    void testInsert1() {
        setup:
        def json = JSON.parse(ResourceMixin.loadResource(this.getClass(), "repository-1.json"))
        when:
        repository = service.insert(json)
        then:
        repository.uid == "drp1"
        repository.guid == GUID1
        repository.name == NAME1
        repository.pubDescription == PUBDESC1
        repository.techDescription == TECDESC1
        repository.websiteUrl == WEBSITE1
        repository.rights == RIGHTS1
        repository.citation == CITATION1
        repository.licenseType == LICENSE_TYPE1
        repository.licenseVersion == LICENSE_VERSION1
        repository.notes == NOTES1
        repository.scannerClass == SCANNER1
        repository.connectionParameters == DataRepositorySpec.CONNECTION1
        repository.lastChecked == CHECKED1
        repository.dateCreated != null
        repository.lastUpdated != null
        repository.userLastModified == "Data services"
     }

    void testUpdate1() {
        setup:
        repository.save();
        repository = DataRepository.findByUid(UID1)
        def json = JSON.parse(ResourceMixin.loadResource(this.getClass(), "repository-2.json"))
        json.user = USER2
        when:
        service.update(repository, json)
        repository = DataRepository.findByUid(UID1)
        then:
        repository.uid == UID1
        repository.guid == GUID1
        repository.name == NAME2
        repository.pubDescription == PUBDESC1
        repository.techDescription == TECDESC1
        repository.websiteUrl == WEBSITE1
        repository.rights == RIGHTS1
        repository.citation == CITATION1
        repository.licenseType == LICENSE_TYPE1
        repository.licenseVersion == LICENSE_VERSION1
        repository.notes == NOTES1
        repository.connectionParameters == DataRepositorySpec.CONNECTION1
        repository.scannerClass == SCANNER1
        repository.lastChecked == CHECKED1
        repository.dateCreated != null
        repository.lastUpdated != null
        repository.userLastModified == USER2
    }
}
