package com.hcg.certificatesystem.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.DefaultDept;
import com.bstek.bdf2.core.model.DefaultUser;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.hcg.certificatesystem.model.Certificate;
import com.hcg.certificatesystem.model.CertificateType;
import com.hcg.certificatesystem.model.CertificateValidHistory;

@Component
public class CertificateValidHistoryDao extends HibernateDao {

	@Resource
	CertificateTypeDao certificateTypeDao;

	@Resource
	CertificateDao certificateDao;
	
	@Resource
	UserDao userDao;
	
	@SuppressWarnings("deprecation")
	@DataProvider
	public ArrayList<CertificateValidHistory> getCertificateValidHistories(String certificateId) throws Exception{
		List<Certificate> certificates = this.query("from Certificate where id=" + "'" + certificateId + "'");
		Certificate certificate = null;
		if (certificates.size() > 0) {
			certificate = certificates.get(0);
		}
		
		CertificateType certificateType=certificate.getCertificateType();
		DefaultUser user=certificate.getUser();
		
		Set<CertificateValidHistory> certificateValidHistories=certificate.getCertificateValidHistories();
		ArrayList<CertificateValidHistory> certificateValidHistoriesReturn=new ArrayList<CertificateValidHistory>();
		
		if (certificateValidHistories.size()==0) {
			Date beginDate=certificate.getBeginDate();//获取证书时间
			int validPeriod=certificateType.getValidPeriod();//有效周期(单位：年)
			int maleExceedByAge=certificateType.getMaleExceedByAge();
			int femaleExceedByAge=certificateType.getFemaleExeedByAge();
			
			boolean isHasValidPeriod=certificateType.isHasValidPeriod();
			boolean isExceedByAge=certificateType.isHasExceedByAge();
			
			int lifeOfCertificate=80;//证书生存期限
			int sortNumber=1;
			Date startDate=beginDate;
			Date currentDate=new Date();
			Date lastEndDate=new Date();

			if(isHasValidPeriod==true){
				Date validEndDate=new Date();
				validEndDate.setYear(beginDate.getYear()+validPeriod);
				validEndDate.setMonth(beginDate.getMonth());
				validEndDate.setDate(beginDate.getDate());
				
				if (isExceedByAge==true) {
					if(user.isMale()){
						lastEndDate.setYear(user.getBirthday().getYear()+maleExceedByAge);	
					}else{
						lastEndDate.setYear(user.getBirthday().getYear()+femaleExceedByAge);
						
					}
					
					lastEndDate.setMonth(user.getBirthday().getMonth());
					lastEndDate.setDate(user.getBirthday().getDate());

					if (lastEndDate.after(validEndDate)) {
						lastEndDate=validEndDate;
					}
				}else {
					lastEndDate=validEndDate;
				}			
			}else {
				if (isExceedByAge==true) {
					if(user.isMale()){
						lastEndDate.setYear(user.getBirthday().getYear()+maleExceedByAge);	
					}else{
						lastEndDate.setYear(user.getBirthday().getYear()+femaleExceedByAge);
						
					}
					
					lastEndDate.setMonth(user.getBirthday().getMonth());
					lastEndDate.setDate(user.getBirthday().getDate());
				}else {
					lastEndDate.setYear(beginDate.getYear()+lifeOfCertificate);	
					lastEndDate.setMonth(beginDate.getMonth());
					lastEndDate.setDate(beginDate.getDate());
				}
			}
			
			CertificateValidHistory certificateValidHistory=new CertificateValidHistory();
			certificateValidHistory.setBeginDate(startDate);
			certificateValidHistory.setEndDate(lastEndDate);
			certificateValidHistory.setSortNumber(sortNumber);
			String status=null;
			if (currentDate.before(startDate)) {
				status="未开始";
			}else if (currentDate.after(lastEndDate)) {
				status="已完成";
			}else if(currentDate.after(startDate)&&currentDate.before(lastEndDate)){
				status="进行中";
			}
			
			certificateValidHistory.setStatus(status);
			certificateValidHistories.add(certificateValidHistory);	
		}else{
			Date currentDate=new Date();
			
			for (CertificateValidHistory certificateValidHistory : certificateValidHistories) {
				String status=null;
				if (currentDate.before(certificateValidHistory.getBeginDate())) {
					status="未开始";
				}else if (currentDate.after(certificateValidHistory.getEndDate())) {
					status="已完成";
				}else if(currentDate.after(certificateValidHistory.getBeginDate())&&currentDate.before(certificateValidHistory.getEndDate())){
					status="进行中";
				}
				certificateValidHistory.setStatus(status);
			}
		}
		
		certificate = EntityUtils.toEntity(certificate);
		EntityUtils.setState(certificate, EntityState.MODIFIED);
		certificate.setCertificateValidHistories(certificateValidHistories);
		Set<Certificate> updateCertificates = new HashSet<Certificate>();
		updateCertificates.add(certificate);
		certificateDao.saveCertificates(updateCertificates);
		
		int sortNums=certificateValidHistories.size();
		ArrayList<CertificateValidHistory> temp=new ArrayList<CertificateValidHistory>();
		temp.addAll(certificateValidHistories);
		for (int i = 1; i <=sortNums; i++) {
			for (CertificateValidHistory certificateValidHistory : temp) {
				if (certificateValidHistory.getSortNumber()==i) {
					certificateValidHistoriesReturn.add(certificateValidHistory);
					break;
				}
			}
		}//将无序的set转换为有序的arraylist，set继承于collection接口，set中元素以key对应，并非有序的

		return certificateValidHistoriesReturn;
	}
	
	@DataProvider
	public CertificateValidHistory getCertificateValidHistory(String id){
		List<CertificateValidHistory> certificateValidHistories=this.query("from CertificateValidHistory where where id=" + "'" + id + "'");
		CertificateValidHistory certificateValidHistory=null;
		if (certificateValidHistories.size()>0) {
			certificateValidHistory=certificateValidHistories.get(0);
		}
		return certificateValidHistory;
	}
	
	@DataProvider
	public Collection<CertificateValidHistory> getAllValidPeriodsInCurrentByCertificateType(String certificateTypeId) throws Exception{
		Collection<Certificate> certificates=certificateDao.getCertificatesByCertificateType(certificateTypeId);
		Collection<CertificateValidHistory> currentCertificateValidHistories=new ArrayList<CertificateValidHistory>();
		Date currentDate=new Date();
		
		for (Certificate certificate : certificates) {
			Collection<CertificateValidHistory> thisCertificateValidHistories=new ArrayList<CertificateValidHistory>();
			if (certificate.getCertificateValidHistories().size()==0) {
				thisCertificateValidHistories=getCertificateValidHistories(certificate.getId());
			}else {
				thisCertificateValidHistories=certificate.getCertificateValidHistories();
			}

			CertificateType certificateType=certificate.getCertificateType();
			DefaultUser user=certificate.getUser();
			DefaultDept dept=userDao.getUserDept(certificate.getUser().getUsername());
			
			for (CertificateValidHistory certificateValidHistory : thisCertificateValidHistories) {
				if (currentDate.before(certificateValidHistory.getEndDate())&&currentDate.after(certificateValidHistory.getBeginDate())) {
					CertificateValidHistory targetCertificateValidHistory=EntityUtils.toEntity(certificateValidHistory);
					EntityUtils.setValue(targetCertificateValidHistory, "certificateType",certificateType);
					EntityUtils.setValue(targetCertificateValidHistory, "certificate",certificate);
					EntityUtils.setValue(targetCertificateValidHistory, "user",user);
					EntityUtils.setValue(targetCertificateValidHistory, "dept",dept);
					currentCertificateValidHistories.add(targetCertificateValidHistory);
					break;
				}
			}
		}
		
		return currentCertificateValidHistories;
	}
	
	@DataResolver
	public void saveCertificateValidHistories(Set<CertificateValidHistory> certificateValidHistories){
		Session session = this.getSessionFactory().openSession();
		try {
			for (CertificateValidHistory certificateValidHistory : certificateValidHistories) {
				EntityState state = EntityUtils.getState(certificateValidHistory);
				if (state.equals(EntityState.NEW)) {
					session.save(certificateValidHistory);
				} else if (state.equals(EntityState.MODIFIED)) {
					session.update(certificateValidHistory);
				} else if (state.equals(EntityState.DELETED)) {
					session.delete(certificateValidHistory);
				}
				session.save(certificateValidHistory);
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
	public void deleteCertificateValidHistories(Set<CertificateValidHistory> certificateValidHistories) {
		Session session = this.getSessionFactory().openSession();	
		try {
			for (CertificateValidHistory certificateValidHistory : certificateValidHistories) {
				session.delete(certificateValidHistory);
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


