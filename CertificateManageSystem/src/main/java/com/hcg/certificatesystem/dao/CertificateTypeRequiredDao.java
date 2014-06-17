package com.hcg.certificatesystem.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultDept;
import com.bstek.bdf2.core.model.DefaultPosition;
import com.bstek.bdf2.core.model.DefaultUser;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.hcg.certificatesystem.model.Certificate;
import com.hcg.certificatesystem.model.CertificateType;
import com.hcg.certificatesystem.model.CertificateTypeGroup;
import com.hcg.certificatesystem.model.CertificateTypeRequired;
import com.hcg.certificatesystem.model.PositionDept;

@Component
public class CertificateTypeRequiredDao extends HibernateDao {

	@Resource
	PositionDao positionDao;

	@Resource
	CertificateTypeDao certificateTypeDao;

	@Resource
	CertificateDao certificateDao;

	@Resource
	UserDao userDao;

	@Resource
	PositionDeptDao positionDeptDao;

	@DataProvider
	public Collection<CertificateTypeRequired> getAll() {
		return this.query("from CertificateTypeRequired");
	}

	@DataProvider
	public Collection<CertificateTypeRequired> getCertificateTypeRequireds(String deptId) {
		Collection<CertificateTypeRequired> certificateTypeRequireds = null;
		String hqlString = "from " + CertificateTypeRequired.class.getName()
				+ " where position.id in (select positionId from "
				+ PositionDept.class.getName() + " where deptId='" + deptId
				+ "')";
		certificateTypeRequireds=this.query(hqlString);
		return certificateTypeRequireds;
	}

	@DataProvider
	public void getAll(Page<CertificateTypeRequired> page, Criteria criteria) {
		DetachedCriteria dt = this.buildDetachedCriteria(criteria,CertificateTypeRequired.class);
		this.pagingQuery(page, dt);
	}

	@DataProvider
	CertificateTypeRequired getCertificateTypeRequired(String id) {
		List<CertificateTypeRequired> certificateTypeRequireds = this
				.query("from " + CertificateTypeRequired.class.getName()
						+ " where id='" + id + "'");
		CertificateTypeRequired certificateTypeRequired = null;
		if (certificateTypeRequireds.size() > 0) {
			certificateTypeRequired = certificateTypeRequireds.get(0);
		}
		return certificateTypeRequired;
	}

	@DataProvider
	public CertificateTypeRequired getCertificateTypeRequiredByPosition(String positionId) {
		CertificateTypeRequired certificateTypeRequired=null;
		List<CertificateTypeRequired> certificateTypeRequireds = this
				.query("from CertificateTypeRequired where position in (from DefaultPosition where id="
						+ "'" + positionId + "')");
		if (certificateTypeRequireds.size()>0) {
			certificateTypeRequired=certificateTypeRequireds.get(0);
		}
		return certificateTypeRequired;
	}

	@DataProvider
	public Collection<CertificateType> getRequiredCertificateTypesByUser(
			String userId) throws Exception {
		Collection<DefaultPosition> positions = positionDao
				.getPositionsByUser(userId);
		List<CertificateType> requiredCertificateTypes = new ArrayList<CertificateType>();

		for (DefaultPosition position : positions) {
			CertificateTypeRequired certificateTypeRequired = getCertificateTypeRequiredByPosition(position.getId());
			Collection<CertificateType> certificateTypes = certificateTypeRequired.getCertificateTypes();
			
			if (requiredCertificateTypes.size() == 0) {
				List<CertificateType> temp = new ArrayList<CertificateType>();
				for (CertificateType certificateType : certificateTypes) {
					CertificateType targetCertificateType = EntityUtils.toEntity(certificateType);
					EntityUtils.setValue(targetCertificateType, "position",position);
					temp.add(targetCertificateType);
				}
				requiredCertificateTypes.addAll(temp);
			} else {
				List<CertificateType> temp = new ArrayList<CertificateType>();
				for (CertificateType certificateType : certificateTypes) {
					boolean isRepeated = false;

					for (CertificateType certificateType2 : requiredCertificateTypes) {
						if (certificateType.getId() == certificateType2
								.getId()) {
							isRepeated = true;
							break;
						}
					}
					if (isRepeated == false) {
						CertificateType targetCertificateType = EntityUtils.toEntity(certificateType);
						EntityUtils.setValue(targetCertificateType, "position",position);
						temp.add(targetCertificateType);
					}
				}
				requiredCertificateTypes.addAll(temp);
			}
		}

		List<CertificateType> temp0 = new ArrayList<CertificateType>();
		List<CertificateType> temp1 = new ArrayList<CertificateType>();
		List<CertificateType> temp2 = new ArrayList<CertificateType>();

		for (CertificateType certificateType : requiredCertificateTypes) {
			boolean isRepeated = false;

			if (temp0.size() == 0) {
				temp0.add(certificateType);
			} else {
				for (CertificateType certificateType0 : temp0) {
					if ((certificateType.getId()).equals(certificateType0
							.getId())) {
						isRepeated = true;
						break;
					}
				}
				if (isRepeated == false) {
					temp0.add(certificateType);
				}
			}
		}

		temp1.addAll(temp0);

		for (int i = 0; i < temp1.size(); i++) {
			CertificateType certificateType = temp1.get(i);
			CertificateTypeGroup certificateTypeGroup = certificateType
					.getCertificateTypeGroup();
			boolean isHighestLevel = true;

			for (int j = 0; j < temp1.size(); j++) {
				CertificateType certificateType2 = temp1.get(j);
				CertificateTypeGroup certificateTypeGroup2 = certificateType2
						.getCertificateTypeGroup();
				if ((certificateTypeGroup.getId().equals(certificateTypeGroup2
						.getId())) && (j != i)) {
					int level1 = certificateType.getLevel();
					int level2 = certificateType2.getLevel();

					if (certificateTypeGroup.getSortType().equals("升序")) {
						if (level1 < level2) {
							isHighestLevel = false;
							break;
						}
					} else {
						if (level1 > level2) {
							isHighestLevel = false;
							break;
						}
					}
				} else {
					continue;
				}
			}

			if (isHighestLevel == true) {
				temp2.add(certificateType);
			}
		}

		return requiredCertificateTypes;
	}
	
	@DataResolver
	public void deleteRequiredCertificateTypes(CertificateType certificateType,CertificateTypeRequired certificateTypeRequired) {
		Session session = this.getSessionFactory().openSession();
		try {
			Collection<CertificateType> certificateTypes = certificateTypeRequired.getCertificateTypes();
			Collection<CertificateType> certificateTypesDeleted = new ArrayList<CertificateType>();

			for (CertificateType certificateType2 : certificateTypes) {
				if (certificateType2.getId().equals(certificateType.getId())) {
					certificateTypesDeleted.add(certificateType2);
				}
			}

			certificateTypes.removeAll(certificateTypesDeleted);
			certificateTypeRequired.setCertificateTypes(certificateTypes);
			session.merge(certificateTypeRequired);// 如果数据库中没有该条记录，则会被创建;如果有该条记录，但内容有变，则会更新
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}

	@DataProvider
	public Collection<CertificateType> getNotYetHasCertificateTypes(
			String userId) throws Exception {
		Collection<CertificateType> requiredCertificateTypes = getRequiredCertificateTypesByUser(userId);
		Collection<Certificate> cerdtificates = certificateDao.getCertificatesByUser(userId);
		DefaultUser user = userDao.getUser(userId);
		DefaultDept dept = userDao.getUserDept(userId);

		Collection<CertificateType> notYetHasCertificateTypes = new ArrayList<CertificateType>();

		for (CertificateType requiredCertificateType : requiredCertificateTypes) {
			boolean isHas = false;

			for (Certificate certificate : cerdtificates) {
				certificate.getCertificateType();
				if ((certificate.getCertificateType().getId())
						.equals(requiredCertificateType.getId())) {
					int level1 = certificate.getCertificateType().getLevel();
					int level2 = requiredCertificateType.getLevel();

					if (requiredCertificateType.getCertificateTypeGroup()
							.getSortType().equals("升序")) {
						if (level1 >= level2) {
							isHas = true;
							break;
						}
					} else {
						if (level1 <= level2) {
							isHas = true;
							break;
						}
					}
				} else {
					continue;
				}
			}

			if (isHas == false) {
				CertificateType targetCertificateType = EntityUtils.toEntity(requiredCertificateType);
				EntityUtils.setValue(targetCertificateType, "username",user.getUsername());
				EntityUtils.setValue(targetCertificateType, "cname",user.getCname());
				EntityUtils.setValue(targetCertificateType, "dept",dept.getName());
				notYetHasCertificateTypes.add(targetCertificateType);
			}
		}

		return notYetHasCertificateTypes;
	}

	@DataProvider
	public Collection<CertificateType> getAllNotYetHasCertificateTypesByDept(String deptId)
			throws Exception {
		Collection<DefaultUser> users=userDao.getUsersByDepartment(deptId);
		Collection<CertificateType> allCertificateTypes = new ArrayList<CertificateType>();
		for (DefaultUser user : users) {
			allCertificateTypes.addAll(getNotYetHasCertificateTypes(user
					.getUsername()));
		}

		return allCertificateTypes;
	}
	
	

	@DataProvider
	public Collection<CertificateType> getCertificateTypes(String certificateTypeRequiredId) {
		List<CertificateTypeRequired> certificateTypeRequireds = this.query("from CertificateTypeRequired where id='" + certificateTypeRequiredId + "'");
		CertificateTypeRequired certificateTypeRequired = new CertificateTypeRequired();
		if (certificateTypeRequireds.size() > 0) {
			certificateTypeRequired = certificateTypeRequireds.get(0);
		}
		return certificateTypeRequired.getCertificateTypes();
	}
	
	@DataResolver
	public void saveRequiredCertificateTypes(Collection<CertificateType> certificateTypes,String certificateTypeRequiredId) {
		Session session = this.getSessionFactory().openSession();
		CertificateTypeRequired certificateTypeRequired=this.getCertificateTypeRequired(certificateTypeRequiredId);
		Collection<CertificateType> oldCertificateTypes=certificateTypeRequired.getCertificateTypes();
		try {
			for (CertificateType certificateType : certificateTypes) {
				EntityState state = EntityUtils.getState(certificateType);
				if (state.equals(EntityState.NEW)) {
					CertificateType tempCertificateType=certificateTypeDao.getCertificateType(certificateType.getId());
					oldCertificateTypes.add(tempCertificateType);
				}else if (state.equals(EntityState.DELETED)) {
					for (CertificateType certificateType2 : oldCertificateTypes) {
						if (certificateType2.getId().equals(certificateType.getId())) {
							oldCertificateTypes.remove(certificateType2);
							break;
						}
					}
				}
			}
			certificateTypeRequired.setCertificateTypes(oldCertificateTypes);
			session.merge(certificateTypeRequired);// 如果数据库中没有该条记录，则会被创建;如果有该条记录，但内容有变，则会更新
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}

	@DataResolver
	public void saveCertificateTypeRequireds(Collection<CertificateTypeRequired> certificateTypeRequireds) {
		Session session = this.getSessionFactory().openSession();
		try {
			for (CertificateTypeRequired certificateTypeRequired : certificateTypeRequireds) {
				EntityState state = EntityUtils.getState(certificateTypeRequired);
				if (state.equals(EntityState.NEW)) {
					session.save(certificateTypeRequired);
				} else if (state.equals(EntityState.MODIFIED)) {
					session.update(certificateTypeRequired);
				} else if (state.equals(EntityState.DELETED)) {
					certificateTypeRequired.setCertificateTypes(null);
					certificateTypeRequired.setPosition(null);
					session.delete(certificateTypeRequired);
				} else if (state.equals(EntityState.NONE)) {
					Collection<CertificateType> certificateTypes = certificateTypeRequired.getCertificateTypes();
					Collection<CertificateType> tempCertificateTypes = new HashSet<CertificateType>();

					for (CertificateType certificateType : certificateTypes) {
						tempCertificateTypes.add(certificateTypeDao.getCertificateType(certificateType.getId()));
					}

					certificateTypes = tempCertificateTypes;
					certificateTypeRequired.setCertificateTypes(certificateTypes);
					session.merge(certificateTypeRequired);// 如果数据库中没有该条记录，则会被创建;如果有该条记录，但内容有变，则会更新
				}

			}
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			session.flush();
			session.close();
		}
	}

	@DataProvider
	public void saveCertificateTypeRequired(CertificateTypeRequired certificateTypeRequired){
		Session session=this.getSessionFactory().openSession();
		try {
			EntityState state=EntityUtils.getState(certificateTypeRequired);
			if(state.equals(EntityState.NEW)){
				session.save(certificateTypeRequired);
			}else if (state.equals(EntityState.MODIFIED)) {
				session.update(certificateTypeRequired);
			}else if (state.equals(EntityState.DELETED)) {
				certificateTypeRequired.setCertificateTypes(null);
				certificateTypeRequired.setPosition(null);
				session.delete(certificateTypeRequired);
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
