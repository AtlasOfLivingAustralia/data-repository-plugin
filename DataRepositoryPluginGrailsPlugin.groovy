import grails.util.Environment

class DataRepositoryPluginGrailsPlugin {
    // the plugin version
    def version = "0.1-SNAPSHOT"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.3 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Data Repository Portal Plugin" // Headline display name of the plugin
    def author = "Doug Palmer"
    def authorEmail = "Doug.Palmer@csiro.au"
    def description = '''\
A plugin that can scan and access a variety of data repositories that are used
to

Designed for the CSIRO Data Access Portal (DAP).
The DAP allows scientists to deposit the datasets used for projects and publications.
This plugin allows the collectory to scan the DAP, looking for new resources that may have appeared.
Since DAP resources do not come in a standard form, the plugin places information about the resources in
a holding table. It also creates github issues that can be used for management, so that
'''

    // URL to the plugin's documentation
    def documentation = "http://github.com/AtlasOfLivingAustralia/data-repository-plugin"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "MPL2"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Atlas of Living Australia", url: "http://www.ala.org.au/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [ system: "github", url: "https://github.com/AtlasOfLivingAustralia/data-repository-plugin/issues" ]

    // Online location of the plugin's browseable source code.
    def scm = [ url: "https://github.com/AtlasOfLivingAustralia/data-repository-plugin" ]

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
        def config = application.config

        // EhCache settings
        if (!config.grails.cache.config) {
            config.grails.cache.config = {
                defaults {1
                    eternal false
                    overflowToDisk false
                    maxElementsInMemory 10000
                    timeToLiveSeconds 3600
                }
                cache {
                    name 'collectoryCache'
                    timeToLiveSeconds (3600 * 4)
                }
                cache {
                    name 'longTermCache'
                    timeToLiveSeconds (3600 * 12)
                }
            }
        }

        // Apache proxyPass & cached-resources seems to mangle image URLs in plugins, so we exclude caching it
        application.config.grails.resources.mappers.hashandcache.excludes = ["**/images/*.*"]

        def loadConfig = new ConfigSlurper(Environment.current.name).parse(application.classLoader.loadClass("defaultConfig"))
        application.config = loadConfig.merge(config) // client app will now override the defaultConfig version
    }

    def doWithDynamicMethods = { ctx ->
    }

    def doWithApplicationContext = { ctx ->
     }

    def onChange = { event ->
     }

    def onConfigChange = { event ->
    }

    def onShutdown = { event ->
    }
}
