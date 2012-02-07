package am.ajf.persistence.jpa;

import java.util.List;

public interface CrudBD<E,P> {
	
	List<E> find(String queryName, Object... params); 
	E save(E entity);
	boolean remove(E entity);
	
	//TODO Temporary removal until a solution is found to use generics in the javaassist impl
	/*
	boolean delete(P pk);
	E fetch(P pk);
	*/ 

}
