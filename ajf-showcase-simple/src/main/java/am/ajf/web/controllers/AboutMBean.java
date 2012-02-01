/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package am.ajf.web.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@ManagedBean
//@ApplicationScoped
// public class AboutMBean extends AbstractWebUtils implements Serializable {
public class AboutMBean implements Serializable {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 1L;

	private transient static Logger logger = LoggerFactory
			.getLogger(AboutMBean.class);

	private final static String IMPL_TITLE = "Implementation-Title";
	private final static String IMPL_VENDOR = "Implementation-Vendor";
	private final static String IMPL_VERSION = "Implementation-Version";

	// private List<KeyValueVO> keyValueList = new ArrayList<KeyValueVO>();
	private String implementationTitle, implementationVendor,
			implementationVersion;

	public AboutMBean() {
		super();
		init();
	}

	// @PostConstruct
	public void init() {

		ServletContext ct = (ServletContext) FacesContext.getCurrentInstance()
				.getExternalContext().getContext();

		// Path of the context
		String manifestPath = ct.getRealPath("/");

		// modify the path in order to be usable
		manifestPath = manifestPath.replace("\\", "/");
		manifestPath = manifestPath.replace("//", "/");

		// Add a "/" if it's not
		if (!manifestPath.endsWith("/"))
			manifestPath = manifestPath.concat("/");

		// Use the manifest complet path
		manifestPath = manifestPath.concat("META-INF/MANIFEST.MF");

		InputStream in = null;
		try {
			in = new FileInputStream(manifestPath);
			Manifest manifest = new Manifest(in);

			Attributes attrs = manifest.getMainAttributes();

			implementationTitle = attrs.getValue(IMPL_TITLE);
			implementationVendor = attrs.getValue(IMPL_VENDOR);
			implementationVersion = attrs.getValue(IMPL_VERSION);

		} catch (FileNotFoundException e) {
			logger.error("Unable to find the 'Manifest' file.", e);
		} catch (IOException e) {
			logger.error("Unable to read the 'Manifest' file.", e);
		} finally {
			in = null;
		}

	}

	// @PreDestroy
	// public void terminate() {
	// implementationTitle = null;
	// implementationVendor = null;
	// implementationVersion = null;
	// }

	public String getImplementationTitle() {
		return implementationTitle;
	}

	public void setImplementationTitle(String implementationTitle) {
		this.implementationTitle = implementationTitle;
	}

	public String getImplementationVendor() {
		return implementationVendor;
	}

	public void setImplementationVendor(String implementationVendor) {
		this.implementationVendor = implementationVendor;
	}

	public String getImplementationVersion() {
		return implementationVersion;
	}

	public void setImplementationVersion(String implementationVersion) {
		this.implementationVersion = implementationVersion;
	}

}
