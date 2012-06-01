package am.ajf.voila.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author E010925
 */
@Entity
@Table(name = "PERSON")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = Person.FIND_ALL, query = "SELECT p FROM Person p"),
		@NamedQuery(name = Person.FIND_BY_LASTNAME, query = "SELECT p FROM Person p WHERE p.lastname LIKE :lastname"),
		@NamedQuery(name = Person.FIND_BY_PERSONID, query = "SELECT p FROM Person p WHERE p.personid=:personid") })
public class Person implements Serializable {

	public static final String FIND_ALL = "Person.findAll";
	public static final String FIND_BY_LASTNAME = "Person.findLastname";
	public static final String FIND_BY_PERSONID = "Person.findByPersonId";
	public static final String REMOVE_BY_PRIMARYKEY = "Person.removeByPrimaryKey";

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "PERSONID", nullable = false)
	private Long personid;

	private String firstname;
	private String lastname;
	private Date birthday;
	private char sex;

	public Long getPersonid() {
		return personid;
	}

	public void setPersonid(Long personid) {
		this.personid = personid;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (personid != null ? personid.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Person)) {
			return false;
		}
		Person other = (Person) object;
		if ((this.personid == null && other.personid != null)
				|| (this.personid != null && !this.personid
						.equals(other.personid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Person [personid=" + personid + ", firstname=" + firstname
				+ ", lastname=" + lastname + ", birthday=" + birthday
				+ ", sex=" + sex + "]";
	}

}
