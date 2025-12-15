package project.web.admin.kyc;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import ext.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.PropertiesUtil;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.mall.MallRedisKeys;
import project.onlinechat.ChatUser;
import project.party.PartyService;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.user.kyc.AdminKycService;

/**
 * 用户基础认证
 */
@RestController
public class AdminKycController extends PageActionSupport {

	private final Logger logger = LoggerFactory.getLogger(AdminKycController.class);

	@Autowired
	private AdminKycService adminKycService;

	@Autowired
	private PartyService partyService;

	@Resource
	private SysparaService sysparaService;

	@Resource
	private RedisHandler redisHandler;

	private final String action = "normal/adminKycAction!";

	/**
	 * 获取 用户基础认证 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String state_para = request.getParameter("state_para");
		String rolename_para = request.getParameter("rolename_para");
		String idnumber_para = request.getParameter("idnumber_para");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sellerName = request.getParameter("sellerName");
		String username_parent = request.getParameter("username_parent");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("kyc_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 15;
			this.page = this.adminKycService.pagedQuery(this.pageNo, this.pageSize, name_para, state_para,
					rolename_para, getLoginPartyId(), idnumber_para, null,startTime,endTime,sellerName, username_parent);

			for (Map<String, Object> map : (List<Map<String, Object>>) this.page.getElements()) {
				map.put("name_encode", map.get("name").toString().replace("\'", "\\\'").replace("\"", "\\\""));
//				map.put("nationality", Constants.COUNTRY_CODE.get(map.get("nationality")));
				if ("".equals(map.get("idimg_1")) || map.get("idimg_1") == null) {
					map.put("idimg_1",  "qr/id_img1.jpg");
				} else {
					map.put("idimg_1",  map.get("idimg_1").toString());
				}
				
				if ("".equals(map.get("idimg_2")) || map.get("idimg_2") == null) {
					map.put("idimg_2", "qr/id_img2.jpg");
				} else {
					map.put("idimg_2",  map.get("idimg_2").toString());
				}

				if ("".equals(map.get("idimg_3")) || map.get("idimg_3") == null) {
					map.put("idimg_3",  "qr/id_img3.jpg");
				} else {
					map.put("idimg_3",  map.get("idimg_3").toString());
				}
				
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
		modelAndView.addObject("sellerName", sellerName);
		modelAndView.addObject("idnumber_para", idnumber_para);
		modelAndView.addObject("username_parent", username_parent);
		modelAndView.addObject("platformName", sysparaService.find("platform_name").getValue());
		return modelAndView;
	}

	/**
	 * 审核通过
	 */
	@RequestMapping(action + "savePassed.action")
	public ModelAndView savePassed(HttpServletRequest request) {
		String partyId = request.getParameter("partyId");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		String lockKey = MallRedisKeys.USER_KYC_LOCK + partyId;
		if (!redisHandler.lock(lockKey, 5)) {
			throw new BusinessException("操作频繁，请稍后再试");
		}
		
		try {

			this.adminKycService.savePassed(partyId);
			
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
		String partyId = request.getParameter("partyId");
		String msg = request.getParameter("msg");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			String lockKey = MallRedisKeys.USER_KYC_LOCK + partyId;
			if (!redisHandler.lock(lockKey, 5)) {
				throw new BusinessException("操作频繁，请稍后再试");
			}
			this.adminKycService.saveFailed(partyId, msg);

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


//	/**
//	 * 通过之后驳回
//	 */
//	@RequestMapping(action + "saveFaileds.action")
//	public ModelAndView saveFaileds(HttpServletRequest request) {
//		String partyId = request.getParameter("failedPartyId");
//		String msg = request.getParameter("msg");
//
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("redirect:/" + action + "list.action");
//
//		try {
//
//			this.adminKycService.saveFaileds(partyId, msg);
//
//		} catch (BusinessException e) {
//			modelAndView.addObject("error", e.getMessage());
//			return modelAndView;
//		} catch (Throwable t) {
//			logger.error("update error ", t);
//			modelAndView.addObject("error", "程序错误");
//			return modelAndView;
//		}
//
//		modelAndView.addObject("message", "操作成功");
//		return modelAndView;
//	}

	/**
	 * 修改认证图片
	 */
	@RequestMapping(action + "updateKycPic.action")
	public ModelAndView updateKycPic(HttpServletRequest request) {
		String partyId = request.getParameter("partyId_updateKycPic");
		String imgId = request.getParameter("img_id_updateKycPic");
		String img = request.getParameter("img_updateKycPic");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			
			this.adminKycService.saveKycPic(partyId, imgId, img);
			
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
	 * 管理后台更新备注
	 */
	@RequestMapping(action +"updateRemarks.action")
	public ModelAndView updateRemarks(HttpServletRequest request) {
		String partyId = request.getParameter("partyId");
		String remarks = request.getParameter("remarks");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		try {
//			this.adminKycService.updateRemarks(partyId, remarks);
			this.partyService.updateUserRemark(partyId, remarks);
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		return modelAndView;
	}

	private String getPath(HttpServletRequest request) {
		return String.format("%s://%s:%s%s", request.getScheme(), request.getServerName(),
				(request.getServerPort() == 80?"":request.getServerPort()), request.getContextPath());
	}
	
}
