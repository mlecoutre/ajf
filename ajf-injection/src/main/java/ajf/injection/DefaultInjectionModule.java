package ajf.injection;

import ajf.datas.AuditData;
import ajf.datas.AuditDataContext;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;

public class DefaultInjectionModule extends AbstractModule {
	
	public DefaultInjectionModule() {
		super();
	}
		
	@Override
	protected void configure() {
		
		bindListener(Matchers.any(), new InjectionTypeListener());
		
		bind(AuditData.class).toProvider(new Provider<AuditData>() {

			@Override
			public AuditData get() {
				AuditData auditData = AuditDataContext.getAuditData();
				return auditData;
			}
			
		});
			
		
	}
	
}
