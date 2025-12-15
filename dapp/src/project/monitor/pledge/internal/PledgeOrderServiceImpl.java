package project.monitor.pledge.internal;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.DateUtils;
import project.Constants;
import project.monitor.pledge.PledgeConfig;
import project.monitor.pledge.PledgeConfigService;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeOrderService;
import project.party.PartyService;
import project.party.model.Party;

public class PledgeOrderServiceImpl extends HibernateDaoSupport implements PledgeOrderService {

	private PledgeConfigService pledgeConfigService;

	private PartyService partyService;

	@Override
	public List<PledgeOrder> findApplyTrue() {
		List<PledgeOrder> list = (List<PledgeOrder>) getHibernateTemplate().find("FROM PledgeOrder WHERE apply=?0", new Object[] { true });

		return list;
	}

//	@Override
//	public PledgeOrder getOrder(Serializable partyId) {
//		List<PledgeOrder> list = getHibernateTemplate().find("FROM PledgeOrder WEHER partyId=?",
//				new Object[] { partyId });
//		if (list.size() > 0) {
//			return list.get(0);
//		}
//		return null;
//	}

	public PledgeOrder findByPartyId(String partyId) {
		List list = getHibernateTemplate().find("FROM PledgeOrder WHERE partyId=?0 ", new Object[] { partyId });
		if (list.size() > 0) {
			return (PledgeOrder) list.get(0);
		}
		return null;
	}

	@Override
	public Map<String, String> saveGetOrder(String from) {
		Map<String, String> map = new HashMap();
		Party party = partyService.findPartyByUsername(from);
		if (party == null) {
			return map;
		}
		PledgeOrder order = this.findByPartyId(party.getId());

		if (order == null) {
			PledgeConfig config = pledgeConfigService.getConfig(party.getId().toString());

			if (config != null) {

				order = new PledgeOrder();

				order.setPartyId(party.getId());
				order.setCreateTime(new Date());

				order.setConfig(config.getConfig());

				order.setUsdt(config.getUsdt());
				order.setEth(config.getEth());

				order.setLimit_days(config.getLimit_days());

				order.setSendtime(
						DateUtils.addDate(DateUtils.toDate(DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)),
								config.getLimit_days()));

				order.setTitle(config.getTitle());
				order.setTitle_img(config.getTitle_img());
				order.setContent(config.getContent());
				order.setContent_img(config.getContent_img());

				order.setApply(false);

				this.getHibernateTemplate().save(order);

			} else {
				return map;
			}

		}
		// 用户usdt达标数量
		map.put("usdt", String.valueOf(order.getUsdt()));
		// 活动奖励派发时间
		map.put("send_date", DateUtils.format(order.getSendtime(), DateUtils.DEFAULT_DATE_FORMAT));
		map.put("id", String.valueOf(order.getId()));
		// 质押活动标题
		map.put("title", order.getTitle());
		// 活动标题图片
		map.put("img", Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + order.getTitle_img());
		// 质押活动内容
		map.put("content", order.getContent());
		map.put("img_detail", Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + order.getContent_img());
		// 质押活动内容图片
		map.put("now_time", DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT));
		if (order.getApply()) {
			// 1已加入质押活动
			map.put("status", "1");
		} else {
			// 0未加入质押活动
			map.put("status", "0");
		}

		return map;
	}

	@Override
	public PledgeOrder savejoin(String partyId) {
		PledgeOrder entity = findByPartyId(partyId);
		if (entity == null) {

			PledgeConfig config = pledgeConfigService.getConfig(partyId.toString());

			if (config == null) {
				config = pledgeConfigService.getGlobalConfig();
			}

			entity = new PledgeOrder();

			entity.setPartyId(partyId);
			entity.setCreateTime(new Date());

			entity.setConfig(config.getConfig());

			entity.setUsdt(config.getUsdt());
			entity.setEth(config.getEth());

			entity.setLimit_days(config.getLimit_days());

			entity.setSendtime(
					DateUtils.addDate(DateUtils.toDate(DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)),
							config.getLimit_days()));

			entity.setTitle(config.getTitle());
			entity.setTitle_img(config.getTitle_img());
			entity.setContent(config.getContent());
			entity.setContent_img(config.getContent_img());

			entity.setApply(true);
			entity.setApplyTime(new Date());

			this.getHibernateTemplate().save(entity);

		} else {

			if (entity.getApply()) {
				return entity;
			}
			entity.setSendtime(
					DateUtils.addDate(DateUtils.toDate(DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)),
							entity.getLimit_days()));
			entity.setApply(true);
			entity.setApplyTime(new Date());

			this.getHibernateTemplate().update(entity);
		}

		return entity;

	}

	public void save(PledgeOrder entity) {
		this.getHibernateTemplate().save(entity);
	}

	public void update(PledgeOrder entity) {
		this.getHibernateTemplate().update(entity);

	}

	public PledgeOrder findById(String id) {
		return this.getHibernateTemplate().get(PledgeOrder.class, id);
	}

	public void delete(PledgeOrder entity) {
		this.getHibernateTemplate().delete(entity);

	}

	public void setPledgeConfigService(PledgeConfigService pledgeConfigService) {
		this.pledgeConfigService = pledgeConfigService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

}
