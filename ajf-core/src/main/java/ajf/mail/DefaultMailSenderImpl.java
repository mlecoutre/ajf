package ajf.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
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

import org.slf4j.Logger;

import ajf.logger.LoggerFactory;

public class DefaultMailSenderImpl implements MailSender {

	private static final Logger logger = LoggerFactory.getLogger();

	private static final String SMTP_HOST_PROPERTY = "applicatifs.appliarmony.net";
	private static final int MAX_FILES_PROPERTY = 5;
	private static final int MAX_FILES_SIZE_PROPERTY = 4;
	private static final String DEFAULT_UPLOAD_TEMP_FILE_DIRECTORY = "./mailSenderAttachmentsTemp/";;

	// private static final String FORMAT = "text/plain;charset=iso-8859-1";

	private static final long MEGA = 1024 * 1024;

	private String smtpServer = SMTP_HOST_PROPERTY;
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
	 */
	public void send(MailBean eMail) throws AddressException, MessagingException, IOException {

		// check required parameters
		if ((null == eMail.getSender()) || eMail.getSender().isEmpty())
			throw new NullPointerException("Sender can not be null");

		if ((null == eMail.getSubject()) || eMail.getSubject().isEmpty())
			throw new NullPointerException("Subject can not be null");

		if ((null == eMail.getBody()) || eMail.getBody().isEmpty())
			throw new NullPointerException("Body can not be null");

		// build the sending
		Properties props = new Properties();
		props.put("mail.host", getSmtpServer());

		// obtain the session and the transport 
		Session session = Session.getDefaultInstance(props, null);
		Transport transport = session.getTransport("smtp");
		transport.connect();

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(eMail.getSender()));

		// set recipients
		/*
		if (!eMail.getTo().isEmpty())
			msg.setRecipients(Message.RecipientType.TO, eMail.getTo());
		*/
		if (!eMail.getCc().isEmpty())
			message.setRecipients(Message.RecipientType.CC, eMail.getCc());
		if (!eMail.getBcc().isEmpty())
			message.setRecipients(Message.RecipientType.BCC, eMail.getBcc());

		// set the subject
		message.setSubject(eMail.getSubject());
		
		// set the body
		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setText(eMail.getBody());
		
		// add attachments
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);

		// check the number of attachments
		if ((eMail.getAttachedFilesUrl().size() + eMail
				.getAttachedFilesByteArrayData().size()) > getMaxAttachedFiles()) {
			throw new AttachedFilesException(
					"The number of attached files must not exceed "
							+ getMaxAttachedFiles() + ".");
		}

		long maxFileSize = getAttachedFileMaxSize() * MEGA;

		String fichier = null;

		if ((eMail.getAttachedFilesUrl() != null)
				&& (!eMail.getAttachedFilesUrl().isEmpty())) {
			// check the files size
			for (String attachedFileUrl : eMail.getAttachedFilesUrl()) {
				File file = new File(attachedFileUrl);
				if (file.length() > maxFileSize)
					throw new AttachedFilesException("The file " + fichier
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
					throw new AttachedFilesException("The file " + fichier
							+ " exceed the maximum file size of "
							+ getAttachedFileMaxSize() + " Mb.");
			}
		}

		if ((eMail.getAttachedFilesUrl() != null)
				&& (!eMail.getAttachedFilesUrl().isEmpty())) {
			MimeBodyPart mbp2 = null;

			// attached the files
			for (Iterator<String> iter = eMail.getAttachedFilesUrl().iterator(); iter
					.hasNext();) {
				fichier = iter.next();
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
				fileByteArrayData = eMail.getAttachedFilesByteArrayData().get(
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
						logger.error(" Error while writing : "
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

		// set the mail message
		message.setContent(mp);
		// set the Date
		message.setSentDate(new Date());
		
		// save the message
		message.saveChanges();
		// send the message
		String[] toArray = eMail.getTo().split(",");
		List<Address> toAddressList = new ArrayList<Address>();
		for (String to : toArray) {
			toAddressList.add(new InternetAddress(to));
		}
		transport.sendMessage(message, toAddressList.toArray(new Address[0]));

		if (tmpFile != null) for (int i = 0; i < tmpFile.length; i++) {
			tmpFile[i].delete();
		}
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

}
