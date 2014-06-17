package com.hcg.certificatesystem.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.UserDept;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;

@Component
public class UserDeptDao extends HibernateDao {
	@DataProvider
	public Collection<UserDept> getAll() {
		String hqlString = "from " + UserDept.class.getName();
		Collection<UserDept> userDepts = this.query(hqlString);
		return userDepts;
	}

	@DataProvider
	public Collection<UserDept> getUserDeptsByDept(String deptId) {
		String hqlString = "from " + UserDept.class.getName()
				+ " where deptId='" + deptId + "'";
		Collection<UserDept> userDepts = this.query(hqlString);
		return userDepts;
	}

	@DataProvider
	public UserDept getUserDeptByUser(String username) {
		String hqlString = "from " + UserDept.class.getName()
				+ " where username='" + username + "'";
		List<UserDept> userDepts = this.query(hqlString);
		UserDept userDept = null;
		if (userDepts.size() > 0) {
			userDept = userDepts.get(0);
		}
		return userDept;
	}

	@DataProvider
	public UserDept getUserDept(String id) {
		String hqlString = "from " + UserDept.class.getName() + " where id='"
				+ id + "'";
		List<UserDept> userDepts = this.query(hqlString);
		UserDept userDept = null;
		if (userDepts.size() > 0) {
			userDept = userDepts.get(0);
		}
		return userDept;
	}

	@DataResolver
	public void deleteUserDeptByUser(String username) {
		Session session = getSessionFactory().openSession();
		try {
			session.createQuery("delete " + UserDept.class.getName()+ " u where u.username = :username").setString("username", username).executeUpdate();
		} finally {
			session.flush();
			session.close();
		}
	}
}
