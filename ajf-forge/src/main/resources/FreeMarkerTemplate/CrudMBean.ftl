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
	private ${function.entity.name}[] selectedItems;

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
		log.debug("Initialization");
		newData = new ${function.entity.name}();
		dataList = new ArrayList<${function.entity.name}>();

	}

<#-- generate UT names as variable -->
<#assign listUT = "list${function.entity.name}"> 
<#assign createUT = "create${function.entity.name}">
<#assign updateUT = "update${function.entity.name}">
<#assign deleteUT = "delete${function.entity.name}">

	/**
	 * UT method for listing ${function.entity.name}
	 * 
	 * @param 
	 * @return 
	 */
	public void  ${listUT}() {

		try {
			log.debug("Start of ${listUT}... ");
		
			//Print here an info message on screen
			FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
			message.addMessage(null, new FacesMessage("Listing ${function.entity.name}"));
						
			//TODO here is your UT ${listUT} code
			

		//} catch (BusinessLayerException e) {

			//TODO manage error

		} catch(Exception e){
			log.error("Error Occured in ${listUT}.",e);
		}
	}
	
   /**
	 * UT method to create ${function.entity.name}
	 * 
	 * @param 
	 * @return 
	 */
	public void ${createUT}() {

		try {
			log.debug("Start of ${createUT}... ");
		
			//Print here an info message on screen
			FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
			message.addMessage(null, new FacesMessage("Creating ${function.entity.name}"));
			
			//TODO here is your UT ${createUT} code
			${function.entity.name} new${function.entity.name} = new ${function.entity.name}();
		
		<#list function.entity.attributes as att>
			<#assign uAtt = "${function.capitalizeFirst(att)}"/>
			new${function.entity.name}.set${uAtt}(newData.get${uAtt}());
		</#list>
		
		dataList.add(new${function.entity.name});
		

		//} catch (BusinessLayerException e) {

			//TODO manage error

		} catch(Exception e){
			log.error("Error Occured in ${createUT}.",e);
		}
	}
	
   /**
	 * UT method to update ${function.entity.name}
	 * 
	 * @param 
	 * @return 
	 */
	public void ${updateUT}() {

		try {
			log.debug("Start of ${updateUT}... ");
		
			//Print here an info message on screen
			FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
			message.addMessage(null, new FacesMessage("Updating ${function.entity.name}"));
			
			//TODO here is your UT ${updateUT} code

		//} catch (BusinessLayerException e) {

			//TODO manage error

		} catch(Exception e){
			log.error("Error Occured in ${updateUT}.",e);		
		}
	}
	
   /**
	 * UT method to delete ${function.entity.name}
	 * 
	 * @param 
	 * @return 
	 */
	public void ${deleteUT}() {

		try {
			log.debug("Start of ${deleteUT}... ");
		
			//Print here an info message on screen
			FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
			message.addMessage(null, new FacesMessage("Deleting ${function.entity.name}"));

			//Delete selected elements from List
			int i;
			for (i = 0; i <= selectedItems.length - 1; i++) {
				dataList.remove(selectedItems[i]);
			}
				
			//TODO here is your UT ${deleteUT} code

		//} catch (BusinessLayerException e) {

			//TODO manage error

		} catch(Exception e){
			log.error("Error Occured in ${deleteUT}.",e);		
		}
	}
	
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

