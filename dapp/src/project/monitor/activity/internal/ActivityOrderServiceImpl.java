package project.monitor.activity.internal;

import kernel.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.monitor.AutoMonitorDAppLogService;
import project.monitor.activity.Activity;
import project.monitor.activity.ActivityOrder;
import project.monitor.activity.ActivityOrderService;
import project.monitor.activity.ActivityService;
import project.monitor.model.AutoMonitorDAppLog;
import project.monitor.telegram.business.TelegramBusinessMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityOrderServiceImpl extends HibernateDaoSupport implements ActivityOrderService {
	private Logger logger = LoggerFactory.getLogger(ActivityOrderServiceImpl.class);
	private ActivityService activityService;
	private PartyService partyService;
	private UserRecomService userRecomService;

	private WalletService walletService;

	private AutoMonitorDAppLogService autoMonitorDAppLogService;
	private TelegramBusinessMessageService telegramBusinessMessageService;

	@Override
	public Map<String, String> saveFindBy(String from) {
		Map<String, String> map = new HashMap<String, String>();
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			return map;
		}

		ActivityOrder order = this.findBy(party.getId());

		if (order == null) {
			Activity config = this.getConfig(party.getId().toString());
			if (config != null && config.getState().equals("1") && config.getSendtime().after(new Date())) {

				order = new ActivityOrder();

				order.setPartyId(party.getId());
				order.setCreateTime(new Date());
				
				order.setSendTime(config.getSendtime());
				order.setEndtime(config.getEndtime());


				order.setSucceeded(0);
				order.setUsdt(config.getUsdt());
				order.setEth(config.getEth());
				order.setTitle(config.getTitle());
				order.setTitle_img(config.getTitle_img());
				order.setContent(config.getContent());
				order.setContent_img(config.getContent_img());
				order.setIndex(config.getIndex());

				save(order);
			} else {
				return map;
			}
		}

		/**
		 * 根据用户钱包地址，返回该用户的活动
		 * 
		 * @return 前端API格式 activity_id:"", //活动id status: 0, //0未参与活动，1已参与活动 pop_up: 0,
		 *         //0无需弹窗，1需弹窗 title: "",//活动标题 img: "",// 活动标题图片 content: "",//活动内容
		 *         img_detail: "",//活动内容图片
		 */

		String activity_id = null;
		String title = null;
		String img = null;
		String content = null;
		String img_detail = null;
		String status = null;
		String pop_up = null;

		activity_id = order.getId().toString();
		title = order.getTitle();
		img = order.getTitle_img();
		content = order.getContent();
		img_detail = order.getContent_img();

		if (order.getSucceeded() == 0 && order.getEndtime().after(new Date())) {
			/**
			 * 未参与活动,未过期
			 */
			status = "0";
		} else if (order.getSucceeded() == 0 && order.getEndtime().before(new Date())) {
			/**
			 * 未参与活动,已过期
			 */
			status = "2";
		} else {

			status = "1";
		}

		if (order.getIndex()) {
			pop_up = "1";
		} else {
			pop_up = "0";
		}

		map.put("activity_id", activity_id);
		map.put("title", title);
		map.put("img", Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + img);
		map.put("content", content);
		map.put("img_detail", Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + img_detail);

		map.put("status", status);
		map.put("pop_up", pop_up);

		return map;
	}

	public ActivityOrder findBy(String partyId) {
		String sql = "FROM ActivityOrder WHERE  partyId =?0 AND  sendTime >=?1 order by createTime desc ";
		List<Object> params = new ArrayList<Object>();
		params.add(partyId);
		params.add(new Date());
		List<ActivityOrder> list = (List<ActivityOrder>) getHibernateTemplate().find(sql, params.toArray());
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<ActivityOrder> findBeforeDate(int succeeded, Date createTime) {
		String sql = "FROM ActivityOrder WHERE 1=1 AND succeeded =?0 AND sendTime<=?1 ";
		List<Object> params = new ArrayList<Object>();
		params.add(succeeded);
		params.add(createTime);
		List<ActivityOrder> list = (List<ActivityOrder>) getHibernateTemplate().find(sql, params.toArray());
		return list;
	}

	/**
	 * 取到应用的收益配置参数
	 * 
	 * @param partyId
	 * @return
	 */
	private Activity getConfig(String partyId) {
		List<UserRecom> parents = userRecomService.getParents(partyId);
		List<Activity> configs = activityService.findBeforeDate(new Date());

		/**
		 * 该用户直接配置
		 */
		for (int i = 0; i < configs.size(); i++) {
			Activity config = configs.get(i);
			if (partyId.equals(config.getPartyId())) {
				/*
				 * 找到返回
				 */
				return config;
			}

		}

		/**
		 * 该用户代理配置
		 */

		/**
		 * 取到代理
		 */
		for (int i = 0; i < parents.size(); i++) {
			Party party = partyService.cachePartyBy(parents.get(i).getReco_id(), true);

			if (!Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
				/**
				 * 非代理
				 */
				continue;
			}

			for (int j = 0; j < configs.size(); j++) {
				Activity config = configs.get(j);
				if (party.getId().toString().equals(config.getPartyId())) {
					return config;
				}
			}

		}

		/**
		 * 全局配置
		 *
		 */

		for (int i = 0; i < configs.size(); i++) {
			Activity config = configs.get(i);
			if (config.getPartyId() == null || "".equals(config.getPartyId().toString())) {
				return config;
			}

		}
		return null;
	}

	@Override
	public void saveOrderProcess(ActivityOrder entity) {
		WalletExtend walletExtend = walletService.saveExtendByPara(entity.getPartyId().toString(),
				Constants.WALLETEXTEND_DAPP_USDT_USER);

		if (walletExtend.getAmount() >= entity.getUsdt()) {
			/**
			 * 成功，送ETH
			 */

			walletService.updateExtend(entity.getPartyId().toString(), Constants.WALLETEXTEND_DAPP_ETH,
					entity.getEth());
			/**
			 * 前端日志
			 */

			AutoMonitorDAppLog dAppLog = new AutoMonitorDAppLog();

			dAppLog.setPartyId(entity.getPartyId());

			dAppLog.setExchange_volume(entity.getEth());
			dAppLog.setStatus(1);

			autoMonitorDAppLogService.save(dAppLog);

			entity.setSucceeded(2);
			this.getHibernateTemplate().update(entity);

		} else {
			/**
			 * 失败，把订单设置为失败
			 */
			entity.setSucceeded(3);
			this.getHibernateTemplate().update(entity);
		}

	}

	@Override
	public boolean savejoin(String from, String activityid) {
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			return false;
		}

//		Activity config = this.getConfig(party.getId().toString());
//		if (!activityid.equals(config.getId().toString())) {
//			throw new BusinessException("当前活动您无法参加");
//		}

		ActivityOrder order = this.findById(activityid);

		WalletExtend walletExtend = walletService.saveExtendByPara(party.getId().toString(),
				Constants.WALLETEXTEND_DAPP_USDT_USER);

		if (walletExtend.getAmount() < order.getUsdt()) {
			logger.error("Unable to join: walletExtend{},order{}", walletExtend.getAmount(), order.getUsdt());
			throw new BusinessException("Unable to join");
		}

		order.setSucceeded(1);
		order.setAdd_activity_time(new Date());

		this.getHibernateTemplate().update(order);

		telegramBusinessMessageService.sendActivityAddTeleg(party, order);
		return true;
	}

	public ActivityOrder findByPartyId(String partyId) {
		List list = getHibernateTemplate().find("FROM ActivityOrder WHERE partyId=?0 ", new Object[] { partyId });
		if (list.size() > 0) {
			return (ActivityOrder) list.get(0);
		}
		return null;
	}

	public void save(ActivityOrder entity) {
		this.getHibernateTemplate().save(entity);
	}

	public void update(ActivityOrder entity) {
		this.getHibernateTemplate().update(entity);

	}

	public ActivityOrder findById(String id) {
		return this.getHibernateTemplate().get(ActivityOrder.class, id);
	}

	public void delete(ActivityOrder entity) {
		this.getHibernateTemplate().delete(entity);

	}

	public void setActivityService(ActivityService activityService) {
		this.activityService = activityService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setAutoMonitorDAppLogService(AutoMonitorDAppLogService autoMonitorDAppLogService) {
		this.autoMonitorDAppLogService = autoMonitorDAppLogService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setTelegramBusinessMessageService(TelegramBusinessMessageService telegramBusinessMessageService) {
		this.telegramBusinessMessageService = telegramBusinessMessageService;
	}

}
