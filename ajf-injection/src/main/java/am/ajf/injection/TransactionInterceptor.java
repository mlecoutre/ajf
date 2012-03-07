package am.ajf.injection;

import am.ajf.core.logger.LoggerFactory;
import java.io.Serializable;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;

/**
 * TransactionInterceptor
 * 
 * @author E016696
 * 
 */
@Transactional
@Interceptor
public class TransactionInterceptor implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	UserTransaction utx;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Add the transaction logic to the method. Dont start a transaction if @NoTransaction
	 * is present. The commit workflow is as follow :
	 * <ol>
	 * <li>Start the transaction</li>
	 * <li>Launch the underlying process</li>
	 * <li>Commit the transaction</li>
	 * </ol>
	 * The transaction will rollback on two cases :
	 * <ul>
	 * <li>transaction was set as rollback only</li>
	 * <li>an exception happened during the underlying method</li>
	 * </ul>
	 * 
	 * @param ctx
	 *            invocation context
	 * @return the object
	 * @throws Throwable
	 *             all errror
	 */
	@AroundInvoke
	public Object manageTransaction(InvocationContext ctx) throws Throwable {

		boolean noTranOnMethod = ctx.getMethod().isAnnotationPresent(
				NonTransactional.class);
		boolean noTranOnClass = ctx.getMethod().getDeclaringClass()
				.isAnnotationPresent(NonTransactional.class);
		Object res;

		if (noTranOnClass || noTranOnMethod) {
			res = ctx.proceed();
		} else {
			logger.debug("Starting Transaction : " + utx.getStatus());
			utx.begin();
			try {
				logger.debug("Starting Process : " + utx.getStatus());
				res = ctx.proceed();
				logger.debug("Process ended sucessfuly : " + utx.getStatus());
				if (utx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
					logger.debug("Rolling back Transaction (set as rollback only) : "
							+ utx.getStatus());
					utx.rollback();
					logger.debug("Transaction Rollbacked : " + utx.getStatus());
				} else {
					logger.debug("Committing Transaction : " + utx.getStatus());
					utx.commit();
					logger.debug("Committed Transaction : " + utx.getStatus());
				}
			} catch (Throwable e) {
				logger.debug("Rolling back transaction : " + utx.getStatus());
				utx.rollback();
				logger.debug("Transaction Rollbacked : " + utx.getStatus());
				throw e;
			}
		}
		return res;
	}

}
