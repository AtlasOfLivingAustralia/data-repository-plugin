package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.ActivityLog
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 *
 * I give up. The behaviour of the test and what's <a href="http://grails.github.io/grails-doc/2.3.11/guide/testing.html#unitTestingControllers">documented</a>
 * seem to bear no relation to each other.
 */
@TestFor(CandidateController)
@Mock([CandidateDataResource, ActivityLog])
class CandidateControllerSpec extends Specification {
    def setup() {
    }

    def cleanup() {
    }

    void testCount() {
        when:
        controller.count()

        then:
        response.text == '{"total":0}'
    }

    /*
    void testList() {
        when:
        views['list'] = 'listed'
        controller.list()

        then:
        response.text == 'listed'
        model.instanceList == []
        model.entityType == 'CandidateDataResource'
        model.instanceTotal == CandidateDataResource.count()

    }
    */
}
