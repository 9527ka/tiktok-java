package project.user.internal;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import project.Constants;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.UserSafewordApply;
import project.user.UserSafewordApplyService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import security.SaltSigureUtils;
import util.Strings;

public class UserSafewordApplyServiceImpl extends HibernateDaoSupport implements UserSafewordApplyService {

	private PasswordEncoder passwordEncoder;
	private KycService kycService;
	private TipService tipService;

	public void save(UserSafewordApply entity) {

		this.getHibernateTemplate().save(entity);

	}

	public void update(UserSafewordApply entity) {
		getHibernateTemplate().update(entity);
	}

	public void delete(String id) {
		UserSafewordApply entity = findById(id);
		getHibernateTemplate().delete(entity);
	}

	public UserSafewordApply findById(String id) {
		return (UserSafewordApply) getHibernateTemplate().get(UserSafewordApply.class, id);
	}

	public List<UserSafewordApply> findByPartyId(String partyId) {
		List<UserSafewordApply> list = (List<UserSafewordApply>) getHibernateTemplate()
				.find(" FROM UserSafewordApply WHERE partyId=?0 order by create_time desc", new Object[] { partyId });
		return list;
	}
	
	/**
	 * 尚未通过的申请
	 */
	public UserSafewordApply findByPartyIdNoPass(String partyId, Integer operate) {
		List<UserSafewordApply> list = (List<UserSafewordApply>) getHibernateTemplate().find(
				" FROM UserSafewordApply WHERE partyId=?0 AND operate=?1 AND status!=2 order by create_time desc",
				new Object[] { partyId, operate });
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}
	
	/**
	 * 人工重置  操作类型 operate:	 0/修改资金密码；1/取消谷歌绑定；2/取消手机绑定；3/取消邮箱绑定；
	 */
	public void saveApply(String partyId, String idcard_path_front, String idcard_path_back, String idcard_path_hold, String safeword, 
			String safeword_confirm, Integer operate, String remark) {
		
		if (null == operate || !Arrays.asList(0, 1, 2, 3).contains(operate)) {
			throw new BusinessException("操作类型不正确");
		}
		
		// 操作类型 operate:	 0/修改资金密码；
		if (0 == operate.intValue()) {

			if (StringUtils.isEmptyString(safeword)) {
				throw new BusinessException("资金密码不能为空");
			}

			if (safeword.length() != 6 || !Strings.isNumber(safeword)) {
				throw new BusinessException("资金密码不符合设定");
			}

			if (StringUtils.isEmptyString(safeword_confirm)) {
				throw new BusinessException("资金密码确认不能为空");
			}
			
			if (!safeword.equals(safeword_confirm)) {
				throw new BusinessException("两次输入的资金密码不相同");
			}
		}
		
//		if (StringUtils.isEmptyString(idcard_path_front)) {
//			throw new BusinessException("请上传证件照正面");
//		}
//		
//		if (StringUtils.isEmptyString(idcard_path_back)) {
//			throw new BusinessException("请上传证件照反面");
//		}
//		
//		if (StringUtils.isEmptyString(idcard_path_hold)) {
//			throw new BusinessException("请上传手持证件照");
//		}
		
//		// 操作类型 operate:	 0/修改资金密码；
//		if (0 == operate.intValue()) {
			Kyc kyc = this.kycService.get(partyId);
			if (null == kyc || kyc.getStatus() != 2) {
				throw new BusinessException(401, "实名认证尚未通过，无法重置");
			}
//		}
		
		UserSafewordApply apply = this.findByPartyIdNoPass(partyId, operate);
		if (null == apply) {
			apply = new UserSafewordApply();
			apply.setCreate_time(new Date());
		} else if (apply.getStatus() != 3) {
			throw new BusinessException("您的申请之前已提交过");
		}
		
		// 操作类型 operate:	 0/修改资金密码；
		if (0 == operate.intValue()) {
			String safewordMd5 = this.passwordEncoder.encodePassword(safeword, SaltSigureUtils.saltfigure);
			apply.setSafeword(safewordMd5);
		} else {
			apply.setSafeword("");
		}
		
		apply.setIdcard_path_front(idcard_path_front);
		apply.setIdcard_path_back(idcard_path_back);
		apply.setIdcard_path_hold(idcard_path_hold);
		apply.setOperate(operate);
		apply.setRemark(remark);
		apply.setPartyId(partyId);
		apply.setStatus(1);
		
		if (null == apply.getId()) {
			this.save(apply);
		} else {
			this.update(apply);
		}
		
		this.tipService.saveTip(apply.getId().toString(), TipConstants.USER_SAFEWORD_APPLY);
	}

	public Map<String, Object> bindOne(UserSafewordApply apply) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		String idcard_path_front_path = "";
		String idcard_path_back_path = "";
		String idcard_path_hold_path = "";
		
		if (!StringUtils.isNullOrEmpty(apply.getIdcard_path_front())) {
			idcard_path_front_path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath="
					+ apply.getIdcard_path_front();
		}
		result.put("idcard_path_front", apply.getIdcard_path_front());
		result.put("idcard_path_front_path", idcard_path_front_path);

		if (!StringUtils.isNullOrEmpty(apply.getIdcard_path_back())) {
			idcard_path_back_path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath="
					+ apply.getIdcard_path_back();
		}
		result.put("idcard_path_back", apply.getIdcard_path_back());
		result.put("idcard_path_back_path", idcard_path_back_path);
		
		if (!StringUtils.isNullOrEmpty(apply.getIdcard_path_hold())) {
			idcard_path_hold_path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath="
					+ apply.getIdcard_path_hold();
		} else {
			idcard_path_hold_path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=qr/id_img3.jpg";
		}
		result.put("idcard_path_hold", apply.getIdcard_path_hold());
		result.put("idcard_path_hold_path", idcard_path_hold_path);
		
		result.put("id", apply.getId());
		result.put("create_time", DateUtils.format(apply.getCreate_time(), DateUtils.DF_yyyyMMddHHmmss));
		result.put("msg", apply.getMsg());
		result.put("apply_time", DateUtils.format(apply.getApply_time(), DateUtils.DF_yyyyMMddHHmmss));
		result.put("status", apply.getStatus());
		result.put("operate", apply.getOperate());
		result.put("remark", apply.getRemark());
		
		return result;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setKycService(KycService kycService) {
		this.kycService = kycService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

}
