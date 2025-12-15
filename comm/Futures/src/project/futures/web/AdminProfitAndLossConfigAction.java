package project.futures.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.futures.AdminProfitAndLossConfigService;
import project.futures.ProfitAndLossConfig;
import project.futures.ProfitAndLossConfigService;
import project.item.ItemService;
import project.item.model.Item;
import project.log.LogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.Syspara;
import project.syspara.SysparaService;

public class AdminProfitAndLossConfigAction extends PageActionSupport {
	private static final long serialVersionUID = -1876081359521100683L;

	private static Log logger = LogFactory.getLog(AdminProfitAndLossConfigAction.class);

	private String name_para;

	private AdminProfitAndLossConfigService adminProfitAndLossConfigService;

	private ProfitAndLossConfigService profitAndLossConfigService;

	private UserRecomService userRecomService;

	private ItemService itemService;
	private PartyService partyService;
	private SysparaService sysparaService;
	private LogService logService;
	

	private String username;
	private String usercode;

	private String type = "1";

	private String remark;

	private String id;

	private Map<String, String> type_map = Constants.PROFIT_LOSS_TYPE;
	/**
	 * 上线币种
	 */
	private Map<String, String> symbol_map = new HashMap<String, String>();
	/**
	 * 币种交割场控类型
	 */
	private String profit_loss_type;
	/**
	 * 交割场控指定币种
	 */
	private String profit_loss_symbol;

	public String list() {
		this.pageSize = 30;
		this.page = this.adminProfitAndLossConfigService.pagedQuery(this.pageNo, this.pageSize, this.name_para,
				getLoginPartyId());
		for (Map<String, Object> map : (List<Map<String, Object>>) page.getElements()) {
			map.put("type", type_map.get(map.get("type")));
		}
		return "list";
	}

	public String toAdd() {
		return "add";
	}

	private String verification() {
//    if (StringUtils.isEmptyString(this.username))
//      return "请输入用户名"; 
		if (StringUtils.isEmptyString(this.usercode))
			return "请输入用户UID";

		return null;
	}

	public String add() {
		try {
			this.error = verification();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toAdd();

//      Party party = this.partyService.findPartyByUsername(this.username);
			Party party = this.partyService.findPartyByUsercode(this.usercode);

			Party party_login = this.partyService.findPartyByUsername(this.getUsername_login());
			if (party == null) {
				throw new BusinessException("用户不存在");

			}
			if (party_login != null) {
				List<String> childrens = this.userRecomService.findChildren(party_login.getId());
				double isChildren = 0;
				if (childrens != null) {
					for (String children : childrens) {
						if (party.getId().equals(children)) {
							isChildren = 1;
						}
					}
				}
				if (isChildren == 0) {
					throw new BusinessException("用户不存在或者不属于登录用户名下");
				}

			}

			ProfitAndLossConfig profitAndLossConfig = new ProfitAndLossConfig();
			profitAndLossConfig.setType(this.type);
			profitAndLossConfig.setRemark(this.remark);
			profitAndLossConfig.setPartyId(party.getId());
			this.profitAndLossConfigService.save(profitAndLossConfig, this.getUsername_login());
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error("UserAction.register error ", t);
			this.error = "[ERROR] " + t.getMessage();
			return toAdd();
		}
		return list();
	}

	public String toUpdate() {
		ProfitAndLossConfig profitAndLossConfig = this.profitAndLossConfigService.findById(this.id);
		Party party = this.partyService.cachePartyBy(profitAndLossConfig.getPartyId(), true);
		this.type = profitAndLossConfig.getType();
		this.remark = profitAndLossConfig.getRemark();
		this.username = party.getUsername();
		this.usercode = party.getUsercode();
		return "update";
	}

	public String update() {
		ProfitAndLossConfig profitAndLossConfig = this.profitAndLossConfigService.findById(this.id);
		try {

			Party party = this.partyService.cachePartyBy(profitAndLossConfig.getPartyId(), true);
			Party party_login = this.partyService.findPartyByUsername(this.getUsername_login());

			if (party == null) {
				throw new BusinessException("用户不存在");

			}
			if (party_login != null) {
				List<String> childrens = this.userRecomService.findChildren(party_login.getId());
				double isChildren = 0;
				if (childrens != null) {
					for (String children : childrens) {
						if (party.getId().equals(children)) {
							isChildren = 1;
						}
					}
				}
				if (isChildren == 0) {
					throw new BusinessException("用户不存在或者不属于登录用户名下");
				}

			}

			profitAndLossConfig.setRemark(remark);
			profitAndLossConfig.setType(this.type);
			this.profitAndLossConfigService.update(profitAndLossConfig, this.getUsername_login());
			this.message = "操作成功";
			return list();
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return "update";
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
			return "update";
		}
	}

	public String toDelete() {
		try {

			ProfitAndLossConfig profitAndLossConfig = this.profitAndLossConfigService.findById(this.id);

			Party party = this.partyService.cachePartyBy(profitAndLossConfig.getPartyId(), true);
			Party party_login = this.partyService.findPartyByUsername(this.getUsername_login());

			if (party == null) {
				throw new BusinessException("用户不存在");

			}
			if (party_login != null) {
				List<String> childrens = this.userRecomService.findChildren(party_login.getId());
				double isChildren = 0;
				if (childrens != null) {
					for (String children : childrens) {
						if (party.getId().equals(children)) {
							isChildren = 1;
						}
					}
				}
				if (isChildren == 0) {
					throw new BusinessException("用户不存在或者不属于登录用户名下");
				}

			}

			this.profitAndLossConfigService.delete(this.id, this.getUsername_login());
			this.message = "操作成功";
			return list();
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return list();
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
			return list();
		}
	}

	public String toProductProfitLoss() {
		profit_loss_symbol = sysparaService.find("profit_loss_symbol").getValue();
		profit_loss_type = sysparaService.find("profit_loss_type").getValue();
		for(Item item:itemService.cacheGetAll()) {
			symbol_map.put(item.getSymbol(), item.getSymbol());
		}
		return "profitLossProduct";
	}
	
	public String updateProductProfitLoss() {
		try {
			Syspara profit_loss_symbol_sys = sysparaService.find("profit_loss_symbol");
			String old_profit_loss_symbol = profit_loss_symbol_sys.getValue();
			profit_loss_symbol_sys.setValue(profit_loss_symbol);
			sysparaService.update(profit_loss_symbol_sys);
			
			Syspara profit_loss_type_sys = sysparaService.find("profit_loss_type");
			String old_profit_loss_type = profit_loss_type_sys.getValue();
			profit_loss_type_sys.setValue(profit_loss_type);
			sysparaService.update(profit_loss_type_sys);
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setOperator(this.getUsername_login());
//			log.setUsername(party.getUsername());
//			log.setPartyId(entity.getPartyId());
			log.setCreateTime(new Date());
			log.setLog("管理员手动修改产品交割场控。原场控币种["+old_profit_loss_symbol+"],修改后场控币种为[" + profit_loss_symbol + "]."
					+ "原币种交割场控类型["+Constants.PROFIT_LOSS_TYPE.get(old_profit_loss_type)+"],修改后币种交割场控类型["+Constants.PROFIT_LOSS_TYPE.get(profit_loss_type)+"]");
			this.logService.saveSync(log);
			
			
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
		}
		return toProductProfitLoss();
	}
	public String getName_para() {
		return this.name_para;
	}

	public void setName_para(String name_para) {
		this.name_para = name_para;
	}

	public void setAdminProfitAndLossConfigService(AdminProfitAndLossConfigService adminProfitAndLossConfigService) {
		this.adminProfitAndLossConfigService = adminProfitAndLossConfigService;
	}

	public void setProfitAndLossConfigService(ProfitAndLossConfigService profitAndLossConfigService) {
		this.profitAndLossConfigService = profitAndLossConfigService;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getType_map() {
		return type_map;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getProfit_loss_type() {
		return profit_loss_type;
	}

	public String getProfit_loss_symbol() {
		return profit_loss_symbol;
	}

	public void setProfit_loss_type(String profit_loss_type) {
		this.profit_loss_type = profit_loss_type;
	}

	public void setProfit_loss_symbol(String profit_loss_symbol) {
		this.profit_loss_symbol = profit_loss_symbol;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public Map<String, String> getSymbol_map() {
		return symbol_map;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setSymbol_map(Map<String, String> symbol_map) {
		this.symbol_map = symbol_map;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	
}
