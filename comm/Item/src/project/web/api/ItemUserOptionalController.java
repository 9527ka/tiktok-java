package project.web.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.item.ItemLock;
import project.item.ItemUserOptionalService;
import project.item.model.ItemUserOptional;

/**
 * 自选币种行情
 *
 */
@RestController
@CrossOrigin
public class ItemUserOptionalController extends BaseAction {

	private Logger logger = LogManager.getLogger(ItemUserOptionalController.class);

	@Autowired
	private ItemUserOptionalService itemUserOptionalService;
	
	private final String action = "/api/itemUserOptional!";

	/**
	 * 返回自选币种的行情
	 */
	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			String symbol = request.getParameter("symbol");
			List<Map<String, Object>> list = itemUserOptionalService.cacheListDataByPartyId(this.getLoginPartyId(), symbol);
			resultObject.setData(list);
			resultObject.setCode("0");
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

	/**
	 * 加入自选
	 */
	@RequestMapping(action + "add.action")
	public Object add(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		boolean lock = false;
		String loginPartyId = this.getLoginPartyId();
		try {
			
			if (ItemLock.add(loginPartyId)) {
				String symbol = request.getParameter("symbol");
				lock = true;
				if (StringUtils.isNullOrEmpty(symbol)) {
					throw new BusinessException("参数错误");
				}
				ItemUserOptional entity = new ItemUserOptional();
				entity.setPartyId(this.getLoginPartyId());
				entity.setSymbol(symbol);
				itemUserOptionalService.save(entity);
				resultObject.setCode("0");
			}else {
				throw new BusinessException("请稍后再试");
			}
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(50);
				ItemLock.remove(loginPartyId);
			}
		}
		return resultObject;
	}

	/**
	 * 删除自选币种
	 */
	@RequestMapping(action + "delete.action")
	public Object delete(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		boolean lock = false;
		String loginPartyId = this.getLoginPartyId();
		try {
			if (ItemLock.add(loginPartyId)) {
				String symbol = request.getParameter("symbol");
				lock = true;
				if (StringUtils.isNullOrEmpty(symbol)) {
					throw new BusinessException("参数错误");
				}
				itemUserOptionalService.delete(this.getLoginPartyId(), symbol);
				resultObject.setCode("0");
			}else {
				throw new BusinessException("请稍后再试");
			}
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(50);
				ItemLock.remove(loginPartyId);
			}
		}
		return resultObject;
	}
	
	/**
	 * 查询是否已加入自选
	 */
	@RequestMapping(action + "getItemOptionalStatus.action")
	public Object getItemOptionalStatus(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		try {
			String symbol = request.getParameter("symbol");
			List<ItemUserOptional> list = itemUserOptionalService.cacheListByPartyId(this.getLoginPartyId());
			Map<String, Object> data = new HashMap<String, Object>();
			if (null == list) {
				data.put("status", "0");
			} else {
				for (ItemUserOptional item : list) {
					if (symbol.equals(item.getSymbol())) {
						data.put("status", "1");
						break;
					}
				}
			}
			
			resultObject.setData(data);
			resultObject.setCode("0");
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
	
}
