package am.ajf.persistence.jpa.test.harness;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import am.ajf.persistence.jpa.audit.evol.EvolAuditDataComponent;
import am.ajf.persistence.jpa.audit.evol.EvolAuditDataListener;
import am.ajf.persistence.jpa.audit.evol.EvolAuditable;


@Entity
	@NamedQueries({ @NamedQuery(name = ModelAudit.FIND_BY_NAME, 
		query = "FROM ModelAudit model WHERE model.name = ?1") ,
	@NamedQuery(name = ModelAudit.FIND_ALL, 
		query = "FROM ModelAudit model")})	
@EntityListeners(EvolAuditDataListener.class)
public class ModelAudit implements EvolAuditable {

	public static final String FIND_ALL = "ModelAudit.findAll";
	public static final String FIND_BY_NAME = "ModelAudit.findByName";
	
	private Long id;
	private String name;
	private EvolAuditDataComponent auditDatas;

	public ModelAudit() {}
	
	public ModelAudit(String name) {
		this.name = name;
	}
	
	
	@Id @GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Basic
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public EvolAuditDataComponent getAuditData() {
		return auditDatas;
	}

	@Override
	public void setAuditData(EvolAuditDataComponent auditData) {
		this.auditDatas = auditData;		
	}
	
}
