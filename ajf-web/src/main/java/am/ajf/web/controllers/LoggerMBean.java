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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;

import am.ajf.core.logger.LoggerFactory;
import am.ajf.web.dto.LoggerVO;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

@Named
@RequestScoped
public class LoggerMBean implements Serializable {

	/** UID */
	private static final long serialVersionUID = 1L;

	/** Looger */
	@SuppressWarnings("unused")
	private static transient final Logger logger = LoggerFactory
			.getLogger(LoggerMBean.class);

	private static transient LoggerContext loggerContext = LoggerFactory
			.getDelegate();
	private static transient String[] loggerLevelValues = new String[] {
			Level.ALL.toString(), Level.TRACE.toString(),
			Level.DEBUG.toString(), Level.INFO.toString(),
			Level.WARN.toString(), Level.ERROR.toString(), Level.OFF.toString() };

	private List<LoggerVO> loggersList = null;

	public LoggerMBean() {
		super();
	}

	@PostConstruct
	public void init() {

		/* get the list of setting values */
		populateLoggers();

	}

	@PreDestroy
	public void destroy() {

		if (null != loggersList)
			loggersList.clear();

	}

	private void populateLoggers() {
		if (null == loggersList) {
			loggersList = new ArrayList<LoggerVO>();
		} else {
			loggersList.clear();
		}
		List<ch.qos.logback.classic.Logger> nativeLoggers = loggerContext
				.getLoggerList();
		for (ch.qos.logback.classic.Logger nativeLogger : nativeLoggers) {
			if (null == nativeLogger.getLevel())
				continue;
			LoggerVO vo = new LoggerVO(nativeLogger.getName(), nativeLogger
					.getLevel().toString(), nativeLogger.isAdditive());
			loggersList.add(vo);
		}

	}

	public void editListener(RowEditEvent rev) {
		LoggerVO bean = (LoggerVO) rev.getObject();
		if (null != bean) {
			if ((null != bean.getName())
					&& (bean.getName().trim().length() > 0)) {
				String loggerName = bean.getName();
				String newLoggerLevel = bean.getLevel();
				boolean newAdditiveValue = bean.isAdditive();
				ch.qos.logback.classic.Logger editedLogger = loggerContext
						.getLogger(loggerName);
				if (null != editedLogger) {
					editedLogger.setLevel(Level.toLevel(newLoggerLevel));
					editedLogger.setAdditive(newAdditiveValue);
				}
			}
			// reload properties
			populateLoggers();
		}

	}

	public void refresh() {
		populateLoggers();
	}

	public List<LoggerVO> getLoggers() {
		return loggersList;
	}

	public String[] getLoggerLevelValues() {
		return loggerLevelValues;
	}

}
