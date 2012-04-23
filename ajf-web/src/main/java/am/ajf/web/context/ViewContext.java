package am.ajf.web.context;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewContext implements Context {
	
	public static Logger logger = LoggerFactory.getLogger(ViewContext.class);
	
	private Map<String, Object> getViewScope() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = fctx.getViewRoot();
        return viewRoot.getViewMap();
    }
	
	@Override
    public Class<? extends Annotation> getScope() {
        return ViewScoped.class;
    }

	@Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        T t = get(contextual);
        if (t == null) {
        	logger.trace("View context doesnt exist yet and will be created");            
            t = create(contextual, creationalContext);
        }
        return t;
    }

	@SuppressWarnings("unchecked")
	@Override
    public <T> T get(Contextual<T> contextual) {
        if (!isBean(contextual)) {
            throw new IllegalArgumentException();
        } else {
            Bean<T> bean = (Bean<T>) contextual;
            return (T) getViewScope().get(bean.getName());
        }
    }

	@Override
    public boolean isActive() {
        return true;
    }

    private <T> T create(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean<T> bean = (Bean<T>) contextual;
        T t = bean.create(creationalContext);
        getViewScope().put(bean.getName(), t);
        return t;
    }
    
    private <T> boolean isBean(Contextual<T> contextual) {
    	if (contextual instanceof Bean) {
    		return true;        
    	}        
    	return false;    
    }

}
