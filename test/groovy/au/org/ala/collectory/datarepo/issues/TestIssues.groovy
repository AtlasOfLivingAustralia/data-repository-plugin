package au.org.ala.collectory.datarepo.issues

/**
 * Test issue management system.
 *
 * @author Doug Palmer &lt;Doug.Palmer@csiro.au&gt;

 * Copyright (c) 2015 CSIRO
 */
class TestIssues extends IssueManagementSystem {
    def created = 0
    def comments = [:]
    def tags = [:]
    def states = [:]

    TestIssues() {
        super([:])
    }

    @Override
    String create(String name, String description, List<String> tags) {
        created++
        def id = "issue=" + created
        tag(id, tags)
        reopen(id)
        return id
    }

    @Override
    void comment(String id, String title, String body) {
        def cs = comments[id]

        if (!cs)
            comments[id] = cs = []
        cs << title
    }

    @Override
    void tag(String id, List<String> tag) {
        tags[id] = tag
    }

    @Override
    String publicIssueUrl(String id) {
        return "urn:x-test:" + id
    }

    @Override
    void close(String id) {
        states[id] = "closed"
    }

    @Override
    void reopen(String id) {
        states[id] = "open"
    }
}
