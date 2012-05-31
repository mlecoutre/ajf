package am.epp.web;

import static ajf.web.WebUtils.getFieldLabel;
import static ajf.web.WebUtils.handleError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.ajf.core.services.exceptions.BusinessLayerException;


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
			.getLogger(${function.MbeanName }.class);

	/**
	 * constructor 
	 */
	public ${function.MbeanName}() {

		super();
	}
	
	
	@PostConstruct
	public void init() {
	
		try {

				
			} catch (BusinessLayerException e) {
				//TODO
			}
	}

	/**
	 * 
	 * 
	 * @param 
	 * @return 
	 */
	public ${function.UTs[0].returnType} ${function.UTs[0].methodName}() {

		log.debug("Start of ${function.UTs[0].methodName}... ");

		FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
		
		try {
			
			//TODO here is your UT ${function.UTs[0..].methodName} business code

		} catch (BusinessLayerException e) {

			//TODO manage error

		}
	}
	
	/**
	 * 
	 * 
	 * @param 
	 * @return 
	 */
	public ${function.UTs[1].returnType} ${function.UTs[1].methodName}() {

		log.debug("Start of ${function.UTs[1].methodName}... ");

		FacesContext message = javax.faces.context.FacesContext
				.getCurrentInstance();
		
		try {
			
			//TODO here is your UT ${function.UTs[1].methodName} business code

		} catch (BusinessLayerException e) {

			//TODO manage error

		}
	}
	

}
