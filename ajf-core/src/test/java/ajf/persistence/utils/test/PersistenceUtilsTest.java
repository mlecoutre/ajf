package ajf.persistence.utils.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import ajf.persistence.exception.PersistenceLayerException;
import ajf.persistence.utils.PersistenceUtils;

public class PersistenceUtilsTest {

	@Mock
	private Exception exception;

	@Mock
	Throwable cause;

	@Mock
	private Logger log;

	@Test
	public void testHandlerError() {
		boolean pleExceptionIsRaised = false;
		MockitoAnnotations.initMocks(this);
		when(exception.getMessage()).thenReturn("exceptionMsg");
		when(exception.getCause()).thenReturn(cause);
		when(cause.getMessage()).thenReturn("causeMsg");
		try {
			PersistenceUtils.handlerError(log, "my.error.msg", exception);

		} catch (PersistenceLayerException ple) {
			// check if only persistenceLayerException is thrown
			// verify(ple, times(1)).getMessage();
			pleExceptionIsRaised = true;
			// check if the message is send to the caller layer;
			verify(exception, times(1)).getMessage();
			assertTrue("contains error msg",
					ple.getMessage().contains("exceptionMsg"));
			assertTrue("contains parent error msg",
					ple.getMessage().contains("causeMsg"));
		}
		// check if we have logger in error the exception
		verify(log, times(1)).error(any(String.class), any(Throwable.class));

		// check if we have logger in error the exception
		// verify(log., times(1)).error(contains("causeMsg"), exception);

		assertTrue("Check if persistenceLayerException is thrown",
				pleExceptionIsRaised);
	}
	
	@Test
	public void testHandlerErrorNoInnerException() {
		boolean pleExceptionIsRaised = false;
		MockitoAnnotations.initMocks(this);
		when(exception.getMessage()).thenReturn("exceptionMsg");
		when(cause.getMessage()).thenReturn("causeMsg");
		//exception without cause. exception.getCause() return null;
		try {
			PersistenceUtils.handlerError(log, "my.error.msg", exception);

		} catch (PersistenceLayerException ple) {
			// check if only persistenceLayerException is thrown
			// verify(ple, times(1)).getMessage();
			pleExceptionIsRaised = true;
		}
		
		assertTrue("Check if persistenceLayerException is thrown",
				pleExceptionIsRaised);
	}
}
