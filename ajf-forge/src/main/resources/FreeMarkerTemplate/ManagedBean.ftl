package ${function.package <#-- package where to generate function MBean-->};

<#assign functionNameCap = "${capitalizeFirst(function.name)}"<#-- function name with upper case first letter-->>
<#assign functionNameUnCap = "${unCapitalizeFirst(function.name)}"<#-- function name with lower case first letter-->>


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

import am.ajf.core.services.exceptions.BusinessLayerException;
import ${function.entity.libPackage}.${function.entity.name};


@Named
@SessionScoped
public class ${function.name}MBean implements Serializable {


	/**
	 * The serial version
	 */
	private static final long serialVersionUID = 1L;

	//selection values in table
	private ${function.entity.name}[] selectedItems;

	// Logger
	private transient static Logger log = LoggerFactory
			.getLogger(${function.name}MBean.class);
			
	//TODO this are initial values to be modified
	private ${function.entity.name} newData;
	private List<${function.entity.name}> dataList;
	
	@Inject
	private ${functionNameCap}BD ${functionNameUnCap}management;

	/**
	 * constructor 
	 */
	public ${function.name}MBean() {
		super();
	}
	
	
	@PostConstruct
	public void init() {
		log.debug("Initialization");
		newData = new ${function.entity.name}();
		dataList = new ArrayList<${function.entity.name}>();

	}

<#list function.UTs as ut>

<#-- generate UT managedBean Mehod -->
<#assign utCap = "${capitalizeFirst(ut)}"<#-- UT name with upper case first letter-->>
<#assign utUnCap = "${unCapitalizeFirst(ut)}"<#-- UT name with lower case first letter-->> 
	/**
	 * UT method for listing ${utCap}
	 * 
	 */
	public void  ${utUnCap}() {

		try {
			log.debug("Start of ${utUnCap}... ");
		
			//TODO fill in Param bean
			${utCap}PB ${utUnCap}Pb= new ${utCap}PB();
			
			//Call Policy
			${functionNameUnCap}management.${utUnCap}(${utUnCap}Pb);
						

		//} catch (BusinessLayerException e) {

			//TODO manage error

		} catch(Exception e){
			log.error("Error Occured in ${utUnCap}.",e);
		}
	}
	
</#list>
	
 	
	/*
	 * Accessors
	 */
	 public ${function.entity.name}[] getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(${function.entity.name}[] selectedItems) {
		this.selectedItems = selectedItems;
	}
	
	public ${function.entity.name} getNewData() {
		return  newData;
	}
	public void setNewData(${function.entity.name} newData) {
		this.newData = newData;
	}
	public List<${function.entity.name}> getDataList() {
		return dataList;
	}
	public void setDataList(List<${function.entity.name}> dataList){
		this.dataList = dataList;
	}
		 
}

