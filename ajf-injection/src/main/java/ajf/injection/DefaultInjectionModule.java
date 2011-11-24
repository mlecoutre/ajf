package ajf.injection;

import ajf.datas.AuditData;
import ajf.datas.AuditDataContext;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class DefaultInjectionModule extends AbstractModule {
	
	public DefaultInjectionModule() {
		super();
	}
		
	@Override
	protected void configure() {
		
		bindListener(Matchers.any(), new InjectionTypeListener());
						
		/* AuditData concern */
		bindAuditData();
		
		//bindInterceptor(new ServiceMatcher(), new ServiceMethodMatcher(), new ServiceMethodInterceptor());
		
	}

	/* AuditData binding */
	private void bindAuditData() {
				
		bind(AuditData.class).toProvider(new Provider<AuditData>() {

			@Override
			public AuditData get() {
				AuditData auditData = AuditDataContext.getAuditData();
				return auditData;
			}
			
		});
		
		bind(String.class).annotatedWith(Names.named(AuditData.KEY_UUID)).toProvider(new Provider<String>() {

			@Override
			public String get() {
				AuditData auditData = AuditDataContext.getAuditData();
				String uuid = auditData.getString(AuditData.KEY_UUID);
				return uuid;
			}
			
		});
		
		bind(String.class).annotatedWith(Names.named(AuditData.KEY_USERID)).toProvider(new Provider<String>() {

			@Override
			public String get() {
				AuditData auditData = AuditDataContext.getAuditData();
				String userId = auditData.getString(AuditData.KEY_USERID);
				return userId;
			}
			
		});
	}
	
}
