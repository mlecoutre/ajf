package am.ajf.showcase.web.controllers;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import static am.ajf.web.WebUtils.*;

@ManagedBean
public class ShowcaseMBean {

	@NotNull(message = "Should not be null")
	@Size(min = 3, max = 10)
	private String parameter;

	public void doSthg() throws Throwable {
		Thread.sleep(3000);
	}

	public void createError() {
		FacesMessage facesMessage = new FacesMessage(
				FacesMessage.SEVERITY_ERROR,
				getFieldLabel("application.error.occured"),
				"mon message d'erreur");
		FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}

	public void createWarning() {
		FacesMessage facesMessage = new FacesMessage(
				FacesMessage.SEVERITY_WARN,
				getFieldLabel("application.error.occured"),
				"mon message de warning");
		FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}
	
	public void createInfo() {
		FacesMessage facesMessage = new FacesMessage(
				FacesMessage.SEVERITY_INFO,
				getFieldLabel("application.error.occured"),
				"mon message d'information");
		FacesContext.getCurrentInstance().addMessage(null, facesMessage);
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

}
