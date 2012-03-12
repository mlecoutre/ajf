package am.ajf.core.beans;

public interface LifecycleAware {

	/**
	 * start the Component
	 */
	void start();

	/**
	 * stop the Component
	 */
	void stop();

}