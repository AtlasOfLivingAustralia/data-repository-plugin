package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.DataProvider
import au.org.ala.collectory.DataResource
import au.org.ala.collectory.datarepo.sources.Scanner
import au.org.ala.util.ISO8601Helper
import org.codehaus.groovy.grails.web.json.JSONObject

import java.sql.Timestamp

class DataRepository implements Serializable {
    static final String ENTITY_TYPE = 'DataRepository'
    static final String ENTITY_PREFIX = 'drp'

    /** Known GUID if one exists */
    String guid
    /** ALA assigned identifier */
    String uid
    /** The associated data provider, if one exists */
    DataProvider dataProvider
    /** The repository name */
    String name
    /** Notes about the repository */
    String notes
    /** The repository public description */
    String pubDescription
    /** The repository public description */
    String techDescription
    /** The website URL */
    String websiteUrl
    /** Default rights information about the repository */
    String rights
    /** The default citation citation */
    String citation
    /** The default license type */
    String licenseType
    /** The default license version */
    String licenseVersion
    /** The repository connection string */
    String connectionParameters
    /** The scanner class */
    Class<Scanner> scannerClass
    /** The date last checked/scanned */
    Timestamp lastChecked

    /** Metadata entries */
    Date dateCreated
    Date lastUpdated
    String userLastModified

    static final auditable = [ignore: ['version','dateCreated','lastUpdated','userLastModified']]

    static mapping = {
        sort: 'name'
        connectionParameters type: 'text'
        pubDescription type: "text"
        techDescription type: "text"
        notes type: "text"
        rights type: "text"
        citation type: "text"
    }

    static final constraints = {
        guid(nullable:true, maxSize:100)
        uid(blank:false, maxSize:20)
        dataProvider(nullable:true)
        name(blank:false, maxSize:1024)
        notes(nullable:true)
        pubDescription(nullable:true)
        techDescription(nullable:true)
        websiteUrl(nullable:true, maxSize:256)
        rights(nullable:true)
        citation(nullable:true)
        licenseType(nullable:true, maxSize: 45, inList: DataResource.licenseTypeList)
        licenseVersion(nullable:true, maxSize: 45)
        connectionParameters(nullable:true)
        scannerClass(nullable: false)
        lastChecked(nullable:true)
    }

    // String properties for updating
    static final stringProperties = [
            'guid', 'lifecycle','name','pubDescription', 'techDescription', 'notes', 'websiteUrl',
            'rights', 'citation', 'licenseType', 'licenseVersion'
    ]

    static final ROLE_EDITOR = "CANDIDATE_EDITOR"

    /**
     * Update from a JSON object
     * @param obj
     * @return
     */
    def update(obj) {
        this.properties[stringProperties] = obj
        if (obj.scannerClass == null || obj.scannerClass == JSONObject.NULL)
            this.scannerClass = null
        else {
            def sc = obj.scannerClass
            if (sc.startsWith("class "))
                sc = sc.substring(6);
            this.scannerClass = Class.forName(sc)
        }
        this.dataProvider = obj.dataProvider == JSONObject.NULL || obj.dataProvider?.uid == null ? null : DataProvider.findByUid(obj.dataProvider.uid)
        this.connectionParameters = obj.connectionParameters?.toString()
        if (obj.lastChecked)
            this.lastChecked = ISO8601Helper.parseTimestamp(obj.lastChecked)
     }

    /**
     * May contain a lat/long
     *
     * @return False
     */
    boolean canBeMapped() {
        return false;
    }

    /**
     * Returns the relative url to the public representation of this entity in the collectory.
     * <p>
     * Services need to add the appropriate server URL
     *
     * @return The relative URL
     */
    String buildRelativeUrl() {
        return "/public/show/" + uid
    }

    /**
     * Returns descriptive text trimmed to the lesser of the first newline and the specified length.
     *
     * @return A null abstract, since this semi-temporary
     */
    String makeAbstract(int length) {
        return ""
    }

    Map summary() {
        return [
           name: name,
           uid: uid,
           uri: buildRelativeUrl()
        ]
    }

    /**
     * Create a scanner for this repository.
     * <p>
     * This is based on the {@link #scannerClass}, initialised with the repository
     * configuration.
     *
     * @return A new scanner
     */
    Scanner createScanner() {
        return scannerClass.newInstance(this)
    }
}
