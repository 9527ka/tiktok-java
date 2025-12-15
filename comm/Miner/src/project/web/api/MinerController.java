package project.web.api;

import java.io.IOException;
import java.util.ArrayList;
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
import kernel.web.ResultObject;
import project.miner.MinerService;
import project.miner.model.Miner;

/**
 * 矿机产品
 *
 */
@RestController
@CrossOrigin
public class MinerController {

	private Logger logger = LogManager.getLogger(MinerController.class);
	
	@Autowired
	protected MinerService minerService;
	
	private final String action = "api/miner!";

	/**
	 * 矿机产品列表
	 */
	@RequestMapping(action + "list.action")
	public Object list() throws IOException {

		ResultObject resultObject = new ResultObject();
		try {
			List<Miner> data = minerService.findAllState_1();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			if (data != null) {
				for (int i = 0; i < data.size(); i++) {
					result.add(minerService.getBindOne(data.get(i)));
				}
			}
			resultObject.setData(result);
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
	 * 矿机产品详情
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		try {
			String id = request.getParameter("id");
			Miner data = minerService.findById(id);
			resultObject.setData(minerService.getBindOne(data));
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
