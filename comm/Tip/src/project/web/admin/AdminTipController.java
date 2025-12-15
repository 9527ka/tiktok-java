package project.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import project.onlinechat.OnlineChatUserMessageService;
import project.tip.TipService;
import security.web.BaseSecurityAction;

/**
 * 管理员消息通知
 *
 */
@RestController
public class AdminTipController extends BaseSecurityAction {

	private final String action = "normal/adminTipAction!";

	@Autowired
	TipService tipService;

	@Resource
	private OnlineChatUserMessageService onlineChatUserMessageService;

	@RequestMapping(value = action + "getTips.action")
	public String getTips() {
//		if (!StringUtils.isNullOrEmpty(getLoginPartyId())) {//以前的逻辑是代理就不处理这些逻辑，现在需要处理所以需注释掉
//			return "";
//		}

		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> maps = new ArrayList<>();
//		if (StringUtils.isNullOrEmpty(getLoginPartyId())) {
			maps = tipService.getCacheSumTips(this.getUsername_login());
//		}
		result.put("tipList", maps);

		int count = Integer.parseInt(String.valueOf(onlineChatUserMessageService.getBuyUnreadCount(getLoginPartyId()).get("count")));
		result.put("unreadCount", count);

		String loginPartyId = this.getLoginPartyId();
		long mixedUnreadCount = onlineChatUserMessageService.getTotalCountMsg(loginPartyId);
		result.put("mixedUnreadCount", mixedUnreadCount);
		return JsonUtils.getJsonString(result);
	}

	@RequestMapping(value = action + "getNewTips.action")
	public String getNewTips(HttpServletRequest request) {
		if (!StringUtils.isNullOrEmpty(getLoginPartyId())) {
			return "";
		}
		
		Long time_stamp = null;
		if (!StringUtils.isNullOrEmpty(request.getParameter("time_stamp"))) {
			time_stamp = Long.valueOf(request.getParameter("time_stamp"));
		}

		String model = request.getParameter("model");
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (!StringUtils.isNullOrEmpty(model)) {
			result.put("tipList", tipService.cacheNewTipsByModel(this.getUsername_login(), time_stamp, model));
		} else {
			result.put("tipList", tipService.getCacheNewTips(this.getUsername_login(), time_stamp));
		}

		return JsonUtils.getJsonString(result);
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}
}
