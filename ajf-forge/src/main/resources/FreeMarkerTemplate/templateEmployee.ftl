<?xml version="1.0" encoding="UTF-8"?>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:p="http://primefaces.org/ui"
	template="/ext/templates/brandClassicLayout.xhtml">

	<ui:define name="content">
		<h:form id="form">

			<center>
				<h1>
					<h:outputText value="#{m['${modelname}.management.title']}" />
				</h1>
			</center>


			<p:panel styleClass="col_right">

				<ul>
					<li><h:link outcome="create${modelname}"
							value="#{m['menu.${modelname}.create.title']}" /></li>
				</ul>
			</p:panel>

			<div class="col_left">
				<p:panel id="searchPanel" header="Search" style="width:600px"
					toggleable="true">
					<p:focus context="searchPanel" />
					<h:panelGrid columns="3" style="margin-bottom:5px" cellpadding="2"
						columnClasses="label, column">
						<h:outputText value="${attribute1} " />

						<p:inputText id="searchAttribute1"
							value="#{TODO}">
							<f:ajax execute="searchLastname" event="blur"
								render="searchLastname-error" />
						</p:inputText>
						<h:panelGroup>
							<p:message id="searchLastname-error" for="searchLastname" />
						</h:panelGroup>
						<p:commandButton id="Search" update=":form:iPnl"
							action="#{employeeMBean.listEmployees}"
							value="#{m['button.search']}">
						</p:commandButton>

					</h:panelGrid>
				</p:panel>
				<br />
				<p:panel header="Result" id="iPnl" style="width:600px">
					<p:dataTable var="person" value="#{employeeMBean.employees}"
						paginator="true" rows="5" selectionMode="single"
						selection="#{employeeMBean.selectedEmployee}"
						rowKey="#{person.personid}">
						<p:column headerText="lastname">
							<h:outputText value="#{person.lastname}" />
						</p:column>
						<p:column headerText="firstname">
							<h:outputText value="#{person.firstname}" />
						</p:column>
						<p:column headerText="Sex">
							<h:outputText value="#{person.sex}" />
						</p:column>
						<p:column headerText="Birthday">
							<h:outputText value="#{person.birthday}">
								<f:convertDateTime pattern="dd.MM.yyyy" />
							</h:outputText>
						</p:column>

					</p:dataTable>
					<p:commandButton id="Delete" update=":form:iPnl"
						action="#{employeeMBean.fireEmployee}"
						value="#{m['button.delete']}">
					</p:commandButton>
				</p:panel>
			</div>
		</h:form>
	</ui:define>

</ui:composition>
