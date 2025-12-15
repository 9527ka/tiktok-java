package project.user.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.tip.TipService;
import project.user.AdminUserSafewordApplyService;
import project.user.UserSafewordApply;
import project.user.UserSafewordApplyService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminUserSafewordApplyServiceImpl extends HibernateDaoSupport implements AdminUserSafewordApplyService {
	protected PagedQueryDao pagedQueryDao;
	protected PartyService partyService;
	protected UserRecomService userRecomService;
	protected KycService kycService;
	protected UserSafewordApplyService userSafewordApplyService;
	private PasswordEncoder passwordEncoder;
	private SecUserService secUserService;
	private TipService tipService;

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer status_para, String rolename_para,
			String checkedPartyId, Integer operate) {
		
		StringBuffer queryString = new StringBuffer();
		
		queryString.append("SELECT ");
		
		queryString.append(
				" party.UUID partyId, party.USERCODE usercode, party.USERNAME username, party.ROLENAME rolename, party.USER_LEVEL user_level, ");
		queryString.append(
				" kyc.UUID kyc_id, kyc.IDNAME kyc_idname, kyc.NAME kyc_name, kyc.IDIMG_1 kyc_idimg_1, kyc.IDIMG_2 kyc_idimg_2, "
				+ " kyc.IDIMG_3 kyc_idimg_3, kyc.STATUS kyc_status, ");
		queryString.append(
				" apply.UUID id, apply.IDCARD_PATH_FRONT idimg_1, apply.IDCARD_PATH_BACK idimg_2, apply.IDCARD_PATH_HOLD idimg_3, "
				+ " apply.MSG msg, apply.STATUS status, apply.CREATE_TIME create_time, apply.OPERATE operate, apply.REMARK remark ");
		
		queryString.append(" FROM T_USER_SAFEWORD_APPLY apply ");
		queryString.append(" LEFT JOIN T_KYC kyc ON kyc.PARTY_ID = apply.PARTY_ID ");
		queryString.append(" LEFT JOIN PAT_PARTY party ON kyc.PARTY_ID = party.UUID ");
		
		queryString.append(" WHERE 1=1 ");

		Map<String, Object> parameters = new HashMap<String, Object>();

		if (!StringUtils.isNullOrEmpty(checkedPartyId)) {

			List<String> checked_list = this.userRecomService.findChildren(checkedPartyId);
			checked_list.add(checkedPartyId);
			if (checked_list.size() == 0) {
				return Page.EMPTY_PAGE;
			}

			queryString.append(" AND party.UUID in(:checked_list) ");
			parameters.put("checked_list", checked_list);
		}

		if (status_para != null) {
			queryString.append(" AND apply.STATUS = :status_para ");
			parameters.put("status_para", status_para);
		}
		
		if (!StringUtils.isNullOrEmpty(rolename_para)) {
			queryString.append(" AND party.ROLENAME =:rolename ");
			parameters.put("rolename", rolename_para);
		}
		
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" AND ( party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + name_para + "%");
		}
		
		if (operate != null) {
			queryString.append(" AND apply.OPERATE =:operate ");
			parameters.put("operate", operate);
		}
		
		queryString.append(" order by apply.CREATE_TIME desc ");

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	@Override
	public void savePassed(String id, String operatorUsername, String safeword) {
		
		UserSafewordApply apply = this.userSafewordApplyService.findById(id);
		
		if (null == apply) {
			throw new BusinessException("申请不存在，或请刷新重试");
		}
		
		if (apply.getStatus() != 1) {
			throw new BusinessException("当前申请已处理");
		}
		
		if (!Arrays.asList(0, 1, 2, 3).contains(apply.getOperate())) {
			throw new BusinessException("操作类型不正确");
		}
		
		this.checkLoginSafeword(operatorUsername, safeword);
		
		Kyc kyc = this.kycService.get(apply.getPartyId().toString());
		if (null == kyc || kyc.getStatus() != 2) {
			throw new BusinessException("认证尚未通过，无法重置");
		}
		
		apply.setApply_time(new Date());
		apply.setStatus(2);
		
		this.userSafewordApplyService.update(apply);
				
		Party party = this.partyService.cachePartyBy(apply.getPartyId(), false);
				
		// 操作类型 operate:	 0/修改资金密码；1/取消谷歌绑定；2/取消手机绑定；3/取消邮箱绑定；
		switch (apply.getOperate()) {
			case 0:
				party.setSafeword(apply.getSafeword());
				this.partyService.update(party);
				break;
			case 1:
				SecUser secUser = this.secUserService.findUserByLoginName(party.getUsername());
				if (null == secUser) {
					throw new BusinessException("用户不存在");
				}
				if (!secUser.isGoogle_auth_bind()) {
					throw new BusinessException("用户未绑定，无需解绑");
				}				
				secUser.setGoogle_auth_bind(false);
				this.secUserService.update(secUser);
				break;
			case 2:
				party.setPhone_authority(false);
				this.partyService.update(party);
				break;
			case 3:
				party.setEmail_authority(false);
				this.partyService.update(party);
				break;
		}
		
		this.tipService.deleteTip(apply.getId().toString());
	}

	@Override
	public void saveFailed(String id, String msg) {
		
		UserSafewordApply apply = this.userSafewordApplyService.findById(id);
		
		if (null == apply) {
			throw new BusinessException("申请不存在，或请刷新重试");
		}
		
		if (apply.getStatus() != 1) {
			throw new BusinessException("当前申请已处理");
		}
		
		Kyc kyc = this.kycService.get(apply.getPartyId().toString());
		if (null == kyc || kyc.getStatus() != 2) {
			throw new BusinessException("认证尚未通过，无法重置");
		}
		
		apply.setApply_time(new Date());
		apply.setStatus(3);
		apply.setMsg(msg);
		
		this.userSafewordApplyService.update(apply);
		
		this.tipService.deleteTip(apply.getId().toString());
	}

	/**
	 * 某个时间后未处理数量,没有时间则全部
	 * 
	 * @param time
	 * @return
	 */
	public Long getUntreatedCount(Date time, String loginPartyId) {
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT COUNT(*) FROM UserSafewordApply WHERE status in(1) ");
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

	/**
	 * 验证登录人资金密码
	 * 
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	private void checkLoginSafeword(String operatorUsername, String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}

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

	public void setKycService(KycService kycService) {
		this.kycService = kycService;
	}

	public void setUserSafewordApplyService(UserSafewordApplyService userSafewordApplyService) {
		this.userSafewordApplyService = userSafewordApplyService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

}
