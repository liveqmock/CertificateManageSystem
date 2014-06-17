package com.hcg.certificatesystem.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="CERTIFICATE_TYPE")
public class CertificateType implements Serializable{
	private String id;
	private String certificateTypeName;
	private int level;
	private int validPeriod;//证书有效期
	private int checkPeriod;//审核期限
	private boolean hasValidPeriod;//是否有有效期限制
	private boolean hasCheckPeriod;//是否有复审限制
	private boolean hasExceedByAge;//是否有年龄限制
	private int maleExceedByAge;//男性证书到期年龄限制
	private int femaleExeedByAge;//女性证书到期年龄限制
	private CertificateTypeGroup certificateTypeGroup;
	private CertificateOrganization certificateOrganization;
	private Set<CertificateParameter> certificateParameters;
	private Set<Certificate> certificates;

	@Id
	@GeneratedValue(generator="systemUUID")
	@GenericGenerator(name="systemUUID",strategy="org.hibernate.id.UUIDGenerator")//采用uuid的主键生成策略
	@Column(name="ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name="TYPE_NAME")
	public String getCertificateTypeName() {
		return certificateTypeName;
	}

	public void setCertificateTypeName(String certificateTypeName) {
		this.certificateTypeName = certificateTypeName;
	}

	@Column(name="LEVEL")
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Column(name="VALID_PERIOD")
	public int getValidPeriod() {
		return validPeriod;
	}

	public void setValidPeriod(int validPeriod) {
		this.validPeriod = validPeriod;
	}

	@Column(name="CHECK_PERIOD")
	public int getCheckPeriod() {
		return checkPeriod;
	}

	public void setCheckPeriod(int checkPeriod) {
		this.checkPeriod = checkPeriod;
	}

	@Column(name="HAS_VALID_PERIOD")
	public boolean isHasValidPeriod() {
		return hasValidPeriod;
	}

	public void setHasValidPeriod(boolean hasValidPeriod) {
		this.hasValidPeriod = hasValidPeriod;
	}

	@Column(name="HAS_CHECK_PERIOD")
	public boolean isHasCheckPeriod() {
		return hasCheckPeriod;
	}

	public void setHasCheckPeriod(boolean hasCheckPeriod) {
		this.hasCheckPeriod = hasCheckPeriod;
	}

	@Column(name="IS_EXCEED_BY_AGE")
	public boolean isHasExceedByAge() {
		return hasExceedByAge;
	}

	public void setHasExceedByAge(boolean hasExceedByAge) {
		this.hasExceedByAge = hasExceedByAge;
	}

	@Column(name="MALE_EXCEED_AGE")
	public int getMaleExceedByAge() {
		return maleExceedByAge;
	}

	public void setMaleExceedByAge(int maleExceedByAge) {
		this.maleExceedByAge = maleExceedByAge;
	}

	@Column(name="FEMALE_EXCEED_AGE")
	public int getFemaleExeedByAge() {
		return femaleExeedByAge;
	}

	public void setFemaleExeedByAge(int femaleExeedByAge) {
		this.femaleExeedByAge = femaleExeedByAge;
	}

	@ManyToOne
	@JoinColumn(name="certificateOrganization")//一对多关系为多的一端维护关联
	public CertificateOrganization getCertificateOrganization() {
		return certificateOrganization;
	}

	public void setCertificateOrganization(
			CertificateOrganization certificateOrganization) {
		this.certificateOrganization = certificateOrganization;
	}

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL,mappedBy="certificateType")//一对多关系为多的一端维护关联
	public Set<CertificateParameter> getCertificateParameters() {
		return certificateParameters;
	}

	public void setCertificateParameters(
			Set<CertificateParameter> certificateParameters) {
		this.certificateParameters = certificateParameters;
	}
	
	@ManyToOne
	@JoinColumn(name="certificateTypeGroup")//一对多关系为多的一端维护关联
	public CertificateTypeGroup getCertificateTypeGroup() {
		return certificateTypeGroup;
	}

	public void setCertificateTypeGroup(CertificateTypeGroup certificateTypeGroup) {
		this.certificateTypeGroup = certificateTypeGroup;
	}

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL,mappedBy="certificateType")//一对多关系为多的一端维护关联
	public Set<Certificate> getCertificates() {
		return certificates;
	}

	public void setCertificates(Set<Certificate> certificates) {
		this.certificates = certificates;
	}
}