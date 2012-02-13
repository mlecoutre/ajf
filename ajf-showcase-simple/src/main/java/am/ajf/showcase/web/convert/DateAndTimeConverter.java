package am.ajf.showcase.web.convert;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "dateAndTimeconverter")
@ManagedBean
public class DateAndTimeConverter implements Converter {

	public DateAndTimeConverter() {
		System.out
				.println("DateAndTimeConverter DateAndTimeConverter DateAndTimeConverter");
	//	setPattern("yyy-MM-dd");
		// this.setDateStyle("short");
	}

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return "birthday";
	}
}