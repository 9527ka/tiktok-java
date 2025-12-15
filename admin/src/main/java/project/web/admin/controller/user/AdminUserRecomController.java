package project.web.admin.controller.user;

import java.math.BigDecimal;
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
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.web.admin.service.user.AdminUserRecomService;
import security.internal.SecUserService;

/**
 * 推荐关系
 */
@RestController
public class AdminUserRecomController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminUserRecomController.class);

	@Autowired
	private AdminUserRecomService adminUserRecomService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private SecUserService secUserService;
	
	private final String action = "normal/adminUserRecomAction!";

	/**
	 * 获取用户推荐列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String query_username2 = request.getParameter("query_username2");
		String query_username = request.getParameter("query_username");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user_recom");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;
			this.page = this.adminUserRecomService.pagedQuery(this.pageNo, this.pageSize, query_username2,
					query_username, this.getLoginPartyId());

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}
			
			modelAndView.addObject("tabs", this.bulidTabs());
			modelAndView.addObject("result", this.result);

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
		modelAndView.addObject("query_username2", query_username2);
		modelAndView.addObject("query_username", query_username);
		return modelAndView;
	}

	/**
	 * 修改用户推荐 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String id = request.getParameter("id");
		String username = request.getParameter("username");
		String name_para = request.getParameter("name_para");
		String partyId = request.getParameter("partyId");
		
		ModelAndView modelAndView = new ModelAndView();

		try {

			String reco_username = "";
			String reco_usercode = "";
			
			UserRecom userRecom = this.adminUserRecomService.get(id);			
			if (userRecom != null) {
				username = this.secUserService.findUserByPartyId(userRecom.getPartyId()).getUsername();
				reco_username = this.secUserService.findUserByPartyId(userRecom.getReco_id()).getUsername();
				reco_usercode = this.partyService.cachePartyBy(userRecom.getReco_id(), true).getUsercode();
			}

			modelAndView.addObject("id", id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("reco_username", reco_username);
			modelAndView.addObject("reco_usercode", reco_usercode);
			modelAndView.addObject("name_para", name_para);
			modelAndView.addObject("partyId", partyId);
			
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
		
		modelAndView.setViewName("user_recom_update");
		return modelAndView;
	}

	/**
	 * 修改用户推荐
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String id = request.getParameter("id");
		String username = request.getParameter("username");
		String reco_username = request.getParameter("reco_username");
		String reco_usercode = request.getParameter("reco_usercode");
		String name_para = request.getParameter("name_para");
		String parent_usercode = request.getParameter("parent_usercode");
		String partyId = request.getParameter("partyId");		
		String login_safeword = request.getParameter("login_safeword");

		ModelAndView modelAndView = new ModelAndView();

		try {
			
			if (!StringUtils.isNotEmpty(parent_usercode)) {
				throw new BusinessException("新推荐人UID不能为空");
			}
			
			Party party = this.partyService.cachePartyBy(partyId, false);
			if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
				throw new BusinessException("试用用户无法修改推荐关系");
			}
			
			Party par = this.partyService.findPartyByUsercode(parent_usercode);
			if (null == par) {
				throw new BusinessException("推荐人UID不存在");
			}
			
			if (Constants.SECURITY_ROLE_TEST.equals(par.getRolename())) {
				throw new BusinessException("试用用户无法成为推荐人");
			}
			
			String parent_username = par.getUsername();
			this.adminUserRecomService.update(partyId, parent_username, this.getUsername_login(), this.getIp(),
					login_safeword);
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("reco_username", reco_username);
			modelAndView.addObject("reco_usercode", reco_usercode);
			modelAndView.addObject("name_para", name_para);
			modelAndView.addObject("parent_usercode", parent_usercode);
			modelAndView.addObject("partyId", partyId);
			modelAndView.addObject("login_safeword", login_safeword);
			modelAndView.setViewName("user_recom_update");
			return modelAndView;
		} catch (Throwable t) {
			modelAndView.addObject("error", t.getMessage());
			modelAndView.addObject("id", id);
			modelAndView.addObject("username", username);
			modelAndView.addObject("reco_username", reco_username);
			modelAndView.addObject("reco_usercode", reco_usercode);
			modelAndView.addObject("name_para", name_para);
			modelAndView.addObject("parent_usercode", parent_usercode);
			modelAndView.addObject("partyId", partyId);
			modelAndView.addObject("login_safeword", login_safeword);
			modelAndView.setViewName("user_recom_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "修改成功");
		modelAndView.setViewName("redirect:/" + action + "list.action");
		return modelAndView;
	}

}
