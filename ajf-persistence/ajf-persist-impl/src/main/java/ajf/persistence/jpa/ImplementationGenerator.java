package ajf.persistence.jpa;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionTarget;

public interface ImplementationGenerator {

	/**
	 * 
	 * @param impl
	 * @param in
	 * @param it
	 * @return
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	Bean<?> createBeanFromImpl(Class<?> impl,
			Class<?> in, final InjectionTarget<?> it) throws NotFoundException,
			CannotCompileException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			SecurityException, NoSuchFieldException;

	Class<?> createImpl(Class<?> serviceBD)
			throws CannotCompileException, NotFoundException,
			ClassNotFoundException;
	
	void addClasspathFor(Class<?> clazz);

}
