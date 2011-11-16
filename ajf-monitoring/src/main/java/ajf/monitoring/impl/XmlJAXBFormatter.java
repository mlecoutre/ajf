package ajf.monitoring.impl;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import ajf.monitoring.AbstractEvent;
import ajf.monitoring.EventFormatter;
import ajf.monitoring.exceptions.EventFormatterException;

public class XmlJAXBFormatter implements  EventFormatter {
	
	private Marshaller marshaller;

	/**
	 * @throws JAXBException 
	 * 
	 */
	public XmlJAXBFormatter(Class<?>... eventClasses) throws JAXBException {
		super();
		JAXBContext ctx = JAXBContext.newInstance(eventClasses);
		marshaller = ctx.createMarshaller();
		
	}

	@Override
	public String format(AbstractEvent event) throws EventFormatterException {
		
		try {
			StringWriter writer = new StringWriter();
			marshaller.marshal(event, writer);
			writer.close();
			return writer.toString();
		} catch (IOException e) {
			throw new EventFormatterException("Unable to close xml writer.", e);
		} catch (JAXBException e) {
			throw new EventFormatterException("Unable to format event of type '" + event.getClass().getName() + "'.", e);
		}				
		
	}
	
	

}
