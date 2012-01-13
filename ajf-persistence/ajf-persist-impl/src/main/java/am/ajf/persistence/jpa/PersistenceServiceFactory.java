package am.ajf.persistence.jpa;

import am.ajf.core.services.ServiceFactory;

public class PersistenceServiceFactory implements ServiceFactory {

	@Override
	public boolean accept(Class<?> serviceClass) {
		return false;
	}

	@Override
	public <T> T get(Class<T> serviceClass) throws Exception {
		return null;
	}

	@Override
	public int getOrdinal() {
		return 1;
	}

}
