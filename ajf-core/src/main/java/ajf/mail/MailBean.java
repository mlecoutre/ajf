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
package ajf.mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author U002617
 * 
 */
public class MailBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEPARATOR = ",";

	private String sender = null;
	private String to = null;
	private String cc = null;
	private String bcc = null;

	private String subject = null;
	private String body = null;

	private Collection<String> attachedFilesUrl = null;
	private Map<String, byte[]> attachedFilesByteArrayData = null;

	/**
	 * 
	 */
	public MailBean(String newSubject) {
		super();
		attachedFilesUrl = new ArrayList<String>();
		attachedFilesByteArrayData = new HashMap<String, byte[]>();
		setSubject(newSubject);
	}

	/**
	 * @return
	 */
	public String getBcc() {
		return bcc;
	}

	/**
	 * @return
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return
	 */
	public String getCc() {
		return cc;
	}

	/**
	 * @return
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @return
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param string
	 */
	public void setBcc(String string) {
		bcc = string;
	}

	/**
	 * @param string
	 */
	public void setBody(String string) {
		body = string;
	}

	/**
	 * @param string
	 */
	public void setCc(String string) {
		cc = string;
	}

	/**
	 * @param string
	 */
	public void setSender(String string) {
		sender = string;
	}

	/**
	 * @param string
	 */
	public void setSubject(String string) {
		subject = string;
	}

	/**
	 * @param string
	 */
	public void setTo(String string) {
		to = string;
	}

	/**
	 * @return
	 */
	public Map<String, byte[]> getAttachedFilesByteArrayData() {
		return attachedFilesByteArrayData;
	}

	/**
	 * @return
	 */
	public Collection<String> getAttachedFilesUrl() {
		return attachedFilesUrl;
	}

	/**
	 * @param collection
	 */
	public void addAttachedFilesByteArrayData(String fileName,
			byte[] fileByeArray) {
		attachedFilesByteArrayData.put(fileName, fileByeArray);
	}

	/**
	 * @param collection
	 */
	public void addAttachedFilesUrl(String fileUrl) {
		attachedFilesUrl.add(fileUrl);
	}

}
