package ajf.persistence.jpa.impl;

import ajf.persistence.jpa.annotation.NamedQuery;
import ajf.persistence.jpa.annotation.QueryParam;
import javassist.CtMethod;
import javassist.NotFoundException;

public class NamedQueryMethodGenerator implements MethodGenerator {

	@Override
	public boolean canImplement(CtMethod method) {		
		return method.hasAnnotation(NamedQuery.class);
	}

	@Override
	public StringBuffer generateBodyFor(CtMethod method) throws ClassNotFoundException, NotFoundException {
		Object[] annotations = method.getAnnotations();
		NamedQuery namedQuery = (NamedQuery)annotations[0];
		Object[][] pAnnotations = method.getParameterAnnotations();
		Object[] pTypes = method.getParameterTypes();
		if (pAnnotations.length != pTypes.length) {
			throw new IllegalArgumentException("method "+method.getLongName()+" dont have annotations on all parameters");
		}					
		
		StringBuffer body = new StringBuffer();
		body.append("{\n");
		body.append("  logger.debug(\"launching query "+namedQuery.name()+"\");\n");
		body.append("  javax.persistence.EntityManager em = emf.createEntityManager();\n");
		body.append("  javax.persistence.Query query = em.createNamedQuery(\""+namedQuery.name()+"\");\n");
		for (int i = 0 ; i < pTypes.length ; i++) {
			QueryParam param = null;
			for (int j = 0 ; j < pAnnotations[i].length ; j++) {
				if (QueryParam.class.isAssignableFrom(pAnnotations[i][j].getClass())) {
					param = (QueryParam) pAnnotations[i][j];
				}
			}
			if (param == null) {
				throw new IllegalArgumentException("method "+method.getLongName()+" dont have annotation '@QueryParam' on parameter : "+i);
			}
			body.append("  query = query.setParameter(\""+param.value()+"\", $"+(i+1)+");\n");
		}
		body.append("  return query.getResultList();\n");
		body.append("}\n");
		return body;
	}

}
