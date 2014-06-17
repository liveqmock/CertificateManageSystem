package com.hcg.certificatesystem.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.hcg.certificatesystem.model.Certificate;
import com.hcg.certificatesystem.model.CertificateAttachment;
import com.hcg.certificatesystem.model.CertificateCheckHistory;
import com.hcg.certificatesystem.model.CertificateValidHistory;
import com.hcg.certificatesystem.model.CertificateParameterValue;

@Component
public class CertificateDao extends HibernateDao {

	@Resource
	CertificateParameterValueDao certificateParameterValueDao;
	
	@Resource
	CertificateTypeDao certificateTypeDao;
	
	@Resource
	CertificateValidHistoryDao certificateValidHistoryDao;
	
	@Resource 
	CertificateCheckHistoryDao certificateCheckHistoryDao;

	@DataProvider
	public Collection<Certificate> getAll() {
		return this.query("from Certificate");
	}

	@DataProvider
	public void getAll(Page<Certificate> page, Criteria criteria) {
		DetachedCriteria dt = this.buildDetachedCriteria(criteria,
				Certificate.class);
		this.pagingQuery(page, dt);
	}
	
	@DataProvider
	public Certificate getCertificate(String id){
		List<Certificate> certificates = this.query("from Certificate where id=" + "'" + id + "'");
		Certificate certificate=null;
		if(certificates.size()>0){
			certificate=certificates.get(0);
		}
		return certificate;
	}

	@DataProvider
	public Collection<Certificate> getCertificatesByCertificateType(String id) {
		return this.query("from Certificate where certificateType.id='" + id
				+ "'");
	}

	@DataProvider
	public Collection<Certificate> getCertificatesByUser(String id) {
		List<Certificate> certificates=new ArrayList<Certificate>();
		if (null!=id) {
			certificates=this.query("from Certificate where user.username='" + id + "'");
		}
		return certificates;
	}

	@DataProvider
	public Collection<CertificateParameterValue> getCertificateParameterValues(
			String id) {
		List<Certificate> certificates = this
				.query("from Certificate where id=" + "'" + id + "'");
		Certificate certificate = null;
		if (certificates.size() != 0) {
			certificate = certificates.get(0);
		}
		return certificate.getCertificateParameterValues();
	}

	@DataProvider
	public Collection<CertificateValidHistory> getCertificateHistories(String id) {
		List<Certificate> certificates = this
				.query("from Certificate where id=" + "'" + id + "'");
		Certificate certificate = null;
		if (certificates.size() != 0) {
			certificate = certificates.get(0);
		}
		return certificate.getCertificateValidHistories();
	}

	@DataProvider
	public Collection<CertificateAttachment> getCertificateAttachments(String id) {
		List<Certificate> certificates = this
				.query("from Certificate where id=" + "'" + id + "'");
		Certificate certificate = null;
		if (certificates.size() != 0) {
			certificate = certificates.get(0);
		}
		return certificate.getCertificateAttachments();
	}

	@DataResolver
	public void saveCertificates(Set<Certificate> certificates) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (Certificate certificate : certificates) {
				EntityState state = EntityUtils.getState(certificate);
				if (state.equals(EntityState.NEW)) {
					session.save(certificate);
				} else if (state.equals(EntityState.MODIFIED)) {
					Certificate oldCertificate=this.getCertificate(certificate.getId());
					if (oldCertificate.getCertificateParameterValues().size()!=0) {
						certificate.setCertificateParameterValues(oldCertificate.getCertificateParameterValues());
					}
					if (oldCertificate.getCertificateValidHistories().size()!=0) {
						certificate.setCertificateValidHistories(oldCertificate.getCertificateValidHistories());
					}
					session.update(certificate);
				} else if (state.equals(EntityState.DELETED)) {	
					Set<CertificateParameterValue> certificateParameterValues=this.getCertificate(certificate.getId()).getCertificateParameterValues();
					certificateParameterValueDao.deleteCertificateParameterValues(certificateParameterValues);
					
					Set<CertificateValidHistory> certificateValidHistories=this.getCertificate(certificate.getId()).getCertificateValidHistories();
					certificateValidHistoryDao.deleteCertificateValidHistories(certificateValidHistories);
					
					Set<CertificateCheckHistory> certificateCheckHistories=this.getCertificate(certificate.getId()).getCertificateCheckHistories();
					certificateCheckHistoryDao.deleteCertificateCheckHistories(certificateCheckHistories);
					
					certificate.setCertificateParameterValues(null);
					certificate.setCertificateValidHistories(null);
					certificate.setCertificateType(null);
					certificate.setUser(null);
					session.delete(certificate);
				}
				
				if (state.equals(EntityState.NONE)) {			
					Set<CertificateParameterValue> certificateParameterValues =certificate.getCertificateParameterValues();
					if (certificateParameterValues.size()>0) {
						for (CertificateParameterValue certificateParameterValue : certificateParameterValues) {
							if (EntityState.MODIFIED.equals(EntityUtils.getState(certificateParameterValue))) {
								certificateParameterValueDao.saveCertificateParameterValues(certificateParameterValues);
							}
						}
					}
					
					Set<CertificateValidHistory> certificateValidHistories=certificate.getCertificateValidHistories();
					
					if (certificateValidHistories.size()>0) {
						for (CertificateValidHistory certificateValidHistory : certificateValidHistories) {
							if (EntityState.MODIFIED.equals(EntityUtils.getState(certificateValidHistory))) {
								certificateValidHistoryDao.saveCertificateValidHistories(certificateValidHistories);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}

	@DataResolver
	public void deleteCertificates(Collection<Certificate> certificates) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (Certificate certificate : certificates) {
				Set<CertificateParameterValue> certificateParameterValues=this.getCertificate(certificate.getId()).getCertificateParameterValues();
				certificateParameterValueDao.deleteCertificateParameterValues(certificateParameterValues);
				
				Set<CertificateValidHistory> certificateValidHistories=this.getCertificate(certificate.getId()).getCertificateValidHistories();
				certificateValidHistoryDao.deleteCertificateValidHistories(certificateValidHistories);
				
				Set<CertificateCheckHistory> certificateCheckHistories=this.getCertificate(certificate.getId()).getCertificateCheckHistories();
				certificateCheckHistoryDao.deleteCertificateCheckHistories(certificateCheckHistories);
				
				certificate.setCertificateParameterValues(null);
				certificate.setCertificateValidHistories(null);
				certificate.setCertificateCheckHistories(null);
				certificate.setCertificateType(null);
				certificate.setUser(null);
				session.delete(certificate);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}
}
