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
package ajf.mail.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;
import ajf.mail.AttachedFilesException;
import ajf.mail.MailBean;
import ajf.mail.MailSender;

public class DefaultMailSenderImpl implements MailSender {

	private static final Logger logger = LoggerFactory.getLogger();

	private static final String SMTP_HOST_PROPERTY = "applicatifs.appliarmony.net";
	private static final int MAX_FILES_PROPERTY = 5;
	private static final int MAX_FILES_SIZE_PROPERTY = 4;
	private static final String DEFAULT_UPLOAD_TEMP_FILE_DIRECTORY = "./mailSenderAttachmentsTemp/";;

	// private static final String FORMAT = "text/plain;charset=iso-8859-1";

	private static final long MEGA = 1024 * 1024;

	private String jndiName = null;
	
	private String smtpServer = SMTP_HOST_PROPERTY;
	private long connectionTimeout = -1;
	private long timeout = -1;
	
	private Properties properties = null;
	
	private int maxAttachedFiles = MAX_FILES_PROPERTY;
	private int attachedFileMaxSize = MAX_FILES_SIZE_PROPERTY;
	private String uploadTempFileDirectory = DEFAULT_UPLOAD_TEMP_FILE_DIRECTORY;

	public DefaultMailSenderImpl() {
		super();
	}

	/**
	 * 
	 * @param eMail
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws IOException
	 * @throws NamingException 
	 */
	public void send(MailBean eMail) throws AddressException, MessagingException, IOException, NamingException {

		// check required parameters
		if ((null == eMail.getSender()) || eMail.getSender().isEmpty())
			throw new NullPointerException("Sender can not be null");

		if ((null == eMail.getSubject()) || eMail.getSubject().isEmpty())
			throw new NullPointerException("Subject can not be null");

		if ((null == eMail.getTo()) || eMail.getTo().isEmpty())
			throw new NullPointerException("Receiver can not be null");
		
		if ((null == eMail.getBody()) || eMail.getBody().isEmpty())
			throw new NullPointerException("Body can not be null");

		// get the session
		Session session = buildSession();
		
		Transport transport = session.getTransport("smtp");
		transport.connect();
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(eMail.getSender()));

		// set recipients
		
		Address[] toAddressArray = processToAddress(eMail.getTo());
		message.setRecipients(Message.RecipientType.TO, toAddressArray);
		
		if ((null != eMail.getCc()) && !eMail.getCc().isEmpty())
			message.setRecipients(Message.RecipientType.CC, eMail.getCc());
		if ((null != eMail.getBcc()) && !eMail.getBcc().isEmpty())
			message.setRecipients(Message.RecipientType.BCC, eMail.getBcc());

		// set the subject
		message.setSubject(eMail.getSubject());
		
		// set the body
		MimeBodyPart mbp1 = new MimeBodyPart();
		//mbp1.setText(eMail.getBody());
		mbp1.setDataHandler(new DataHandler(new HTMLDataSource(eMail.getBody())));
				
		// add attachments
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);

		// check the number of attachments
		checkNumberOfAttachments(eMail);
		// check the attachments total size
		checkAttachmentsSize(eMail);

		// add the attchments
		File[] tmpFiles = null;
		
		try {
			tmpFiles = addAttachments(eMail, mp);

			// set the mail message
			message.setContent(mp);
			// set the Date
			message.setSentDate(new Date());
			
			// save the message
			message.saveChanges();

			// send the message
			transport.sendMessage(message, toAddressArray);
			
		}
		finally {
			// finally remove the tmp files
			if (tmpFiles != null) {
				for (File tmpFile : tmpFiles) {
					tmpFile.delete();
				}
			}
		}
	}

	private Session buildSession() throws NamingException {
		
		String jndi = getJndiName();
		if ((null != jndi) && (!jndi.trim().isEmpty())) {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			Session session = (Session) envCtx.lookup(jndi);
			return session;
		}
		
		// build the sending
		Properties props = new Properties();
		// complete list of properties :
		// 	 http://javamail.kenai.com/nonav/javadocs/com/sun/mail/smtp/package-summary.html
		props.put("mail.smtp.host", getSmtpServer());
		props.put("mail.smtp.connectiontimeout", getConnectionTimeout());
		props.put("mail.smtp.timeout", getTimeout());
		
		Properties customProperties = getProperties();
		if ((null != customProperties) && (!customProperties.isEmpty())) {
			props.putAll(customProperties);
		}
		
		// obtain the session and the transport 
		Session session = Session.getDefaultInstance(props, null);
		return session;
	}

	/**
	 * 
	 * @param eMail
	 * @return
	 * @throws AddressException
	 */
	private Address[] processToAddress(String to) throws AddressException {
		String[] toArray = to.split(",");
		List<Address> toAddressList = new ArrayList<Address>();
		for (String toOne : toArray) {
			toAddressList.add(new InternetAddress(toOne));
		}
		Address[] toAddressArray = toAddressList.toArray(new Address[0]);
		return toAddressArray;
	}

	/**
	 * 
	 * @param eMail
	 * @param mp
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	private File[] addAttachments(MailBean eMail, Multipart mp)
			throws MessagingException, IOException {
		
		if ((eMail.getAttachedFilesUrl() != null)
				&& (!eMail.getAttachedFilesUrl().isEmpty())) {
			MimeBodyPart mbp2 = null;

			// attached the files
			for (Iterator<String> iter = eMail.getAttachedFilesUrl().iterator(); iter
					.hasNext();) {
				String fichier = iter.next();
				mbp2 = new MimeBodyPart();

				FileDataSource fds = new FileDataSource(fichier);
				mbp2.setDataHandler(new DataHandler(fds));
				mbp2.setFileName(fds.getName());
				mp.addBodyPart(mbp2);
			}

		}

		File tmpFile[] = null;

		if ((eMail.getAttachedFilesByteArrayData() != null)
				&& (!eMail.getAttachedFilesByteArrayData().isEmpty())) {
			MimeBodyPart mbp3 = null;
			String fileName = null;
			tmpFile = new File[eMail.getAttachedFilesByteArrayData().keySet()
					.size()];
			FileOutputStream fos = null;

			File directoryFile = new File(uploadTempFileDirectory);
			directoryFile.mkdirs();

			int numCurrentFile = 0;
			// attached the files
			for (Iterator<String> iter = eMail.getAttachedFilesByteArrayData()
					.keySet().iterator(); iter.hasNext();) {
				fileName = iter.next();
				byte[] fileByteArrayData = eMail.getAttachedFilesByteArrayData().get(
						fileName);
				mbp3 = new MimeBodyPart();

				tmpFile[numCurrentFile] = new File(uploadTempFileDirectory
						+ fileName);
				int i = 1;
				while (tmpFile[numCurrentFile].exists()) {
					tmpFile[numCurrentFile] = new File(uploadTempFileDirectory
							+ fileName + "_" + i);
					i++;
				}

				if (tmpFile[numCurrentFile].createNewFile()
						&& tmpFile[numCurrentFile].canWrite()) {
					try {
						fos = new FileOutputStream(tmpFile[numCurrentFile]);
						fos.write(fileByteArrayData);
						fos.close();
					}
					catch (IOException e) {
						logger.error("Error while writing : "
								+ tmpFile[numCurrentFile].getAbsolutePath()
								+ ".", e);
						throw e;
					}
				}

				mbp3.setDataHandler(new DataHandler(new FileDataSource(
						tmpFile[numCurrentFile])));
				mbp3.setFileName(fileName);
				mp.addBodyPart(mbp3);
				numCurrentFile++;
			}
		}
		
		return tmpFile;
		
	}

	/**
	 * 
	 * @param eMail
	 * @throws AttachedFilesException
	 */
	private void checkAttachmentsSize(MailBean eMail)
			throws AttachedFilesException {
		long maxFileSize = getAttachedFileMaxSize() * MEGA;

		if ((eMail.getAttachedFilesUrl() != null)
				&& (!eMail.getAttachedFilesUrl().isEmpty())) {
			// check the files size
			for (String attachedFileUrl : eMail.getAttachedFilesUrl()) {
				File file = new File(attachedFileUrl);
				if (file.length() > maxFileSize)
					throw new AttachedFilesException("The file " + file.getName()
							+ " exceed the maximum file size of "
							+ getAttachedFileMaxSize() + " Mb.");

			}
		}

		byte[] fileByteArrayData = null;

		if ((eMail.getAttachedFilesByteArrayData() != null)
				&& (!eMail.getAttachedFilesByteArrayData().isEmpty())) {
			// check the files size
			for (Iterator<byte[]> iter = eMail.getAttachedFilesByteArrayData()
					.values().iterator(); iter.hasNext();) {
				fileByteArrayData = iter.next();
				if (fileByteArrayData.length > maxFileSize)
					throw new AttachedFilesException("A file exceed the maximum file size of "
							+ getAttachedFileMaxSize() + " Mb.");
			}
		}
	}

	/**
	 * 
	 * @param eMail
	 * @throws AttachedFilesException
	 */
	private void checkNumberOfAttachments(MailBean eMail)
			throws AttachedFilesException {
		if ((eMail.getAttachedFilesUrl().size() + eMail
				.getAttachedFilesByteArrayData().size()) > getMaxAttachedFiles()) {
			throw new AttachedFilesException(
					"The number of attached files must not exceed "
							+ getMaxAttachedFiles() + ".");
		}
	}
	
	
	
	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return the connectionTimeout
	 */
	public long getConnectionTimeout() {
		return connectionTimeout;
	}

	/**
	 * @param connectionTimeout the connectionTimeout to set
	 */
	public void setConnectionTimeout(long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the jndiName
	 */
	public String getJndiName() {
		return jndiName;
	}

	/**
	 * @param jndiName the jndiName to set
	 */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	/**
	 * @return
	 */
	public String getSmtpServer() {
		return smtpServer;
	}

	/**
	 * @param string
	 */
	public void setSmtpServer(String string) {
		smtpServer = string;
	}

	/**
	 * @return
	 */
	public int getAttachedFileMaxSize() {
		return attachedFileMaxSize;
	}

	/**
	 * @return
	 */
	public int getMaxAttachedFiles() {
		return maxAttachedFiles;
	}

	/**
	 * @param i
	 */
	public void setAttachedFileMaxSize(int i) {
		attachedFileMaxSize = i;
	}

	/**
	 * @param i
	 */
	public void setMaxAttachedFiles(int i) {
		maxAttachedFiles = i;
	}

	/**
	 * @return
	 */
	public String getUploadTempFileDirectory() {
		return uploadTempFileDirectory;
	}

	/**
	 * @param string
	 */
	public void setUploadTempFileDirectory(String string) {
		uploadTempFileDirectory = string;
	}
	
	/*
     * Inner class to act as a JAF datasource to send HTML e-mail content
     */
    static class HTMLDataSource implements DataSource {
        private String html;

        public HTMLDataSource(String htmlString) {
            html = htmlString;
        }

        // Return html string in an InputStream.
        // A new stream must be returned each time.
        public InputStream getInputStream() throws IOException {
            if (html == null) throw new IOException("Null HTML");
            return new ByteArrayInputStream(html.getBytes());
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        public String getContentType() {
            return "text/html";
        }

        public String getName() {
            return "JAF text/html dataSource to send e-mail only";
        }
    }

}
