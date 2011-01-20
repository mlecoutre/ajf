package foo.lib.model;

import javax.inject.Inject;

import foo.core.dao.MyMockDAO;

public class MyMock {

	@Inject
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
