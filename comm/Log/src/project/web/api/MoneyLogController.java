package project.web.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.Page;
import kernel.web.ResultObject;
import project.Constants;
import project.RedisKeys;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.orders.model.MallOrderRebate;
import project.redis.RedisHandler;

@RestController
@CrossOrigin
public class MoneyLogController extends BaseAction {
	
	private Logger logger = LogManager.getLogger(MoneyLogController.class);

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Autowired
	protected MoneyLogService moneyLogService;

	@Autowired
	protected RedisHandler redisHandler;

	@RequestMapping("api/moneylog!list.action")
	public Object list(HttpServletRequest request) throws IOException {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);

		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		String partyId = this.getLoginPartyId();
		try {
			
			String page_no = request.getParameter("page_no");
			if (StringUtils.isNullOrEmpty(page_no) 
					|| !StringUtils.isInteger(page_no) || Integer.valueOf(page_no) <= 0) {
				page_no = "1";
			}
			int pageNo = Integer.valueOf(page_no);
			String category = request.getParameter("category");
			String content_type = request.getParameter("content_type");

			String beginTime = request.getParameter("beginTime");
			String endTimeStr = request.getParameter("endTime");
			Date startTime = null;
			Date endTime = null;
			try {
				startTime = format.parse(beginTime);
				endTime = format.parse(endTimeStr);
			}catch (Exception  exception){

			}
			Page pagedQuery = moneyLogService.pagedQuery(pageNo, 20, category, content_type, partyId, startTime, endTime);
			Pattern pattern = Pattern.compile("\\[(.*?)\\]");
			for (MoneyLog log : (List<MoneyLog>) pagedQuery.getElements()) {
				String contentType = log.getContent_type();
				if (StringUtils.isNotEmpty(contentType) && contentType.equals(Constants.MONEYLOG_CONTNET_ORDER_INCOME)){
					String logType = log.getLog();
					if (StringUtils.isNotEmpty(logType)){
						logType = logType.replaceAll("订单：", "");
						log.setDetail(moneyLogService.getOrderRebate(logType));
					}
				}
				if (StringUtils.isNotEmpty(log.getLog())) {
					Matcher matcher = pattern.matcher(log.getLog());
					if (matcher.find()) {
						log.setOderNum(matcher.group(1));
					}
				}
				log.setCreateTimeStr(DateUtils.format(log.getCreateTime(), DateUtils.DF_yyyyMMddHHmmss));
			}
			resultObject.setData(pagedQuery.getElements());
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}


	@RequestMapping("api/moneylog!addNotify.action")
	public Object notify(HttpServletRequest request) throws IOException {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);

		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		String partyId = this.getLoginPartyId();
		try {
			Set<String> smembers = redisHandler.smembers(RedisKeys.SELLER_MONEY_ADD_NOTIFY + partyId);
			resultObject.setData(smembers);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}

	@RequestMapping("api/moneylog!notifyCallback.action")
	public Object notifyCallback(HttpServletRequest request) throws IOException {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);

		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		String partyId = this.getLoginPartyId();
		try {
			String logId = request.getParameter("moneyLogId");
			MoneyLog money = moneyLogService.findById(logId);
			if (!Objects.isNull(money)){
				money.setNotify(0);
				money.setNotifyTime(new Date());
				moneyLogService.update(money);
			}
			redisHandler.srem(RedisKeys.SELLER_MONEY_ADD_NOTIFY + partyId,logId);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}
		return resultObject;
	}
}
