class DataRepositoryUrlMappings {

	static mappings = {
        "/ws/candidate/count/$groupBy?" (controller: 'candidate', action: 'count')
        "/ws/candidate/$uid?(.$format)?" (controller:'candidate') {
            action = [HEAD: 'head', GET:'getEntity', PUT:'saveEntity', DELETE:'delete', POST:'saveEntity']
        }
        "/ws/candidate/$uid/summary?(.$format)?" (controller:'candidate') {
            action = [HEAD: 'head', GET:'getEntity']
            constraints {
                summary = 'true'
            }
        }
        "/candidate/$action/$uid" (controller:'candidate')
        "/candidate/$action" (controller:'candidate')
        "/ws/dataRepository/count/$groupBy?" (controller: 'dataRepository', action: 'count')
        "/ws/dataRepository/$uid?(.$format)?" (controller:'dataRepository') {
            action = [HEAD: 'head', GET:'getEntity', PUT:'saveEntity', DELETE:'delete', POST:'saveEntity']
        }
        "/ws/dataRepository/$uid/summary?(.$format)?" (controller:'dataRepository') {
            action = [HEAD: 'head', GET:'getEntity']
            constraints {
                summary = 'true'
            }
        }
        "/ws/dataRepository/$uid/scan(.$format)?" (controller: 'dataRepository', action: 'scan')
        "/dataRepository/$action/$uid" (controller:'dataRepository')
        "/dataRepository/$action" (controller:'dataRepository')
        "500" (view:'/error')
	}
}
