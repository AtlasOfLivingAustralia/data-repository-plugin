package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.*

class DataRepositoryTagLib {

    def grailsApplication

    static namespace = 'drp'

    def returnLink = { attrs ->
        if (attrs.uid) {
            def pg = CandidateDataResource.findByUid(attrs.uid)
            if (pg) {
                out << link(class: 'return', controller: 'candidate', action: 'show', id: pg.uid) {'Return to ' + pg.name}
            }
        }
    }


    def viewPublicLink = { attrs, body ->
        out << link(class:"preview", controller:"public", action:'show', id:attrs.uid) { "<img class='ala' alt='ala' src='${resource(dir:"images", file:"favicon.gif")}'/> View public page" }
    }

    def jsonSummaryLink = { attrs, body ->
        def uri = "${grailsApplication.config.grails.serverURL}/ws/candidate/summary/${attrs.uid}.json"        // have to use this method rather than 'link' so we can specify the accept format as json
        out << "<a class='json' href='${uri}'><img class='json' alt='summary' src='${resource(dir:"images", file:"json.png")}'/> View summary</a>"
    }

    def jsonDataLink = { attrs, body ->
        def uri = "${grailsApplication.config.grails.serverURL}/ws/candidate/data/${attrs.uid}.json"
        // have to use this method rather than 'link' so we can specify the accept format as json
        out << "<a class='json' href='${uri}'><img class='json' alt='json' src='${resource(dir:"images", file:"json.png")}'/> View raw data</a>"
    }

    def viewLink = {attrs, body ->
        out << link(class:"preview", controller:"public", action:'show', id:attrs.uid) { body() }
    }

    def editLink = {attrs, body ->
        out << link(class:"preview", controller:ProviderGroup.urlFormFromUid(attrs.uid), action:'show', id:attrs.uid) { body() }
    }

}