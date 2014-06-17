package com.hcg.certificatesystem.dao;

import java.util.Collection;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.orm.ParseResult;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.hcg.certificatesystem.model.CertificateOrganization;

@Component
public class CertificateOrganizationDao extends HibernateDao{

	@Resource
	private CertificateTypeDao certificateTypeDao;
	
	@DataProvider
	public Collection<CertificateOrganization> getAll(){
		Collection<CertificateOrganization> certificateOrganizations=this.query("from CertificateOrganization");
		return certificateOrganizations;
	}
	
	@DataProvider
	public void getAll(Page<CertificateOrganization> page,Criteria criteria) throws Exception{
		String hqlString = "from " + CertificateOrganization.class.getName() + " u";
		ParseResult parseResult=this.parseCriteria(criteria, true, "u");
		if (parseResult!=null) {
			hqlString+=" where"+parseResult.getAssemblySql().toString();
			this.pagingQuery(page,hqlString,"select count(*)"+hqlString,parseResult.getValueMap());
		}else {
			this.pagingQuery(page,hqlString,"select count(*)"+hqlString);
		}
	}
	
	@DataResolver
	public void saveCertificateOrganizations(Collection<CertificateOrganization> certificateOrganizations){
		Session session=this.getSessionFactory().openSession();
		try {
			for (CertificateOrganization certificateOrganization : certificateOrganizations) {
				EntityState state=EntityUtils.getState(certificateOrganization);
				if (state.equals(EntityState.NEW)) {
					session.save(certificateOrganization);
				} else if (state.equals(EntityState.MODIFIED)) {
					session.update(certificateOrganization);
				} else if (state.equals(EntityState.DELETED)) {
					session.delete(certificateOrganization);
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}finally{
			session.flush();
			session.close();
		}
	}
}
