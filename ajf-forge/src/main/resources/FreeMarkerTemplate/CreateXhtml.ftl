<?xml version="1.0" encoding="UTF-8"?>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:p="http://primefaces.org/ui" 
	template="/ext/templates/brandClassicLayout.xhtml">
	
	<#assign beanName = "${function.MbeanName}"/>
	<#assign capEntityName = "${capitalizeFirst(function.entity.name)}"/>
	<#assign unCapEntityName = "${unCapitalizeFirst(function.entity.name)}"/>
	<#assign addUT = "${function.addUT}" <#--add UT name-->/>
	
	<ui:define name="applicationTitle">${capEntityName} Crud screen</ui:define>
	
	<ui:define name="content">
	
	<h:form id="form">
	
	
			<center>
				<H1>
					<h:outputText value="Create new ${capEntityName}"/>
				</H1>
			</center>

			<p:panel styleClass="col_left">

				<ul>
					<li><h:link outcome="list${capEntityName}"
							value="list ${capEntityName}" /></li>
				</ul>
			</p:panel>

			<p:panel header="Create" styleClass="col_right" style="width:600px"
				toggleable="true">
				
				<h:panelGrid columns="3" style="margin-bottom:5px" cellpadding="2"
					columnClasses="label, column">
					
					<#-- Loop on UT list -->
					<#list function.entity.attributes as att>
					<#assign newData = "new${function.entity.name}">
						<h:outputText value="${att}" style="text-align:left" />
						<p:inputText id="${att}"
								value="${setToEl(beanName,newData ,att)}">
							<f:ajax execute="${att}" event="blur" render="${att}-error" />
						</p:inputText>
						<h:panelGroup>
									<p:message id="${att}-error" for="${att}" />
						</h:panelGroup>
					</#list>
					
					<h:panelGroup>
					
					</h:panelGroup>

					<p:commandButton id="Create" value="Create ${function.entity.name}"
								action="${setToEl(beanName,addUT)}">
					</p:commandButton>

				</h:panelGrid>

			</p:panel>

	</h:form>
	</ui:define>


</ui:composition>