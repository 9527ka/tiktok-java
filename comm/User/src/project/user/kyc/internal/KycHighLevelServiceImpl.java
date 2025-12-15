package project.user.kyc.internal;

import java.text.MessageFormat;
import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import project.redis.RedisHandler;
import project.tip.TipService;
import project.user.UserRedisKeys;
import project.user.kyc.KycHighLevel;
import project.user.kyc.KycHighLevelService;

public class KycHighLevelServiceImpl extends HibernateDaoSupport implements KycHighLevelService {

	private RedisHandler redisHandler;
	private TipService tipService;

	@Override
	public KycHighLevel get(String partyId) {
//		StringBuffer queryString = new StringBuffer(" FROM KycHighLevel where partyId = ?");
//		List<KycHighLevel> list = null;
//		list = this.getHibernateTemplate().find(queryString.toString(), new Object[] { partyId });
//		if (list.size() > 0) {
//			return list.get(0);
//		}
		KycHighLevel kycHighLevel = (KycHighLevel) redisHandler.get(UserRedisKeys.KYC_HIGHLEVEL_PARTY_ID + partyId);
		if (kycHighLevel != null)
			return kycHighLevel;
		KycHighLevel obj = new KycHighLevel();
		obj.setPartyId(partyId);
		return obj;
	}

	@Override
	public void save(KycHighLevel entity) {
		KycHighLevel kycHighLevel = get(entity.getPartyId().toString());
		if(entity.getStatus()==1) {
			entity.setApply_time(new Date());
		}else {
			entity.setOperation_time(new Date());
		}
		if (kycHighLevel.getId() == null) {
			this.getHibernateTemplate().save(entity);
			redisHandler.setSync(UserRedisKeys.KYC_HIGHLEVEL_PARTY_ID + entity.getPartyId().toString(), entity);
		} else {
//			entity.setPartyId(kycHighLevel.getPartyId());
			entity.setId(kycHighLevel.getId());
			
			BeanUtils.copyProperties(entity, kycHighLevel);

//			kycHighLevel.setApply_time(new Date());
			this.getHibernateTemplate().merge(kycHighLevel);
			redisHandler.setSync(UserRedisKeys.KYC_HIGHLEVEL_PARTY_ID + kycHighLevel.getPartyId().toString(),
					kycHighLevel);
		}
//		tipService.saveTip(kycHighLevel.getId().toString(), TipConstants.KYC_HIGH_LEVEL);
	}

	/**
	 * 验证审核结果
	 */
	public String checkApplyResult(String partyId) throws BusinessException {
		KycHighLevel kycHighLevel = get(partyId);
		if (null == kycHighLevel.getId())
			return "";
		String msg = "";
		switch (kycHighLevel.getStatus()) {
		case 0:
			msg = "已经提交申请，请等待审核";
			break;
		case 1:
			msg = "审核中";
			break;
		case 2:
			msg = "审核已通过";
			break;
		case 3:
			msg = MessageFormat.format("审核未通过,原因:{0}", kycHighLevel.getMsg());
			break;
		default:
			msg = "审核状态异常请联系客服";
			break;
		}
		if (kycHighLevel.getStatus() != 3) {
			throw new BusinessException(msg);
		} else {
			return msg;
		}
	}

	public void delete(String partyId) {
		KycHighLevel kycHighLevel = get(partyId);
		if (kycHighLevel != null) {
			this.getHibernateTemplate().delete(kycHighLevel);
			redisHandler.remove(UserRedisKeys.KYC_HIGHLEVEL_PARTY_ID + partyId);
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

}
