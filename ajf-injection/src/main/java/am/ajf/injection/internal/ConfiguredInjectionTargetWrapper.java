package am.ajf.injection.internal;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

public class ConfiguredInjectionTargetWrapper<T> implements InjectionTarget<T> {
	
	private final InjectionTarget<T> wrappedInjectionTarget;
	
	public ConfiguredInjectionTargetWrapper(
			InjectionTarget<T> wrappedInjectionTarget) {
		super();
		this.wrappedInjectionTarget = wrappedInjectionTarget;
	}

	@Override
	public void dispose(T instance) {
		wrappedInjectionTarget.dispose(instance);
		
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return wrappedInjectionTarget.getInjectionPoints();
	}

	@Override
	public T produce(CreationalContext<T> ctx) {
		return wrappedInjectionTarget.produce(ctx);
	}

	@Override
	public void inject(T instance, CreationalContext<T> ctx) {
		wrappedInjectionTarget.inject(instance, ctx);
	}

	@Override
	public void postConstruct(T instance) {
		wrappedInjectionTarget.postConstruct(instance);		
	}

	@Override
	public void preDestroy(T instance) {
		wrappedInjectionTarget.preDestroy(instance);		
	}
	
}
