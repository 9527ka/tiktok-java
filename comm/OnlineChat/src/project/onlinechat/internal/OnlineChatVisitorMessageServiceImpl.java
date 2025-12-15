package project.onlinechat.internal;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kernel.util.ThreadUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import project.onlinechat.MessageUser;
import project.onlinechat.OnlineChatMessage;
import project.onlinechat.OnlineChatMessageService;
import project.onlinechat.OnlineChatVisitorMessageService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.tip.model.Tip;
import systemuser.CustomerService;
import systemuser.model.Customer;

@EnableAsync
public class OnlineChatVisitorMessageServiceImpl extends HibernateDaoSupport
		implements OnlineChatVisitorMessageService {

	private OnlineChatMessageService onlineChatMessageService;
	private TipService tipService;
	private CustomerService customerService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public OnlineChatMessage saveSend(String ip, String type, String send_receive, String content, String username) {
		OnlineChatMessage onlineChatMessage = new OnlineChatMessage();
//		onlineChatMessage.setPartyId(ip);
		onlineChatMessage.setType(type);
		onlineChatMessage.setSend_receive(send_receive);
		onlineChatMessage.setContent(content);
		onlineChatMessage.setCreateTime(new Date());
		onlineChatMessage.setUsername(username);
		onlineChatMessage.setIp(ip);

		this.getHibernateTemplate().save(onlineChatMessage);
		List<OnlineChatMessage> list = onlineChatMessageService.cacheMessage(ip);
		if (list == null) {
			list = new LinkedList<OnlineChatMessage>();
		}
		list.add(onlineChatMessage);
		Collections.sort(list);
		Collections.reverse(list);// 添加完后，时间倒叙排序加回
		onlineChatMessageService.putMessage(ip, list);
		if (onlineChatMessageService.cacheMessageUser(ip) == null) {// 不存在则添加用户
			saveCreate(ip,username);
		}
		switch (send_receive) {
		case "receive":// 客服发送
			updateUnread(ip, "user", "write");
			break;
		case "send":// 用户发送
			updateUnread(ip, "customer", "write");
//			tipService.saveTip(onlineChatMessage.getId().toString(), TipConstants.ONLINECHAT);
			break;
		}
		return onlineChatMessage;
	}

	/**
	 * 更新未读数
	 * 
	 * @param ip
	 * @param user_customer 更新对象，用户，客服
	 * @param type          read:读，write：写
	 */
	public void updateUnread(final String ip, String user_customer, String type) {
		MessageUser messageUser = onlineChatMessageService.cacheMessageUser(ip);
		if (messageUser == null) {
			saveCreate(ip,null);
			messageUser = onlineChatMessageService.cacheMessageUser(ip);
		}
		int removeTipNum = 0;
		switch (user_customer) {
		case "user":
			if ("read".equals(type)) {
				messageUser.setUser_unreadmsg(0);
			} else if ("write".equals(type)) {
				messageUser.setUser_unreadmsg(messageUser.getUser_unreadmsg() + 1);
				messageUser.setDelete_status(0);
			}
			break;
		case "customer":
			if ("read".equals(type)) {
				removeTipNum = messageUser.getCustomer_unreadmsg();
				messageUser.setCustomer_unreadmsg(0);
			} else if ("write".equals(type)) {
				messageUser.setCustomer_unreadmsg(messageUser.getCustomer_unreadmsg() + 1);
				messageUser.setDelete_status(0);
				final String targetUsername = onlineChatMessageService.userSendTarget(ip, new Date(), messageUser.getTarget_username());
				if (StringUtils.isNotEmpty(targetUsername)
						&& !targetUsername.equals(messageUser.getTarget_username())) {
					final Customer customer = customerService.cacheByUsername(targetUsername);
					// 客服不存在或者回复内容无效则不回复
					if (customer != null && customer.getAuto_answer() != null
							&& !StringUtils.isEmptyString(customer.getAuto_answer().trim())) {
						// 客服自动回复一条
//						saveSend(ip, "text", "receive", customer.getAuto_answer(), targetUsername + "_SYSTEM");
						ThreadUtils.sleep(1000);
						asyncMethod(customer,ip,targetUsername);
					}
				}
				messageUser.setTarget_username(targetUsername);
				if (StringUtils.isNotEmpty(targetUsername)) {// 指定的在线客服存在，则发起通知
					Tip tip = new Tip();
					tip.setBusiness_id(onlineChatMessageService.cacheMessage(ip).get(0).getId().toString());
					tip.setModel(TipConstants.ONLINECHAT);
					tip.setTarget_username(targetUsername);
					tipService.saveTip(tip);
				}
			}
			break;
		}
		onlineChatMessageService.updateMessageUserByIp(messageUser);
		if (removeTipNum > 0)
			onlineChatMessageService.removeTips(messageUser.getIp(), removeTipNum);
	}

	@Async
	public void asyncMethod(final  Customer customer, final String ip,String targetUsername){
		ThreadUtils.sleep(1000);
		saveSend(ip, "text", "receive", customer.getAuto_answer(), targetUsername + "_SYSTEM");// 客服自动回复一条
	}

	@Override
	public MessageUser saveCreate(String ip,String username) {
		MessageUser messageUser = onlineChatMessageService.cacheMessageUser(ip);
		if (messageUser == null) {
			messageUser = new MessageUser();
			messageUser.setIp(ip);
		}
		messageUser.setUpdateTime(new Date());
		messageUser.setDelete_status(0);
		messageUser.setTarget_username(username);
		this.getHibernateTemplate().saveOrUpdate(messageUser);
		onlineChatMessageService.putMessageUser(ip, messageUser);
		return messageUser;
	}

	/**
	 * 设置备注
	 * 
	 * @param ip
	 * @param remarks
	 */
	public String updateResetRemarks(String ip, String remarks) throws Exception {
		if (StringUtils.isEmptyString(remarks) || StringUtils.isEmptyString(remarks.trim())) {
			return null;
		}
		MessageUser messageUser = onlineChatMessageService.cacheMessageUser(ip);
		if (messageUser == null) {
			throw new BusinessException("用户不存在");
		}
		messageUser.setRemarks(URLDecoder.decode(remarks, "utf-8"));
		onlineChatMessageService.updateMessageUserByIp(messageUser);
		return remarks;
	}

	/**
	 * 获取用户信息
	 * 
	 * @param ip
	 * @return
	 */
	public Map<String, Object> getUserInfo(String ip) {
		MessageUser messageUser = this.onlineChatMessageService.cacheMessageUser(ip);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("partyId", ip);
		result.put("remarks", messageUser.getRemarks());
//		result.put("username", party.getUsername());
//		result.put("usercode", party.getUsercode());
//		result.put("last_login_time", DateUtils.format(party.getLast_loginTime(),DateUtils.DF_yyyyMMddHHmmss));
//		result.put("create_time", DateUtils.format(party.getCreateTime(),DateUtils.DF_yyyyMMddHHmmss));
//		result.put("role_name", party.getRolename());
//		result.put("login_ip", party.getLogin_ip());
//		List<UserRecom> parents = userRecomService.getParents(party.getId());
//		if(!CollectionUtils.isEmpty(parents)&&parents.size()>=2) {
//			Party parentParty = partyService.cachePartyBy(parents.get(1).getPartyId(), true);
//			result.put("recom_parent_name", parentParty==null?null:parentParty.getUsername());
//		}else {
//			result.put("recom_parent_name", null);
//		}
		return result;
	}

	public void setOnlineChatMessageService(OnlineChatMessageService onlineChatMessageService) {
		this.onlineChatMessageService = onlineChatMessageService;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	
}
