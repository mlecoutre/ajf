<?xml version="1.0" encoding="UTF-8"?>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:p="http://primefaces.org/ui" 
	template="/ext/templates/brandClassicLayout.xhtml">
	
	<#assign beanName = "${function.MbeanName}">
	<#assign capEntityName = "${capitalizeFirst(function.entity.name)}">
	<#assign unCapEntityName = "${unCapitalizeFirst(function.entity.name)}">
	
	<ui:define name="applicationTitle">${capEntityName} Crud screen</ui:define>

	<ui:define name="content">
	
	<h:form id="form">
	
	
	<#--CREATE PANEL-->
	<#if "${function.addFlag}" = "true">
		<#assign addUT = "${function.addUT}">
			<p:panel styleClass="col_right">
			<ul>
				<li><h:link outcome="${addUT}"
					value="Create new ${function.entity.name}" /></li>
				</ul>
			</p:panel>
	</#if>
	
	<#if "${function.listFlag}" = "true">
		<#assign listUT = "${function.listUT}" <#--list UT name-->>
		<#-- SEARCH ENTITY PANEL-->
<div class="col_left">
	
		<p:panel id="searchPanelId" header="Search ${capEntityName}" toggleable="true" style="width:600px">
			
		<h:outputText value="Enter search criteria here :"
					style="text-align:left" />
					
		<p:focus context="searchPanelId" />
		<h:panelGrid style="margin-bottom:2px;width:100%;" cellpadding="2" columns="3" columnClasses="label, column">

		<#list function.entity.attributes as att>
		<#assign listData = "list${capEntityName}">
		
		
				<h:outputText value="${att}"
					style="text-align:left" />
					
				<p:inputText id="list${att}" value="${setToEl(beanName,listData ,att)}">
				<f:ajax execute="list${att}" event="blur" render="list${att}-error" />
				</p:inputText>
				
				<h:panelGroup>
					<p:message id="list${att}-error" for="list${att}" />
				</h:panelGroup>
				
				
		</#list>
			<p:commandButton value="Search ${function.entity.name}"
					actionListener="${setToEl(beanName,listUT)}" update=":form:iPnl">
			</p:commandButton>
		</h:panelGrid>
	</p:panel>

	
	<br/>
	
	<#-- LIST ENTITY PANEL-->
	<p:panel id="iPnl" header="List of ${function.entity.name}"
			widgetVar="${function.entity.name}Panel" toggleable="false" collapsed="false" style="width:600px">

				<center>
				
				<#assign dataList = "${unCapEntityName}List">
			
				<p:dataTable id="infosTbl" var="info"
					value="${setToEl(beanName,dataList)}" paginator="true"
					rows="10" resizableColumns="true" liveScroll="true"
					scrollWidth="1148" rowKey="${setToEl("info",function.entity.attributes[0])<#-- The first attribute is used as id -->}"
					paginatorTemplate="{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}"
					selection="${setToEl(beanName,"selectedItems")}" style="align:left;">
					
					<f:facet name="header">  
            				List of  ${function.entity.name}
     				</f:facet>

					<p:column selectionMode="multiple" style="width:18px" />
							
					<#list function.entity.attributes as att>
					<p:column headerText="${att}">
							${setToEl("info",att)}
					</p:column>
					</#list>
					
					
					<#if  "${function.deleteFlag}" = "true">
						<#assign addUT = "${function.deleteUT}" <#--delete UT name-->>
						<f:facet name="footer">
							<#assign deleteUT = "delete${capEntityName}">
							<p:commandButton value="Delete"
								actionListener="${setToEl(beanName,deleteUT)}"
								update=":form:iPnl" icon="ui-icon-trash">
							</p:commandButton>
						</f:facet>
					</#if>	<#-- End if for delete Flag -->			
				</p:dataTable>
				</center>
		</p:panel>
	</div>
	</#if><#-- End if for list Flag -->
	</h:form>
</ui:define>


</ui:composition>
