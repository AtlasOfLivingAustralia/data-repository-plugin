<%@ page import="au.org.ala.collectory.datarepo.plugin.CandidateDataResource; au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <g:set var="entityName" value="${instance.ENTITY_TYPE}" />
        <g:set var="entityNameLower" value="${drp.controller(type: instance.ENTITY_TYPE)}"/>
        <title><g:message code="default.show.label" args="[entityName]" /></title>
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
            <li><span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span></li>
            <li><span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span></li>
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
                <p><span class="category"><g:message code="dataresource.show.guid" />:</span> <cl:guid target="_blank" guid='${fieldValue(bean: instance, field: "guid")}'/></p>

                <!-- UID    -->
                <p><span class="category"><g:message code="providerGroup.uid.label" />:</span> ${fieldValue(bean: instance, field: "uid")}</p>

                <!-- Web site -->
                <p><span class="category"><g:message code="dataresource.show.website"/>:</span> <cl:externalLink
                        href="${fieldValue(bean: instance, field: 'websiteUrl')}"/></p>

                <!-- Provider -->
                <p>
                <span class="category"><g:message code="dataRepository.show.dataProvider" /></span><br/>
                <cl:valueOrOtherwise value="${instance.dataProvider}"><g:link controller="dataProvider" action="show" params="[id: instance.dataProvider?.uid]">${instance.dataProvider?.name}</g:link></cl:valueOrOtherwise>
                </p>

                <!-- Notes -->
                <g:if test="${instance.notes}">
                    <p><cl:formattedText>${fieldValue(bean: instance, field: "notes")}</cl:formattedText></p>
                </g:if>

                <!-- last edit -->
                <p><span class="category"><g:message code="datahub.show.lastchange" />:</span> ${fieldValue(bean: instance, field: "userLastModified")} on ${fieldValue(bean: instance, field: "lastUpdated")}</p>

              </div>

              <!-- description -->
              <div class="show-section well">
                <!-- Pub Desc -->
                <h2><g:message code="collection.show.title.description" /></h2>
                <span class="category"><g:message code="collection.show.span04" /></span><br/>
                <cl:formattedText body="${instance.pubDescription?:'Not provided'}"/>

                <!-- Tech Desc -->
                <span class="category"><g:message code="collection.show.span05" /></span><br/>
                <cl:formattedText body="${instance.techDescription?:'Not provided'}"/>

              </div>

              <div class="show-section well">
                  <h2><g:message code="dataRepository.show.title.connect" /></h2>
                  <!-- Connection -->
                  <p><span class="category"><g:message code="dataRepository.show.connectionParameters" />:</span> ${fieldValue(bean: instance, field: "connectionParameters")}</p>
                  <!-- Scanner -->
                  <p><span class="category"><g:message code="dataRepository.show.scannerClass" />:</span> ${fieldValue(bean: instance, field: "scannerClass")}</p>
                  <!-- Last checked on -->
                  <p><span class="category"><g:message code="dataRepository.show.lastChecked" />: </span> ${fieldValue(bean: instance, field: "lastChecked")}</p>

              </div>


                <!-- change history -->
               <g:render template="/shared/changes" model="[changes: changes, instance: instance]" plugin="collectory"/>

            </div>
            <div class="buttons">
              <g:form>
                <g:hiddenField name="uid" value="${instance?.uid}"/>
                  <cl:ifGranted role="${ProviderGroup.ROLE_EDITOR}">
                  <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}"/></span>
                  </cl:ifGranted>
                  <cl:ifGranted role="${ProviderGroup.ROLE_ADMIN}">
                  <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
                  </cl:ifGranted>
                  <span class="button"><drp:jsonSummaryLink entity="${entityNameLower}" uid="${instance.uid}"/></span>
                  <span class="button"><drp:jsonDataLink entity="${entityNameLower}" uid="${instance.uid}"/></span>
              </g:form>
            </div>
        </div>
    </body>
</html>
