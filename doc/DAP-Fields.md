#DAP Collection Metadata

| DAP Field | Data Resource Field | Comments |
| --------- | ------------------- | -------- |
| dap:id    | | Access metadata via https://ws.data.csiro.au/collections/{dap:id} |
| dap:self  | guid | Link to resource. Not sure why this should be different to the id |
| dap:landingPage | websiteUrl | Uses rel, type, href |
| dap:title | name | |
| dap:published | | |
| dap:leadResearcher | | |
| dap:description | pubDescription | |
| dap:fieldsOfResearch/dap:fieldOfResearch | | Numeric key; possibly mappable onto focus? Comes from Australian and New Zealand Standard Research Classification thesaurus http://www.arc.gov.au/era/ANZSRC.htm eg. 060301=Animal Systematics and Taxonomy |
| dap:dataStartDate | | |
| dap:dataEndDate | | |
| dap:keywords | | Separated by semi-colons |
| dap:relatedMaterials | | TBD |
| dap:licence | | Text licence |
| dap:link[@rel='licence'] | licenseType, licenseVersion | Licence is a URL, needs to be mapped onto standard ALA terms |
| dap:organisations/dap:organisation | institution | Multiple institutions? |
| dap:attributionStatement | citation | |
| dap:rights | rights | |
| dap:access | | Indicates public/private access |
| dap:size | | large, small, etc. |
| dap:metadata | | Link to different representations of metadata. ANZLIC, CSMD, RIF, DC |
| dap:data | | Link to the actual data itself |
| dap:supportingFiles | | TBD |

#DAP Data

| DAP Field | Data Resource Field | Comments |
| --------- | ------------------- | -------- |
| dap:id    | |  |
| dap:self  | | Link to resource |
| dap:licence | | Text licence |
| dap:link[@rel='licence'] | licenseType, licenseVersion | See above |
| dap:rights | rights | |
| dap:access | | See above |
| dap:files/dap:file/dap:id | | File identifier |
| dap:files/dap:file/dap:filename | | Original file name |
| dap:files/dap:file/dap:lastUpdated | | Date of update |
| dap:files/dap:file/dap:fileSize | | File size in bytes |
| dap:files/dap:file/dap:link[@rel="self"] | | File link, contains a MIME type and an href for downloading |

#DAP Support

Seems to contain a similar structure to DAP Data, no examples of what's in it

#DAP Metadata

The dap:metadata link allows access to various formats for metadata. None seem very useful.

* ANZLIC Spatial Information http://www.anzlic.gov.au/ Uses OGC Geographical Markup Language (GML) and GML metadata (GMD)
* CSMD Core Scientific Metadata https://code.google.com/p/icatproject/wiki/CSMD Contains spatial boundary
* RIF-CS Registry Interchange Format - Collections and Services http://ands.org.au/resource/rif-cs.html Library-style data, contains spatial boundary
* DC Dublin Core http://dublincore.org/