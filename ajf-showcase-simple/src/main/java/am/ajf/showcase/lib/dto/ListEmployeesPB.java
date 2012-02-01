package am.ajf.showcase.lib.dto;

public class ListEmployeesPB {

	private String lastname;

	public ListEmployeesPB() {

	}

	public ListEmployeesPB(String lastname) {
		this.lastname = lastname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
}
