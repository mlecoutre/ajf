package am.ajf.injection.implhandlers;

import java.lang.reflect.Method;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import am.ajf.injection.ImplementationHandler;

public class FirstImplHandler implements ImplementationHandler {

	@Override
	public boolean canHandle(Method method) {		
		return "doSomething".equals(method.getName());
	}

	@Override
	public Class<?> implementMethodsFor(Class<?> superClass, Class<?> interfaceClass,
			List<Method> methods) {
		ClassPool pool = ClassPool.getDefault();		
		pool.insertClassPath(new ClassClassPath(superClass));
		pool.insertClassPath(new ClassClassPath(interfaceClass));
		Class<?> clazz = null;
		
		try {
			CtClass cc = null;
			if (superClass != null) {
				//System.out.println("super : "+superClass.getName());
				CtClass sc = pool.get(superClass.getName());
				CtClass ic = pool.get(interfaceClass.getName());
				cc = pool.makeClass(superClass.getName().concat("$javassist$ajf$FirstServiceHandler$Service"), sc);
				cc.setInterfaces(new CtClass[] {ic});
			} else {
				//System.out.println("interface : " + interfaceClass.getName());
				CtClass ic = pool.get(interfaceClass.getName());
				cc = pool.makeClass(interfaceClass.getName().concat("$javassist$ajf$FirstServiceHandler$Service"));
				cc.setInterfaces(new CtClass[] {ic});
			}
						
			cc.addMethod(CtMethod.make("public java.lang.String doSomething() {return \"result\";}", cc));
						
			clazz = cc.toClass();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
		return clazz;
	}

}
