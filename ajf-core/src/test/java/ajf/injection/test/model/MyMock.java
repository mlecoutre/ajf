package ajf.injection.test.model;

import ajf.injection.DefaultInjectionModule;
import ajf.injection.annotations.InjectionModule;
import ajf.injection.test.dao.MyMockDAO;
import ajf.persistence.annotations.InjectDAO;

@InjectionModule(value = DefaultInjectionModule.class)
public class MyMock {

	@InjectDAO
	private MyMockDAO mockDAO;

	public MyMock() {

	}

	public MyMockDAO getMockDAO() {
		return mockDAO;
	}

	public void setMockDAO(MyMockDAO mockDAO) {
		this.mockDAO = mockDAO;
	}

}
