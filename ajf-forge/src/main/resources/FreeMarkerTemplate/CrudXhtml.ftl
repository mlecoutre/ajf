<?xml version="1.0" encoding="UTF-8"?>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:p="http://primefaces.org/ui" 
	template="/ext/templates/brandClassicLayout.xhtml">
	
	<#assign beanName = "${function.MbeanName}">
	<#assign capEntityName = "${capitalizeFirst(function.entity.name)}">
	<#assign addUT = "create${capEntityName}">
	<ui:define name="applicationTitle">${capEntityName} Crud screen</ui:define>

	<ui:define name="content">
	
	<h:form id="form">
		<p:panel id="searchPanelID" header="Create ${capEntityName}" toggleable="true">
				
		<p:panelGrid style="margin-bottom:2px;width:100%;" cellpadding="2">

		<#list function.entity.attributes as att>
		<p:row>
			
			<p:column>
				<h:outputText value="${att}"
					style="text-align:left" />
			</p:column>
			
			<p:column>
				<p:inputText id="${att}"
					value="${setToEl(beanName,"newData",att)}">
					<f:ajax execute="${att}" event="blur" render="${att}-error" />
				</p:inputText>
				<h:panelGroup>
					<p:message id="${att}-error" for="${att}" />
				</h:panelGroup>
			</p:column>
		</p:row>
		</#list>
		<p:row>
			<p:column colspan="4">
				<center>
					<p:commandButton value="Add ${function.entity.name}"
						actionListener="${setToEl(beanName,addUT)}"
						update=":form:iPnl">
					</p:commandButton>
				</center>
			</p:column>
		</p:row>
		</p:panelGrid>
	</p:panel>
		
	
	<p:panel id="iPnl"
			header="List of ${function.entity.name}"
			widgetVar="${function.entity.name}Panel" toggleable="false" collapsed="false" >
				<center>
				<p:dataTable id="infosTbl" var="info"
					value="${setToEl(beanName,"dataList")}" paginator="true"
					rows="10" resizableColumns="true" liveScroll="true"
					scrollWidth="1148" rowKey="${setToEl("info",function.entity.attributes[0])<#-- The first attribute is used as id -->}"
					paginatorTemplate="{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}"
					selection="${setToEl(beanName,"selectedItems")}" style="align:left;">
					
					<f:facet name="header">  
            				RadioButton Based Selection  
     				</f:facet>

					<p:column selectionMode="multiple" style="width:18px" />
							
					<#list function.entity.attributes as att>
					<p:column headerText="${att}">
							${setToEl("info",att)}
					</p:column>
					</#list>
					
					<f:facet name="footer">
						<#assign deleteUT = "delete${capEntityName}">
						<p:commandButton value="Delete"
							actionListener="${setToEl(beanName,deleteUT)}"
							update=":form:iPnl" icon="ui-icon-trash">
						</p:commandButton>
					</f:facet>
					
				</p:dataTable>
				</center>
	</p:panel>
</h:form>
</ui:define>


</ui:composition>