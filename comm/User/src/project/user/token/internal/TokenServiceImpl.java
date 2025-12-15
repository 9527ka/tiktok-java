package project.user.token.internal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.util.UUIDGenerator;
import project.redis.RedisHandler;
import project.user.UserRedisKeys;
import project.user.token.Token;
import project.user.token.TokenService;
import util.TokenUtils;

public class TokenServiceImpl extends HibernateDaoSupport implements TokenService {
	
	private Logger logger = LogManager.getLogger(TokenServiceImpl.class);

	private RedisHandler redisHandler;

	public String savePut(String partyId) {
		String uuid = UUIDGenerator.getUUID();
		Token token = this.find(partyId);

		if (token == null) {
			token = new Token();
			token.setPartyId(partyId);
		}
		token.setToken(uuid);
		this.getHibernateTemplate().saveOrUpdate(token);
		redisHandler.setSync(UserRedisKeys.TOKEN + uuid, partyId);
		redisHandler.setSync(UserRedisKeys.TOKEN_PARTY_ID + partyId, token);
		return uuid;
	}

	@Override
	public String platFromSavePut(String partyId) {
		Token token = new Token();
		token.setPartyId(partyId);
		String uuid = TokenUtils.token(null, null, false, partyId);
		token.setToken(uuid);
		redisHandler.setSync(UserRedisKeys.PLAT_FROM_TOKEN + uuid, partyId);
		redisHandler.setSync(UserRedisKeys.PLAT_FROM_TOKEN_PARTY_ID + partyId, token);
		return uuid;

	}

	public String cacheGet(String token) {
		if (StringUtils.isNullOrEmpty(token)) {
			logger.error("cacheGet:token is null");
			return null;
		}

		String partyId;
		if (token.length() > 36){

			partyId = (String) redisHandler.get(UserRedisKeys.PLAT_FROM_TOKEN + token);
			if (StringUtils.isNullOrEmpty(partyId)) {
				logger.error("cacheGet:partyId is null, token:" + token);
				return null;
			}

			Token token_redis = (Token) redisHandler.get(UserRedisKeys.PLAT_FROM_TOKEN_PARTY_ID + partyId);
			if (null == token_redis) {
				logger.error("cacheGet:token_redis is null, token:" + token);
				return null;
			}
			if (!token.equals(token_redis.getToken())) {

				logger.error("cacheGet:请求token:{}和token_redis{}不同", token, token_redis.getToken());
//			redisHandler.remove(UserRedisKeys.TOKEN + token);
				return null;
			}

		} else {

			partyId = (String) redisHandler.get(UserRedisKeys.TOKEN + token);
			if (StringUtils.isNullOrEmpty(partyId)) {
				logger.error("cacheGet:partyId is null, token:" + token);
				return null;
			}

			Token token_redis = (Token) redisHandler.get(UserRedisKeys.TOKEN_PARTY_ID + partyId);
			if (null == token_redis) {
				logger.error("cacheGet:token_redis is null, token:" + token);
				return null;
			}
			if (!token.equals(token_redis.getToken())) {

				logger.error("cacheGet:请求token:{}和token_redis{}不同", token, token_redis.getToken());
//			redisHandler.remove(UserRedisKeys.TOKEN + token);
				return null;
			}
		}

		return partyId;
	}

	public Token find(String partyId) {
		return (Token) redisHandler.get(UserRedisKeys.TOKEN_PARTY_ID + partyId);
	}

	@Override
	public void delete(String token) {
		if (StringUtils.isNullOrEmpty(token)) {
			return;
		}
		String partyId = cacheGet(token);
		if (!StringUtils.isNullOrEmpty(partyId)) {
			Token entity = find(partyId);
			if (entity != null) {
				this.getHibernateTemplate().delete(entity);
			}
		}
		redisHandler.remove(UserRedisKeys.TOKEN + token);
		redisHandler.remove(UserRedisKeys.TOKEN_PARTY_ID + partyId);

	}

	@Override
	public void removePlatFromToken(String token) {
		if (StringUtils.isNullOrEmpty(token)) {
			return;
		}
		String partyId = cacheGet(token);
		redisHandler.remove(UserRedisKeys.PLAT_FROM_TOKEN + token);
		redisHandler.remove(UserRedisKeys.PLAT_FROM_TOKEN_PARTY_ID + partyId);
	}

	public void removeLoginToken(String partyId) {
		if (StringUtils.isNullOrEmpty(partyId)) {
			return;
		}

		Token entity = find(partyId);
		if (entity != null) {
			this.getHibernateTemplate().delete(entity);

			redisHandler.remove(UserRedisKeys.TOKEN + entity.getToken());
			redisHandler.remove(UserRedisKeys.TOKEN_PARTY_ID + partyId);
		}
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

}
