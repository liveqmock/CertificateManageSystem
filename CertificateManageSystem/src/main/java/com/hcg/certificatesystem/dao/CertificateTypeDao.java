package com.hcg.certificatesystem.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Component;

import java.util.List;

import com.bstek.bdf2.core.orm.ParseResult;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.hcg.certificatesystem.model.Certificate;
import com.hcg.certificatesystem.model.CertificateOrganization;
import com.hcg.certificatesystem.model.CertificateParameter;
import com.hcg.certificatesystem.model.CertificateParameterValue;
import com.hcg.certificatesystem.model.CertificateType;
import com.hcg.certificatesystem.model.CertificateTypeRequired;
import com.hcg.certificatesystem.model.CertificateValidHistory;

@Component
public class CertificateTypeDao extends HibernateDao {
	@Resource
	private CertificateDao certificateDao;
	@Resource
	private CertificateParameterDao certificateParameterDao;
	@Resource
	private CertificateTypeRequiredDao certificateTypeRequiredDao;
	

	@DataProvider
	public Collection<CertificateType> getAll() {
		Collection<CertificateType> certificateTypes = this
				.query("from CertificateType");
		return certificateTypes;
	}
	
	@DataProvider
	public void getAll(Page<CertificateType> page,Criteria criteria){
		DetachedCriteria dt=this.buildDetachedCriteria(criteria, CertificateType.class);
		this.pagingQuery(page, dt);
	}
	
	@DataProvider
	public void getCertificateTypes(Page<CertificateType> page,String certificateTypeName) throws Exception{
		if (null!=certificateTypeName){
			String hqlString="from "+CertificateType.class.getName()+" where certificateTypeName like '%"+certificateTypeName+"%'";
			this.pagingQuery(page, hqlString, "select count(*)"+hqlString);
		}else {
			String hqlString="from "+CertificateType.class.getName();
			this.pagingQuery(page, hqlString, "select count(*)"+hqlString);
		}
	}
	
	@DataProvider
	public CertificateType getCertificateType(String id){
		List<CertificateType> certificateTypes = this.query("from CertificateType where id="+"'"+id+"'");
		CertificateType certificateType=null;
		if (certificateTypes.size()>0) {
			certificateType=certificateTypes.get(0);
		}
		return certificateType;
	}

	@DataProvider
	public CertificateOrganization getCertificateOrganization(String id) {
		List<CertificateType> certificateTypes = this
				.query("from CertificateType where id=" + "'" + id + "'");
		CertificateType certificateType = certificateTypes.get(0);
		return certificateType.getCertificateOrganization();
	}

	@DataProvider
	public Collection<CertificateParameter> getCertificateParameter(String id) {
		List<CertificateType> certificateTypes = this
				.query("from CertificateType where id=" + "'" + id + "'");
		CertificateType certificateType = certificateTypes.get(0);
		return certificateType.getCertificateParameters();
	}
	
	@DataProvider
	public Collection<CertificateTypeRequired> getCertificateTypeRequireds(String certificateTypeId){
		Collection<CertificateTypeRequired> certificateTypeRequireds=this.query("from "+CertificateTypeRequired.class.getName());
		Collection<CertificateTypeRequired> certificateTypeRequiredsLinks=new ArrayList<CertificateTypeRequired>();
		
		for (CertificateTypeRequired certificateTypeRequired : certificateTypeRequireds) {
			boolean isLinkedWithThisType=false;
			Collection<CertificateType> certificateTypes=certificateTypeRequired.getCertificateTypes();
			
			for (CertificateType certificateType : certificateTypes) {
				if (certificateType.getId().equals(certificateTypeId)) {
					isLinkedWithThisType=true;
					break;
				}
			}
			
			if (isLinkedWithThisType==true) {
				certificateTypeRequiredsLinks.add(certificateTypeRequired);
			}
		}
		
		return certificateTypeRequiredsLinks;
	}

	@DataProvider
	public Collection<Certificate> getCertificates(String id) {
		List<CertificateType> certificateTypes = this
				.query("from CertificateType where id=" + "'" + id + "'");
		CertificateType certificateType = certificateTypes.get(0);
		return certificateType.getCertificates();
	}

	@DataResolver
	public void saveCertificateTypes(Collection<CertificateType> certificateTypes) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (CertificateType certificateType : certificateTypes) {
				EntityState state = EntityUtils.getState(certificateType);
				if (state.equals(EntityState.NEW)) {
					session.save(certificateType);
				} else if (state.equals(EntityState.MODIFIED)) {
					session.update(certificateType);
				} else if (state.equals(EntityState.DELETED)) {
					Collection<CertificateTypeRequired> certificateTypeRequireds=this.getCertificateTypeRequireds(certificateType.getId());
					for (CertificateTypeRequired certificateTypeRequired : certificateTypeRequireds) {
						certificateTypeRequiredDao.deleteRequiredCertificateTypes(certificateType, certificateTypeRequired);
					}

					Set<Certificate> certificates=this.getCertificateType(certificateType.getId()).getCertificates();
					certificateDao.deleteCertificates(certificates);
					
					Set<CertificateParameter> certificateParameters=this.getCertificateType(certificateType.getId()).getCertificateParameters();
					certificateParameterDao.deleteCertificateParameters(certificateParameters);

					certificateType.setCertificateOrganization(null);
					certificateType.setCertificateTypeGroup(null);
					certificateType.setCertificates(null);
					certificateType.setCertificateParameters(null);
					session.delete(certificateType);
				}
				
				if (EntityState.isVisible(state)) {			
					Set<CertificateParameter> certificateParameters =certificateType.getCertificateParameters();
					if (certificateParameters != null) {
						for (CertificateParameter certificateParameter : certificateParameters) {
							if (EntityState.NEW.equals(EntityUtils.getState(certificateParameter))) {
								certificateParameter.setCertificateType(certificateType);
							}
							if (EntityState.MODIFIED.equals(EntityUtils.getState(certificateParameter))) {
								certificateParameter.setCertificateType(certificateType);
							}
						}
						certificateParameterDao.saveCertificateParameters(certificateParameters);
					}

					Set<Certificate> certificates = certificateType.getCertificates();
					if (certificates != null) {
						for (Certificate certificate : certificates) {
							if (EntityState.NEW.equals(EntityUtils.getState(certificate))) {
								certificate.setCertificateType(certificateType);
							}
							if (EntityState.MODIFIED.equals(EntityUtils.getState(certificate))) {
								certificate.setCertificateType(certificateType);
							}
						}
						certificateDao.saveCertificates(certificates);
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
	public void deleteCertificateTypes(Set<CertificateType> certificateTypes){
		Session session = this.getSessionFactory().openSession();
		try {
			for (CertificateType certificateType : certificateTypes) {
				Set<Certificate> certificates=this.getCertificateType(certificateType.getId()).getCertificates();
				certificateDao.deleteCertificates(certificates);
				
				Set<CertificateParameter> certificateParameters=this.getCertificateType(certificateType.getId()).getCertificateParameters();
				certificateParameterDao.deleteCertificateParameters(certificateParameters);
				
				certificateType.setCertificateOrganization(null);
				certificateType.setCertificateTypeGroup(null);
				certificateType.setCertificates(null);
				certificateType.setCertificateParameters(null);
				session.delete(certificateType);
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
