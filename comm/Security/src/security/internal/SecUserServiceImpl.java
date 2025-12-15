package security.internal;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import project.Constants;
import project.invest.goods.model.Useraddress;
import project.party.model.Party;
import security.Role;
import security.SecUser;

public class SecUserServiceImpl extends HibernateDaoSupport implements SecUserService {

	private PasswordEncoder passwordEncoder;

	public void saveUser(SecUser user) {
		SecUser db = findUserByLoginName(user.getUsername());
		if (null != db) {
			throw new BusinessException("系统存在相同[系统登录名]！");
		}
		user.setCreateTime(new Date());
		user.setPassword(passwordEncoder.encodePassword(user.getPassword(), user.getUsername()));
		this.getHibernateTemplate().merge(user);
	}
	
	public void deleteUser(SecUser user) {
		SecUser db = this.findUserByLoginName(user.getUsername());
		if (null == db) {
			throw new BusinessException("系统用户登录名不存在！");
		}
		this.getHibernateTemplate().delete(db);
	}

	@SuppressWarnings("unchecked")
	public SecUser findUserByLoginName(String loginName) {
		List<SecUser> users =  (List<SecUser>) this.getHibernateTemplate().find("FROM SecUser WHERE username = ?0" ,new Object[] {loginName});
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}

	@Override
	public Party findUserByPhone(String phone) {
		DetachedCriteria query = DetachedCriteria.forClass(Party.class);
		query.add( Property.forName("phone").eq(phone) );
		List list = getHibernateTemplate().findByCriteria(query,0,1);
		if(list.size()>0){
			return (Party) list.get(0);
		}
		return null;
	}

	@Override
	public SecUser findValidUserByLoginName(String loginName, String[] rolesArrty) {
		SecUser user = findUserByLoginName(loginName);
		if (user == null) {
			return null;
		}

		Set<Role> roles = user.getRoles();
		boolean find = false;
		for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
			Role role = (Role) iterator.next();
			for (int i = 0; i < rolesArrty.length; i++) {
				if (role.getRoleName().equals(rolesArrty[i])) {
					find = true;
				}
			}
		}
		if (!find) {
			return null;
		}
		return user;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void updatePassword(String username, String oldpassword, String password) {
		
		SecUser user = this.findUserByLoginName(username);

		if (user == null) {
			return;
		}
		String oldpassword_encoder = passwordEncoder.encodePassword(oldpassword, user.getUsername());

		// String oldpassword_encoder = oldpassword;

		if (oldpassword_encoder.equals(user.getPassword())) {
			user.setPassword(passwordEncoder.encodePassword(password, user.getUsername()));
			// user.setPassword(password);
			this.getHibernateTemplate().update(user);
		} else {
			throw new BusinessException("旧密码不正确");
		}

	}

	@Override
	public SecUser findUserByPartyId(String partyId) {
		StringBuffer queryString = new StringBuffer(" FROM SecUser where partyId = ?0");
		List<SecUser> list = null;
		list = (List<SecUser>) this.getHibernateTemplate().find(queryString.toString() ,new Object[] {partyId});

		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public SecUser findUserByLoginCode(String loginCode,String loginFlag) {
		String loginType = "";
		if(StringUtils.isEmpty(loginFlag)){

		} else if (loginFlag.equals("1")) {
			loginType = "LINECODE";
		} else if (loginFlag.equals("2")) {
			loginType = "TIKTOKCODE";
		} else if (loginFlag.equals("3")) {
			loginType = "FACEBOOKCODE";
		}
		StringBuffer queryString = new StringBuffer(" FROM SecUser where "+loginType+" = ?0");
		List<SecUser> list = null;
		list = (List<SecUser>) this.getHibernateTemplate().find(queryString.toString() ,new Object[] {loginCode});

		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public List<SecUser> findAllSysUsers() {
		StringBuffer queryString = new StringBuffer(" FROM SecUser where partyId is null OR partyId=''");
		List<SecUser> list= (List<SecUser>) this.getHibernateTemplate().find(queryString.toString());
		return list;
	}
	
	@Override
	public void update(SecUser user) {
//		this.getHibernateTemplate().update(user);
		this.getHibernateTemplate().merge(user);

	}

	@Override
	public SecUser findUserById(String id) {
		return this.getHibernateTemplate().get(SecUser.class, id);
	}

	@Override
	public void updatePassword(String loginName, String password) {
		SecUser secUser = findUserByLoginName(loginName);
		if (secUser != null) {
			secUser.setPassword(passwordEncoder.encodePassword(password, secUser.getUsername()));
			// secUser.setPassword(password);
			this.update(secUser);

		} else {
			throw new BusinessException("没有找到用户");
		}
		// codeTimeWindow.getCodeForgot().remove(loginName);
	}

	@Override
	public void updateSecUser(String partyId, String userName, String password) {
		SecUser secUser = findUserByPartyId(partyId);
		if (secUser != null) {
			secUser.setPassword(passwordEncoder.encodePassword(password, userName));
			secUser.setUsername(userName);
			this.update(secUser);
		} else {
			throw new BusinessException("sec用户不存在");
		}
	}

	@Override
	public void updateSafeword(String username, String oldpassword, String password) {

		SecUser user = this.findUserByLoginName(username);

		if (user == null) {
			return;
		}
		String oldpassword_encoder = passwordEncoder.encodePassword(oldpassword, user.getUsername());
		/**
		 * 旧资金密码为空则不验证旧密码
		 */
		if(!StringUtils.isNullOrEmpty(user.getSafeword())) {
			if (oldpassword_encoder.equals(user.getSafeword())) {
				user.setSafeword(passwordEncoder.encodePassword(password, user.getUsername()));
				// user.setPassword(password);
				this.getHibernateTemplate().update(user);
			} else {
				throw new BusinessException("旧密码不正确");
			}
		}else {
			user.setSafeword(passwordEncoder.encodePassword(password, user.getUsername()));
			this.getHibernateTemplate().update(user);
		}
		

	}

	@Override
	public void updateSafeword(String loginName, String password) {
		SecUser secUser = findUserByLoginName(loginName);
		if (secUser != null) {
			secUser.setSafeword(passwordEncoder.encodePassword(password, secUser.getUsername()));
			// secUser.setPassword(password);
			this.update(secUser);

		} else {
			throw new BusinessException("没有找到用户");
		}
		// codeTimeWindow.getCodeForgot().remove(loginName);
	}
	
	public String test() {
		return "test";
	}


	/**
	 * 判断一个用户是否是演示账号
	 *
	 * @param partyId
	 * @return
	 */
	public boolean queryCheckGuestAccount(String partyId) {
		SecUser user = this.findUserByPartyId(partyId);
		user.getRoles();
		boolean guest = false;
		for (Role role : user.getRoles()) {
			if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
				guest = true;
			}
		}

		return guest;
	}
}
