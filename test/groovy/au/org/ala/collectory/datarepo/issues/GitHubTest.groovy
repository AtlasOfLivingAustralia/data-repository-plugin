package au.org.ala.collectory.datarepo.issues

import au.org.ala.util.ResourceMixin
import groovyx.net.http.HttpResponseDecorator
import org.apache.http.ProtocolVersion
import org.apache.http.message.BasicHttpRequest
import org.apache.http.message.BasicHttpResponse
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.PatternLayout

/**
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
@Mixin(ResourceMixin)
class GitHubTest extends GroovyTestCase {
    static final TITLE1 = "A test issue"
    static final DESCRIPTION1 = "A description of a test issue"
    static final ISSUE_NUMBER1 = "1000"
    static final VERSION = new ProtocolVersion("HTTP", 1, 1)
    static final STATUS_MAP = [200:"OK", 404:"Not Found"]

    def response // The expected response
    def testBuilder // Mock HTTPBuilder
    GitHub issues


    def buildResponse(code, body = null) {
        return new HttpResponseDecorator(new BasicHttpResponse(VERSION, code, STATUS_MAP[code]), body)
    }

    // What the hell going on here?
    // We want to test the github interface without invoking github itself
    // So we replace the builder method with a testBuilder that is a mock http builder that just implements the request method
    // This calls the closure in response, set on a per-method basis
    void setUp() {
        def config = [issueManagement: [api: 'https://api.github.com', pub: 'https://github.com', repository: "/xxxx/test-issues", user: 'xxxx', password: 'yyyy']]

        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d{ABSOLUTE} %-5p [%c{1}] %m%n")))
        LogManager.rootLogger.level = Level.INFO
        testBuilder = [
            request: { method, closure ->
                closure.delegate = testBuilder
                closure(new BasicHttpRequest(method.toString(), "/"))
                response(closure.body)
            },
            body: [:],
            response: [:]
        ]
        issues = new GitHub(config)
        issues.metaClass.builder = { path -> testBuilder }
    }

    void testPublicIssueUrl1() {
        assertEquals(null, issues.publicIssueUrl(null))
    }

    void testPublicIssueUrl2() {
        assertEquals("https://github.com/xxxx/test-issues/issues/456", issues.publicIssueUrl("456"))
    }

    void testCreate1() {
        def result

        response = { body ->
            def data = [number: ISSUE_NUMBER1]
            testBuilder.response.success(buildResponse(200, data), data)
        }
        result = issues.create(TITLE1, DESCRIPTION1, [])
        assertEquals(ISSUE_NUMBER1, result)
    }

    void testCreate2() {
        def result

        response = { body ->
            testBuilder.response.failure(buildResponse(404))
        }
        result = issues.create(TITLE1, DESCRIPTION1, [])
        assertNull(result)
    }

    void testComment1() {
        def result

        response = { body ->
            result = body.body
            def data = []
            testBuilder.response.success(buildResponse(200, data), data)
        }
        issues.comment(ISSUE_NUMBER1, TITLE1, DESCRIPTION1)
        assertEquals(TITLE1 + "\n" + DESCRIPTION1, result)
    }

    void testTag1() {
        def result

        response = { body ->
            result = body
            def data = []
            testBuilder.response.success(buildResponse(200, data), data)
        }
        issues.tag(ISSUE_NUMBER1, ["tags"])
        assertEquals(["tags"], result)
    }

    void testClose1() {
        def result

        response = { body ->
            result = body.state
            def data = []
            testBuilder.response.success(buildResponse(200, data), data)
        }
        issues.close(ISSUE_NUMBER1)
        assertEquals("closed", result)
    }

    void testReopen1() {
        def result

        response = { body ->
            result = body.state
            def data = []
            testBuilder.response.success(buildResponse(200, data), data)
        }
        issues.reopen(ISSUE_NUMBER1)
        assertEquals("open", result)
    }

}
