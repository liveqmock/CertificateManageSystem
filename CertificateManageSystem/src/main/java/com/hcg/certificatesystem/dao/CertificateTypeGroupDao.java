package com.hcg.certificatesystem.dao;

import java.util.Collection;
import java.util.List;

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
import com.hcg.certificatesystem.model.CertificateType;
import com.hcg.certificatesystem.model.CertificateTypeGroup;

@Component
public class CertificateTypeGroupDao extends HibernateDao {

	@Resource
	private CertificateTypeDao certificateTypeDao;
	
	@DataProvider
	public Collection<CertificateTypeGroup> getAll() {
		Collection<CertificateTypeGroup> certificateTypeGroups = this
				.query("from CertificateTypeGroup");
		return certificateTypeGroups;
	}

	@DataProvider
	public void getAll(Page<CertificateTypeGroup> page, Criteria criteria)
			throws Exception {
		/*
		 * String hqlString = "from " + Product.class.getName() + "";
		 * this.pagingQuery(page,hqlString,"select count(*)"+hqlString);
		 */
		DetachedCriteria dt = this.buildDetachedCriteria(criteria,CertificateTypeGroup.class);
		this.pagingQuery(page, dt);
	}
	
	@DataProvider
	public Collection<CertificateType> getCertificateTypes(String id){
		List<CertificateTypeGroup> certificateTypeGroups=this.query("from CertificateTypeGroup where id="+"'"+id+"'");
		CertificateTypeGroup certificateTypeGroup = null;
		if(certificateTypeGroups.size()!=0){
			certificateTypeGroup=certificateTypeGroups.get(0);
		}	
		return certificateTypeGroup.getCertificateTypes();
	}

	@DataResolver
	public void saveCertificateTypeGroups(
			Collection<CertificateTypeGroup> certificateTypeGroups) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (CertificateTypeGroup certificateTypeGroup : certificateTypeGroups) {
				EntityState state = EntityUtils.getState(certificateTypeGroup);
				if (state.equals(EntityState.NEW)) {
					session.save(certificateTypeGroup);
				} else if (state.equals(EntityState.MODIFIED)) {
					session.update(certificateTypeGroup);
				} else if (state.equals(EntityState.DELETED)) {
					session.delete(certificateTypeGroup);
				}
				if(EntityState.isVisible(state)){
					Collection<CertificateType> certificateTypes=certificateTypeGroup.getCertificateTypes();
					if(certificateTypes!=null){
						for (CertificateType certificateType : certificateTypes) {
							if(EntityState.NEW.equals(EntityUtils.getState(certificateType))){
								certificateType.setCertificateTypeGroup(certificateTypeGroup);
							}
							if(EntityState.MODIFIED.equals(EntityUtils.getState(certificateType))){
								certificateType.setCertificateTypeGroup(certificateTypeGroup);
							}
						}
						certificateTypeDao.saveCertificateTypes(certificateTypes);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}

}
