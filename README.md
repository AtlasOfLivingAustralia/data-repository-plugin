#Data Repository Plugin

A plugin for the ala-collectory that allows the collectory to query other repositories of datasets
for things that might be interesting and provide a workflow for checking on the data sources and
integrating them into the collectory

##Dependencies

* https://github.com/AtlasOfLivingAustralia/ala-collectory
* https://github.com/AtlasOfLivingAustralia/collectory-plugin

##Configuration

Configuration settings should be either
``/data/data-repository-plugin/config/ala-collectory-config.properties` (for development) or
`/data/ala-collectory/config/ala-collectory-config.properties` (for production)

Issue management is handled by an interface to an issue tracking system.
The properties for issue management are

| property | description | default |
| -------- | ----------- | ------- |
| issueManagement.type | Class that implements the issue management interface | au.org.ala.collectory.datarepo.issues.GitHub |
| issueManagement.api | The issue management API for making changes to issues |  https://api.github.com |
| issueManagement.pub | The public issue management website for building links to the issues | https://github.com |

## Scanners

Scanners scan specific data repositories.
A DataRepository is created for each data repository instance.

### Null Scanner

A scanner that doesn't do anything.
Useful when setting up and testing a new data repository.

### DAP Scanner

The DAP scanner accesses the CSIRO Data Access Portal API http://data.csiro.au
Connection parameters are

| parameter | required | default | description |
| --------- | -------- | ------- | ----------- |
| api | true | | The URL of the web services API to the DAP |
| query | false | | An optional query part to limit the type of results returned |
| unrestricted | false | true | Only scan for data sets that are unrestricted |

An example set of connection parameters are

    {
      "api": "http://ws.data.csiro.au/collections",
      "query": "sightings",
      "unrestricted": true
    }


