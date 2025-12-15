package project.web.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.onlinechat.MessageUser;
import project.onlinechat.OnlineChatMessage;
import project.onlinechat.OnlineChatMessageService;
import project.onlinechat.OnlineChatVisitorMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.user.token.TokenService;

@RestController
@CrossOrigin
public class NewOnlineChatController extends BaseAction {

	private Logger logger = LogManager.getLogger(NewOnlineChatController.class);

	@Resource
	private OnlineChatMessageService onlineChatMessageService;
	@Resource
	private OnlineChatVisitorMessageService onlineChatVisitorMessageService;
	@Resource
	private SysparaService sysparaService;
	@Resource
	private PartyService partyService;
	@Resource
	private TokenService tokenService;

	public final String action = "api/newOnlinechat";

	@RequestMapping(action + "!list.action")
	public Object list(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		try {
			String message_id = request.getParameter("message_id");
			String token = request.getParameter("token");
			String selectType = request.getParameter("selectType");//管理后台查看消息记录
			String partyId_para = request.getParameter("partyId");//管理后台查看的目标用户
			String partyId = tokenService.cacheGet(token);
			partyId = StringUtils.isNullOrEmpty(partyId) ? this.getIp() : partyId;
			if ("background".equals(selectType)) {//后台管理查看系统聊天时传递参数
				partyId=partyId_para;
			}
			List<OnlineChatMessage> list = onlineChatMessageService.cacheGetList(message_id, 10, partyId, "user");
			if (!"background".equals(selectType)) {//后台管理查看系统聊天时，未读数不做变动
				// 首页的时候才更新未读数
				if (StringUtils.isNullOrEmpty(message_id)) {
					MessageUser cacheMessageUser = onlineChatMessageService.cacheMessageUser(partyId);
					if (cacheMessageUser != null && cacheMessageUser.getUser_unreadmsg() > 0) {
						if (partyId.indexOf(".") != -1) {
							onlineChatVisitorMessageService.updateUnread(partyId, "user", "read");
						} else {
							onlineChatMessageService.updateUnread(partyId, "user", "read");
						}
					}
				}
			}
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", list.get(i).getId().toString());
				map.put("send_receive", list.get(i).getSend_receive());
				String type = list.get(i).getType();
				map.put("type", type);
				String content = list.get(i).getContent();
				/*if ("img".equals(type)) {
					content = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + content;
				}*/
				map.put("content", content);
				map.put("targetUsername", list.get(i).getTarget_username());
				map.put("createtime", DateUtils.format(list.get(i).getCreateTime(), "yyyy-MM-dd HH:mm"));
				map.put("delete_status", list.get(i).getDelete_status());
				data.add(map);
			}
			resultObject.setData(data);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

	@RequestMapping(action + "!unread.action")
	public Object unread(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		try {
			int unreadMsg = 0;
			String token = request.getParameter("token");
			String partyId = tokenService.cacheGet(token);
			partyId = StringUtils.isNullOrEmpty(partyId) ? this.getIp() : partyId;
			unreadMsg = onlineChatMessageService.getUnreadMsg(partyId, "user", null);
			resultObject.setData(unreadMsg);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

	@RequestMapping(value = action + "!send.action")
	public Object send(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		try {
			String content = request.getParameter("content");
			String type = request.getParameter("type");
			String token = request.getParameter("token");
			if (StringUtils.isNullOrEmpty(content.trim()) || StringUtils.isNullOrEmpty(type)) {
				return resultObject;
			}

//			content = URLDecoder.decode(content, "utf-8");
			content = StringUtils.replacer(new StringBuffer(content));
			String loginPartyId = tokenService.cacheGet(token);
			if (StringUtils.isNullOrEmpty(loginPartyId)) {
				if (checkVisitorIp()) {
					return resultObject;
				}
				onlineChatVisitorMessageService.saveSend(this.getIp(), type, "send", content, null);
			} else {
				if (checkUserBlack(loginPartyId)) {
					return resultObject;
				}
				onlineChatMessageService.saveSend(loginPartyId, type, "send", content, null);
			}
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("------用户给平台发送消息方法错误，报错消息为:"+e.getMessage());
		}
		return resultObject;
	}

	/**
	 * 检验游客ip是否是黑名单，true：是，false：否
	 */
	private boolean checkVisitorIp() {
		String blackMenu = sysparaService.find("online_visitor_black_ip_menu").getValue();
		List<String> list = new ArrayList<String>(Arrays.asList(blackMenu.split(",")));
		return list.contains(this.getIp());
	}

	private boolean checkUserBlack(String loginPartyId) {
		Party party = partyService.cachePartyBy(loginPartyId, true);
		String username = party.getUsername();
		String blackMenu = sysparaService.find("online_username_black_menu").getValue();
		List<String> list = new ArrayList<String>(Arrays.asList(blackMenu.split(",")));
		return list.contains(username);
	}

}
