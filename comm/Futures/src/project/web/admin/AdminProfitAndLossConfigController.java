package project.web.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.futures.AdminProfitAndLossConfigService;
import project.futures.ProfitAndLossConfig;
import project.futures.ProfitAndLossConfigService;
import project.item.ItemService;
import project.log.LogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;

/**
 * 交割场控设置
 */
@RestController
public class AdminProfitAndLossConfigController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminProfitAndLossConfigController.class);

	@Autowired
	private AdminProfitAndLossConfigService adminProfitAndLossConfigService;
	@Autowired
	private ProfitAndLossConfigService profitAndLossConfigService;
	@Autowired
	private UserRecomService userRecomService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private SysparaService sysparaService;
	@Autowired
	private LogService logService;

	private final String action = "normal/adminProfitAndLossConfigAction!";

	/**
	 * 获取 交割场控设置 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("profit_loss_config_list");
		
		Map<String, String> type_map = Constants.PROFIT_LOSS_TYPE;
		
		try {
			
			this.checkAndSetPageNo(pageNo);
			
			this.pageSize = 30;
			
			this.page = this.adminProfitAndLossConfigService.pagedQuery(this.pageNo, this.pageSize, name_para, this.getLoginPartyId());

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				
				map.put("type", type_map.get(map.get("type")));
				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("name_para", name_para);
		return modelAndView;
	}

	/**
	 * 新增 交割场控设置 页面
	 */
	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();		
		Map<String, String> type_map = Constants.PROFIT_LOSS_TYPE;
		modelAndView.addObject("type_map", type_map);
		modelAndView.setViewName("profit_loss_config_add");
		return modelAndView;
	}
	
	/**
	 * 新增 交割场控设置
	 */
	@RequestMapping(action + "add.action")
	public ModelAndView add(HttpServletRequest request) {
		String usercode = request.getParameter("usercode");
		String type = request.getParameter("type");
		String remark = request.getParameter("remark");

		ModelAndView modelAndView = new ModelAndView();
		
		Map<String, String> type_map = Constants.PROFIT_LOSS_TYPE;

		try {
			
			if (StringUtils.isEmptyString(usercode)) {
				throw new BusinessException("请输入用户UID");
		    } 

			Party party = this.partyService.findPartyByUsercode(usercode);
			if (null == party) {
				throw new BusinessException("用户不存在");
			}

			Party party_login = this.partyService.findPartyByUsername(this.getUsername_login());
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
			profitAndLossConfig.setType(type);
			profitAndLossConfig.setRemark(remark);
			profitAndLossConfig.setPartyId(party.getId());
			
			this.profitAndLossConfigService.save(profitAndLossConfig, this.getUsername_login());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("type", type);
			modelAndView.addObject("remark", remark);
			modelAndView.addObject("type_map", type_map);
			modelAndView.setViewName("profit_loss_config_add");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("type", type);
			modelAndView.addObject("remark", remark);
			modelAndView.addObject("type_map", type_map);
			modelAndView.setViewName("profit_loss_config_add");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}
	
	/**
	 * 修改 交割场控设置 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		
		Map<String, String> type_map = Constants.PROFIT_LOSS_TYPE;

		try {

			ProfitAndLossConfig profitAndLossConfig = this.profitAndLossConfigService.findById(id);
			
			Party party = this.partyService.cachePartyBy(profitAndLossConfig.getPartyId(), true);

			modelAndView.addObject("id", id);
			modelAndView.addObject("type", profitAndLossConfig.getType());
			modelAndView.addObject("remark", profitAndLossConfig.getRemark());
			modelAndView.addObject("username", party.getUsername());
			modelAndView.addObject("usercode", party.getUsercode());
			modelAndView.addObject("type_map", type_map);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.setViewName("redirect:/" + action + "list.action");
			return modelAndView;
		}
		
		modelAndView.setViewName("profit_loss_config_update");
		return modelAndView;
	}
	
	/**
	 * 修改 交割场控设置
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String type = request.getParameter("type");
		String remark = request.getParameter("remark");
		String username = request.getParameter("username");
		String usercode = request.getParameter("usercode");

		ModelAndView modelAndView = new ModelAndView();
		
		Map<String, String> type_map = Constants.PROFIT_LOSS_TYPE;
		
		try {

			ProfitAndLossConfig profitAndLossConfig = this.profitAndLossConfigService.findById(id);

			Party party = this.partyService.cachePartyBy(profitAndLossConfig.getPartyId(), true);
			if (null == party) {
				throw new BusinessException("用户不存在");
			}
			
			Party party_login = this.partyService.findPartyByUsername(this.getUsername_login());
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
				
				if (0 == isChildren) {
					throw new BusinessException("用户不存在或者不属于登录用户名下");
				}
			}

			profitAndLossConfig.setRemark(remark);
			profitAndLossConfig.setType(type);
			
			this.profitAndLossConfigService.update(profitAndLossConfig, this.getUsername_login());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("type", type);
			modelAndView.addObject("remark", remark);
			modelAndView.addObject("username", username);
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("type_map", type_map);
			modelAndView.setViewName("profit_loss_config_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("type", type);
			modelAndView.addObject("remark", remark);
			modelAndView.addObject("username", username);
			modelAndView.addObject("usercode", usercode);
			modelAndView.addObject("type_map", type_map);
			modelAndView.setViewName("profit_loss_config_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}
	
	/**
	 * 删除 交割场控设置
	 */
	@RequestMapping(action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {
		String id = request.getParameter("id");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {

			ProfitAndLossConfig profitAndLossConfig = this.profitAndLossConfigService.findById(id);

			Party party = this.partyService.cachePartyBy(profitAndLossConfig.getPartyId(), true);
			if (null == party) {
				throw new BusinessException("用户不存在");
			}
			
			Party party_login = this.partyService.findPartyByUsername(this.getUsername_login());
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
				
				if (0 == isChildren) {
					throw new BusinessException("用户不存在或者不属于登录用户名下");
				}
			}

			this.profitAndLossConfigService.delete(id, this.getUsername_login());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

//	public String toProductProfitLoss() {
//		profit_loss_symbol = sysparaService.find("profit_loss_symbol").getValue();
//		profit_loss_type = sysparaService.find("profit_loss_type").getValue();
//		for (Item item : itemService.cacheGetAll()) {
//			symbol_map.put(item.getSymbol(), item.getSymbol());
//		}
//		return "profitLossProduct";
//	}

//	public String updateProductProfitLoss() {
//		try {
//			Syspara profit_loss_symbol_sys = sysparaService.find("profit_loss_symbol");
//			String old_profit_loss_symbol = profit_loss_symbol_sys.getValue();
//			profit_loss_symbol_sys.setValue(profit_loss_symbol);
//			sysparaService.update(profit_loss_symbol_sys);
//
//			Syspara profit_loss_type_sys = sysparaService.find("profit_loss_type");
//			String old_profit_loss_type = profit_loss_type_sys.getValue();
//			profit_loss_type_sys.setValue(profit_loss_type);
//			sysparaService.update(profit_loss_type_sys);
//			project.log.Log log = new project.log.Log();
//			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
//			log.setOperator(this.getUsername_login());
////			log.setUsername(party.getUsername());
////			log.setPartyId(entity.getPartyId());
//			log.setCreateTime(new Date());
//			log.setLog("管理员手动修改产品交割场控。原场控币种[" + old_profit_loss_symbol + "],修改后场控币种为[" + profit_loss_symbol + "]."
//					+ "原币种交割场控类型[" + Constants.PROFIT_LOSS_TYPE.get(old_profit_loss_type) + "],修改后币种交割场控类型["
//					+ Constants.PROFIT_LOSS_TYPE.get(profit_loss_type) + "]");
//			this.logService.saveSync(log);
//
//			this.message = "操作成功";
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//		} catch (Throwable t) {
//			logger.error("update error ", t);
//			this.error = "程序错误";
//		}
//		return toProductProfitLoss();
//	}

}
