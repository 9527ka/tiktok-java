package project.wallet.rate.internal;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import project.RedisKeys;
import project.redis.RedisHandler;
import project.wallet.PropertiesUtilWallet;
import project.wallet.rate.ExchangeRate;
import project.wallet.rate.ExchangeRateService;
import project.wallet.rate.UserRateConfig;
import project.wallet.rate.UserRateConfigService;

public class UserRateConfigServiceImpl extends HibernateDaoSupport implements UserRateConfigService {

	private ExchangeRateService exchangeRateService;

	private RedisHandler redisHandler;

	public void update(String rateId, String partyId) {
		ExchangeRate exchangeRate = exchangeRateService.get(rateId);
		if (null == exchangeRate) {
			logger.error(String.format("rate is null,rateId:{%s}", rateId));
			throw new BusinessException("rate is null");
		}
		UserRateConfig userConfig = this.getByPartyId(partyId);
		if (userConfig == null) {
			userConfig = new UserRateConfig();
			userConfig.setPartyId(partyId);
		}
		userConfig.setCurrency(exchangeRate.getCurrency());

		this.getHibernateTemplate().saveOrUpdate(userConfig);
		redisHandler.setSync(RedisKeys.USER_RATE_CONFIG_PARTY_ID + partyId, userConfig);
	}

	public UserRateConfig getByPartyId(String partyId) {
		return (UserRateConfig) redisHandler.get(RedisKeys.USER_RATE_CONFIG_PARTY_ID + partyId);
//		List<UserRateConfig> userConfigList = this.getHibernateTemplate().find("FROM UserRateConfig WHERE partyId=?",
//				partyId);
//		if (!CollectionUtils.isEmpty(userConfigList))
//			return userConfigList.get(0);
//		return null;
	}

	@Override
	public ExchangeRate findUserConfig(String partyId) {
		ExchangeRate exchangeRate = null;
		
		String user_default_currency = "USD";
//		if (StringUtils.isNullOrEmpty(partyId)) {
//			exchangeRate = exchangeRateService.findBy(ExchangeRate.IN, user_default_currency);
//
//		} else {
//			UserRateConfig userRateConfig = this.getByPartyId(partyId);
//			if (userRateConfig == null) {
//				exchangeRate = exchangeRateService.findBy(ExchangeRate.IN, user_default_currency);
//			} else {
//				exchangeRate = exchangeRateService.findBy(ExchangeRate.IN, userRateConfig.getCurrency());
//			}
//		}

		return exchangeRate;
	}

	public void setExchangeRateService(ExchangeRateService exchangeRateService) {
		this.exchangeRateService = exchangeRateService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
