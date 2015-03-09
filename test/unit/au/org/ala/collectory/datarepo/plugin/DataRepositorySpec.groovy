package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.Address
import au.org.ala.collectory.datarepo.sources.NullScanner
import au.org.ala.collectory.datarepo.sources.TestScanner
import grails.test.mixin.TestFor
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

import java.sql.Timestamp

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(DataRepository)
@Mixin(DomainClassUnitTestMixin)
class DataRepositorySpec extends Specification {
    static final UID1 = "dx1"
    static final GUID1 = "urn:lsid:ala.org.au:dr:1"
    static final NAME1 = "Test repository 1"
    static final PUBDESC1 = "Public description 1"
    static final TECDESC1 = "Technical description 1"
    static final CONNECTION1 = '{"uri":"nowhere@at.all"}'
    static final WEBSITE1 = "http://www.nowhere.com.xx"
    static final NOTES1 = "First notes"
    static final CREATED1 = new Date(1000000000)
    static final CHECKED1 = new Timestamp(300000000)
    static final USER1 = "nobody@nowhere.com.xx"
    static final VERYLONGSTRING = "A".multiply(2048)
    static final SCANNER1 = NullScanner.class

    DataRepository repository

    def setup() {
        repository = new DataRepository(
                uid: UID1,
                guid: GUID1,
                name: NAME1,
                pubDescription: PUBDESC1,
                techDescription: TECDESC1,
                notes: NOTES1,
                websiteUrl: WEBSITE1,
                userLastModified: USER1,
                lastChecked: CHECKED1,
                connectionParameters: CONNECTION1,
                scannerClass: SCANNER1
        )
        repository.dateCreated = CREATED1
        repository.lastUpdated = CREATED1
        assertEquals(CREATED1, this.repository.dateCreated)
    }

    def cleanup() {
    }

    void testBuildRelativeUrl1() {
        expect:
        repository.buildRelativeUrl() == "/public/show/" + UID1
    }

    void testSave1() {
        setup:
        def saved = this.repository.save()
        when:
        def restored = DataRepository.findByUid(UID1)
        then:
        restored.uid == UID1
        restored.guid == GUID1
        restored.name == NAME1
        restored.pubDescription == PUBDESC1
        restored.techDescription == TECDESC1
        restored.websiteUrl == WEBSITE1
        restored.lastChecked == CHECKED1
        restored.notes == NOTES1
        restored.connectionParameters == CONNECTION1
        restored.scannerClass == SCANNER1
        System.currentTimeMillis() - restored.dateCreated.time < 5000
        System.currentTimeMillis() - restored.lastUpdated.time < 5000
        restored.userLastModified == USER1
    }

    void testCreateScanner1() {
        when:
        def scanner = this.repository.createScanner()
        then:
        scanner.class == SCANNER1
        scanner.repository != null
        scanner.repository.guid == repository.guid
    }

    void testValidate1() {
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == false
    }

    void testValidate2() {
        setup:
        repository.uid = null
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == true
    }

    void testValidate3() {
        setup:
        repository.uid = ""
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == true
    }

    void testValidate4() {
        setup:
        repository.name = null
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == true
    }

    void testValidate5() {
        setup:
        repository.name = VERYLONGSTRING
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == true
    }

    void testValidate6() {
        setup:
        repository.pubDescription = null
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == false
    }

    void testValidate7() {
        setup:
        repository.pubDescription = VERYLONGSTRING
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == false
    }

    void testValidate8() {
        setup:
        repository.techDescription = null
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == false
    }

    void testValidate9() {
        setup:
        repository.techDescription = VERYLONGSTRING
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == false
    }

    void testValidate10() {
        setup:
        repository.websiteUrl = null
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == false
    }

    void testValidate11() {
        setup:
        repository.websiteUrl = VERYLONGSTRING
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == true
    }

    void testValidate12() {
        setup:
        repository.notes = VERYLONGSTRING
        when:
        repository.validate()
        then:
        repository.errors.hasErrors() == false
    }
}
