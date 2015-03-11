// configuration for plugin testing - will not be included in the plugin zip
grails.project.groupId = "au.org.ala" // change this to alter the default package name and Maven publishing destination

grails.appName = "${appName}"
grails.resources.resourceLocatorEnabled = true

default_config = "/data/${appName}/config/${appName}-config.properties"
if(!grails.config.locations || !(grails.config.locations instanceof List)) {
    grails.config.locations = []
}
if (new File(default_config).exists()) {
    println "[${appName}] Including default configuration file: " + default_config;
    grails.config.locations.add "file:" + default_config
} else {
    println "[${appName}] No external configuration file defined."
}

println "[${appName}] (*) grails.config.locations = ${grails.config.locations}"
println "default_config = ${default_config}"

if (!issueManagement.type)
    issueManagement.type = "au.org.ala.collectory.datarepo.issues.GitHub"
if (!issueManagement.api)
    issueManagement.api = "https://api.github.com"
if (!issueManagement.pub)
    issueManagement.pub = "https://github.com"

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
    debug  'grails.app.services.au.org.ala.collectory.datarepo',
           'au.org.ala.collectory.state',
           'grails.app.controllers.au.org.ala.collectory.datarepo'
    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping' // URL mapping
    error  'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}
