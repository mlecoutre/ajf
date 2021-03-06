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
package am.ajf.core.mail;

import java.io.IOException;

public class AttachedFilesException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AttachedFilesException() {
		super();
	}

	public AttachedFilesException(String message, Throwable cause) {
		super(message, cause);
	}

	public AttachedFilesException(String message) {
		super(message);
	}

	public AttachedFilesException(Throwable cause) {
		super(cause);
	}
	

}
