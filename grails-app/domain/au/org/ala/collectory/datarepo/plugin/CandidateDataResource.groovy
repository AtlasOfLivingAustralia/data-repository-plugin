package au.org.ala.collectory.datarepo.plugin

import au.org.ala.collectory.Address
import au.org.ala.collectory.DataProvider
import au.org.ala.collectory.DataResource
import au.org.ala.collectory.ProviderGroup
import au.org.ala.util.ISO8601Helper
import org.codehaus.groovy.grails.web.json.JSONObject

import java.sql.Timestamp
import java.text.SimpleDateFormat

class CandidateDataResource implements Serializable {
    static final String ENTITY_TYPE = 'CandidateDataResource'
    static final String ENTITY_PREFIX = 'cdr'

    /** Known GUID if one exists */
    String guid
    /** ALA assigned identifier */
    String uid
    /** The lifecycle state of the candidate resource */
    String lifecycle
    /** The associated data provider */
    DataProvider dataProvider
    /** The associated data resource, if this candidate has been accepted */
    DataResource dataResource;
    /** The resource name */
    String name
    /** The resource public description */
    String pubDescription
    /** The resource technical description */
    String techDescription
    /** The provider address */
    Address address
    /** The resource latitude */
    BigDecimal latitude = ProviderGroup.NO_INFO_AVAILABLE
    /** The expected longitude */
    BigDecimal longitude = ProviderGroup.NO_INFO_AVAILABLE
    /** The state/province the resource comes from */
    String state
    /** The contact email */
    String email
    /** The contact phonr */
    String phone
    /** The website URL */
    String websiteUrl
    /** The connection to the data source */
    String connectionParameters
    /** Primary contact */
    String primaryContact
    /** Additional notes */
    String notes
    /** The issue identifier for tracking */
    String issueId
    /** The date this resource was last modified */
    Timestamp lastModified

    /** Metadata entries */
    Date dateCreated
    Date lastUpdated
    String userLastModified

    static final auditable = [ignore: ['version','dateCreated','lastUpdated','userLastModified']]

    static final embedded = ['address']

    static final constraints = {
        guid(nullable:true, maxSize:100)
        uid(blank:false, maxSize:20)
        lifecycle(blank:false, maxSize:32)
        dataProvider(nullable:true)
        dataResource(nullable:true)
        name(blank:false, maxSize:1024)
        pubDescription(nullable:true)
        techDescription(nullable:true)
        address(nullable:true)
        latitude(max:360.0, min:-360.0, scale:10)
        longitude(max:360.0, min:-360.0, scale:10)
        state(nullable:true, maxSize:45, inList: ProviderGroup.statesList)
        email(nullable:true, maxSize:256)
        phone(nullable:true, maxSize:45)
        websiteUrl(nullable:true, maxSize:256)
        connectionParameters(nullable:true)
        lastModified(nullable: true)
        primaryContact(nullable:true, maxSize: 256)
        notes(nullable:true)
        issueId(nullable:true, maxSize:32)
    }

    // String properties for updating
    static final stringProperties = [
            'guid', 'lifecycle','name','pubDescription','techDescription',
            'state', 'email', 'phone', 'websiteUrl', 'connectionParameters', 'primaryContact',
            'notes', 'issueId'
    ]

    static final numberProperties = [
            'latitude', 'longitude'
    ]

    static final ROLE_EDITOR = "CANDIDATE_EDITOR"

    /**
     * Update from a JSON object
     * @param obj
     * @return
     */
    def update(obj) {
        this.properties[stringProperties] = obj
        this.properties[numberProperties] = obj
        this.address = obj.address ? new Address(obj.address) : null
        this.dataProvider = obj.dataProvider == JSONObject.NULL || obj.dataProvider?.uid == null ? null : DataProvider.findByUid(obj.dataProvider.uid)
        this.dataResource = obj.dataResource == JSONObject.NULL || obj.dataResource?.uid == null ? null : DataProvider.findByUid(obj.dataResource.uid)
        if (obj.lastModified)
            this.lastModified = ISO8601Helper.parseTimestamp(obj.lastModified)
    }

    /**
     * May contain a lat/long
     *
     * @return True
     */
    boolean canBeMapped() {
        return true;
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
}
