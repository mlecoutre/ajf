package foo.lib.model;

import ajf.persistence.annotations.InjectDAO;
import foo.core.dao.MyMockDAO;

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
