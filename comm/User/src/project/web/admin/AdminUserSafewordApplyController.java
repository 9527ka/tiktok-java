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
import kernel.util.PropertiesUtil;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.syspara.SysparaService;
import project.user.AdminUserSafewordApplyService;

/**
 * 人工重置管理
 */
@RestController
public class AdminUserSafewordApplyController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminUserSafewordApplyController.class);

	@Autowired
	private AdminUserSafewordApplyService adminUserSafewordApplyService;
	@Autowired
	protected SysparaService sysparaService;

	private final String action = "normal/adminUserSafewordApplyAction!";

	/**
	 * 获取 人工重置管理 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String state_para = request.getParameter("state_para");
		String rolename_para = request.getParameter("rolename_para");
		String operate = request.getParameter("operate");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("user_safeword_apply_list");

		try {

			String basePath = PropertiesUtil.getProperty("admin_url");
			basePath = this.getPath(request);

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;
			
			Integer state_para_int = null;

			if (StringUtils.isNullOrEmpty(state_para)) {
				state_para_int = null;
			} else {
				state_para_int = Integer.valueOf(state_para);
			}
			
			Integer operate_int = null;

			if (StringUtils.isNullOrEmpty(operate)) {
				operate_int = null;
			} else {
				operate_int = Integer.valueOf(operate);
			}

			this.page = this.adminUserSafewordApplyService.pagedQuery(this.pageNo, this.pageSize, name_para, state_para_int,
					rolename_para, this.getLoginPartyId(), operate_int);
			
			String preImg = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=";
			
			for (Map<String, Object> map : (List<Map<String, Object>>) this.page.getElements()) {
				
				map.put("idimg_1", preImg + map.get("idimg_1").toString());
				map.put("idimg_2", preImg + map.get("idimg_2").toString());
				if ("".equals(map.get("idimg_3")) || map.get("idimg_3") == null) {
					map.put("idimg_3", preImg + "qr/id_img3.jpg");
				} else {
					map.put("idimg_3", preImg + map.get("idimg_3").toString());
				}

				map.put("kyc_idimg_1", preImg + map.get("kyc_idimg_1").toString());
				map.put("kyc_idimg_2", preImg + map.get("kyc_idimg_2").toString());
				if ("".equals(map.get("kyc_idimg_3")) || map.get("kyc_idimg_3") == null) {
					map.put("kyc_idimg_3", preImg + "qr/id_img3.jpg");
				} else {
					map.put("kyc_idimg_3", preImg + map.get("kyc_idimg_3").toString());
				}
				
				// 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
				// 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
				// 如：级别11表示：新注册的前端显示为VIP1；
				Integer userLevel = (Integer) map.get("user_level");
				if (null == userLevel) {
					userLevel = 1;
				}
				int user_level_system = (int) (userLevel % 10);
				map.put("user_level_system", user_level_system);
				
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
		modelAndView.addObject("state_para", state_para);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("operate", operate);
		return modelAndView;
	}

	/**
	 * 审核通过
	 */
	@RequestMapping(action + "savePassed.action")
	public ModelAndView savePassed(HttpServletRequest request) {
		String id = request.getParameter("id");
		String safeword = request.getParameter("safeword");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			
			this.adminUserSafewordApplyService.savePassed(id, this.getUsername_login(), safeword);
			
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

	/**
	 * 驳回
	 */
	@RequestMapping(action + "saveFailed.action")
	public ModelAndView saveFailed(HttpServletRequest request) {
		String id = request.getParameter("id");
		String msg = request.getParameter("msg");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			
			this.adminUserSafewordApplyService.saveFailed(id, msg);
			
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

	private String getPath(HttpServletRequest request) {
		return String.format("%s://%s:%s%s", request.getScheme(), request.getServerName(),
				(request.getServerPort() == 80?"":request.getServerPort()), request.getContextPath());
	}

}
