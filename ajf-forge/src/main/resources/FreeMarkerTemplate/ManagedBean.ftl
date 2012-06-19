package ${function.package <#-- package where to generate function MBean-->};

<#assign functionNameCap = "${capitalizeFirst(function.name)}"<#-- function name with upper case first letter-->>
<#assign functionNameUnCap = "${unCapitalizeFirst(function.name)}"<#-- function name with lower case first letter-->>
<#assign entityNameUncap = "${unCapitalizeFirst(function.entity.name)}"<#-- entity name with lower case first letter-->>
<#assign entityNameCap = "${capitalizeFirst(function.entity.name)}"<#-- entity name with upper case first letter-->>



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;

import ${function.libBDPackage}.${functionNameCap}BD;
import ${function.libDTOPackage}.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${function.entity.libPackage}.${function.entity.name};

<#assign entityNameUncap = "${unCapitalizeFirst(function.entity.name)}"<#-- Entity name with lower case first letter-->>

@Named
@SessionScoped
public class ${function.name}MBean implements Serializable {


	/**
	 * The serial version
	 */
	private static final long serialVersionUID = 1L;
	
	// Logger
	private transient static Logger log = LoggerFactory
			.getLogger(${function.name}MBean.class);

	<#-- Generated only for listUT -->
	<#if "${function.listFlag}" = "true">
		//selection values in table
		private ${function.entity.name}[] selectedItems;
		
		//data table value
		private List<${function.entity.name}> ${entityNameUncap}List;
		
		//for search person
		private ${function.entity.name} list${function.entity.name};
		
	</#if>
			
	<#-- Generated only for create UT -->
	<#if "${function.addFlag}" = "true">
	private ${function.entity.name} new${function.entity.name};
	</#if>

	
	@Inject
	private transient ${functionNameCap}BD ${functionNameUnCap}management;

	/**
	 * constructor 
	 */
	public ${function.name}MBean() {
		super();
	}
	
	
	/**
	 *Init Method
	 *
	 */
	@PostConstruct
	public void init() { //Do not remove or rename the init method (mandatory for code generation tool)
	
		log.debug("Initialization");
		
		<#if "${function.addFlag}" = "true">
			new${function.entity.name} = new ${function.entity.name}();
		</#if>
		<#if "${function.listFlag}" = "true">
			${entityNameUncap}List = new ArrayList<${function.entity.name}>();
			list${function.entity.name} = new ${function.entity.name}();
		</#if>
	}
	


<#list function.UTs as ut>

<#-- generate UT managedBean Mehod -->
<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>
<#assign utUnCap = "${unCapitalizeFirst(ut)}"<#-- UT name with lower case first letter-->> 
	/**
	 * UT method for ${utCap}
	 * @throws Exception 
	 */
	public void  ${utUnCap}() throws Exception {

			log.debug("Start of ${utUnCap}... ");
		
			//TODO fill in Param bean
			${utCap}PB ${utUnCap}Pb= new ${utCap}PB();
			
			//Call Policy
			${functionNameUnCap}management.${utUnCap}(${utUnCap}Pb);
	}
	
</#list>
	
 	
	
	<#if "${function.listFlag}" = "true"> 
	public ${function.entity.name}[] getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(${function.entity.name}[] selectedItems) {
		this.selectedItems = selectedItems;
	}
	
	public List<${function.entity.name}> get${function.entity.name}List() {
		return ${entityNameUncap}List;
	}
	public void set${function.entity.name}List(List<${function.entity.name}> ${entityNameUncap}List){
		this.${entityNameUncap}List = ${entityNameUncap}List;
	}
	public ${function.entity.name} getList${function.entity.name}() {
		return list${function.entity.name};
	}

	public void setList${function.entity.name}(${function.entity.name} list${function.entity.name}) {
		this.list${function.entity.name} = list${function.entity.name};
	}
	
	</#if>
	
	<#if "${function.addFlag}" = "true"> 
	public ${function.entity.name} getNew${function.entity.name}() {
		return  new${function.entity.name};
	}
	public void setNew${function.entity.name}(${function.entity.name} new${function.entity.name}) {
		this.new${function.entity.name} = new${function.entity.name};
	}
	</#if>
	

		 
}

