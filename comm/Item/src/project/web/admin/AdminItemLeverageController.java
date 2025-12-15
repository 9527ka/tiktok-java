package project.web.admin;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.item.AdminItemLeverageService;
import project.item.ItemService;
import project.item.model.ItemLever;

/**
 * 交易杠杆
 *
 */
@RestController
public class AdminItemLeverageController extends PageActionSupport {
	
	private static final Log logger = LogFactory.getLog(AdminItemLeverageController.class);
	@Autowired
	private AdminItemLeverageService adminItemLeverageService;
	
	private final String action = "normal/adminItemLeverageAction!";

	/**
	 * 交易杠杆-列表
	 */
	@RequestMapping(value = action + "list.action") 
	public ModelAndView list(HttpServletRequest request) {
		String itemid = request.getParameter("itemid");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 10;
		this.page = this.adminItemLeverageService.pagedQuery(this.pageNo, this.pageSize, itemid);
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		model.addObject("itemid", itemid);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("item_leverage_list");
		return model;
	}
	
	/**
	 * 新增杠杆参数
	 */
	@RequestMapping(value = action + "toAdd.action") 
	public ModelAndView toAdd(HttpServletRequest request) {
		
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String itemid = request.getParameter("itemid");
		String leverage = request.getParameter("leverage");
		
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.addObject("itemid", itemid);
		model.addObject("leverage", leverage);
		model.setViewName("item_leverage_add");
		return model;
	}

	@RequestMapping(value = action + "add.action") 
	public ModelAndView add(HttpServletRequest request) {
		
		String itemid = request.getParameter("itemid");
		String leverage = request.getParameter("leverage");
		
		ModelAndView model = new ModelAndView();
		String error = "";
		
		if (StringUtils.isNullOrEmpty(leverage) 
				|| !StringUtils.isDouble(leverage)
				|| Double.valueOf(leverage) < 1) {
			throw new BusinessException("杠杆倍数不能小于1");
		}
		
		ItemLever entity = new ItemLever();
		try {
			entity.setItem_id(itemid);
			entity.setLever_rate(Double.valueOf(leverage));
			
			this.adminItemLeverageService.save(entity);
			model.addObject("message", "操作成功");
			model.setViewName("redirect:/" + action + "list.action");
			return model;
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error("add error ", t);
			error = "程序错误";
		}
		model.addObject("error", error);
		model.setViewName("item_leverage_add");
		return model;
	}
	
	/**
	 * 交易杠杆-删除
	 */
	@RequestMapping(action + "toDelete.action")
	public ModelAndView toDelete(HttpServletRequest request) {
		
		String id = request.getParameter("id");
		ModelAndView model = new ModelAndView();
		String error = "";
		try {
			
			this.adminItemLeverageService.delete(id);
			model.addObject("message", "操作成功");
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error("add error ", t);
			error = "程序错误";
		}
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
		return model;
	}
}