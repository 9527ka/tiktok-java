package project.web.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import kernel.util.StringUtils;
import kernel.web.BaseAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.web.ResultObject;
import project.cms.Cms;
import project.cms.CmsService;

/**
 * 用户端内容管理
 */
@RestController
@CrossOrigin
public class CmsController extends BaseAction {

	private Logger logger = LogManager.getLogger(CmsController.class);

	@Autowired
	private CmsService cmsService;

	private final String action = "/api/cms!";

	/**
	 * 获取 用户端内容管理
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) {
		String language = request.getParameter("lang");
		ResultObject resultObject = new ResultObject();

		try {
			if (null == language){
				throw new BusinessException("语言参数错误");
			}

			String partyId = getLoginPartyId();

			List<Cms> list = this.cmsService.findCmsListByLang(language);
			//skyxx
			if (null != list) {
				Cms cms = list.get(0);
				if(StringUtils.isNotEmpty(cms.getStoreUuid())){
					List<String> storeUuid = Arrays.asList(cms.getStoreUuid().split(","));
					if (storeUuid.contains(partyId)){
						resultObject.setData(cms);
					}
				}else {
					resultObject.setData(cms);
				}
			}
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

	/**
	 * 获取 用户端内容管理 列表
	 */
	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) {
		String language = request.getParameter("lang");

		ResultObject resultObject = new ResultObject();

		try {

			String partyId = getLoginPartyId();

			List<Cms> cacheListByModel = this.cmsService.cacheListByModelAndLanguage(language);
			List<Cms> list = new ArrayList<>();
			if (null != cacheListByModel && !cacheListByModel.isEmpty()) {
				for (Cms cms : cacheListByModel) {
					if(StringUtils.isNotEmpty(cms.getStoreUuid())){
						List<String> storeUuid = Arrays.asList(cms.getStoreUuid().split(","));
						if (storeUuid.contains(partyId)){
							list.add(cms);
						}
					}else {
						list.add(cms);
					}
				}
			}
			//skyxx
			resultObject.setData(list);

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
