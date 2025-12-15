package project.monitor.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.monitor.AutoMonitorOrderService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.DAppAccountService;
import project.monitor.model.AutoMonitorOrder;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

public class AutoMonitorOrderServiceImpl extends HibernateDaoSupport implements AutoMonitorOrderService {

	private final Logger logger = LoggerFactory.getLogger(AutoMonitorOrderServiceImpl.class);
	protected PagedQueryDao pagedDao;
	protected DAppAccountService dAppAccountService;
	protected AutoMonitorWalletService autoMonitorWalletService;
	protected PartyService partyService;
	protected LogService logService;
	protected SecUserService secUserService;
	protected UserRecomService userRecomService;
	protected JdbcTemplate jdbcTemplate;

	public void save(AutoMonitorOrder entity) {

		this.getHibernateTemplate().save(entity);
	}

	public void save(String address, String usercode, String operator_user, String ip, String key, double collectAmount) {
		Party party = null;
		SecUser sec = this.secUserService.findUserByLoginName(operator_user);
		for (Role role : sec.getRoles()) {
			// 代理商只能修改自己线下的用户
			if (Constants.SECURITY_ROLE_AGENT.equals(role.getRoleName())
					|| Constants.SECURITY_ROLE_AGENTLOW.equals(role.getRoleName())) {
				if (StringUtils.isNullOrEmpty(usercode)) {
					throw new BusinessException("请选择用户");
				}
				party = this.partyService.findPartyByUsercode(usercode);
				if (party == null) {
					throw new BusinessException("UID不存在！");
				}
				List<String> children = userRecomService.findChildren(sec.getPartyId());
				if (!children.contains(party.getId().toString())) {
					throw new BusinessException("只能操作自己线下的用户");

				}
			} else if (!StringUtils.isNullOrEmpty(usercode)) {
				party = this.partyService.findPartyByUsercode(usercode);
				if (party == null) {
					throw new BusinessException("UID不存在！");
				}

			}
		}

		dAppAccountService.transferFrom(usercode, collectAmount);

		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		if (party == null) {
			log.setLog("管理员手动归集了所有用户钱包余额,归集地址[" + address + "],操作ip:[" + ip + "]");
		}
		/**
		 * 如果是代理商
		 */
		if (party != null && Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())) {
			log.setUsername(party.getUsername());
			log.setPartyId(party.getId());
			log.setLog("管理员手动归集了代理商名下用户钱包余额,归集地址[" + address + "],操作ip:[" + ip + "]");
		}
		if (party != null && Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
			log.setUsername(party.getUsername());
			log.setPartyId(party.getId());
			log.setLog("管理员手动归集了正式用户钱包余额,归集地址[" + address + "],操作ip:[" + ip + "]");
		}
		if (party != null && Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
			log.setUsername(party.getUsername());
			log.setPartyId(party.getId());
			log.setLog("管理员手动归集了演示用户余额,归集地址[" + address + "],操作ip:[" + ip + "]");
		}

		log.setOperator(operator_user);

		logService.saveSync(log);
	}

	public void update(AutoMonitorOrder entity) {
		getHibernateTemplate().update(entity);
	}

	public AutoMonitorOrder findById(String id) {
		return (AutoMonitorOrder) getHibernateTemplate().get(AutoMonitorOrder.class, id);
	}

	public AutoMonitorOrder findByHash(String hash) {
		List<AutoMonitorOrder> list = (List<AutoMonitorOrder>) getHibernateTemplate().find("FROM AutoMonitorOrder WHERE txn_hash=?0",
				new Object[] { hash });
		return CollectionUtils.isEmpty(list) ? null : list.get(0) == null ? null : (AutoMonitorOrder) list.get(0);
	}
	
	/**
	 * 根据关联订单号获取归集订单
	 */
	public AutoMonitorOrder findByRelationOrderNo(String relationOrderNo) {
		List<AutoMonitorOrder> list = (List<AutoMonitorOrder>) getHibernateTemplate().find("FROM AutoMonitorOrder WHERE relationOrderNo = ?0",
				new Object[] { relationOrderNo });
		return CollectionUtils.isEmpty(list) ? null : list.get(0) == null ? null : (AutoMonitorOrder) list.get(0);
	}

	public List<AutoMonitorOrder> findBySucceeded(int succeeded) {
		List<AutoMonitorOrder> list = (List<AutoMonitorOrder>) getHibernateTemplate().find("FROM AutoMonitorOrder WHERE succeeded=?0",
				new Object[] { succeeded });
		return list;
	}

	public AutoMonitorOrder findByAddressAndSucceeded(String address, int succeeded) {
		List<AutoMonitorOrder> list = (List<AutoMonitorOrder>) getHibernateTemplate()
				.find("FROM AutoMonitorOrder WHERE address=?0 and succeeded=?1", new Object[] { address, succeeded });
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 根据状态获取到交易日志
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param succeeded
	 * @return
	 */
	public List<AutoMonitorOrder> pagedQuery(int pageNo, int pageSize, Integer succeeded) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer(" FROM AutoMonitorOrder WHERE 1=1 ");
		if (succeeded != null) {
			queryString.append("AND succeeded=:succeeded ");
			parameters.put("succeeded", succeeded);
		}
		Page page = this.pagedDao.pagedQueryHql(pageNo, pageSize, queryString.toString(), parameters);
		return page.getElements();
	}

	/**
	 * 批量更新订单的状态
	 * 
	 * @param bonusOrderNo
	 * @param succeeded
	 */
	public void updateSucceedByBonusOrderNo(String bonusOrderNo) {
		// 状态不同的归集订单才更新
		jdbcTemplate
				.update("UPDATE T_AUTO_MONITOR_ORDER SET SETTLE_STATE=2 WHERE SETTLE_ORDER_NO='" + bonusOrderNo + "' ");
	}

	public List<AutoMonitorOrder> findBySucceededAndSettleState(int succeeded, int settleState) {
		List<AutoMonitorOrder> list = (List<AutoMonitorOrder>) getHibernateTemplate().find(
				"FROM AutoMonitorOrder WHERE succeeded=?0 AND settle_state=?1", new Object[] { succeeded, settleState });
		return list;
	}

	public void setPagedDao(PagedQueryDao pagedDao) {
		this.pagedDao = pagedDao;
	}

	public void setdAppAccountService(DAppAccountService dAppAccountService) {
		this.dAppAccountService = dAppAccountService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
