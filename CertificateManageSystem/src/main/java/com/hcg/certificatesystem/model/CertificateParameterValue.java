package com.hcg.certificatesystem.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "CERTIFICATE_PARAMETER_VALUE")
public class CertificateParameterValue implements Serializable{
	private String id;
	private CertificateParameter certificateParameter;
	private String value;

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

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "PARAMETER_ID")
	public CertificateParameter getCertificateParameter() {
		return certificateParameter;
	}

	public void setCertificateParameter(CertificateParameter certificateParameter) {
		this.certificateParameter = certificateParameter;
	}

	@Column(name="VALUE")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}