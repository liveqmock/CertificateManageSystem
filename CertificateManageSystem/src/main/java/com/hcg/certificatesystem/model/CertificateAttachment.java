package com.hcg.certificatesystem.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name="CERTIFICATE_ATTACHMENT")
public class CertificateAttachment implements Serializable {
	private String id;
	private String attachmentName;
	private String url;

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

	@Column(name="ATTACHMENT_NAME")
	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}

	@Column(name="URL")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
