package ${function.package <#-- package where to generate function MBean-->};


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.core.services.exceptions.BusinessLayerException;
import ${function.entity.libPackage}.${function.entity.name};


@Named
@SessionScoped
public class ${function.MbeanName <#-- The name of Managed Bean class -->} implements Serializable {

	/**
	 * The serial version
	 */
	private static final long serialVersionUID = 1L;

	//selection values in table
	private Collection<SelectItem> selectedItems;

	// Logger
	private transient static Logger log = LoggerFactory
			.getLogger(${function.MbeanName}.class);
			
	//TODO this are initial values to be modified
	private ${function.entity.name} newData;
	private List<${function.entity.name}> dataList;

	/**
	 * constructor 
	 */
	public ${function.MbeanName}() {

		super();
	}
	
	
	@PostConstruct
	public void init() {
	
				<#--TODO
				
				${function.entity.name} data1 = new ${function.entity.name}();
				
				<#list function.entity.attributes as att>
					
					data1.set${att}(null);
				</#list>
	
				//Load initial example data list
				dataList = new ArrayList<String>();
				dataList.add(data1);
				-->
				
				
			
	}


<#assign listUT = "list${function.entity.name}"> <#-- generate listEntity UT name -->
<#assign createUT = "create${function.entity.name}"> <#-- generate createEntity UT name -->
<#assign updateUT = "update${function.entity.name}"> <#-- generate updateEntity UT name -->
<#assign deleteUT = "delete${function.entity.name}"> <#-- generate deleteEntity UT name -->

	/**
	 * UT method for listing ${function.entity.name}
	 * 
	 * @param 
	 * @return 
	 */
	public void  ${listUT}() {

		log.debug("Start of ${listUT}... ");

		FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
		
		//try {
			
			//TODO here is your UT ${listUT} business code

		//} catch (BusinessLayerException e) {

			//TODO manage error

		//}
	}
	
   /**
	 * UT method to create ${function.entity.name}
	 * 
	 * @param 
	 * @return 
	 */
	public void ${createUT}() {

		log.debug("Start of ${createUT}... ");

		FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
		
		//try {
			
			//TODO here is your UT ${createUT} business code
			dataList.add(newData);
			

		//} catch (BusinessLayerException e) {

			//TODO manage error

		//}
	}
	
   /**
	 * UT method to update ${function.entity.name}
	 * 
	 * @param 
	 * @return 
	 */
	public void ${updateUT}() {

		log.debug("Start of ${updateUT}... ");

		FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
		
		//try {
			
			//TODO here is your UT ${updateUT} business code

	   //} catch (BusinessLayerException e) {

			//TODO manage error

		//}
	}
	
   /**
	 * UT method to delete ${function.entity.name}
	 * 
	 * @param 
	 * @return 
	 */
	public void ${deleteUT}() {

		log.debug("Start of ${deleteUT}... ");

		FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
		
		//try {
			
			//TODO here is your UT ${deleteUT} business code

		//} catch (BusinessLayerException e) {

			//TODO manage error

		//}
	}
	
	/*
	 * Accessors
	 */
	 public Collection<SelectItem> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(Collection<SelectItem> selectedItems) {
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

