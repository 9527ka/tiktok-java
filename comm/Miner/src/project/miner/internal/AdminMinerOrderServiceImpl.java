package project.miner.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.miner.AdminMinerOrderService;
import project.miner.MinerOrderService;
import project.miner.MinerService;
import project.miner.model.Miner;
import project.miner.model.MinerOrder;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import util.DateUtil;
import util.RandomUtil;

public class AdminMinerOrderServiceImpl extends HibernateDaoSupport implements AdminMinerOrderService {
	protected PagedQueryDao pagedQueryDao;
	protected UserRecomService userRecomService;
	protected MinerOrderService minerOrderService;
	protected MinerService minerService;
	protected PartyService partyService;

	public Page pagedQuery(int pageNo, int pageSize, String name_para, String miner_para, String status_para,
			String partyId, String orderNo,String rolename_para) {
//		List children = null;
//		if (!StringUtils.isNullOrEmpty(partyId)) {
//			children = this.userRecomService.findChildren(partyId);
//		}
		Map<String, Object> parameters = new HashMap<>();
		StringBuffer queryString = new StringBuffer(
				" SELECT minerOrder.UUID id,minerOrder.ORDER_NO order_no  ,minerOrder.MINER_ID minerId  , ");
		queryString.append(" minerOrder.AMOUNT amount,minerOrder.CREATE_TIME create_time,minerOrder.BASE_COMPUTE_AMOUNT base_compute_amount, ");
		queryString.append(" minerOrder.EARN_TIME earn_time,minerOrder.STOP_TIME stop_time,minerOrder.PROFIT profit, ");
		queryString.append(" minerOrder.STATE state,minerOrder.CLOSE_TIME close_time,minerOrder.DEFAULT_MONEY default_money, ");
		queryString.append(" party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename, ");
		queryString.append(" miner.NAME miner_name,miner.NAME_EN miner_name_en ");
		queryString.append(" FROM T_MINER_ORDER minerOrder   ");
		queryString.append(" LEFT JOIN PAT_PARTY party ON minerOrder.PARTY_ID = party.UUID  ");
		queryString.append(" LEFT JOIN T_MINER miner ON miner.UUID = minerOrder.MINER_ID ");
//		if (!StringUtils.isNullOrEmpty(partyId)) {
//			queryString.append(" LEFT JOIN T_AGENT agent ON minerOrder.PARTY_ID = agent.PARTY_ID  ");
//		}
		queryString.append(" WHERE 1 = 1 ");
		if (!StringUtils.isNullOrEmpty(partyId)) {
			List children = this.userRecomService.findChildren(partyId);
			if (children.size() == 0) {
//				return Page.EMPTY_PAGE;
				return new Page();
			}
			queryString.append(" and minerOrder.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}
//		if (!StringUtils.isNullOrEmpty(partyId)) {
//			queryString.append(" and  minerOrder.PARTY_ID in (:partyId) ");
//			parameters.put("partyId", children);
//		}

		if (!StringUtils.isNullOrEmpty(miner_para)) {
			queryString.append(
					" and miner.UUID=:miner_para  ");
			parameters.put("miner_para", miner_para);
		}
//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append(" and  party.USERNAME =:name or party.USERCODE =:usercode ");
//			parameters.put("name", name_para);
//			parameters.put("usercode", name_para);
//		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(status_para)) {
			String status = status_para;
			if("0".equals(status)) {
				status ="'0','2'";
			}else {
				status ="'"+status+"'";
			}
			queryString.append(" and  minerOrder.STATE  in ("+status+") ");
		}
		if (!StringUtils.isNullOrEmpty(orderNo)) {
			queryString.append(" and minerOrder.ORDER_NO = :orderNo  ");
			parameters.put("orderNo", orderNo);

		}
		if (!StringUtils.isNullOrEmpty(rolename_para)) {
			queryString.append(" and   party.ROLENAME =:rolename");
			parameters.put("rolename", rolename_para);
		}
		if("0".equals(status_para)) {
			queryString.append(" order by minerOrder.CLOSE_TIME desc ");
		}else {
			queryString.append(" order by minerOrder.CREATE_TIME desc ");
		}
		
		

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}
	
	public void addOrder(String uid,double amount,String minerId,String operator_username) {
		Miner miner = this.minerService.findById(minerId);
		if(null == miner) {
			throw new BusinessException("矿机不存在");
		}
		Party party = partyService.findPartyByUsercode(uid);
		if(null==party) {
			throw new BusinessException("购买用户不存在");
		}else {
			if(!(Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())
			||Constants.SECURITY_ROLE_GUEST.equals(party.getRolename()))) {
				throw new BusinessException("该用户并非正式用户或演示用户，无法购买");
			}
		}
		
		MinerOrder order = new MinerOrder();
		order.setPartyId(party.getId());
		order.setMinerId(minerId);
		order.setOrder_no(DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8));
		order.setAmount(amount);
		order.setState("1");
		this.minerOrderService.saveCreateByManage(order, operator_username);
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setMinerOrderService(MinerOrderService minerOrderService) {
		this.minerOrderService = minerOrderService;
	}

	public void setMinerService(MinerService minerService) {
		this.minerService = minerService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}
	
	
}
