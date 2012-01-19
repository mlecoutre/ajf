package am.ajf.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadState;
import org.junit.AfterClass;

public abstract class AbstractShiroTest {

	 private static ThreadState subjectThreadState;

	    public AbstractShiroTest() {
	    	super();
	    }

	    /**
	     * Allows subclasses to set the currently executing {@link Subject} instance.
	     *
	     * @param subject the Subject instance
	     */
	    protected void setSubject(Subject subject) {
	        clearSubject();
	        subjectThreadState = createThreadState(subject);
	        subjectThreadState.bind();
	    }

	    protected Subject getSubject() {
	        return SecurityUtils.getSubject();
	    }
	    
	    protected Subject buildNewSubject() {
			//Create the subject
			Subject subjectUnderTest = new Subject.Builder(getSecurityManager()).buildSubject();
			return subjectUnderTest;
		}
	    
	    

	    protected ThreadState createThreadState(Subject subject) {
	        return new SubjectThreadState(subject);
	    }

	    /**
	     * Clears Shiro's thread state, ensuring the thread remains clean for future test execution.
	     */
	    protected void clearSubject() {
	        doClearSubject();
	    }

	    private static void doClearSubject() {
	        if (subjectThreadState != null) {
	            subjectThreadState.clear();
	            subjectThreadState = null;
	        }
	    }

	    protected static void setSecurityManager(org.apache.shiro.mgt.SecurityManager securityManager) {
	        SecurityUtils.setSecurityManager(securityManager);
	    }

	    protected static org.apache.shiro.mgt.SecurityManager getSecurityManager() {
	        return SecurityUtils.getSecurityManager();
	    }

	    @AfterClass
	    public static void tearDownShiro() {
	        doClearSubject();
	        try {
	            org.apache.shiro.mgt.SecurityManager securityManager = getSecurityManager();
	            LifecycleUtils.destroy(securityManager);
	        } catch (UnavailableSecurityManagerException e) {
	            //we don't care about this when cleaning up the test environment
	            //(for example, maybe the subclass is a unit test and it didn't
	            // need a SecurityManager instance because it was using only 
	            // mock Subject instances)
	        }
	        setSecurityManager(null);
	    }
	
}
