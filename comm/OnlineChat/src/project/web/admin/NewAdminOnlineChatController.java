package project.web.admin;

import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import project.onlinechat.MessageUser;
import project.onlinechat.OnlineChatMessage;
import project.onlinechat.OnlineChatMessageService;
import project.onlinechat.OnlineChatVisitorMessageService;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.user.UserRedisKeys;
import project.user.token.Token;
import project.user.token.TokenService;
import systemuser.CustomerService;
import systemuser.model.Customer;

@RestController
public class NewAdminOnlineChatController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(NewAdminOnlineChatController.class);
	
	@Autowired
	OnlineChatMessageService onlineChatMessageService;
	@Autowired
	OnlineChatVisitorMessageService onlineChatVisitorMessageService;
	@Autowired
	PartyService partyService;
	@Autowired
    CustomerService customerService;
	@Autowired
	TokenService tokenService;
	@Autowired
	RedisHandler redisHandler;

	private final String action = "public/newAdminOnlineChatAction!";

	/**
	 * 在线聊天-人员列表
	 */
	@RequestMapping(value = action + "userlist.action", produces="text/html;charset=UTF-8") 
	public String userlist(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return JsonUtils.getJsonString(resultObject);
		}
		try {

			int pageno = 1;
			if (null != request.getParameter("page_no")) {
				pageno = Integer.valueOf(request.getParameter("page_no"));
			}
			
			String token = request.getParameter("token");
			
			int pageSize = 50;
			List<MessageUser> list = onlineChatMessageService
					.cacheGetMessageUserPage(pageno, pageSize, this.tokenService.cacheGet(token));
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < list.size(); i++) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", list.get(i).getId());
				Party party = null;
				party=this.partyService.cachePartyBy(list.get(i).getPartyId(), true);
				if (party != null) {
					map.put("username", party.getUsername());
					map.put("uid", party.getUsercode());
					map.put("partyid", party.getId().toString());
//					2023-04-24 需求修改，增加离开状态，用户状态为 在线1 离开2 离线3  超过10分钟算状态2 离开
					ConcurrentHashMap statusParams = (ConcurrentHashMap) redisHandler
							.get(UserRedisKeys.ONLINE_USER_STATUS_PARTYID + party.getId().toString());
					if (Objects.nonNull(statusParams) &&
							DateUtils.calcTimeBetween("s", (Date)statusParams.get("operateTime"), new Date())>300) {
//						这里一旦不发送心跳以后，操作时间超过10分钟操作时间没有更新算离线，手动退出登录也算离线
						statusParams.put("status",3);
						redisHandler.setSync(UserRedisKeys.ONLINE_USER_STATUS_PARTYID + party.getId().toString(),statusParams);
					}
					int status = Optional.ofNullable(statusParams).map(s->(int)s.get("status")).orElse(3);
					map.put("online",status);
				} else {
					map.put("username", list.get(i).getIp());
					map.put("partyid", list.get(i).getIp());
				}

				map.put("remarks", list.get(i).getRemarks());
				map.put("unreadmsg", list.get(i).getCustomer_unreadmsg());
				List<OnlineChatMessage> chats = onlineChatMessageService.cacheGetList(null, 1,
						StringUtils.isNullOrEmpty(list.get(i).getPartyId()) ? list.get(i).getIp() : list.get(i).getPartyId());
				String content = "";
				Date chatDate = null;
				if (chats.size() > 0) {
					chatDate = chats.get(0).getCreateTime();
					if ("img".equals(chats.get(0).getType())) {
						content = "[picture]";
					} else {
						content = chats.get(0).getContent();
					}
				}
				map.put("content", content);
				map.put("updatetime",
						DateUtils.format(chatDate != null ? chatDate : list.get(i).getUpdateTime(), "MM-dd HH:mm"));

				map.put("order_updatetime", chatDate != null&&chatDate.after(list.get(i).getUpdateTime()) ? chatDate : list.get(i).getUpdateTime());// 用作排序
				data.add(map);
			}
			Collections.sort(data, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> paramT1, Map<String, Object> paramT2) {
					Date date1 = (Date) paramT1.get("order_updatetime");
					Date date2 = (Date) paramT2.get("order_updatetime");
					return -date1.compareTo(date2);
				}
			});
			int start = 0;
			int end = start + pageSize;

			if (data.size() <= end) {
				end = data.size();
			}

			List<Map<String, Object>> resultData = new ArrayList<Map<String, Object>>();
			resultData.addAll(data);
			List<Map<String, Object>> subList = resultData.subList(start, end);
			resultObject.setData(subList);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("系统聊天获取聊天用户列表报错，msg:", e);
		}
		return JsonUtils.getJsonString(resultObject);
	}
	
	/**
	 * 聊天记录列表
	 */
	@RequestMapping(value = action + "list.action", produces="text/html;charset=UTF-8") 
	public String list(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return JsonUtils.getJsonString(resultObject);
		}
		try {
			String message_id = request.getParameter("message_id");
			String partyid = request.getParameter("partyid");

			List<OnlineChatMessage> list = onlineChatMessageService.cacheGetList(message_id, 30, partyid);
			// 首页的时候才更新未读数
			if (StringUtils.isNullOrEmpty(message_id) && !StringUtils.isNullOrEmpty(partyid)) {
				MessageUser cacheMessageUser = onlineChatMessageService.cacheMessageUser(partyid);
				// ip，表示游客
				if (partyid.indexOf(".") != -1 || partyid.indexOf(":") != -1) {
					if (cacheMessageUser != null && cacheMessageUser.getCustomer_unreadmsg() > 0) {
						onlineChatVisitorMessageService.updateUnread(partyid, "customer", "read");
					}
				} else {
					if (cacheMessageUser != null && cacheMessageUser.getCustomer_unreadmsg() > 0) {
						onlineChatMessageService.updateUnread(partyid, "customer", "read");
					}
				}
			}
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			int status=1;
			for (int i = 0; i < list.size(); i++) {

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", list.get(i).getId().toString());
				map.put("send_receive", list.get(i).getSend_receive());
				String type = list.get(i).getType();
				map.put("type", type);
				String content = list.get(i).getContent();
//				if ("img".equals(type)) {
//					content = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + content;
//				}
				map.put("content", content);
				map.put("createtime", DateUtils.format(list.get(i).getCreateTime(), "MM-dd HH:mm"));
				map.put("delete_status", list.get(i).getDelete_status());
//				聊天记录增加在线状态记录显示
				if ("send".equals(list.get(i).getSend_receive())&&(!StringUtils.isNullOrEmpty(partyid)) && (!(partyid.contains(".") || partyid.contains(":")))) {
					ConcurrentHashMap statusParams = (ConcurrentHashMap) redisHandler.get(UserRedisKeys.ONLINE_USER_STATUS_PARTYID + partyid);
					if (Objects.nonNull(statusParams) &&
							DateUtils.calcTimeBetween("s", (Date)statusParams.get("operateTime"), new Date())>300) {
						statusParams.put("status",3);
						redisHandler.setSync(UserRedisKeys.ONLINE_USER_STATUS_PARTYID + partyid,statusParams);
					}
					status = Optional.ofNullable(statusParams).map(s->(int)s.get("status")).orElse(3);
					map.put("online",status);
				}
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
		return JsonUtils.getJsonString(resultObject);
	}
	
	/**
	 * 发送消息
	 * 
	 */
	@RequestMapping(value = action + "send.action", produces="text/html;charset=UTF-8")
	public String send(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String token = request.getParameter("token");
		if (StringUtils.isNullOrEmpty(token)) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			return JsonUtils.getJsonString(resultObject);
		}
		String partyId = tokenService.cacheGet(token);
		if (StringUtils.isNullOrEmpty(partyId)) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			return JsonUtils.getJsonString(resultObject);
		}
		
		try {
			String partyid = request.getParameter("partyid");
			String type = request.getParameter("type");
			String content = request.getParameter("content");
			String send_time_stmp = request.getParameter("send_time_stmp");
			
			if (StringUtils.isNullOrEmpty(partyid)) {
				throw new BusinessException("暂无用户");
			}
			
			if (StringUtils.isNullOrEmpty(content.trim()) || StringUtils.isNullOrEmpty(type)) {
				throw new BusinessException("请输入内容");
			}
			
			Customer customer = customerService.cacheByUsername(this.tokenService.cacheGet(token));
			if (customer != null && customer.getOnline_state() != 1) {
				throw new BusinessException("您已下线无法发送消息");
			}

			// 文字内容乱码处理
			content = URLDecoder.decode(content, "utf-8");
			
			String loginUsername = this.tokenService.cacheGet(token);
			MessageUser messageUser = onlineChatMessageService.cacheMessageUser(partyid);
			if (!loginUsername.equals(messageUser.getTarget_username())) {
				throw new BusinessException("该用户已移交");
			}
			OnlineChatMessage onlineChatMessage = null;
			// ip，表示游客
			if (partyid.indexOf(".") != -1 || partyid.indexOf(":") != -1) {
				onlineChatMessage = onlineChatVisitorMessageService.saveSend(partyid, type, "receive", content,
						loginUsername);
			} else {
				onlineChatMessage = onlineChatMessageService.saveSend(partyid, type, "receive", content, loginUsername);
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("send_time_stmp", send_time_stmp);
			data.put("chat_id", onlineChatMessage != null ? onlineChatMessage.getId() : null);
			data.put("updatetime",
					onlineChatMessage != null ? DateUtils.format(onlineChatMessage.getCreateTime(), "MM-dd HH:mm")
							: null);
			resultObject.setData(data);
		} /*catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} */catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}

		return JsonUtils.getJsonString(resultObject);
	}
	
	/**
	 * 创建新用户消息列表
	 */
	@RequestMapping(value = action + "create.action", produces="text/html;charset=UTF-8")
	public String create(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return JsonUtils.getJsonString(resultObject);
		}
		try {
			String uid = request.getParameter("uid");
			String token = request.getParameter("token");
			
			Customer customer = customerService.cacheByUsername(this.tokenService.cacheGet(token));
			if (customer != null && customer.getOnline_state() != 1) {
				throw new BusinessException("您已下线无法接收用户");
			}
			String loginUsername = this.tokenService.cacheGet(token);
			if (uid.indexOf(".") != -1 || uid.indexOf(":") != -1) {// ip，表示游客
				MessageUser messageUser = onlineChatMessageService.cacheMessageUser(uid);
				if (messageUser == null) {// 该ip没有发起聊天
					throw new BusinessException("用户不存在");
				}
				MessageUser user = onlineChatVisitorMessageService.saveCreate(uid, loginUsername);
				resultObject.setData(user.getIp());
			} else {
				MessageUser user = onlineChatMessageService.saveCreate(uid, loginUsername);
				resultObject.setData(user.getPartyId());
			}
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return JsonUtils.getJsonString(resultObject);
	}
	
	/**
	 * 删除聊天
	 */
	@RequestMapping(value = action + "del.action")
	public String del(HttpServletRequest request) {

		HttpServletResponse response = this.getResponse();
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return JsonUtils.getJsonString(resultObject);
		}
		
		try {
			String partyid = request.getParameter("partyid");
			if (partyid.indexOf(".") != -1 || partyid.indexOf(":") != -1) {// ip，表示游客
				onlineChatMessageService.deleteByIp(partyid);
			} else {
				onlineChatMessageService.delete(partyid);
			}

		} /*catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		}*/ catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return JsonUtils.getJsonString(resultObject);
	}

	/**
	 * 查询未读消息
	 */
	@RequestMapping(value = action + "unread.action", produces="text/html;charset=UTF-8") 
	public String unread(HttpServletRequest request) {
		String token = request.getParameter("token");
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return JsonUtils.getJsonString(resultObject);
		}
		try {
			// 只有admin才有客服
			int unreadMsg = onlineChatMessageService.getUnreadMsg(null, "customer", this.tokenService.cacheGet(token));
			resultObject.setData(unreadMsg);

		} /*catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} */catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return JsonUtils.getJsonString(resultObject);
	}

	/**
	 * 设置用户备注
	 */
	@RequestMapping(value = action + "resetRemarks.action", produces="text/html;charset=UTF-8") 
	public String resetRemarks(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return JsonUtils.getJsonString(resultObject);
		}
		try {
			String partyid = request.getParameter("partyid");
			String remarks = request.getParameter("remarks");
			
			if (partyid.indexOf(".") != -1 || partyid.indexOf(":") != -1) {// ip，表示游客
				resultObject.setData(onlineChatVisitorMessageService.updateResetRemarks(partyid, remarks));
			} else {
				resultObject.setData(onlineChatMessageService.updateResetRemarks(partyid, remarks));
			}
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return JsonUtils.getJsonString(resultObject);
	}

	@RequestMapping(value = action + "getUserInfo.action", produces="text/html;charset=UTF-8")
	public String getUserInfo(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return JsonUtils.getJsonString(resultObject);
		}
		
		try {
			String partyid = request.getParameter("partyid");
			// ip，表示游客
			if (partyid.indexOf(".") != -1 || partyid.indexOf(":") != -1) {
				resultObject.setData(onlineChatVisitorMessageService.getUserInfo(partyid));
			} else {
				resultObject.setData(onlineChatMessageService.getUserInfo(partyid));
			}
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		
		return JsonUtils.getJsonString(resultObject);
	}

	
	@RequestMapping(value = action + "getChatToken.action") 
	public String getChatToken(){

		ResultObject resultObject = new ResultObject();
		try {
			Token t = this.tokenService.find(this.getUsername_login());
			if (null == t) {
				throw new BusinessException("token为空");
			}
			resultObject.setData(t.getToken());
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("loginuser:{" + this.getUsername_login() + "},error:", e);
		}
		return JsonUtils.getJsonString(resultObject);
	}
	
	@RequestMapping(value = action + "deleteOnlineChatMessage.action")
	public String deleteOnlineChatMessage(HttpServletRequest request) {
		
		String token = request.getParameter("token");
		
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return JsonUtils.getJsonString(resultObject);
		}
		try {
			
			String message_id = request.getParameter("message_id");

			onlineChatMessageService.updateMessageDelete(message_id, this.tokenService.cacheGet(token));

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}

		return JsonUtils.getJsonString(resultObject);
	}
	
	public ResultObject readSecurityContextFromSession(ResultObject resultObject) {
		HttpServletRequest request = this.getRequest();
		String token = request.getParameter("token");
		if (StringUtils.isNullOrEmpty(token)) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			return resultObject;
		}
		String partyId = tokenService.cacheGet(token);
		if (StringUtils.isNullOrEmpty(partyId)) {
			resultObject.setCode("403");
			resultObject.setMsg("请重新登录");
			return resultObject;
		}
		return resultObject;
	}
}
