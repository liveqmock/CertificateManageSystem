package com.hcg.certificatesystem.dao;

import java.util.Collection;
import java.util.Set;

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
import com.hcg.certificatesystem.model.CertificateParameter;

@Component
public class CertificateParameterDao extends HibernateDao {

	@DataProvider
	public Collection<CertificateParameter> getAll(){
		Collection<CertificateParameter> certificateParameters=this.query("from CertificateParameter");
		return certificateParameters;
	}
	
	@DataProvider 
	public void getAll(Page<CertificateParameter> page, Criteria criteria)
			throws Exception{
		DetachedCriteria dt=this.buildDetachedCriteria(criteria,CertificateParameter.class);
		this.pagingQuery(page, dt);
	}
	
	@DataResolver
	public void saveCertificateParameters(Set<CertificateParameter> certificateParameters){
		Session session=this.getSessionFactory().openSession();
		try {
			for (CertificateParameter certificateParameter : certificateParameters) {
				EntityState state=EntityUtils.getState(certificateParameter);
				if(state.equals(EntityState.NEW)){
					session.save(certificateParameter);
				}else if (state.equals(EntityState.MODIFIED)) {
					session.update(certificateParameter);
				}else if (state.equals(EntityState.DELETED)) {
					session.delete(certificateParameter);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	@DataResolver
	public void deleteCertificateParameters(Set<CertificateParameter> certificateParameters){
		Session session=this.getSessionFactory().openSession();
		try {
			for (CertificateParameter certificateParameter : certificateParameters) {
				session.delete(certificateParameter);
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
	
	
}
