package com.hcg.certificatesystem.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.hcg.certificatesystem.model.Certificate;
import com.hcg.certificatesystem.model.CertificateParameter;
import com.hcg.certificatesystem.model.CertificateParameterValue;
import com.hcg.certificatesystem.model.CertificateType;

@Component
public class CertificateParameterValueDao extends HibernateDao {

	@Resource
	CertificateTypeDao certificateTypeDao;

	@Resource
	CertificateDao certificateDao;
	
	@DataProvider
	public Set<CertificateParameterValue> getCertificateParameterValues(String id) throws Exception {
		List<Certificate> certificates = this.query("from Certificate where id=" + "'" + id + "'");
		Certificate certificate = null;
		if (certificates.size() > 0) {
			certificate = certificates.get(0);
		}

		Set<CertificateParameterValue> certificateParameterValues = certificate.getCertificateParameterValues();

		if (certificateParameterValues.size() == 0) {
			CertificateType certificateType = certificate.getCertificateType();
			Set<CertificateParameter> certificateParameters = certificateType.getCertificateParameters();

			for (CertificateParameter certificateParameter : certificateParameters) {
				CertificateParameterValue certificateParameterValue = new CertificateParameterValue();
				certificateParameterValue.setCertificateParameter(certificateParameter);
				certificateParameterValues.add(certificateParameterValue);
			}

			certificate = EntityUtils.toEntity(certificate);
			EntityUtils.setState(certificate, EntityState.MODIFIED);
			certificate.setCertificateParameterValues(certificateParameterValues);
			Set<Certificate> updateCertificates = new HashSet<Certificate>();
			updateCertificates.add(certificate);
			certificateDao.saveCertificates(updateCertificates);
		}

		return certificateParameterValues;
	}
	
	@DataProvider
	public CertificateParameterValue getCertificateParameterValue(String id){
		List<CertificateParameterValue> certificateParameterValues = this.query("from CertificateParameterValue where id=" + "'" + id + "'");
		CertificateParameterValue certificateParameterValue = null;
		if (certificateParameterValues.size() > 0) {
			certificateParameterValue = certificateParameterValues.get(0);
		}
		return certificateParameterValue;
	}

	@DataResolver
	public void saveCertificateParameterValues(Set<CertificateParameterValue> certificateParameterValues) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (CertificateParameterValue certificateParameterValue : certificateParameterValues) {
				EntityState state = EntityUtils.getState(certificateParameterValue);
				if (state.equals(EntityState.NEW)) {
					session.save(certificateParameterValue);
				} else if (state.equals(EntityState.MODIFIED)) {
					session.update(certificateParameterValue);
				} else if (state.equals(EntityState.DELETED)) {
					session.delete(certificateParameterValue);
				}
				session.save(certificateParameterValue);
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
	public void deleteCertificateParameterValues(Set<CertificateParameterValue> certificateParameterValues) {
		Session session = this.getSessionFactory().openSession();	
		try {
			for (CertificateParameterValue certificateParameterValue : certificateParameterValues) {
				certificateParameterValue.setCertificateParameter(null);
				session.delete(certificateParameterValue);	
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
