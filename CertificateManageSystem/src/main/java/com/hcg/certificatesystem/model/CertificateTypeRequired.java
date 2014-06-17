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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultPosition;

@Component
@Entity
@Table(name = "CERTIFICATE_TYPE_REQUIRED")
public class CertificateTypeRequired implements Serializable {
	private String id;
	private DefaultPosition position;
	private Collection<CertificateType> certificateTypes;

	@Id
	@GeneratedValue(generator = "systemUUID")
	@GenericGenerator(name = "systemUUID", strategy = "org.hibernate.id.UUIDGenerator")
	// 采用uuid的主键生成策略
	@Column(name = "ID")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "POSITION_ID", unique = true)
	public DefaultPosition getPosition() {
		return position;
	}

	public void setPosition(DefaultPosition position) {
		this.position = position;
	}

	@ManyToMany(fetch = FetchType.EAGER, targetEntity = CertificateType.class, cascade = CascadeType.ALL)
	@JoinTable(name = "REQUIRED_TYPE_MAP", joinColumns = { @JoinColumn(name = "REQUIRED_ID") }, inverseJoinColumns = { @JoinColumn(name = "TYPE_ID") })
	public Collection<CertificateType> getCertificateTypes() {
		return certificateTypes;
	}

	public void setCertificateTypes(Collection<CertificateType> certificateTypes) {
		this.certificateTypes = certificateTypes;
	}
}