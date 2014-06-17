package com.hcg.certificatesystem.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "CERTIFICATE_PARAMETER")
public class CertificateParameter implements Serializable{
	private String id;
	private String certificateParameterName;
	private String valueType;
	private String valueTypeParam;
	private CertificateType certificateType;

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

	@Column(name="PARAMETER_NAME")
	public String getCertificateParameterName() {
		return certificateParameterName;
	}

	public void setCertificateParameterName(String certificateParameterName) {
		this.certificateParameterName = certificateParameterName;
	}

	@Column(name="VALUE_TYPE")
	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	@Column(name="VALUE_TYPE_PARAM")
	public String getValueTypeParam() {
		return valueTypeParam;
	}

	public void setValueTypeParam(String valueTypeParam) {
		this.valueTypeParam = valueTypeParam;
	}

	@ManyToOne
	@JoinColumn(name="certificateType")//一对多关系为多的一端维护关联
	public CertificateType getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(CertificateType certificateType) {
		this.certificateType = certificateType;
	}
}