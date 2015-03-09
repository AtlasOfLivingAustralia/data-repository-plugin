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
                        <cl:helpText code="candidate.dataProvider"/>
                        <cl:helpTD/>
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

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="connectionParameters"><g:message code="candidate.connectionParameters.label"
                                                                     default="ConnectionParameters"/></label>
                    </td>
                    <td valign="top"
                        class="value ${hasErrors(bean: instance, field: 'connectionParameters', 'errors')}">
                        <g:textField name="connectionParameters" maxLength="256"
                                     value="${instance?.connectionParameters}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="scannerClass"><g:message code="candidate.scannerClass.label"
                                                             default="Scanner"/></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: instance, field: 'scannerClass', 'errors')}">
                        <g:select name="scannerClass"
                                  from="${Scanner.list()}"
                                  optionKey="scannerClass"
                                  optionValue="name"
                                  noSelection="${['null': 'Select a scanner']}"
                                  value="${instance.scannerClass}" class="input-xlarge"/>
                        <cl:helpText code="candidate.scannerClass"/>
                        <cl:helpTD/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="lastChecked"><g:message code="dataResource.lastChecked.label"
                                                            default="Last checked"/></label>
                    </td>
                    <td valign="top" class="value">
                        <g:textField name="lastChecked" value="${instance?.lastChecked}"/>
                        <cl:helpText code="dataResource.lastChecked"/>
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
