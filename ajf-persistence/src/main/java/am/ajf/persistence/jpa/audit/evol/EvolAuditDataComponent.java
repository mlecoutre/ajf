package am.ajf.persistence.jpa.audit.evol;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Standard EVOL fields on DB2 mainframe Florange.
 * 
 * @author Nicolas Radde (E016696)
 *
 */
@Embeddable
public class EvolAuditDataComponent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(nullable = false, name="EVOL_DHECREENR")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@Column(nullable = false, length = 8, name="EVOL_IDTCREENR")
	private String createUser;
	
	@Column(nullable = true, length = 8, name="EVOL_FCTCREENR")
	private String creationFct;
	
	@Column(nullable = true, name="EVOL_DHEMAJENR")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
	@Column(nullable = true, length = 8, name="EVOL_IDTMAJENR")
	private String updateUser;	
	
	@Column(nullable = true, length = 8, name="EVOL_FCTMAJENR")
	private String updateFct;

	//@Version //doesnt work for now
	@Column(name="EVOL_NUMVRSENR")
	private int recordVersion;
		
	@Column(nullable = true, length = 1, name="EVOL_ETAENR")
	private String recordStatus;
	
	/**
	 * 
	 */
	public EvolAuditDataComponent() {
		super();
	}

	

	public Date getCreateDate() {
		return createDate;
	}



	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}



	public String getCreateUser() {
		return createUser;
	}



	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}



	public String getCreationFct() {
		return creationFct;
	}



	public void setCreationFct(String creationFct) {
		this.creationFct = creationFct;
	}



	public Date getUpdateDate() {
		return updateDate;
	}



	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}



	public String getUpdateUser() {
		return updateUser;
	}



	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}



	public String getUpdateFct() {
		return updateFct;
	}



	public void setUpdateFct(String updateFct) {
		this.updateFct = updateFct;
	}



	public int getRecordVersion() {
		return recordVersion;
	}



	public void setRecordVersion(int recordVersion) {
		this.recordVersion = recordVersion;
	}



	public String getRecordStatus() {
		return recordStatus;
	}



	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	@Override
	public String toString() {
		return "EvolAuditDataComponent [createDate=" + createDate
				+ ", createUser=" + createUser + ", creationFct=" + creationFct
				+ ", updateDate=" + updateDate + ", updateUser=" + updateUser
				+ ", updateFct=" + updateFct + ", recordVersion="
				+ recordVersion + ", recordStatus=" + recordStatus + "]";
	}	
}
