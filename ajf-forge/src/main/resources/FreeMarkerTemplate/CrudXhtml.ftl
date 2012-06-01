<?xml version="1.0" encoding="UTF-8"?>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:p="http://primefaces.org/ui" 
	template="/ext/templates/brandClassicLayout.xhtml">
	
	<#assign beanName = "${function.MbeanName}">
	<#assign addUT = "create${function.entity.name}">
	<ui:define name="applicationTitle">${function.entity.name} Crud screen</ui:define>

	<ui:define name="content">
	
	<h:form id="form">
	<p:fieldset id="searchPanelID" legend="Create ${function.entity.name}" toggleable="true">
				
		<p:panelGrid style="margin-bottom:2px;width:100%;" cellpadding="2">

		<#list function.entity.attributes as att>
		<p:row>
			
			<p:column>
				<h:outputText value="${att}"
					style="text-align:left" />
			</p:column>
			
			<p:column>
				<p:inputText
					value="${setToEl(beanName,"newData",att)}"/>
			</p:column>
		</p:row>
		</#list>
		<p:row>
			<p:column colspan="4" style="text-align:right;">
				<center>
					<p:commandButton value="Add"
						actionListener="${setToEl(beanName,addUT)}"
						update=":form:iPnl">
					</p:commandButton>
				</center>
			</p:column>
		</p:row>
		</p:panelGrid>
	</p:fieldset>
		
	
	<p:panel id="iPnl"
			header="List of ${function.entity.name}"
			widgetVar="${function.entity.name}Panel" toggleable="false" collapsed="false" >
				<center>
				<p:dataTable id="infosTbl" var="info"
					value="${setToEl(beanName,"dataList")}" paginator="true"
					rows="10" resizableColumns="true" liveScroll="true"
					scrollWidth="1148"
					paginatorTemplate="{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}"
					selection="${setToEl(beanName,"selectedItems")}" style="align:left;">
			
					<#-- TODO : DataModel must be correct for selection -->
					<p:column selectionMode="multiple" style="width:18px" />

					<#-- <p:column sortBy="${setToEl("info")}"
						filterBy="${setToEl("info")}"> -->
						
					<#list function.entity.attributes as att>
					<p:column>
						<f:facet name="header">
							<h:outputText value="${setToEl("info",att)}" />
						</f:facet> 
					</p:column>
					</#list>
					
					<f:facet name="footer">
						<#assign deleteUT = "delete${function.entity.name}">
						<p:commandButton value="Delete"
							actionListener="${setToEl(beanName,deleteUT)}"
							update=":form:iPnl" image="ui-icon-arrowrefresh-1-e">
						</p:commandButton>
					</f:facet>
					
				</p:dataTable>
				</center>
	</p:panel>
</h:form>
</ui:define>


</ui:composition>