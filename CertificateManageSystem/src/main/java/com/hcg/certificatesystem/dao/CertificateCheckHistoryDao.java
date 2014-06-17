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
import com.hcg.certificatesystem.model.CertificateCheckHistory;
import com.hcg.certificatesystem.model.CertificateType;

@Component
public class CertificateCheckHistoryDao extends HibernateDao {

	@Resource
	CertificateTypeDao certificateTypeDao;

	@Resource
	CertificateDao certificateDao;
	
	@Resource
	UserDao userDao;
	
	@SuppressWarnings("deprecation")
	@DataProvider
	public ArrayList<CertificateCheckHistory> getCertificateCheckHistories(String certificateId) throws Exception{
		List<Certificate> certificates = this.query("from Certificate where id=" + "'" + certificateId + "'");
		Certificate certificate = null;
		if (certificates.size() > 0) {
			certificate = certificates.get(0);
		}
		
		CertificateType certificateType=certificate.getCertificateType();
		DefaultUser user=certificate.getUser();
		
		Set<CertificateCheckHistory> certificateCheckHistories=certificate.getCertificateCheckHistories();
		ArrayList<CertificateCheckHistory> certificateCheckHistoriesReturn=new ArrayList<CertificateCheckHistory>();
		
		if (certificateCheckHistories.size()==0) {
			Date beginDate=certificate.getBeginDate();//获取证书时间
			int checkPeriod=certificateType.getCheckPeriod();//复审周期(单位：年)，有复审的都有周期
			int validPeriod=certificateType.getValidPeriod();//有效期（单位：年），复审时间点都应在有效期内
			int maleExceedByAge=certificateType.getMaleExceedByAge();
			int femaleExceedByAge=certificateType.getFemaleExeedByAge();

			boolean isHasCheckPeriod=certificateType.isHasCheckPeriod();
			boolean isExceedByAge=certificateType.isHasExceedByAge();
			
			int lifeOfCertificate=80;//证书生存期限
			int periodsCount=0;
			int sortNumber=1;
			Date startDate=beginDate;
			Date currentDate=new Date();
			int spanYears=0;
			Date lastEndDate=new Date();

			if(isHasCheckPeriod==true){
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
					
					spanYears=lastEndDate.getYear()-beginDate.getYear();
					if ((lastEndDate.getMonth()-beginDate.getMonth())==0) {
						if ((lastEndDate.getDate()-beginDate.getDate())<0) {
							spanYears=spanYears-1;
						}
					}else if ((lastEndDate.getMonth()-beginDate.getMonth())<0) {
						spanYears=spanYears-1;
					}
					
					periodsCount=spanYears/checkPeriod;	
				}else {
					periodsCount=validPeriod/checkPeriod;
				}
				
				for (int i = 1; i <= periodsCount; i++) {
					CertificateCheckHistory certificateCheckHistory=new CertificateCheckHistory();
					Date endDate=new Date();
					endDate.setYear(startDate.getYear()+checkPeriod);
					endDate.setMonth(startDate.getMonth());
					endDate.setDate(startDate.getDate());
					
					certificateCheckHistory.setBeginDate(startDate);
					certificateCheckHistory.setEndDate(endDate);
					certificateCheckHistory.setSortNumber(sortNumber);
	
					String status=null;
					if (currentDate.before(startDate)) {
						status="未开始";
					}else if (currentDate.after(endDate)) {
						status="已完成";
					}else if(currentDate.after(startDate)&&currentDate.before(endDate)){
						status="进行中";
					}
					certificateCheckHistory.setStatus(status);
					certificateCheckHistories.add(certificateCheckHistory);	
					
					startDate=endDate;
					sortNumber=sortNumber+1;
				}
				
				if (((spanYears%checkPeriod)>0)&&(isExceedByAge==true)) {
					CertificateCheckHistory certificateCheckHistory=new CertificateCheckHistory(); 
					certificateCheckHistory.setBeginDate(startDate);
					certificateCheckHistory.setEndDate(lastEndDate);
					certificateCheckHistory.setSortNumber(sortNumber);
					
					String status=null;
					if (currentDate.before(startDate)) {
						status="未开始";
					}else if (currentDate.after(lastEndDate)) {
						status="已完成";
					}else if(currentDate.after(startDate)&&currentDate.before(lastEndDate)){
						status="进行中";
					}
					certificateCheckHistory.setStatus(status);
					certificateCheckHistories.add(certificateCheckHistory);	
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
				
				CertificateCheckHistory certificateCheckHistory=new CertificateCheckHistory();
				certificateCheckHistory.setBeginDate(startDate);
				certificateCheckHistory.setEndDate(lastEndDate);
				certificateCheckHistory.setSortNumber(sortNumber);

				String status=null;
				if (currentDate.before(startDate)) {
					status="未开始";
				}else if (currentDate.after(lastEndDate)) {
					status="已完成";
				}else if(currentDate.after(startDate)&&currentDate.before(lastEndDate)){
					status="进行中";
				}
				certificateCheckHistory.setStatus(status);
				certificateCheckHistories.add(certificateCheckHistory);	
			}
		}else{
			Date currentDate=new Date();
			
			for (CertificateCheckHistory certificateCheckHistory : certificateCheckHistories) {
				String status=null;
				if (currentDate.before(certificateCheckHistory.getBeginDate())) {
					status="未开始";
				}else if (currentDate.after(certificateCheckHistory.getEndDate())) {
					status="已完成";
				}else if(currentDate.after(certificateCheckHistory.getBeginDate())&&currentDate.before(certificateCheckHistory.getEndDate())){
					status="进行中";
				}
				certificateCheckHistory.setStatus(status);
			}
		}
		
		certificate = EntityUtils.toEntity(certificate);
		EntityUtils.setState(certificate, EntityState.MODIFIED);
		certificate.setCertificateCheckHistories(certificateCheckHistories);
		Set<Certificate> updateCertificates = new HashSet<Certificate>();
		updateCertificates.add(certificate);
		certificateDao.saveCertificates(updateCertificates);
		
		int sortNums=certificateCheckHistories.size();
		ArrayList<CertificateCheckHistory> temp=new ArrayList<CertificateCheckHistory>();
		temp.addAll(certificateCheckHistories);
		for (int i = 1; i <=sortNums; i++) {
			for (CertificateCheckHistory certificateCheckHistory : temp) {
				if (certificateCheckHistory.getSortNumber()==i) {
					certificateCheckHistoriesReturn.add(certificateCheckHistory);
					break;
				}
			}
		}//将无序的set转换为有序的arraylist，set继承于collection接口，set中元素以key对应，并非有序的

		return certificateCheckHistoriesReturn;
	}
	
	@DataProvider
	public Collection<CertificateCheckHistory> getAllCheckPeriodsInCurrentByCertificateType(String certificateTypeId) throws Exception{
		Collection<Certificate> certificates=certificateDao.getCertificatesByCertificateType(certificateTypeId);
		Collection<CertificateCheckHistory> currentCertificateCheckHistories=new ArrayList<CertificateCheckHistory>();
		Date currentDate=new Date();
		
		for (Certificate certificate : certificates) {
			Collection<CertificateCheckHistory> thisCertificateCheckHistories=new ArrayList<CertificateCheckHistory>();
			if (certificate.getCertificateValidHistories().size()==0) {
				thisCertificateCheckHistories=getCertificateCheckHistories(certificate.getId());
			}else {
				thisCertificateCheckHistories=certificate.getCertificateCheckHistories();
			}

			CertificateType certificateType=certificate.getCertificateType();
			DefaultUser user=certificate.getUser();
			DefaultDept dept=userDao.getUserDept(certificate.getUser().getUsername());
			
			for (CertificateCheckHistory certificateCheckHistory : thisCertificateCheckHistories) {
				if (currentDate.before(certificateCheckHistory.getEndDate())&&currentDate.after(certificateCheckHistory.getBeginDate())) {
					CertificateCheckHistory targetCertificateValidHistory=EntityUtils.toEntity(certificateCheckHistory);
					EntityUtils.setValue(targetCertificateValidHistory, "certificateType",certificateType);
					EntityUtils.setValue(targetCertificateValidHistory, "certificate",certificate);
					EntityUtils.setValue(targetCertificateValidHistory, "user",user);
					EntityUtils.setValue(targetCertificateValidHistory, "dept",dept);
					currentCertificateCheckHistories.add(targetCertificateValidHistory);
					break;
				}
			}
		}
		
		return currentCertificateCheckHistories;
	}
	
	@DataResolver
	public void deleteCertificateCheckHistories(Set<CertificateCheckHistory> certificateCheckHistories) {
		Session session = this.getSessionFactory().openSession();	
		try {
			for (CertificateCheckHistory certificateCheckHistory : certificateCheckHistories) {
				session.delete(certificateCheckHistory);
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
