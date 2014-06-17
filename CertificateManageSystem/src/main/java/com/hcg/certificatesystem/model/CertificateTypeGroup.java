package com.hcg.certificatesystem.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="CERTIFICATE_TYPE_GROUP")
public class CertificateTypeGroup implements Serializable{
	private String id;
	private String certificateTypeGroupName;
	private Collection<CertificateType> certificateTypes;
	private int levels;
	private String sortType;

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

	@Column(name="TYPE_GROUP_NAME")
	public String getCertificateTypeGroupName() {
		return certificateTypeGroupName;
	}

	public void setCertificateTypeGroupName(String certificateTypeGroupName) {
		this.certificateTypeGroupName = certificateTypeGroupName;
	}

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL,mappedBy="certificateTypeGroup")//一对多关系为多的一端维护关联
	public Collection<CertificateType> getCertificateTypes() {
		return certificateTypes;
	}

	public void setCertificateTypes(Collection<CertificateType> certificateTypes) {
		this.certificateTypes = certificateTypes;
	}

	@Column(name="LEVELS")
	public int getLevels() {
		return levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	@Column(name="SORT_TYPE")
	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
}