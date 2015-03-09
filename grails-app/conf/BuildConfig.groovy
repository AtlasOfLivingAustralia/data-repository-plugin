grails.servlet.version = "2.5"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        dependencies {
            runtime 'mysql:mysql-connector-java:5.1.5'
        }
    }

    plugins {
        plugins {
            build(  ":tomcat:7.0.52.1",
                    ":release:3.0.1",
                    ":rest-client-builder:1.0.3") {
                export = false
            }
            compile ":cache:1.1.2"
            compile ":collectory:1.0-SNAPSHOT"
            runtime ":hibernate:3.6.10.11"
            runtime ":jquery:1.8.3"
            runtime ":resources:1.2.14"
            runtime ":audit-logging:0.5.5.3"
            runtime ":cache-headers:1.1.6"
            runtime ":rest:0.8"
            runtime ":richui:0.8"
            runtime ":tiny-mce:3.4.4"
        }
    }
}
