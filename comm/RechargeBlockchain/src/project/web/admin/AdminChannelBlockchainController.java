package project.web.admin;

import ext.Strings;
import ext.Types;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.blockchain.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 区块链充值地址维护
 */
@RestController
public class AdminChannelBlockchainController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminChannelBlockchainController.class);

	@Autowired
	private AdminChannelBlockchainService adminChannelBlockchainService;
	@Autowired
	private ChannelBlockchainService channelBlockchainService;
	@Autowired
	private QRProducerService qRProducerService;

	private final String action = "normal/adminChannelBlockchainAction!";

	/**
	 * 获取 区块链充值地址 列表
	 * 
	 * name_para 链名称
	 * coin_para 币种名称
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String coin_para = request.getParameter("coin_para");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("channel_blockchain_list");
		try {
			this.checkAndSetPageNo(pageNo);
			this.pageSize = 300;
			this.page = this.adminChannelBlockchainService.pagedQuery(this.pageNo, this.pageSize, name_para, coin_para);
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
		modelAndView.addObject("coin_para", coin_para);
		return modelAndView;
	}


	@RequestMapping(action + "toAdd.action")
	public ModelAndView toAdd(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("channel_blockchain_add");
		if(!request.getMethod().equals("GET")) {
			try {
				this.adminChannelBlockchainService.toAdd(request);
				modelAndView.addObject("succes","操作成功");
			} catch (Exception e) {
				modelAndView.addObject("error", e.getMessage());
			}
		}
		return modelAndView;
	}

	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("channel_blockchain_update");
		String id = request.getParameter("id");

        ChannelBlockchain chan = this.adminChannelBlockchainService.selectById(id);
        modelAndView.addObject("entity", chan);
        modelAndView.addObject("id",id);
		if(!request.getMethod().equals("GET")) {
			try {
				ResultObject update = this.adminChannelBlockchainService.toUpdate(request);
				if(update.getCode().equals("200")) {
					modelAndView.addObject("message", "操作成功");
				}else{
					modelAndView.addObject("error", update.getMsg());
					modelAndView.setViewName("redirect:/" + action + "list.action?id="+id);
				}
			} catch (Exception e) {
				modelAndView.addObject("error", e.getMessage());
				modelAndView.setViewName("redirect:/" + action + "list.action?id="+id);
			}
		}
		return modelAndView;
	}

	@RequestMapping(action + "toDelete.action")
	public ResultObject toDelete(HttpServletRequest request) {
		return this.adminChannelBlockchainService.toDelete(request);
	}

	@RequestMapping(action + "selectById.action")
	public ResultObject selectById(HttpServletRequest request) {
		return this.adminChannelBlockchainService.selectById(request);
	}

	private String verif(String blockchain_name, String coin, String address, String img) {
		if (StringUtils.isEmptyString(blockchain_name))
			return "请输入链名称";
		if (StringUtils.isEmptyString(coin))
			return "请输入币种";
		if (StringUtils.isEmptyString(address))
			return "请输入地址";
		return null;
	}



	@RequestMapping(action + "personList.action")
	public ModelAndView personList(HttpServletRequest request) {
		String address = request.getParameter("address");
		String pageNoStr = request.getParameter("pageNo");
		String roleName = request.getParameter("roleName");
		String username = request.getParameter("username");
		String chainName = request.getParameter("chainName");
		String coinSymbol = request.getParameter("coinSymbol");
		String fromPage = request.getParameter("from_page");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("person_blockchain_list");
		modelAndView.addObject("from_page", Types.orValue(fromPage,"").toLowerCase());
		try {

			if("agent".equals(fromPage) && !Strings.isNullOrEmpty(username)){
				// 从代理列表页面进入,则初始化用户的区块链地址
				this.adminChannelBlockchainService.initialPersonBlockChains(username);
			}

			this.checkAndSetPageNo(pageNoStr);
			this.pageSize = 20;
			page=adminChannelBlockchainService.pagedPersonQuery(pageNo, pageSize,username,roleName,chainName,coinSymbol,address);

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
		modelAndView.addObject("roleName", roleName);
		modelAndView.addObject("address", address);
		modelAndView.addObject("username", username);
		modelAndView.addObject("chainName", chainName);
		modelAndView.addObject("coinSymbol", coinSymbol);
		return modelAndView;
	}


	@RequestMapping(action + "personAdd.action")
	public ModelAndView personAdd(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("channel_blockchain_add");
		if(!request.getMethod().equals("GET")) {
			try {
				this.adminChannelBlockchainService.toAdd(request);
				modelAndView.addObject("succes","操作成功");
			} catch (Exception e) {
				modelAndView.addObject("error", e.getMessage());
			}
		}
		return modelAndView;
	}

	@RequestMapping(action + "personUpdate.action")
	public ModelAndView personUpdate(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("person_blockchain_update");
		String id = request.getParameter("id");
		String username = request.getParameter("username");
		String fromPage = request.getParameter("from_page");
		modelAndView.addObject("username",username);
		modelAndView.addObject("from_page",fromPage);
		modelAndView.addObject("id",id);

		if(request.getMethod().equals("GET")) {
			PartyBlockchain chan = this.adminChannelBlockchainService.selectPersonById(id);
			modelAndView.addObject("entity", chan);
		}else{
			try {
				ResultObject update = this.adminChannelBlockchainService.updatePersonBlockChain(request);
				if(update.getCode().equals("200")) {
					modelAndView.addObject("message", "操作成功");
					modelAndView.setViewName("redirect:/" + action + "personList.action?username=" + username + "&from_page=" + fromPage);
				}else{
					modelAndView.addObject("error", update.getMsg());
					modelAndView.setViewName("redirect:/" + action + "personList.action?username=" + username + "&from_page=" + fromPage);
				}
			} catch (Exception e) {
				modelAndView.addObject("error", e.getMessage());
				modelAndView.setViewName("redirect:/" + action + "personList.action");
			}
		}
		return modelAndView;
	}


	@RequestMapping(action + "personDelete.action")
	public ResultObject personDelete(HttpServletRequest request) {
		return this.adminChannelBlockchainService.personDelete(request);
	}
}
