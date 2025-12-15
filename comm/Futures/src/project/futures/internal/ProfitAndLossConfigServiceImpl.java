package project.futures.internal;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.Constants;
import project.futures.FuturesRedisKeys;
import project.futures.ProfitAndLossConfig;
import project.futures.ProfitAndLossConfigService;
import project.log.Log;
import project.log.LogService;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;

public class ProfitAndLossConfigServiceImpl extends HibernateDaoSupport implements ProfitAndLossConfigService {

	private RedisHandler redisHandler;
	
	private LogService logService;
	private PartyService partyService;

	public void save(ProfitAndLossConfig entity, String Operater_username) {
		ProfitAndLossConfig profitAndLossConfig = this.findByPartyId(entity.getPartyId());
		// 如果存在则更新
		if (profitAndLossConfig != null) {
			profitAndLossConfig.setRemark(entity.getRemark());
			profitAndLossConfig.setType(entity.getType());
			getHibernateTemplate().update(profitAndLossConfig);
			redisHandler.setSync(
					FuturesRedisKeys.FUTURES_PROFIT_LOSS_PARTY_ID + profitAndLossConfig.getPartyId().toString(),
					profitAndLossConfig);
		} else {
			this.getHibernateTemplate().save(entity);
			redisHandler.setSync(
					FuturesRedisKeys.FUTURES_PROFIT_LOSS_PARTY_ID + entity.getPartyId().toString(),
					entity);
		}
		
		String type = "";
		
		if("profit".equals(entity.getType())) {
			type = "盈利";
		}
		if("loss".equals(entity.getType())) {
			type = "亏损";
		}
		if("buy_profit".equals(entity.getType())) {
			type = "买多盈利";
		}
		if("sell_profit".equals(entity.getType())) {
			type = "买空盈利";
		}
		if("buy_profit_sell_loss".equals(entity.getType())) {
			type = "买多盈利并且买空亏损";
		}
		if("sell_profit_buy_loss".equals(entity.getType())) {
			type = "买空盈利并且买多亏损";
		}
		
		Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(Operater_username);
		log.setUsername(party.getUsername());
		log.setPartyId(entity.getPartyId());
		log.setCreateTime(new Date());
		log.setLog("管理员手动添加场控交割状态。操作类型[" + type + "]。");
		this.logService.saveSync(log);


	}

	public void update(ProfitAndLossConfig entity, String Operater_username) {
		getHibernateTemplate().update(entity);
		redisHandler.setSync(FuturesRedisKeys.FUTURES_PROFIT_LOSS_PARTY_ID + entity.getPartyId().toString(), entity);
		
		String type = "";
		
		if("profit".equals(entity.getType())) {
			type = "盈利";
		}
		if("loss".equals(entity.getType())) {
			type = "亏损";
		}
		if("buy_profit".equals(entity.getType())) {
			type = "买多盈利";
		}
		if("sell_profit".equals(entity.getType())) {
			type = "买空盈利";
		}
		if("buy_profit_sell_loss".equals(entity.getType())) {
			type = "买多盈利并且买空亏损";
		}
		if("sell_profit_buy_loss".equals(entity.getType())) {
			type = "买空盈利并且买多亏损";
		}
		Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(Operater_username);
		log.setUsername(party.getUsername());
		log.setPartyId(entity.getPartyId());
		log.setCreateTime(new Date());
		log.setLog("管理员手动修改场控交割状态。修改后操作类型为[" + type + "]。");
		this.logService.saveSync(log);
	}

	public void delete(String id, String Operater_username) {
		ProfitAndLossConfig entity = findById(id);
		getHibernateTemplate().delete(entity);
		redisHandler.remove(FuturesRedisKeys.FUTURES_PROFIT_LOSS_PARTY_ID + entity.getPartyId().toString());

		Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(Operater_username);
		log.setUsername(party.getUsername());
		log.setPartyId(entity.getPartyId());
		log.setCreateTime(new Date());
		log.setLog("管理员手动删除场控交割状态");
		this.logService.saveSync(log);
	}

	public ProfitAndLossConfig findById(String id) {
		return (ProfitAndLossConfig) getHibernateTemplate().get(ProfitAndLossConfig.class, id);
	}

	public ProfitAndLossConfig findByPartyId(String partyId) {
		List<ProfitAndLossConfig> list = (List<ProfitAndLossConfig>) getHibernateTemplate().find(" FROM ProfitAndLossConfig WHERE partyId = ?0",
				new Object[] { partyId });
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public ProfitAndLossConfig cacheByPartyId(String partyId) {
		return (ProfitAndLossConfig) redisHandler
				.get(FuturesRedisKeys.FUTURES_PROFIT_LOSS_PARTY_ID + partyId.toString());
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}
	
	

}
