package project.user.kyc.internal;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import project.redis.RedisHandler;
import project.user.UserRedisKeys;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;

@Slf4j
public class KycServiceImpl extends HibernateDaoSupport implements KycService {

    private RedisHandler redisHandler;

    @Override
    public Kyc get(String partyId) {
//		StringBuffer queryString = new StringBuffer(" FROM Kyc where partyId = ?");
//		List<Kyc> list = null;
//		list = this.getHibernateTemplate().find(queryString.toString(), new Object[] { partyId });
//		if (list.size() > 0) {
//			return list.get(0);
//		}
        Kyc kyc = (Kyc) redisHandler.get(UserRedisKeys.KYC_PARTY_ID + partyId);
        if (kyc != null) {
            return kyc;
        } else {
            StringBuffer queryString = new StringBuffer(" FROM Kyc where partyId = ?0 ");
            List<Kyc> list = (List<Kyc>) this.getHibernateTemplate().find(queryString.toString(), new Object[]{partyId});
            if (list.size() > 0) {
                kyc = list.get(0);
            }
            if (kyc != null) {
                redisHandler.setSync(UserRedisKeys.KYC_PARTY_ID + partyId, kyc);
                return kyc;
            }
        }

        kyc = new Kyc();
        kyc.setPartyId(partyId);
        return kyc;
    }

    @Override
    public void save(Kyc entity) {
        Kyc kyc = get(entity.getPartyId().toString());
        List<Kyc> kycs = findByIdNumber(entity.getIdnumber().trim());
        if (kycs != null && entity.getStatus() != 3 && entity.getStatus() != 1) {
            for (Kyc kyc_othde : kycs) {
                if (kyc_othde.getStatus() == 2 && !kyc_othde.getPartyId().equals(entity.getPartyId())) {
                    throw new BusinessException("身份证号已被其他用户实名");
                }
            }

//			 if(kycs.size() == 1 && kyc != null && kyc.getId() != null) {
//				 if(!kyc.getId().equals(kycs.get(0).getId()) &&
//						 kyc.getIdnumber().equals(kycs.get(0).getIdnumber())) {
//					 throw new BusinessException("身份证已实名过!");
//				 }
//			 }
//			 if(kycs.size() > 1 ) {
//				 throw new BusinessException("身份证已实名过!");
//			 }
        }

        if (kyc.getId() == null) {
            entity.setApply_time(new Date());
            this.getHibernateTemplate().save(entity);
            redisHandler.setSync(UserRedisKeys.KYC_PARTY_ID + entity.getPartyId().toString(), entity);
        } else {
            kyc.setIdnumber(entity.getIdnumber());
            kyc.setStatus(entity.getStatus());
            kyc.setIdname(entity.getIdname());
            kyc.setName(entity.getName());
            kyc.setIdimg_1(entity.getIdimg_1());
            kyc.setIdimg_2(entity.getIdimg_2());
            kyc.setIdimg_3(entity.getIdimg_3());
            kyc.setNationality(entity.getNationality());
            if (entity.getStatus() == 1) {
                kyc.setApply_time(new Date());
            } else {
                kyc.setOperation_time(new Date());
            }

            kyc.setMsg(entity.getMsg());
            this.getHibernateTemplate().merge(kyc);
            entity.setId(kyc.getId());
            redisHandler.setSync(UserRedisKeys.KYC_PARTY_ID + kyc.getPartyId().toString(), kyc);

        }

    }

    public void update(String partyId, String signPdfUrl) {
        Kyc kyc = get(partyId);
        kyc.setSignPdfUrl(signPdfUrl);
        log.info("更新商家人认证信息{}", JSONObject.toJSONString(kyc));
        this.getHibernateTemplate().merge(kyc);
        redisHandler.setSync(UserRedisKeys.KYC_PARTY_ID + kyc.getPartyId().toString(), kyc);
    }

    /**
     * 查询是否有多个实名用户
     *
     * @return
     */
    public List<Kyc> findByIdNumber(String idNumber) {
        StringBuffer queryString = new StringBuffer(" FROM Kyc where  idnumber = ?0");
        List<Kyc> list = (List<Kyc>) this.getHibernateTemplate().find(queryString.toString(), new Object[]{idNumber});
        if (list.size() > 0) {
            return list;
        }
        return null;

    }


    @Override
    public boolean isExistsIdNumber(String idNUmber, String partyId){
        StringBuffer queryString = new StringBuffer(" FROM Kyc where  idnumber = ?0 and party_id <> ?1");
        List<Kyc> list = (List<Kyc>) this.getHibernateTemplate().find(queryString.toString(), new Object[]{idNUmber,partyId});
        if (list.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 验证审核结果
     */
    public String checkApplyResult(String partyId) throws BusinessException {
        Kyc kyc = get(partyId);
        if (null == kyc.getId())
            return "";
        String msg = "";
        switch (kyc.getStatus()) {
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
                msg = MessageFormat.format("审核未通过,原因:{0}", kyc.getMsg());
                break;
            default:
                msg = "审核状态异常请联系客服";
                break;
        }
        if (kyc.getStatus() != 3) {
            throw new BusinessException(msg);
        } else {
            return msg;
        }
    }

    public boolean isPass(String partyId) {
        Kyc kyc = get(partyId);
        if (null == kyc)
            return Boolean.FALSE;
        return kyc.getStatus() == 2;
    }

    public void delete(String partyId) {
        Kyc kyc = get(partyId);
        if (kyc != null) {
            this.getHibernateTemplate().delete(kyc);
            redisHandler.remove(UserRedisKeys.KYC_PARTY_ID + partyId);
        }
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

}
