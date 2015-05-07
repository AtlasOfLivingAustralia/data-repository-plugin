<%@ page import="au.org.ala.collectory.datarepo.plugin.CandidateDataResource; au.org.ala.collectory.ProviderGroup" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="${grailsApplication.config.ala.skin}"/>
    <g:set var="entityName" value="${instance.ENTITY_TYPE}"/>
    <g:set var="entityNameLower" value="${drp.controller(type: instance.ENTITY_TYPE)}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<style>
#mapCanvas {
    width: 200px;
    height: 170px;
    float: right;
}
</style>

<div class="nav">

    <p class="pull-right">
        <span class="button"><drp:jsonSummaryLink entity="${entityNameLower}" uid="${instance.uid}"/></span>
        <span class="button"><drp:jsonDataLink entity="${entityNameLower}" uid="${instance.uid}"/></span>
    </p>

    <ul>
        <li><span class="menuButton"><cl:homeLink/></span></li>
        <li><span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                                   args="[entityName]"/></g:link></span>
        </li>
        <li><span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label"
                                                                                       args="[entityName]"/></g:link></span>
        </li>
    </ul>
</div>

<div class="body">
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog emulate-public">
        <!-- base attributes -->
        <div class="show-section well titleBlock">
            <!-- Name --><!-- Acronym -->
            <h1 style="display:inline">${fieldValue(bean: instance, field: "name")}</h1>

            <!-- GUID    -->
            <p><span class="category"><g:message code="dataresource.show.guid"/>:</span> <cl:guid target="_blank"
                                                                                                  guid='${fieldValue(bean: instance, field: "guid")}'/>
            </p>

            <!-- UID    -->
            <p><span class="category"><g:message
                    code="providerGroup.uid.label"/>:</span> ${fieldValue(bean: instance, field: "uid")}</p>

            <!-- Web site -->
            <p><span class="category"><g:message code="dataresource.show.website"/>:</span>
               <cl:valueOrOtherwise value="${instance.websiteUrl}"><a href="${instance.websiteUrl}">${fieldValue(bean: instance, field: "websiteUrl")}</a></cl:valueOrOtherwise>
            </p>

            <!--Lifecycle -->
            <p><span class="category"><g:message
                    code="candidate.show.lifecycle"/>:</span> ${fieldValue(bean: instance, field: "lifecycle")}</p>

            <!-- Issue management -->
            <p><span class="category"><g:message code="candidate.show.issue"/>:</span>
                <cl:valueOrOtherwise value="${instance.issueId}"><a href="${publicIssueUrl}">${fieldValue(bean: instance, field: "issueId")}</a></cl:valueOrOtherwise>
            </p>

            <!-- Provider -->
            <p>
                <span class="category"><g:message code="candidate.show.dataProvider"/></span><br/>
                <cl:valueOrOtherwise value="${instance.dataProvider}"><g:link controller="dataProvider" action="show"
                                                                              params="[id: instance.dataProvider?.uid]">${instance.dataProvider?.name}</g:link></cl:valueOrOtherwise>
            </p>
            <!-- Resource -->
            <p>
                <span class="category"><g:message code="candidate.show.dataResource"/></span><br/>
                <cl:valueOrOtherwise value="${instance.dataResource}"><g:link controller="dataResource" action="show"
                                                                              params="[id: instance.dataResource?.uid]">${instance.dataResource?.name}</g:link></cl:valueOrOtherwise>
            </p>
        <!-- Notes -->
            <g:if test="${instance.notes}">
                <p><cl:formattedText>${fieldValue(bean: instance, field: "notes")}</cl:formattedText></p>
            </g:if>

        <!-- last edit -->
            <p><span class="category"><g:message
                    code="datahub.show.lastchange"/>:</span> ${fieldValue(bean: instance, field: "userLastModified")} on ${fieldValue(bean: instance, field: "lastUpdated")}
            </p>

        </div>

        <!-- description -->
        <div class="show-section well">
            <!-- Pub Desc -->
            <h2><g:message code="collection.show.title.description"/></h2>
            <span class="category"><g:message code="collection.show.span04"/></span><br/>
            <cl:formattedText body="${instance.pubDescription ?: 'Not provided'}"/>

            <!-- Tech Desc -->
            <span class="category"><g:message code="collection.show.span05"/></span><br/>
            <cl:formattedText body="${instance.techDescription ?: 'Not provided'}"/>

        </div>

        <!-- Citation and rights -->
        <div class="show-section well">
            <h2><g:message code="dataresource.show.title04" /></h2>

            <!-- citation -->
            <p><span class="category"><g:message code="dataResource.citation.label" />: </span> ${fieldValue(bean: instance, field: "citation")}</p>

            <!-- rights -->
            <p><span class="category"><g:message code="dataResource.rights.label" />: </span> ${fieldValue(bean: instance, field: "rights")}</p>

            <!-- license -->
            <p><span class="category"><g:message code="dataResource.licenseType.label" />: </span> <cl:displayLicenseType type="${instance.licenseType}"/></p>

            <!-- license version -->
            <p><span class="category"><g:message code="dataResource.licenseVersion.label" />: </span> ${fieldValue(bean: instance, field: "licenseVersion")}</p>
        </div>

        <!-- Connection information -->
        <div class="show-section well">
            <h2><g:message code="dataresource.show.title02"/></h2>
            <cl:formattedText body="${instance.connectionParameters ?: 'Not provided'}"/>
            <p><span class="category"><g:message
                    code="candidate.show.lastModified"/>:</span> ${fieldValue(bean: instance, field: "lastModified")}</p>
        </div>

        <!-- description -->
        <div class="show-section well">
            <h2><g:message code="shared.location.title01"/></h2>
            <table>
                <tr class="prop">
                    <td valign="top" class="name category"><g:message code="address.label" default="Address"/></td>

                    <td valign="top" class="value">
                        <address>
                            ${fieldValue(bean: instance, field: "address.street")}<br/>
                            ${fieldValue(bean: instance, field: "address.city")}<br/>
                            ${fieldValue(bean: instance, field: "address.state")}
                            ${fieldValue(bean: instance, field: "address.postcode")}
                            <g:if test="${fieldValue(bean: instance, field: 'address.country') != 'Australia'}">
                                <br/>${fieldValue(bean: instance, field: "address.country")}
                            </g:if>
                        </address>
                    </td>
                </tr>

                <!-- Postal -->
                <tr class="prop">
                    <td valign="top" class="name category"><g:message code="providerGroup.address.postal.label"
                                                                      default="Postal"/></td>
                    <td valign="top" class="value">${fieldValue(bean: instance, field: "address.postBox")}</td>
                </tr>

                <!-- Latitude -->
                <tr class="prop">
                    <td valign="top" class="name category"><g:message code="providerGroup.latitude.label"
                                                                      default="Latitude"/></td>
                    <td valign="top" class="value"><cl:showDecimal value='${instance.latitude}' degree='true'/></td>
                </tr>

                <!-- Longitude -->
                <tr class="prop">
                    <td valign="top" class="name category"><g:message code="providerGroup.longitude.label"
                                                                      default="Longitude"/></td>
                    <td valign="top" class="value"><cl:showDecimal value='${instance.longitude}' degree='true'/></td>
                </tr>

                <!-- State -->
                <tr class="prop">
                    <td valign="top" class="name category"><g:message code="providerGroup.state.label"
                                                                      default="State"/></td>
                    <td valign="top" class="value">${fieldValue(bean: instance, field: "state")}</td>
                </tr>

                <!-- Email -->
                <tr class="prop">
                    <td valign="top" class="name category"><g:message code="providerGroup.email.label"
                                                                      default="Email"/></td>
                    <td valign="top" class="value">${fieldValue(bean: instance, field: "email")}</td>
                </tr>

                <!-- Phone -->
                <tr class="prop">
                    <td valign="top" class="name category"><g:message code="providerGroup.phone.label"
                                                                      default="Phone"/></td>
                    <td valign="top" class="value">${fieldValue(bean: instance, field: "phone")}</td>
                </tr>
            </table>
        </div>

        <!-- change history -->
        <g:render template="/shared/changes" model="[changes: changes, instance: instance]" plugin="collectory"/>

    </div>

    <div class="buttons">
        <g:form>
            <g:hiddenField name="uid" value="${instance?.uid}"/>
            <cl:ifGranted role="${ProviderGroup.ROLE_EDITOR}">
                <g:each var="event" in="${events}">
                    <span class="button"><g:actionSubmit class="${event.name}" action="${event.name}"
                                                         value="${message(code: event.titleKey, default: event.title)}"/></span>
                </g:each>
                <span class="button"><g:actionSubmit class="edit" action="edit"
                                                     value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
            </cl:ifGranted>
            <cl:ifGranted role="${ProviderGroup.ROLE_ADMIN}">
                <span class="button"><g:actionSubmit class="delete" action="delete"
                                                     value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                                                     onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
            </cl:ifGranted>
            <span class="button"><drp:jsonSummaryLink entity="${entityNameLower}" uid="${instance.uid}"/></span>
            <span class="button"><drp:jsonDataLink entity="${entityNameLower}" uid="${instance.uid}"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
