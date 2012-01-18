package am.ajf.persistence.jpa.impl;

import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.NotFoundException;

public interface MethodGenerator {
	
	/**
	 * return yes if the Generator can implement the method.
	 * @param method
	 * @return
	 */
	boolean canImplement(CtMethod method);
	
	/**
	 * Generate the body of the method as a StringBuffer.
	 * 
	 * @param method
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 */
	StringBuffer generateBodyFor(CtMethod method) throws ClassNotFoundException, NotFoundException, CannotCompileException;

}
