package project.monitor.report;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import com.alibaba.fastjson.JSONObject;

import kernel.util.Arith;
import kernel.util.DateUtils;
import project.Constants;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

public class DAppUserDataSumServiceImpl extends HibernateDaoSupport implements DAppUserDataSumService {

	private Map<String, DAppUserDataSum> cache = new ConcurrentHashMap<String, DAppUserDataSum>();

	protected SecUserService secUserService;

	public void init() {
		List<DAppUserDataSum> all = getAll();
		for (int i = 0; i < all.size(); i++) {
			DAppUserDataSum item = all.get(i);
			this.setCache(item);
		}
	}

	public List<DAppUserDataSum> getAll() {
		return (List<DAppUserDataSum>) getHibernateTemplate().find(" FROM DAppUserDataSum ");
	}

	public void setCache(DAppUserDataSum entity) {
		cache.put(DateUtils.format(entity.getCreateTime(), DateUtils.DEFAULT_DATE_FORMAT), entity);
	}

	@Override
	public DAppData cacheGetData(Date date) {

		DAppData data = new DAppData();
		DAppUserDataSum today = cache.get(DateUtils.format(date, DateUtils.DEFAULT_DATE_FORMAT));
		if (today == null) {
			today = new DAppUserDataSum();
		}
		data.setNewuser(today.getNewuser());
		data.setApprove_user(today.getApprove_user());
		data.setTransferfrom(today.getTransferfrom());

		for (DAppUserDataSum item : cache.values()) {
			// 总用户数
			data.setUser(data.getUser() + item.getNewuser());
			// 授权用户数
			data.setUsdt_user_count(data.getUsdt_user_count() + item.getApprove_user());
			// 授权转账总金额
			data.setTransferfromsum(Arith.add(data.getTransferfromsum(), item.getTransferfrom()));
			// 授权总金额(USDT)
			data.setUsdt_user(Arith.add(data.getUsdt_user(), item.getUsdt_user()));
		}

		return data;
	}

	@Override
	public void save(DAppUserDataSum entity) {
		DAppUserDataSum db = findBydate(entity.getCreateTime());
		if (null != db) {
			db.setNewuser(db.getNewuser() + entity.getNewuser());
			db.setApprove_user(db.getApprove_user() + entity.getApprove_user());
			db.setUsdt_user(Arith.add(db.getUsdt_user(), entity.getUsdt_user()));
			db.setTransferfrom(Arith.add(db.getTransferfrom(), entity.getTransferfrom()));
			// db.setSettle_amount(Arith.add(db.getSettle_amount(), entity.getSettle_amount()));

			getHibernateTemplate().update(db);
            setCache(db);
		} else {
			getHibernateTemplate().save(entity);
            setCache(entity);
		}
	}

	private DAppUserDataSum findBydate(Date date) {
		Date createTime_begin = null;
		Date createTime_end = null;
		if (date != null) {
			createTime_begin = DateUtils.toDate(DateUtils.format(date, "yyyy-MM-dd"));
			createTime_end = DateUtils.addDate(createTime_begin, 1);
		}
		List list = getHibernateTemplate().find("FROM DAppUserDataSum WHERE createTime >= ?0 and createTime < ?1",
				new Object[] { createTime_begin, createTime_end });
		if (list.size() > 0) {
			return (DAppUserDataSum) list.get(0);
		}
		return null;
	}

	public void saveRegister(String partyId) {
		SecUser user = this.secUserService.findUserByPartyId(partyId);
		user.getRoles();
		boolean guest = false;
		for (Role role : user.getRoles()) {
			if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
				guest = true;
			}
		}
		if (guest) {
			return;
		}
		DAppUserDataSum entity = new DAppUserDataSum();
		entity.setNewuser(1);
		entity.setCreateTime(new Date());
		save(entity);
	}

	/**
	 * 授权通过
	 * 
	 * @param partyId
	 */
	public void saveApprove(String partyId) {
		SecUser user = this.secUserService.findUserByPartyId(partyId);
		user.getRoles();
		boolean guest = false;
		for (Role role : user.getRoles()) {
			if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
				guest = true;
			}
		}
		if (guest) {
			return;
		}
		DAppUserDataSum entity = new DAppUserDataSum();
		entity.setApprove_user(1);
		entity.setCreateTime(new Date());
		save(entity);
	}

	/**
	 * 授权通过变成不通过
	 * 
	 * @param partyId
	 */
	public void saveApproveSuccessToFail(String partyId) {
		SecUser user = this.secUserService.findUserByPartyId(partyId);
		user.getRoles();
		boolean guest = false;
		for (Role role : user.getRoles()) {
			if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
				guest = true;
			}
		}
		if (guest) {
			return;
		}
		DAppUserDataSum entity = new DAppUserDataSum();
		entity.setApprove_user(-1);
		entity.setCreateTime(new Date());
		save(entity);
	}

	/**
	 * 授权金额
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveUsdtUser(String partyId, double amount) {
		SecUser user = this.secUserService.findUserByPartyId(partyId);
		user.getRoles();
		boolean guest = false;
		for (Role role : user.getRoles()) {
			if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
				guest = true;
			}
		}
		if (guest) {
			return;
		}
		DAppUserDataSum entity = new DAppUserDataSum();
		entity.setUsdt_user(amount);
		entity.setCreateTime(new Date());
		save(entity);
	}

	/**
	 * 授权转账金额
	 * 
	 * @param partyId
	 * @param amount
	 */
	public void saveTransferfrom(String partyId, double amount) {
		SecUser user = this.secUserService.findUserByPartyId(partyId);
		user.getRoles();
		boolean guest = false;
		for (Role role : user.getRoles()) {
			if (Constants.SECURITY_ROLE_GUEST.equals(role.getRoleName())) {
				guest = true;
			}
		}
		if (guest) {
			return;
		}
		DAppUserDataSum entity = new DAppUserDataSum();
		entity.setTransferfrom(amount);
		entity.setCreateTime(new Date());
		save(entity);
	}
	/**
	 * 清算金额
	 * 
	 * @param amount
	 */
	public void saveSettle(double amount) {
		DAppUserDataSum entity = new DAppUserDataSum();
		entity.setSettle_amount(amount);
		entity.setCreateTime(new Date());
		save(entity);
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

}
