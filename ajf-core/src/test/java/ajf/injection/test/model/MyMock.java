package ajf.injection.test.model;

import ajf.annotations.InjectModule;
import ajf.injection.DefaultInjectionModule;
import ajf.injection.test.dao.MyMockDAO;
import ajf.persistence.annotations.InjectDAO;

@InjectModule(value = DefaultInjectionModule.class)
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
