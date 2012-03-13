package am.ajf.testing.junit;

import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import am.ajf.core.utils.BeanUtils;

public class DITestRunnner extends BlockJUnit4ClassRunner {

	public DITestRunnner(Class<?> classToRun) throws InitializationError {
		super(classToRun);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.junit.runners.BlockJUnit4ClassRunner#createTest()
	 */
	@Override
	protected Object createTest() throws Exception {

		Object testBean = BeanUtils.newInstance(this.getTestClass().getJavaClass());
		return testBean;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.junit.runners.BlockJUnit4ClassRunner#validateZeroArgConstructor(java
	 * .util.List)
	 */
	@Override
	protected void validateZeroArgConstructor(List<Throwable> arg0) {

		// can inject constructors with parameters ?
		// super.validateZeroArgConstructor(arg0);

	}

}
