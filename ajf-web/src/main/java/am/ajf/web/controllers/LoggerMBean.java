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

import am.ajf.core.logger.LoggerFactory;
import am.ajf.web.dto.LoggerVO;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;

/**
 * LoggerMBean
 * 
 * @author U002617
 * 
 */
@Named
@RequestScoped
public class LoggerMBean implements Serializable {

	/** UID */
	private static final long serialVersionUID = 1L;

	private static transient LoggerContext loggerContext = LoggerFactory
			.getDelegate();
	private static transient String[] loggerLevelValues = new String[] {
			Level.ALL.toString(), Level.TRACE.toString(),
			Level.DEBUG.toString(), Level.INFO.toString(),
			Level.WARN.toString(), Level.ERROR.toString(), Level.OFF.toString() };

	private List<LoggerVO> loggersList = null;

	/** Logger */
	private transient Logger logger = LoggerFactory
			.getLogger(LoggerMBean.class);

	/**
	 * Default constructor
	 */
	public LoggerMBean() {
		super();
	}

	/**
	 * populate Loggers
	 */
	@PostConstruct
	public void init() {

		/* get the list of setting values */
		populateLoggers();

	}

	/**
	 * Clean the logger list
	 */
	@PreDestroy
	public void destroy() {

		if (null != loggersList) {
			loggersList.clear();
		}
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

			if (null == nativeLogger.getLevel()) {
				continue;
			}
			LoggerVO vo = new LoggerVO(nativeLogger.getName(), nativeLogger
					.getLevel().toString(), nativeLogger.isAdditive());
			logger.trace(String.format("Add logger %s", vo.toString()));
			loggersList.add(vo);
		}

	}

	/**
	 * Allow to edit level of each logger
	 * 
	 * @param rev
	 *            RowEditEvent
	 */
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

	/**
	 * Allow to refresh the list of loggers
	 */
	public void refresh() {
		populateLoggers();
	}

	/**
	 * @return list of loggers
	 */
	public List<LoggerVO> getLoggers() {
		return loggersList;
	}

	/**
	 * 
	 * @return LoggerLevelValues
	 */
	public String[] getLoggerLevelValues() {
		return loggerLevelValues;
	}

}
