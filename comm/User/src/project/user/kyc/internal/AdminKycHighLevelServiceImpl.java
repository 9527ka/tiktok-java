package project.user.kyc.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.tip.TipService;
import project.user.kyc.AdminKycHighLevelService;
import project.user.kyc.KycHighLevel;
import project.user.kyc.KycHighLevelService;

public class AdminKycHighLevelServiceImpl extends HibernateDaoSupport implements AdminKycHighLevelService {
	private static final String KycHighLevel = null;
	private PagedQueryDao pagedQueryDao;
	private PartyService partyService;
	private UserRecomService userRecomService;
	private KycHighLevelService kycHighLevelService;
	private TipService tipService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer status_para, String rolename_para,
			String checkedPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT");
		queryString.append(
				"  party.UUID partyId,party.USERCODE usercode,party.USERNAME username,party.ROLENAME rolename,");
		queryString.append(" kyc.UUID id,kyc.WORK_PLACE work_place,kyc.HOME_PLACE home_place,"
				+ "kyc.RELATIVES_RELATION relatives_relation,"
				+ "kyc.APPLY_TIME apply_time,kyc.OPERATION_TIME operation_time,"
				+ " kyc.IDIMG_1 idimg_1,kyc.IDIMG_2 idimg_2,kyc.IDIMG_3 idimg_3, "
				+ "kyc.RELATIVES_NAME relatives_name," + "kyc.RELATIVES_PLACE relatives_place,kyc.STATUS status,"
				+ "kyc.MSG msg,kyc.RELATIVES_PHONE relatives_phone,party_parent.USERNAME username_parent ");
		queryString.append(" FROM ");
		queryString.append(" T_KYC_HIGH_LEVEL kyc LEFT JOIN PAT_PARTY party ON kyc.PARTY_ID = party.UUID   ");
		queryString.append("  LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID   ");
		queryString.append("  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   ");
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(checkedPartyId)) {

			List<String> checked_list = this.userRecomService.findChildren(checkedPartyId);
			checked_list.add(checkedPartyId);
			if (checked_list.size() == 0) {
				return Page.EMPTY_PAGE;
			}

			queryString.append(" and   party.UUID in(:checked_list) ");
			parameters.put("checked_list", checked_list);

		}

		if (status_para != null) {
			queryString.append(" and kyc.STATUS = :status_para  ");
			parameters.put("status_para", status_para);

		}
		if (!StringUtils.isNullOrEmpty(rolename_para)) {
			queryString.append(" and   party.ROLENAME =:rolename");
			parameters.put("rolename", rolename_para);
		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}
		queryString.append(" order by kyc.APPLY_TIME desc ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	@Override
	public KycHighLevel findByPartyId(Serializable partyId) {
		List<KycHighLevel> list = (List<project.user.kyc.KycHighLevel>) getHibernateTemplate().find(" FROM KycHighLevel WHERE partyId = ?0",
				new Object[] { partyId });
		if (list.size() > 0) {
			KycHighLevel k = (KycHighLevel) list.get(0);
			Party party = partyService.cachePartyBy(k.getPartyId(), true);
			k.setUsername(party.getUsername());
			return (KycHighLevel) list.get(0);
		}
		return null;
	}

	public KycHighLevel findById(Serializable id) {
		KycHighLevel kycHighLevel = getHibernateTemplate().get(KycHighLevel.class, id);
		if (kycHighLevel != null) {
			Party party = partyService.cachePartyBy(kycHighLevel.getPartyId(), true);
			kycHighLevel.setUsername(party.getUsername());
			return kycHighLevel;
		}
		return null;
	}

	@Override
	public void savePassed(String partyId) {
		KycHighLevel kycHighLevel = findByPartyId(partyId);
		if (kycHighLevel != null) {
			kycHighLevel.setStatus(2);
//			this.getHibernateTemplate().update(kycHighLevel);
			kycHighLevelService.save(kycHighLevel);

			Party party = partyService.cachePartyBy(partyId, false);
			party.setKyc_highlevel_authority(true);
			
			// 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
			int userLevelSystem = this.partyService.getUserLevelByAuth(party);

			// 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
			// 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
			// 如：级别11表示：新注册的前端显示为VIP1；
			int userLevel = party.getUser_level();
			party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);
			
			this.partyService.update(party);

			tipService.deleteTip(kycHighLevel.getId().toString());
		}
	}

	@Override
	public void saveFailed(String partyId, String msg) {
		KycHighLevel kycHighLevel = findByPartyId(partyId);
		if (kycHighLevel != null) {
			kycHighLevel.setStatus(3);
			kycHighLevel.setMsg(msg);
//			this.getHibernateTemplate().update(kycHighLevel);
			kycHighLevelService.save(kycHighLevel);
			Party party = partyService.cachePartyBy(partyId, false);
			party.setKyc_highlevel_authority(false);
			partyService.update(party);
			tipService.deleteTip(kycHighLevel.getId().toString());
		}

	}

	/**
	 * 某个时间后未处理数量,没有时间则全部
	 * 
	 * @param time
	 * @return
	 */
	public Long getUntreatedCount(Date time, String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT COUNT(*) FROM KycHighLevel WHERE status in(0,1) ");
		List<Object> para = new ArrayList<Object>();
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			String childrensIds = this.userRecomService.findChildrensIds(loginPartyId);
			if (StringUtils.isEmptyString(childrensIds)) {
				return 0L;
			}
			queryString.append(" and partyId in (" + childrensIds + ") ");
		}
		if (null != time) {
			queryString.append("AND apply_time > ?0");
			para.add(time);
		}
		List find = this.getHibernateTemplate().find(queryString.toString(), para.toArray());
		return CollectionUtils.isEmpty(find) ? 0L : find.get(0) == null ? 0L : Long.valueOf(find.get(0).toString());
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setKycHighLevelService(KycHighLevelService kycHighLevelService) {
		this.kycHighLevelService = kycHighLevelService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

}
