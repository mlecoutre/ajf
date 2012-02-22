<?xml version="1.0" encoding="UTF-8"?>
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:p="http://primefaces.prime.com.tr/ui"
	template="WEB-INF/templates/simpleLayout.xhtml">

	<ui:define name="content">

		<h1>Welcome employee N° ${employee}!</h1>
	 
	 <p>Name: ${identity.name}</a>
	 <p>Age: ${identity.age}</a>
	 <p>Sex: ${identity.sex}</a>
	 
	 <p> Address: </p>
	  <p>City: ${identity.address.city}</a>
	  <p>Country: ${identity.address.country}</a>
  
	</ui:define>