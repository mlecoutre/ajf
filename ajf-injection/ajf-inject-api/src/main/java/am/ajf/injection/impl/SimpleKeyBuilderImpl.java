package am.ajf.injection.impl;

import am.ajf.injection.Cached;
import am.ajf.injection.KeyBuilder;

import com.google.common.base.Joiner;

public class SimpleKeyBuilderImpl implements KeyBuilder {
	
	private final static Joiner joiner = Joiner.on(":"); 
	
	public SimpleKeyBuilderImpl() {
		super();
	}

	@Override
	public Object build(Cached cachedAnnotation, Object[] parameters) {
		
		String processedKey = joiner.join(parameters);
		return processedKey;
	}

}
