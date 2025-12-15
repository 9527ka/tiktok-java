package project.monitor.pledgegalaxy.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import project.Constants;
import project.monitor.pledgegalaxy.PledgeGalaxyConfig;
import project.monitor.pledgegalaxy.PledgeGalaxyConfigService;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;
import project.monitor.pledgegalaxy.PledgeGalaxyRedisKeys;
import project.monitor.pledgegalaxy.PledgeGalaxyStatusConstants;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.Syspara;
import project.syspara.SysparaService;

/**
 * 质押2.0配置
 */
public class PledgeGalaxyConfigServiceImpl extends HibernateDaoSupport implements PledgeGalaxyConfigService {

	private PartyService partyService;
	private UserRecomService userRecomService;
	private RedisHandler redisHandler;
	private SysparaService sysparaService;
	private PledgeGalaxyOrderService pledgeGalaxyOrderService;

	/**
	 * 获取配置 优先级：用户>代理>个人
	 */
	public PledgeGalaxyConfig getConfig(String partyId) {
		
		// 获取用户配置
		PledgeGalaxyConfig userConfig = (PledgeGalaxyConfig) this.redisHandler.get(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_CONFIG + partyId);
		if (null != userConfig) {
			return userConfig;
		}
		
		// 获取代理
		List<UserRecom> parents = this.userRecomService.getParents(partyId.toString());
		for (int i = 0; i < parents.size(); i++) {
			
			Party party = this.partyService.cachePartyBy(parents.get(i).getReco_id(), true);

			if (!Constants.SECURITY_ROLE_AGENT.equals(party.getRolename()) && !Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
				// 非代理
				continue;
			}
			
			// 获取代理配置
			PledgeGalaxyConfig agentConfig = (PledgeGalaxyConfig) this.redisHandler.get(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_CONFIG + party.getId().toString());
			if (null != agentConfig) {
				return agentConfig;
			}
		}
		
		// 获取全局配置
		return this.getGlobalConfig();
	}
	
	/**
	 * 获取配置利率Map
	 */
	public Map<String, String> getRateMap(String partyId, int days, double amount) {
		
		String projectType = this.sysparaService.find("project_type").getValue();
		
		Map<String, String> rateMap = new HashMap<>();
		PledgeGalaxyConfig config = getConfig(partyId);
		// 静态收益
		String staticIncome = config.getStaticIncomeForceValue();
		String dynamicIncome = config.getDynamicIncomeAssistValue();
		String teamIncome = config.getTeamIncomeProfitRatio();
		
		if (projectType.equals("DAPP_EXCHANGE")) {
			vickersConfig(rateMap, staticIncome, dynamicIncome, teamIncome, days, amount, partyId);
		}
		
		if (projectType.equals("DAPP_EXCHANGE_SAFEPAL5")) {
			safePal5Config(rateMap, staticIncome, dynamicIncome, teamIncome, days, amount, partyId);
		}
		
		if (projectType.equals("DAPP_EXCHANGE_IOEAI")) {
			ioeAiConfig(rateMap, staticIncome, dynamicIncome, teamIncome, days, amount, partyId);
		}
		
		return rateMap;
	}

	/**
	 * 获取所有配置 查数据库
	 */
	public List<PledgeGalaxyConfig> getAll() {
		List<PledgeGalaxyConfig> list = (List<PledgeGalaxyConfig>) this.getHibernateTemplate().find("FROM PledgeGalaxyConfig");
		return list;
	}

	/**
	 * 获取全局配置 查redis
	 */
	public PledgeGalaxyConfig getGlobalConfig() {
		return (PledgeGalaxyConfig) this.redisHandler.get(PledgeGalaxyRedisKeys.PLEDGE_GALAXY_CONFIG);
	}

	public PledgeGalaxyConfig findByPartyId(String partyId) {
		List list = this.getHibernateTemplate().find("FROM PledgeGalaxyConfig WHERE partyId=?0 ", new Object[] { partyId });
		if (list.size() > 0) {
			return (PledgeGalaxyConfig) list.get(0);
		}
		return null;
	}

	public void save(PledgeGalaxyConfig entity) {
		this.getHibernateTemplate().save(entity);
	}

	public void update(PledgeGalaxyConfig entity) {
		this.getHibernateTemplate().update(entity);

	}

	public PledgeGalaxyConfig findById(String id) {
		return this.getHibernateTemplate().get(PledgeGalaxyConfig.class, id);
	}

	public void delete(PledgeGalaxyConfig entity) {
		this.getHibernateTemplate().delete(entity);
	}
	
	private Map<String, String> vickersConfig(Map<String, String> rateMap, String staticIncome, String dynamicIncome,
			String teamIncome, int days, double amount, String partyId) {
		// 静态收益
		String[] staticSplit = staticIncome.split("\\|");
		String min = staticSplit[0].split(":")[0].split("-")[0];
		String max = staticSplit[staticSplit.length -1].split(":")[0].split("-")[1];
		if (amount >= Integer.valueOf(min) && Arith.sub(amount, Double.valueOf(max)) <= 0) {
			for (int i = 0; i < staticSplit.length; i++) {
				String value = staticSplit[i];
				String[] valueSplit = value.split(":");
				int amountMin = Integer.valueOf(valueSplit[0].split("-")[0]);
				int amountMax = Integer.valueOf(valueSplit[0].split("-")[1]);
				
				if (amount >= amountMin && amount <= amountMax) {
					String daysArea = valueSplit[1];
					String[] daysSplit = daysArea.split(";");
					for (int j = 0; j < daysSplit.length; j++) {
						String[] rateSplit = daysSplit[j].split("#");
						String day = rateSplit[0];
						if (Integer.valueOf(day) == days) {
							rateMap.put("staticRate", rateSplit[1]);
						}
					}
				}
			}
		}
		// 大于最大值
		else if (Arith.sub(amount, Double.valueOf(max)) > 0) {
			String value = staticSplit[staticSplit.length -1];
			String[] valueSplit = value.split(":");
			String daysArea = valueSplit[1];
			String[] daysSplit = daysArea.split(";");
			for (int j = 0; j < daysSplit.length; j++) {
				String[] rateSplit = daysSplit[j].split("#");
				String day = rateSplit[0];
				if (Integer.valueOf(day) == days) {
					rateMap.put("staticRate", rateSplit[1]);
				}
			}
		}
		
		// 一级代理人数
		List<String> partyLists = userRecomService.findRecomsToPartyId(partyId);
		int level_1_sum = 0;
		if (partyLists.size() >= 3) {
			for (String id : partyLists) {
				Party party = partyService.cachePartyBy(id, false);
				if (null != party) {
					Map<String, PledgeGalaxyOrder> map = (Map<String, PledgeGalaxyOrder>)pledgeGalaxyOrderService.findByPartyId(id);
					if (null != map && map.size() > 0) {
						for (PledgeGalaxyOrder galaxyOrder : map.values()) {
							if (galaxyOrder.getDays() >= 7 
									&& galaxyOrder.getStatus() == PledgeGalaxyStatusConstants.PLEDGE_SUCCESS) {
								if (Constants.SECURITY_ROLE_GUEST.equals(party.getRolename()) 
										|| party.getKyc_authority()) {
									level_1_sum ++;
									break;
								}
							}
						}
					}
				}
			}
		}
		
		// 助力收益
		String[] dynamicSplit = dynamicIncome.split("\\|");
		String levelMin = dynamicSplit[0].split(";")[0];
		// 无论直属下级拥有多少人，选择质押1天都不能享受额外的动态收益
		if (days > 1 && level_1_sum >= Integer.valueOf(levelMin)) {
			// 动态收益
			for (int i = 0; i < dynamicSplit.length; i++) {
				String levelNum = dynamicSplit[i].split(";")[0];
				String dynamicRate = dynamicSplit[i].split(";")[1];
				if (level_1_sum >= Integer.valueOf(levelNum)) {
					rateMap.put("dynamicRate", dynamicRate);
				}
			}
		}
		
		// 团队收益
		rateMap.put("teamRate", teamIncome);
		return rateMap;
	}
	
	private Map<String, String> safePal5Config(Map<String, String> rateMap, String staticIncome, String dynamicIncome,
			String teamIncome, int days, double amount, String partyId) {
		// 静态收益
        staticIncome = staticIncome.split("&")[0];
		
		String[] staticSplit = staticIncome.split("\\|");
		String min = staticSplit[0].split(":")[0].split("-")[0];
		String max = staticSplit[staticSplit.length -1].split(":")[0].split("-")[1];
		if (amount >= Integer.valueOf(min) && Arith.sub(amount, Double.valueOf(max)) <= 0) {
			for (int i = 0; i < staticSplit.length; i++) {
				String value = staticSplit[i];
				String[] valueSplit = value.split(":");
				int amountMin = Integer.valueOf(valueSplit[0].split("-")[0]);
				int amountMax = Integer.valueOf(valueSplit[0].split("-")[1]);
				
				if (amount >= amountMin && amount <= amountMax) {
					String daysArea = valueSplit[1];
					String[] daysSplit = daysArea.split(";");
					for (int j = 0; j < daysSplit.length; j++) {
						String[] rateSplit = daysSplit[j].split("#");
						String day = rateSplit[0];
						if (Integer.valueOf(day) == days) {
							rateMap.put("staticRate", rateSplit[1]);
						}
					}
				}
			}
		}
		// 大于最大值
		else if (Arith.sub(amount, Double.valueOf(max)) > 0) {
			String value = staticSplit[staticSplit.length -1];
			String[] valueSplit = value.split(":");
			String daysArea = valueSplit[1];
			String[] daysSplit = daysArea.split(";");
			for (int j = 0; j < daysSplit.length; j++) {
				String[] rateSplit = daysSplit[j].split("#");
				String day = rateSplit[0];
				if (Integer.valueOf(day) == days) {
					rateMap.put("staticRate", rateSplit[1]);
				}
			}
		}
		
		// 一级代理人数
		List<String> partyLists = userRecomService.findRecomsToPartyId(partyId);
		int level_1_sum = 0;
		if (partyLists.size() >= 3) {
			for (String id : partyLists) {
				Map<String, PledgeGalaxyOrder> map = (Map<String, PledgeGalaxyOrder>)pledgeGalaxyOrderService.findByPartyId(id);
				if (null != map && map.size() > 0) {
					for (PledgeGalaxyOrder galaxyOrder : map.values()) {
						if (galaxyOrder.getDays() >= 10 
								&& galaxyOrder.getStatus() == PledgeGalaxyStatusConstants.PLEDGE_SUCCESS) {
							level_1_sum ++;
							break;
						}
					}
				}
			}
		}
		
		// 助力收益
		String[] dynamicSplit = dynamicIncome.split("\\|");
		String levelMin = dynamicSplit[0].split(";")[0];
		// 无论直属下级拥有多少人，选择质押1天都不能享受额外的动态收益
		if (days > 1 && level_1_sum >= Integer.valueOf(levelMin)) {
			// 动态收益
			for (int i = 0; i < dynamicSplit.length; i++) {
				String levelNum = dynamicSplit[i].split(";")[0];
				String dynamicRate = dynamicSplit[i].split(";")[1];
				if (level_1_sum >= Integer.valueOf(levelNum)) {
					rateMap.put("dynamicRate", dynamicRate);
				}
			}
		}
		
		// 团队收益
		String[] teamSplit = teamIncome.split("#");
		for (int i = 0; i < teamSplit.length; i++) {
			String levelNum = teamSplit[i].split(":")[0];
			String teamRate = teamSplit[i].split(":")[1];
			if (level_1_sum >= Integer.valueOf(levelNum)) {
				rateMap.put("teamRate", teamRate);
			}
		}
		
		return rateMap;
	}
	
	private Map<String, String> ioeAiConfig(Map<String, String> rateMap, String staticIncome, String dynamicIncome,
			String teamIncome, int days, double amount, String partyId) {
		// 静态收益
		String[] staticSplit = staticIncome.split("\\|");
		String min = staticSplit[0].split(":")[0].split("-")[0];
		String max = staticSplit[staticSplit.length -1].split(":")[0].split("-")[1];
		if (amount >= Integer.valueOf(min) && Arith.sub(amount, Double.valueOf(max)) <= 0) {
			for (int i = 0; i < staticSplit.length; i++) {
				String value = staticSplit[i];
				String[] valueSplit = value.split(":");
				int amountMin = Integer.valueOf(valueSplit[0].split("-")[0]);
				int amountMax = Integer.valueOf(valueSplit[0].split("-")[1]);
				
				if (amount >= amountMin && amount <= amountMax) {
					String daysArea = valueSplit[1];
					String[] daysSplit = daysArea.split(";");
					for (int j = 0; j < daysSplit.length; j++) {
						String[] rateSplit = daysSplit[j].split("#");
						String day = rateSplit[0];
						if (Integer.valueOf(day) == days) {
							rateMap.put("staticRate", rateSplit[1]);
						}
					}
				}
			}
		}
		// 大于最大值
		else if (Arith.sub(amount, Double.valueOf(max)) > 0) {
			String value = staticSplit[staticSplit.length -1];
			String[] valueSplit = value.split(":");
			String daysArea = valueSplit[1];
			String[] daysSplit = daysArea.split(";");
			for (int j = 0; j < daysSplit.length; j++) {
				String[] rateSplit = daysSplit[j].split("#");
				String day = rateSplit[0];
				if (Integer.valueOf(day) == days) {
					rateMap.put("staticRate", rateSplit[1]);
				}
			}
		}
		
		// 助力收益
		double pledgeAmountSum = 0D;
		Map<String, PledgeGalaxyOrder> selfmap = (Map<String, PledgeGalaxyOrder>)pledgeGalaxyOrderService.findByPartyId(partyId);
		if (null != selfmap && selfmap.size() > 0) {
			for (PledgeGalaxyOrder galaxyOrder : selfmap.values()) {
				if (galaxyOrder.getStatus() == PledgeGalaxyStatusConstants.PLEDGE_SUCCESS) {
					pledgeAmountSum += galaxyOrder.getAmount();
				}
			}
		}
		
		// 伞下代理人数 是 所有下级的
		List<String> partyLists = userRecomService.findChildren(partyId);
		System.out.println("伞下代理人数 是 所有下级的 " + partyLists.size());
		int level_1_sum = 0;
		if (partyLists.size() >= 3) {
			for (String id : partyLists) {
				Party party = partyService.cachePartyBy(id, false);
				if (null != party) {
					Map<String, PledgeGalaxyOrder> map = (Map<String, PledgeGalaxyOrder>)pledgeGalaxyOrderService.findByPartyId(id);
					if (null != map && map.size() > 0) {
						for (PledgeGalaxyOrder galaxyOrder : map.values()) {
							if (galaxyOrder.getDays() >= 7 
									&& galaxyOrder.getStatus() == PledgeGalaxyStatusConstants.PLEDGE_SUCCESS) {
								if (Constants.SECURITY_ROLE_GUEST.equals(party.getRolename()) 
										|| party.getKyc_authority()) {
									System.out.println(partyId + " 的有效下级 " + party.getUsername());
									level_1_sum ++;
									break;
								}
							}
						}
						
						for (PledgeGalaxyOrder galaxyOrder : map.values()) {
							if (galaxyOrder.getStatus() == PledgeGalaxyStatusConstants.PLEDGE_SUCCESS) {
								pledgeAmountSum += galaxyOrder.getAmount();
							}
						}
					}
				}
			}
		}
		
		System.out.println("伞下代理人 总质押额度 " + pledgeAmountSum);
		
		String[] dynamicSplit = dynamicIncome.split("\\|");
		
		// 无论直属下级拥有多少人，选择质押1天都不能享受额外的动态收益
		Integer levelMin = Integer.valueOf(dynamicSplit[0].split(";")[0]);
		String[] amountSplit = dynamicSplit[0].split(";")[1].split("-");
		Integer amountMin = Integer.valueOf(amountSplit[0]);
		if (days > 1 && level_1_sum >= levelMin && pledgeAmountSum >= amountMin) {
			// 动态收益
			int levelIndex = 0;
			for (int i = 0; i < dynamicSplit.length; i++) {
				Integer levelNum = Integer.valueOf(dynamicSplit[i].split(";")[0]);
				if (level_1_sum < levelNum) {
					break;
				}
				levelIndex = i;
			}
			
			int amountIndex = 0;
			for (int i = 0; i < dynamicSplit.length; i++) {
				String dynamicAmount = dynamicSplit[i].split(";")[1];
				String[] dynamicAmountSplit = dynamicAmount.split("-");
				Integer dynamicAmountMin = Integer.valueOf(dynamicAmountSplit[0]);
				if (pledgeAmountSum < dynamicAmountMin) {
					break;
				}
				amountIndex = i;
			}
			
			if (levelIndex <= amountIndex) {
				rateMap.put("dynamicRate", dynamicSplit[levelIndex].split(";")[2]);
			} else {
				rateMap.put("dynamicRate", dynamicSplit[amountIndex].split(";")[2]);
			}
		}
		
		rateMap.put("teamRate", teamIncome);
		return rateMap;
	}
	
	/**
	 * 获取IoeAi用户级别
	 */
	public int getIoeAiLevel(String partyId) {
		
		PledgeGalaxyConfig config = getConfig(partyId);
		// 静态收益
		String dynamicIncome = config.getDynamicIncomeAssistValue();
		
		int ioeAiLevel = -1;
		
		// 助力收益
		double pledgeAmountSum = 0D;
		Map<String, PledgeGalaxyOrder> selfmap = (Map<String, PledgeGalaxyOrder>)pledgeGalaxyOrderService.findByPartyId(partyId);
		if (null != selfmap && selfmap.size() > 0) {
			for (PledgeGalaxyOrder galaxyOrder : selfmap.values()) {
				if (galaxyOrder.getStatus() == PledgeGalaxyStatusConstants.PLEDGE_SUCCESS) {
					pledgeAmountSum += galaxyOrder.getAmount();
				}
			}
		}
		
		// 伞下代理人数 是 所有下级的
		List<String> partyLists = userRecomService.findChildren(partyId);
		System.out.println("伞下代理人数 是 所有下级的 " + partyLists.size());
		int level_1_sum = 0;
		if (partyLists.size() >= 3) {
			for (String id : partyLists) {
				Party party = partyService.cachePartyBy(id, false);
				if (null != party) {
					Map<String, PledgeGalaxyOrder> map = (Map<String, PledgeGalaxyOrder>)pledgeGalaxyOrderService.findByPartyId(id);
					if (null != map && map.size() > 0) {
						for (PledgeGalaxyOrder galaxyOrder : map.values()) {
							if (galaxyOrder.getDays() >= 7 
									&& galaxyOrder.getStatus() == PledgeGalaxyStatusConstants.PLEDGE_SUCCESS) {
								if (Constants.SECURITY_ROLE_GUEST.equals(party.getRolename()) 
										|| party.getKyc_authority()) {
									System.out.println(partyId + " 的有效下级 " + party.getUsername());
									level_1_sum ++;
									break;
								}
							}
						}
						
						for (PledgeGalaxyOrder galaxyOrder : map.values()) {
							if (galaxyOrder.getStatus() == PledgeGalaxyStatusConstants.PLEDGE_SUCCESS) {
								pledgeAmountSum += galaxyOrder.getAmount();
							}
						}
					}
				}
			}
		}
		
		System.out.println("伞下代理人 总质押额度 " + pledgeAmountSum);
		
		String[] dynamicSplit = dynamicIncome.split("\\|");
		
		// 无论直属下级拥有多少人，选择质押1天都不能享受额外的动态收益
		Integer levelMin = Integer.valueOf(dynamicSplit[0].split(";")[0]);
		String[] amountSplit = dynamicSplit[0].split(";")[1].split("-");
		Integer amountMin = Integer.valueOf(amountSplit[0]);
		if (level_1_sum >= levelMin && pledgeAmountSum >= amountMin) {
			// 动态收益
			int levelIndex = 0;
			for (int i = 0; i < dynamicSplit.length; i++) {
				Integer levelNum = Integer.valueOf(dynamicSplit[i].split(";")[0]);
				if (level_1_sum < levelNum) {
					break;
				}
				levelIndex = i;
			}
			
			int amountIndex = 0;
			for (int i = 0; i < dynamicSplit.length; i++) {
				String dynamicAmount = dynamicSplit[i].split(";")[1];
				String[] dynamicAmountSplit = dynamicAmount.split("-");
				Integer dynamicAmountMin = Integer.valueOf(dynamicAmountSplit[0]);
				if (pledgeAmountSum < dynamicAmountMin) {
					break;
				}
				amountIndex = i;
			}
			
			if (levelIndex <= amountIndex) {
				ioeAiLevel = levelIndex;
			} else {
				ioeAiLevel = amountIndex;
			}
		}
		
		return ioeAiLevel;
		
	}
	
	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setPledgeGalaxyOrderService(PledgeGalaxyOrderService pledgeGalaxyOrderService) {
		this.pledgeGalaxyOrderService = pledgeGalaxyOrderService;
	}
	
}
