package am.ajf.remoting.procs.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import am.ajf.remoting.ConfigurationException;
import am.ajf.remoting.procs.annotation.Out;
import am.ajf.remoting.procs.annotation.Result;

/**
 * Helper class to compute informations on a return object
 * provided by a StoredProcedure method.
 * This class deal with the 'how' to get informations like
 * real type at runtime, if the object is wrapped, or if 
 * is is null.
 * 
 * @author Nicolas Radde (E016696)
 *
 */
public class ResultInfo {
	
	private Class<?> returnClass;
	private Type returnGenericType;
	
	private boolean resultList;
	private boolean resultNull;
	private boolean resultWrapped;
	private Class<?> resultTrueType;
	private List<String> outParameters;
	private String resultWrappedName;
	
	public ResultInfo(Class<?> returnClass, Type returnGenericType) throws ConfigurationException {
		this.returnClass = returnClass;
		this.returnGenericType = returnGenericType;
		this.outParameters = new ArrayList<String>();
		computeResultWrapped();
		computeResultNull();		
		computeResultList();
		computeResultTrueType();
		computeOutParameters();
	}
	
	public boolean isResultNull() {
		return resultNull;
	}
	
	public boolean isResultList() {
		return resultList;		
	}
	
	public boolean isResultWrapped() {
		return resultWrapped;
	}
	
	/**
	 * Retrieve the real type of the Model objects mapped
	 * by the output of the stored procedure.
	 * 
	 * @return
	 * @throws ConfigurationException
	 */
	public Class<?> getResultTrueType() throws ConfigurationException {		
		return resultTrueType;
	}		
	
	public String getResultWrappedName() {
		return resultWrappedName;
	}

	public List<String> getOutParameters() {
		return outParameters; 
	}
	
	private void computeResultNull() {
		if (resultWrapped) {
			boolean resultPresent = false;
			for(Field field : returnClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Result.class)) {
					resultPresent = true;
					break;
				}			
			}
			resultNull = !resultPresent;
		} else {
			resultNull = Void.class.equals(returnClass);
		}
	}
	
	private void computeResultWrapped() {
		resultWrapped = false;
		for(Field field : returnClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Out.class)) {
				resultWrapped = true;
				break;
			}			
			if (field.isAnnotationPresent(Result.class)) {
				resultWrappedName = field.getName();
				resultWrapped = true;
				break;
			}
		}
	}
	
	private void computeOutParameters() {
		if (resultWrapped) {
			for(Field field : returnClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Out.class)) {				
					outParameters.add(field.getName());				
				}			
			}
		}
	}
	
	private void computeResultList() {
		if (resultWrapped) {
			for(Field field : returnClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Result.class)) {
					resultList = List.class.isAssignableFrom(field.getType());					
					break;
				}
			}
		} else {
			resultList =  List.class.isAssignableFrom(returnClass);
		}
	}
	
	private void computeResultTrueType() throws ConfigurationException {
		Type tmpGenericType = null;
		if (isResultWrapped()) {			
			for(Field field : returnClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(Result.class)) {
					tmpGenericType = field.getGenericType();					
					break;
				}
			}
		} else {
			tmpGenericType = returnGenericType;
		}				
		
		if (isResultList()) {
			Type listType = tmpGenericType;
			if (listType instanceof ParameterizedType) {
				Type subType = ((ParameterizedType) listType).getActualTypeArguments()[0];
				if (subType instanceof Class<?>) {
					resultTrueType = (Class<?>) subType;
				} else {
					throw new ConfigurationException(subType.toString()+" is not parameterized List of Beans. List of native types are not supported.");
				}
			} else {
				throw new ConfigurationException(listType.toString()+" doesnt define a parameterized List as the return type. You need to define the type of your list content.");
			}			
		} else {			
			resultTrueType = returnClass;
		}
	}

}
