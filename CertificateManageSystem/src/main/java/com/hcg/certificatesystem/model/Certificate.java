package com.hcg.certificatesystem.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultUser;

@Component
@Entity
@Table(name = "CERTIFICATE")
public class Certificate implements Serializable {

	private String id;
	private CertificateType certificateType;
	private String certificateName;
	private String serialNumber;
	private DefaultUser user;
	private Date beginDate;
	private Set<CertificateParameterValue> certificateParameterValues;
	private Set<CertificateValidHistory> certificateValidHistories;
	private Set<CertificateCheckHistory> certificateCheckHistories;
	private Set<CertificateAttachment> certificateAttachments;

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")// 采用uuid的主键生成策略
	@Column(name = "ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="certificateType")//一对多关系为多的一端维护关联
	public CertificateType getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(CertificateType certificateType) {
		this.certificateType = certificateType;
	}

	@Column(name = "CERTIFICATE_NAME")
	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	@Column(name = "SERIAL_NUM")
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "USER_ID")
	public DefaultUser getUser() {
		return user;
	}

	public void setUser(DefaultUser user) {
		this.user = user;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = CertificateParameterValue.class, cascade = CascadeType.ALL)
	@JoinColumns(value = { @JoinColumn(name = "CERTIFICATE_ID", referencedColumnName = "ID")})
	public Set<CertificateParameterValue> getCertificateParameterValues() {
		return certificateParameterValues;
	}

	public void setCertificateParameterValues(
			Set<CertificateParameterValue> certificateParameterValues) {
		this.certificateParameterValues = certificateParameterValues;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = CertificateValidHistory.class, cascade = CascadeType.ALL)
	@JoinColumns(value = { @JoinColumn(name = "CERTIFICATE_ID", referencedColumnName = "ID")})
	public Set<CertificateValidHistory> getCertificateValidHistories() {
		return certificateValidHistories;
	}

	public void setCertificateValidHistories(
			Set<CertificateValidHistory> certificateValidHistoriesHistories) {
		this.certificateValidHistories = certificateValidHistoriesHistories;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = CertificateCheckHistory.class, cascade = CascadeType.ALL)
	@JoinColumns(value = { @JoinColumn(name = "CERTIFICATE_ID", referencedColumnName = "ID")})
	public Set<CertificateCheckHistory> getCertificateCheckHistories() {
		return certificateCheckHistories;
	}

	public void setCertificateCheckHistories(
			Set<CertificateCheckHistory> certificateCheckHistories) {
		this.certificateCheckHistories = certificateCheckHistories;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = CertificateAttachment.class, cascade = CascadeType.ALL)
	@JoinColumns(value = { @JoinColumn(name = "CERTIFICATE_ID", referencedColumnName = "ID")})
	public Set<CertificateAttachment> getCertificateAttachments() {
		return certificateAttachments;
	}

	public void setCertificateAttachments(Set<CertificateAttachment> certificateAttachments) {
		this.certificateAttachments = certificateAttachments;
	}

	@Column(name = "BEGIN_DATE")
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
}