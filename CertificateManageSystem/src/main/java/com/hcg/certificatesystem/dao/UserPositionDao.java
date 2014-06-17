package com.hcg.certificatesystem.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.core.model.UserPosition;
import com.bstek.bdf2.core.orm.hibernate.HibernateDao;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;

@Component
public class UserPositionDao extends HibernateDao {
	@DataProvider
	public Collection<UserPosition> getAll() {
		String hqlString = "from " + UserPosition.class.getName();
		Collection<UserPosition> userPositions = this.query(hqlString);
		return userPositions;
	}

	@DataProvider
	public Collection<UserPosition> getUserPositionsByPosition(String positionId) {
		String hqlString = "from " + UserPosition.class.getName()
				+ " where positionId='" + positionId + "'";
		Collection<UserPosition> userPositions = this.query(hqlString);
		return userPositions;
	}

	@DataProvider
	public Collection<UserPosition> getUserPositionsByUser(String username) {
		String hqlString = "from " + UserPosition.class.getName()
				+ " where username='" + username + "'";
		List<UserPosition> userPositions = this.query(hqlString);
		return userPositions;
	}

	@DataProvider
	public UserPosition getUserPosition(String id) {
		String hqlString = "from " + UserPosition.class.getName() + " where id='" + id + "'";
		List<UserPosition> userPositions = this.query(hqlString);
		UserPosition userPosition = null;
		if (userPositions.size() > 0) {
			userPosition = userPositions.get(0);
		}
		return userPosition;
	}

	@DataResolver
	public void deleteUserPositionsByUser(String username) {
		Session session = getSessionFactory().openSession();
		try {
			session.createQuery("delete " + UserPosition.class.getName() + " u where u.username = :username").setString("username", username).executeUpdate();
		} finally {
			session.flush();
			session.close();
		}
	}
}
