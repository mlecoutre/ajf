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

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface MailSender {

	/**
	 * send an eMail
	 * @param eMail
	 * @throws AddressException
	 * @throws MessagingException
	 * @throws IOException
	 */
	void send(MailBean eMail) throws AddressException, MessagingException,
			IOException;

}
