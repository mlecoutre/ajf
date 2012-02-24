package am.ajf.persistence.jpa.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
import am.ajf.persistence.jpa.annotation.PersistenceUnit;

public class AbstractPersistenceImplHandler {

	private static final String PERSISTENCE_UNIT_DEFAULT_NAME = "default";
	protected ClassPool pool;
	
	public AbstractPersistenceImplHandler() {
		super();
		pool = ClassPool.getDefault();
	}

	protected CtField createEntityManager(CtClass cc)
			throws CannotCompileException, ClassNotFoundException,
			NotFoundException {
		CtField cEm = CtField.make("private transient javax.persistence.EntityManager em;", cc);
		PersistenceUnit puAnn = null;
		if (cc.hasAnnotation(PersistenceUnit.class)) {
			puAnn = (PersistenceUnit) cc.getAnnotation(PersistenceUnit.class);
		}
		AnnotationsAttribute attribute = new AnnotationsAttribute(cc.getClassFile().getConstPool(), AnnotationsAttribute.visibleTag);
		Annotation injectAnnotation = new Annotation(cc.getClassFile().getConstPool(), ClassPool.getDefault().get("javax.inject.Inject"));
		Annotation puAnnotation = new Annotation(cc.getClassFile().getConstPool(), ClassPool.getDefault().get("am.ajf.persistence.jpa.annotation.PersistenceUnit"));
		StringMemberValue mv = (StringMemberValue) Annotation.createMemberValue(cc.getClassFile().getConstPool(), pool.get("java.lang.String"));
		if (puAnn != null) {			
			mv.setValue(puAnn.value());			
		} else {			
			mv.setValue(PERSISTENCE_UNIT_DEFAULT_NAME);			
		}
		puAnnotation.addMemberValue("value", mv);
		attribute.addAnnotation(injectAnnotation);
		attribute.addAnnotation(puAnnotation);
		cEm.getFieldInfo().addAttribute(attribute);
		return cEm;
	}

}