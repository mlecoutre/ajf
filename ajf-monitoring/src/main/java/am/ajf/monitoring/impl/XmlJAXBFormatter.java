package am.ajf.monitoring.impl;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import am.ajf.monitoring.EventFormatter;
import am.ajf.monitoring.exceptions.EventFormatterException;

public class XmlJAXBFormatter implements  EventFormatter {
	
	private Marshaller marshaller;

	/**
	 * @throws JAXBException 
	 * 
	 */
	public XmlJAXBFormatter(Class<?>... eventClasses) throws JAXBException {
		
		JAXBContext ctx = JAXBContext.newInstance(eventClasses);
		marshaller = ctx.createMarshaller();
		
	}

	@Override
	public String format(Object eventSource) throws EventFormatterException {
		
		try {
			StringWriter writer = new StringWriter();
			marshaller.marshal(eventSource, writer);
			writer.close();
			return writer.toString();
		} catch (IOException e) {
			throw new EventFormatterException("Unable to close xml writer.", e);
		} catch (JAXBException e) {
			throw new EventFormatterException("Unable to format event '" + eventSource.getClass().getName() + "'.", e);
		}				
		
	}
	
}
