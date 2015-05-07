package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.Address
import grails.test.mixin.TestFor
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(CandidateDataResource)
@Mixin(DomainClassUnitTestMixin)
class CandidateDataResourceSpec extends Specification {
    static final UID1 = "cdr1"
    static final GUID1 = "urn:lsid:ala.org.au:cdr:1"
    static final LIFECYCLE1 = "New"
    static final NAME1 = "Test Resource 1"
    static final PUBDESC1 = "Public description 1"
    static final TECDESC1 = "Technical description 1"
    static final ADDRESS1 = new Address(street: "101 Collins St", city: "Melbourne", state: "Victoria", postcode: "3000", country: "Australia")
    static final CONTACT1 = "Mr Fred Nurke, +99 555 555 555, nobody@nowhere.com.xx"
    static final WEBSITE1 = "http://www.nowhere.com.xx"
    static final NOTES1 = "First notes"
    static final CREATED1 = new Date(1000000000)
    static final USER1 = "nobody@nowhere.com.xx"
    static final VERYLONGSTRING = "A".multiply(2048)
    static final CONNECTION1 = '{"endpoint": "http://localhost:8080/nowhere", "uid": 85}'
    static final CITATION1 = "Somewhere special"
    static final RIGHTS1 = "All rights reserved"
    static final LICENSE_TYPE1 = "CC BY"
    static final LICENSE_VERSION1 = "3.0"

    CandidateDataResource resource

    def setup() {
        resource = new CandidateDataResource(
                uid: UID1,
                guid: GUID1,
                lifecycle: LIFECYCLE1,
                name: NAME1,
                pubDescription: PUBDESC1,
                techDescription: TECDESC1,
                address: ADDRESS1,
                connectionParameters: CONNECTION1,
                state: ADDRESS1.state,
                websiteUrl: WEBSITE1,
                rights: RIGHTS1,
                citation: CITATION1,
                licenseType: LICENSE_TYPE1,
                licenseVersion: LICENSE_VERSION1,
                primaryContact: CONTACT1,
                notes: NOTES1,
                userLastModified: USER1
        )
        resource.dateCreated = CREATED1
        resource.lastUpdated = CREATED1
        assertEquals(CREATED1, this.resource.dateCreated)
    }

    def cleanup() {
    }

    void testBuildRelativeUrl1() {
        expect:
        resource.buildRelativeUrl() == "/public/show/" + UID1
    }

    void testSave1() {
        setup:
        def saved = this.resource.save()
        when:
        def restored = CandidateDataResource.findByUid(UID1)
        then:
        restored.uid == UID1
        restored.guid == GUID1
        restored.lifecycle == LIFECYCLE1
        restored.name == NAME1
        restored.pubDescription == PUBDESC1
        restored.techDescription == TECDESC1
        restored.address != null
        restored.address.buildAddress() == ADDRESS1.buildAddress()
        restored.state == ADDRESS1.state
        restored.websiteUrl == WEBSITE1
        restored.rights == RIGHTS1
        restored.citation == CITATION1
        restored.licenseType == LICENSE_TYPE1
        restored.licenseVersion == LICENSE_VERSION1
        restored.primaryContact == CONTACT1
        restored.notes == NOTES1
        System.currentTimeMillis() - restored.dateCreated.time < 5000
        System.currentTimeMillis() - restored.lastUpdated.time < 5000
        restored.userLastModified == USER1
    }

    void testValidate1() {
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

    void testValidate2() {
        setup:
        resource.uid = null
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate3() {
        setup:
        resource.uid = ""
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate4() {
        setup:
        resource.lifecycle = null
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate5() {
        setup:
        resource.lifecycle = VERYLONGSTRING
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate6() {
        setup:
        resource.name = null
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate7() {
        setup:
        resource.name = VERYLONGSTRING
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate8() {
        setup:
        resource.pubDescription = null
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

    void testValidate9() {
        setup:
        resource.pubDescription = VERYLONGSTRING
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

    void testValidate10() {
        setup:
        resource.techDescription = null
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

    void testValidate11() {
        setup:
        resource.techDescription = VERYLONGSTRING
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

    void testValidate12() {
        setup:
        resource.state = null
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

    void testValidate13() {
        setup:
        resource.state = "NotAState"
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate14() {
        setup:
        resource.websiteUrl = null
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

    void testValidate15() {
        setup:
        resource.websiteUrl = VERYLONGSTRING
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate16() {
        setup:
        resource.primaryContact = VERYLONGSTRING
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate17() {
        setup:
        resource.notes = VERYLONGSTRING
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

    void testValidate18() {
        setup:
        resource.licenseType = "NothingToSeeHere"
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == true
    }

    void testValidate19() {
        setup:
        resource.licenseType = null
        when:
        resource.validate()
        then:
        resource.errors.hasErrors() == false
    }

}
