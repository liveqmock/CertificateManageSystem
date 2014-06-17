package com.hcg.certificatesystem.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.CoreHibernateDao;
import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.core.exception.NoneLoginException;
import com.bstek.bdf2.core.model.DefaultDept;
import com.bstek.bdf2.core.model.DefaultUser;
import com.bstek.bdf2.core.model.GroupMember;
import com.bstek.bdf2.core.model.RoleMember;
import com.bstek.bdf2.core.model.UserDept;
import com.bstek.bdf2.core.model.UserPosition;
import com.bstek.bdf2.core.service.IGroupService;
import com.bstek.bdf2.core.service.IRoleService;
import com.bstek.bdf2.core.service.MemberType;
import com.bstek.bdf2.core.service.impl.DefaultGroupService;
import com.bstek.bdf2.core.service.impl.DefaultRoleService;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.provider.Criteria;
import com.bstek.dorado.data.provider.Page;
import com.hcg.certificatesystem.model.Certificate;

@Component
public class UserDao extends CoreHibernateDao {
	
	@Resource
	UserDeptDao userDeptDao;
	
	@Resource
	UserPositionDao userPositionDao;
	
	@Resource
	CertificateDao certificateDao;
	
	private IRoleService roleService;
	private IGroupService groupService;

	private ShaPasswordEncoder passwordEncoder;//���ܷ�ʽ

	@DataProvider
	public Collection<DefaultUser> getAll() {
		return this.query("from DefaultUser");
	}

	@DataProvider
	public void getAll(Page<DefaultUser> page, Criteria criteria) {
		DetachedCriteria dt = this.buildDetachedCriteria(criteria,
				DefaultUser.class);
		this.pagingQuery(page, dt);
	}

	@DataProvider
	public void getUsers(Page<DefaultUser> page, String cname) throws Exception {
		if (null != cname) {
			String hqlString = "from " + DefaultUser.class.getName()
					+ " where cname like '%" + cname + "%'";
			this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
		} else {
			String hqlString = "from " + DefaultUser.class.getName();
			this.pagingQuery(page, hqlString, "select count(*)" + hqlString);
		}
	}

	@DataProvider
	public Collection<DefaultUser> getUsersByDepartment(String id) {
		List<DefaultUser> users = new ArrayList<DefaultUser>();
		if (null != id) {
			users = this
					.query("from DefaultUser where username in (select username from UserDept where deptId="
							+ "'" + id + "')");
		}
		return users;
	}

	@DataProvider
	public Collection<DefaultUser> getUserByPosition(String id) {
		List<DefaultUser> users = this
				.query("from DefaultUser where username in (select username from UserPosition where positionId="
						+ "'" + id + "')");
		return users;
	}

	@DataProvider
	public DefaultDept getUserDept(String id) {
		String hqlString = "from " + DefaultDept.class.getName()
				+ " where id in (select deptId from "
				+ UserDept.class.getName() + " where username='" + id + "')";
		List<DefaultDept> defaultDepts = this.query(hqlString);
		DefaultDept defaultDept = null;

		if (defaultDepts.size() > 0) {
			defaultDept = defaultDepts.get(0);
		}
		return defaultDept;
	}

	@DataProvider
	public DefaultUser getUser(String id) {
		String hqlString = "from " + DefaultUser.class.getName()
				+ " where username='" + id + "'";
		List<DefaultUser> defaultUsers = this.query(hqlString);
		DefaultUser user = null;

		if (defaultUsers.size() > 0) {
			user = defaultUsers.get(0);
		}
		return user;
	}

	@DataResolver
	public void saveUsers(Collection<DefaultUser> users) throws Exception{
		IUser loginuser = ContextHolder.getLoginUser();
		if (loginuser == null) {
			throw new NoneLoginException("Please login first!");
		}
		String companyId = loginuser.getCompanyId();
		if (StringUtils.isNotEmpty(getFixedCompanyId())) {
			companyId = getFixedCompanyId();
		}
		Session session = getSessionFactory().openSession();
		try {
			for (DefaultUser user : users) {
				EntityState state = EntityUtils.getState(user);
				if (state.equals(EntityState.NEW)) {
					String salt = String.valueOf(RandomUtils.nextInt(100));//������
					passwordEncoder=new ShaPasswordEncoder();//BDF2��ܼ��ܷ�ʽΪShaPasswordEncoder
					String password = this.passwordEncoder.encodePassword(user.getPassword(), salt);// ������Ѽ��ܵ�����
					user.setPassword(password);
					user.setSalt(salt);
					user.setCompanyId(companyId);
					session.save(user);
				} else if (state.equals(EntityState.MODIFIED)) {
					session.update(user);
				} else if (state.equals(EntityState.DELETED)) {
					if (this.query("from "+GroupMember.class.getName()+" where username='"+user.getUsername()+"'").size()>0) {
						groupService=new DefaultGroupService();
						groupService.deleteGroupMemeber(user.getUsername(),MemberType.User);
					}
					if (this.query("from "+RoleMember.class.getName()+" where username='"+user.getUsername()+"'").size()>0) {
						roleService=new DefaultRoleService();
						roleService.deleteRoleMemeber(user.getUsername(),MemberType.User);
					}
					if (this.query("from "+UserDept.class.getName()+" where username='"+user.getUsername()+"'").size()>0) {
						userDeptDao.deleteUserDeptByUser(user.getUsername());
					}
					if (this.query("from "+UserPosition.class.getName()+" where username='"+user.getUsername()+"'").size()>0) {
						userPositionDao.deleteUserPositionsByUser(user.getUsername());
					}
					
					Collection<Certificate> certificates=certificateDao.getCertificatesByUser(user.getUsername());
					if (certificates.size()>0) {
						certificateDao.deleteCertificates(certificates);
					}
					
					session.delete(user);
				}
			}
		} finally {
			session.flush();
			session.close();
		}
	}
}
