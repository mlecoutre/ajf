package am.ajf.persistence.jpa.audit.standard;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class StdAuditDataComponent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(nullable = false, name="CREATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Column(nullable = false, length = 25, name="CREATE_USER")
	private String createUser;
	
	@Column(nullable = true, name="UPDATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
	@Column(nullable = true, length = 25, name="UPDATE_USER")
	private String updateUser;

	/**
	 * 
	 */
	public StdAuditDataComponent() {
		super();
	}

	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * @return the createUser
	 */
	public String getCreateUser() {
		return createUser;
	}

	/**
	 * @param createUser the createUser to set
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the updateUser
	 */
	public String getUpdateUser() {
		return updateUser;
	}

	/**
	 * @param updateUser the updateUser to set
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	@Override
	public String toString() {
		return "AuditDataComponent [createUser=" + createUser + ", createDate="
				+ createDate + ", updateUser=" + updateUser + ", updateDate="
				+ updateDate + "]";
	}
	
	

	
}
