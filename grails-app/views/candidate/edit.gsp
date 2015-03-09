<%@ page import="au.org.ala.collectory.datarepo.plugin.CandidateDataResource;au.org.ala.collectory.DataProvider;au.org.ala.collectory.DataResource" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="${grailsApplication.config.ala.skin}"/>
    <g:set var="entityName" value="${message(code: 'candidate.label', default: 'Candidate')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<div class="nav">
    <span class="menuButton"><cl:homeLink/></span>
    <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label"
                                                                           args="[entityName]"/></g:link></span>
    <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label"
                                                                               args="[entityName]"/></g:link></span>
</div>

<div class="body">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${instance}">
        <div class="errors">
            <g:renderErrors bean="${instance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post" url="[action: 'save']">
        <g:hiddenField name="id" value="${instance?.id}"/>
        <g:hiddenField name="version" value="${instance?.version}"/>
        <g:hiddenField name="uid" value="${instance?.uid}"/>
        <g:hiddenField name="lifecycle" value="${instance?.lifecycle}"/>
        <!-- event field is used by submit buttons to pass the web flow event (rather than using the text of the button as the event name) -->
        <g:hiddenField id="event" name="_eventId" value="done"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="guid"><g:message code="providerGroup.guid.label" default="Guid"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'guid', 'errors')}">
                        <g:textField name="guid" maxlength="45" value="${instance?.guid}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name"><g:message code="providerGroup.name.label" default="Name"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'name', 'errors')}">
                        <g:textField name="name" maxlength="128" value="${instance?.name}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="websiteUrl"><g:message code="providerGroup.websiteUrl.label"
                                                           default="Website Url"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'websiteUrl', 'errors')}">
                        <g:textField name="websiteUrl" maxLength="256" value="${instance?.websiteUrl}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="dataProvider.uid"><g:message code="candidate.dataProvider.label"
                                                                 default="Data provider"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'dataProvider', 'errors')}">
                        <g:select name="dataProvider.uid"
                                  from="${DataProvider.list([sort: 'name'])}"
                                  optionKey="uid"
                                  noSelection="${['null': 'Select a data provider']}"
                                  value="${instance.dataProvider?.uid}" class="input-xlarge"/>
                        <cl:helpText code="candidiate.dataProvider"/>
                        <cl:helpTD/>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="dataResource.uid"><g:message code="candidate.dataResource.label"
                                                                 default="Data resource"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'dataProvider', 'errors')}">
                        <g:select name="dataResource.uid"
                                  from="${DataResource.list([sort: 'name'])}"
                                  optionKey="uid"
                                  noSelection="${['null': 'Select a data resource']}"
                                  value="${instance.dataResource?.uid}" class="input-xlarge"/>
                        <cl:helpText code="candidate.dataProvider"/>
                        <cl:helpTD/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="websiteUrl"><g:message code="candidate.issue.label" default="Issue"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'issueId', 'errors')}">
                        <g:textField name="issueId" maxLength="32" value="${instance?.issueId}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="notes"><g:message code="providerGroup.notes.label" default="Notes"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'notes', 'errors')}">
                        <g:textArea name="notes" cols="40" rows="5" value="${instance?.notes}"/>
                    </td>
                <tr class="name"><td colspan="2"><h2><g:message code="providerGroup.show.title.description"
                                                                default="Description"/></h2></td></tr>
                <!-- pub description -->    <tr class="prop">
                    <td valign="top" class="name">
                        <label for="pubDescription"><g:message code="candidate.pubDescription.label"
                                                               default="Public Description"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: command, field: 'pubDescription', 'errors')}">
                        <g:textArea name="pubDescription" cols="40" rows="5" value="${instance?.pubDescription}"/>
                        <cl:helpText code="institution.pubDescription"/>
                    </td>
                    <cl:helpTD/>
                </tr>

                <!-- tech description -->   <tr class="prop">
                    <td valign="top" class="name">
                        <label for="techDescription"><g:message code="candidate.techDescription.label"
                                                                default="Technical Description"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: command, field: 'techDescription', 'errors')}">
                        <g:textArea name="techDescription" cols="40" rows="5" value="${instance?.techDescription}"/>
                        <cl:helpText code="institution.techDescription"/>
                    </td>
                    <cl:helpTD/>
                </tr>
                <tr class="name"><td colspan="2"><h2><g:message code="candidate.location.label"
                                                                default="Location"/></h2></td></tr>
                <!-- address -->            <tr class="prop">
                    <td valign="top" class="name">
                        <g:message code="candidate.address.label" default="Address"/>
                    </td>
                    <td valign="top">
                        <table class="shy">
                            <tr class='prop'>
                                <td valign="top" class="name">
                                    <label for="address.street"><g:message code="candidiate.address.street.label"
                                                                           default="Street"/></label>
                                </td>
                                <td valign="top"
                                    class="value ${hasErrors(bean: instance, field: 'address.street', 'errors')}">
                                    <g:textField name="address.street" maxlength="128"
                                                 value="${instance?.address?.street}"/>
                                </td>
                            </tr>
                            <tr class='prop'>
                                <td valign="top" class="name">
                                    <label for="address.postBox"><g:message code="candidate.address.postBox.label"
                                                                            default="Post box"/></label>
                                </td>
                                <td valign="top"
                                    class="value ${hasErrors(bean: instance, field: 'address.postBox', 'errors')}">
                                    <g:textField name="address.postBox" maxlength="128"
                                                 value="${instance?.address?.postBox}"/>
                                </td>
                            </tr>
                            <tr class='prop'>
                                <td valign="top" class="name">
                                    <label for="address.city"><g:message code="candidate.address.city.label"
                                                                         default="City"/></label>
                                </td>
                                <td valign="top"
                                    class="value ${hasErrors(bean: instance, field: 'address.city', 'errors')}">
                                    <g:textField name="address.city" maxlength="128"
                                                 value="${instance?.address?.city}"/>
                                </td>
                            </tr>
                            <tr class='prop'>
                                <td valign="top" class="name">
                                    <label for="address.state"><g:message code="candidate.address.state.label"
                                                                          default="State or territory"/></label>
                                </td>
                                <td valign="top"
                                    class="value ${hasErrors(bean: instance, field: 'address.state', 'errors')}">
                                    <g:textField name="address.state" maxlength="128"
                                                 value="${instance?.address?.state}"/>
                                </td>
                            </tr>
                            <tr class='prop'>
                                <td valign="top" class="name">
                                    <label for="address.postcode"><g:message code="candidate.address.postcode.label"
                                                                             default="Postcode"/></label>
                                </td>
                                <td valign="top"
                                    class="value ${hasErrors(bean: instance, field: 'address.street', 'errors')}">
                                    <g:textField name="address.postcode" maxlength="128"
                                                 value="${instance?.address?.postcode}"/>
                                </td>
                            </tr>
                            <tr class='prop'>
                                <td valign="top" class="name">
                                    <label for="address.country"><g:message code="candidate.address.country.label"
                                                                            default="Country"/></label>
                                </td>
                                <td valign="top"
                                    class="value ${hasErrors(bean: instance, field: 'address.country', 'errors')}">
                                    <g:textField name="address.country" maxlength="128"
                                                 value="${instance?.address?.country}"/>
                                </td>
                            </tr>
                        </table>

                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="state"><g:message code="candidate.state.label" default="State"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'state', 'errors')}">
                        <g:textField name="state" maxlength="45" value="${instance?.state}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="state"><g:message code="candidate.email.label" default="Email"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'email', 'errors')}">
                        <g:textField name="email" maxlength="45" value="${instance?.email}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="state"><g:message code="candidate.phone.label" default="Phone"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'phone', 'errors')}">
                        <g:textField name="phone" maxlength="45" value="${instance?.phone}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="latitude"><g:message code="candidate.latitude.label" default="Latitude"/>
                            <br/><span class=hint>(decimal degrees)</span>
                        </label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'latitude', 'errors')}">
                        <g:textField id="latitude" name="latitude"
                                     value="${cl.numberIfKnown(number: instance.latitude)}"/>
                        <cl:helpText code="collection.latitude"/>
                    </td>
                    <cl:helpTD/>
                </tr>

                <!-- longitude -->
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="longitude">
                            <g:message code="candidate.longitude.label" default="Longitude"/>
                            <br/><span class=hint>(decimal degrees)</span>
                        </label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'longitude', 'errors')}">
                        <g:textField id="longitude" name="longitude"
                                     value="${cl.numberIfKnown(number: instance.longitude)}"/>
                        <cl:helpText code="collection.longitude"/>
                    </td>
                    <cl:helpTD/>
                </tr>
                <tr class="name"><td colspan="2"><h2><g:message code="candidate.contact.label" default="Contact"/></h2>
                </td></tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="state"><g:message code="candidate.primaryContact.label"
                                                      default="Primary Contact"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'primaryContact', 'errors')}">
                        <g:textField name="primaryContact" maxlength="256" value="${instance?.primaryContact}"/>
                    </td>
                </tr>
                <!-- Connection information -->
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="connectionParameters"><g:message code="candidate.connectionParameters.label"
                                                                     default="Connection Parameters"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: command, field: 'connectionParameters', 'errors')}">
                        <g:textArea name="connectionParameters" cols="40" rows="5"
                                    value="${instance?.techDescription}"/>
                        <cl:helpText code="candidate.connectionParameters"/>
                    </td>
                    <cl:helpTD/>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="lastModified"><g:message code="dataResource.lastModified.label"
                                                             default="Last modified"/></label>
                    </td>
                    <td valign="top" class="value">
                        <g:textField name="lastModified" value="${instance?.lastModified}"/>
                        <cl:helpText code="dataResource.lastModified"/>
                    </td>
                    <cl:helpTD/>
                </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <span class="button"><input type="submit" name="_action_save"
                                        value="${message(code: "candidate.button.save")}" class="save"></span>
            <span class="button"><input type="submit" name="_action_cancel"
                                        value="${message(code: "candidate.button.cancel")}" class="cancel"></span>
        </div>
    </g:form>
</div>
</body>
</html>
