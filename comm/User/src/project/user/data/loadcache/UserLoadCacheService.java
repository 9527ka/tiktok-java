package project.user.data.loadcache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.redis.RedisHandler;
import project.user.UserRedisKeys;
import project.user.kyc.Kyc;
import project.user.kyc.KycHighLevel;
import project.user.token.Token;

public class UserLoadCacheService extends HibernateDaoSupport {
	private static final Log logger = LogFactory.getLog(UserLoadCacheService.class);

	private RedisHandler redisHandler;

	public void loadcache() {
		loadToken();
		loadKyc();
		loadKycHighLevel();
		logger.info("完成User数据加载redis");
	}


	public void loadToken() {
		StringBuffer queryString = new StringBuffer(" FROM Token ");
		List<Token> list = (List<Token>) this.getHibernateTemplate().find(queryString.toString());

		for (Token token : list) {
			redisHandler.setSync(UserRedisKeys.TOKEN + token.getToken(), token.getPartyId().toString());
			redisHandler.setSync(UserRedisKeys.TOKEN_PARTY_ID + token.getPartyId().toString(), token);
		}
	}
	
	public void loadKyc() {
		StringBuffer queryString = new StringBuffer(" FROM Kyc ");
		List<Kyc> list = (List<Kyc>) this.getHibernateTemplate().find(queryString.toString());
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		for (Kyc kyc : list) {
			params.put(UserRedisKeys.KYC_PARTY_ID + kyc.getPartyId().toString(), kyc);
		}
		this.redisHandler.setBatchSync(params);
	}

	public void loadKycHighLevel() {
		StringBuffer queryString = new StringBuffer(" FROM KycHighLevel ");
		List<KycHighLevel> list = (List<KycHighLevel>) this.getHibernateTemplate().find(queryString.toString());

		for (KycHighLevel kycHighLevel : list) {
			redisHandler.setSync(UserRedisKeys.KYC_HIGHLEVEL_PARTY_ID + kycHighLevel.getPartyId().toString(),
					kycHighLevel);
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
