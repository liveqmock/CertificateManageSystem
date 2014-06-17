package com.hcg.certificatesystem.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="CERTIFICATE_ORGANIZATION")
public class CertificateOrganization implements Serializable{
	
	private String id;
	private String organizationName;
	private String organizationAddress;
	private Collection<CertificateType> certificateTypes;

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

	@Column(name="ORGANIZATION_NAME")
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Column(name="ORGANIZATION_ADDRESS")
	public String getOrganizationAddress() {
		return organizationAddress;
	}

	public void setOrganizationAddress(String organizationAddress) {
		this.organizationAddress = organizationAddress;
	}

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL,mappedBy="certificateOrganization")
	public Collection<CertificateType> getCertificateTypes() {
		return certificateTypes;
	}

	public void setCertificateTypes(Collection<CertificateType> certificateTypes) {
		this.certificateTypes = certificateTypes;
	}
}