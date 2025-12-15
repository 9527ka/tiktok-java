package project.web.admin.goods;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.log.LogService;
import project.mall.auto.AutoConfig;
import project.mall.event.message.SellerGoodsUpdateEvent;
import project.mall.event.model.SellerGoodsUpdateInfo;
import project.mall.goods.AdminMallGoodsService;
import project.mall.goods.GoodsAttributeCategoryService;
import project.mall.goods.GoodsAttributeService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.*;
import project.mall.type.AdminCategoryService;
import project.mall.type.model.CategoryLang;
import project.party.PartyService;
import project.syspara.SysparaService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 商品库管理
 */
@RestController
@RequestMapping("/mall/goods")
public class AdminMallGoodsController extends PageActionSupport {
    private static Log logger = LogFactory.getLog(AdminMallGoodsController.class);

    @Resource
    protected AdminMallGoodsService adminMallGoodsService;

    @Resource
    protected AdminCategoryService adminCategoryService;

    @Resource
    protected GoodsAttributeCategoryService goodsAttributeCategoryService;

    @Resource
    protected GoodsAttributeService goodsAttributeService;

    @Resource
    protected PartyService partyService;
    @Resource
    protected LogService logService;
    @Resource
    protected SecUserService secUserService;
    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected SellerGoodsService sellerGoodsService;

    @Autowired
    private SysparaService sysparaService;
    /**
     * 商品分类列表
     */
    private Map<String, String> categoryList = new LinkedHashMap<String, String>();



    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {

        this.pageSize = 20;
        Integer isShelf = null;
        String error = request.getParameter("error");
        String name = request.getParameter("name");
        String PName = request.getParameter("PName");
        if(StringUtils.isNotEmpty(request.getParameter("isShelf"))){
            isShelf = Integer.valueOf(request.getParameter("isShelf"));
        } else {
            isShelf = -2;
        }
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String message = request.getParameter("message");
        ModelAndView model = new ModelAndView("admin_goods_list");

        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            // 待取代 TODO
            //this.page = adminMallGoodsService.pagedQuery1(this.pageNo, this.pageSize, name , isShelf, startTime, endTime, PName);
            this.page = adminMallGoodsService.pagedQuery(this.pageNo, this.pageSize, name , isShelf, startTime, endTime, PName);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());

        }
        model.addObject("name",name);
        model.addObject("page",page);
        model.addObject("isShelf",isShelf);
        model.addObject("message",message);
        model.addObject("pageNo",this.pageNo);
        model.addObject("PName",PName);
        return model;
    }


    /**
     * 新增页面
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/toAdd.action")
    public ModelAndView toAdd(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        String name = request.getParameter("name");
        String pageNo = request.getParameter("pageNo");

        model.setViewName("admin_goods_add");
        model.addObject("error", error);
        model.addObject("categoryList",categoryList);
        model.addObject("name",name);
        model.addObject("pageNo",pageNo);
        try {
            List<CategoryLang> cn = adminCategoryService.findLanByCategoryId(null, "cn");
            if(CollectionUtils.isEmpty(cn)){
                throw new BusinessException("请先添加商品分类");
            }
            for (CategoryLang categoryLang : cn) {
                categoryList.put((String) categoryLang.getCategoryId(),categoryLang.getName());
            }
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/list.action");
            return model;
        } catch (Throwable t) {
            model.addObject("error", error);
            model.setViewName("redirect:/" +  "mall/goods/list.action");
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
    }

    @RequestMapping(value =  "/add.action")
    public ModelAndView add(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String goodsId = request.getParameter("goodsId");
        String categoryId = request.getParameter("categoryId");

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("goodsId", goodsId);
        model.addObject("categoryList",categoryList);
        model.addObject("pageNo", pageNo);
        try {
            if(StringUtils.isEmptyString(name)){
                throw new BusinessException("商品名称不能为空");
            }
            adminMallGoodsService.save(name, categoryId);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_goods_add");
            return model;
        } catch (Exception e) {
            logger.error("error ", e);
            model.setViewName("admin_goods_add");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/goods/list.action");
        return modelAndView;
    }

    @RequestMapping(value =  "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String goodsId = request.getParameter("goodsId");
        String lang = request.getParameter("lang");
        String message = request.getParameter("message");

        ModelAndView model = new ModelAndView();
        try {
            SystemGoods goods = this.adminMallGoodsService.findById(goodsId);
            List<SystemGoodsLang> goodsLanList = adminMallGoodsService.findLanByGoodsId(goodsId, lang);
            // 配置了中文语言的，有效状态的商品类型集合: categoryId -> categoryName
            LinkedHashMap<String, String> categoryList = this.dataStructure(goods.getCategoryId());
            // 公共商品属性记录，几万条数据
            LinkedHashMap<String, String> attributeList = this.attributeSelect(goods.getAttributeCategoryId());
            if (CollectionUtils.isEmpty(goodsLanList)) {
                model.addObject("goodsLanId",null);
                model.addObject("name",null);
                model.addObject("des",null);
                model.addObject("imgDes",null);
            } else {
                SystemGoodsLang goodsLang = goodsLanList.get(0);
                model.addObject("goodsLanId",goodsLang.getId());
                model.addObject("name",goodsLang.getName());
                model.addObject("des",goodsLang.getDes());
                model.addObject("imgDes",goodsLang.getImgDes());
                model.addObject("unit",goodsLang.getUnit());
            }

            model.addObject("error", error);
            model.addObject("goods",goods);
            model.addObject("goodsId",goodsId);
            model.addObject("categoryList",categoryList);
            model.addObject("attributeId",goods.getAttributeCategoryId());
            model.addObject("attributeList",attributeList);
            model.addObject("lang",lang);
            model.addObject("pageNo",pageNo);
            model.addObject("message",message);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/list");
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/list");
            return model;
        }

        model.setViewName("admin_goods_update");
        return model;
    }

    @RequestMapping(value =  "/update.action")
    public ModelAndView update(HttpServletRequest request, SystemGoods goods) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String lang = request.getParameter("lang");
        String goodsLanId = request.getParameter("goodsLanId");
        String content = request.getParameter("content");
        String content1 = request.getParameter("content1");
        String unit = request.getParameter("unit");
        String attributeId = request.getParameter("attributeId");

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("pageNo", pageNo);
        model.addObject("lang", lang);
        model.addObject("des", content);
        model.addObject("imgDes", content1);
        model.addObject("unit", unit);

        try {
            this.verification(goods,name,content,unit);
            SystemGoods bean = adminMallGoodsService.findById(goods.getId().toString());
            if(Objects.isNull(bean)){
                throw new BusinessException("此商品已被删除");
            }
            if (goods.getIsShelf() == 0 && goods.getSystemPrice() <=0 ){
                throw new BusinessException("上架商品单价不能小于等于0");
            }
            goods.setCreateTime(bean.getCreateTime());
//            goods.setRecTime(bean.getRecTime());
            goods.setUpTime(new Date().getTime());
            goods.setAttributeCategoryId(attributeId);
//            goods.setIsShelf(bean.getIsShelf());
//            goods.setNewTime(0L);
            adminMallGoodsService.update(goods, name, lang, content, content1, unit, goodsLanId, attributeId);
        } catch (BusinessException e) {
            LinkedHashMap<String, String> categoryList = this.dataStructure(goods.getCategoryId());
            LinkedHashMap<String, String> attributeList = this.attributeSelect(attributeId);
            model.addObject("attributeId",goods.getAttributeCategoryId());
            model.addObject("error", e.getMessage());
            model.addObject("goods", goods);
            model.addObject("goodsId", goods.getId());
            model.setViewName("admin_goods_update");
            model.addObject("categoryList", categoryList);
            model.addObject("attributeList", attributeList);
            return model;
        } catch (Throwable t) {
            LinkedHashMap<String, String> categoryList = this.dataStructure(goods.getCategoryId());
            LinkedHashMap<String, String> attributeList = this.attributeSelect(attributeId);
            model.addObject("attributeId",goods.getAttributeCategoryId());
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.addObject("categoryList", categoryList);
            model.addObject("attributeList", attributeList);
            model.addObject("goods", goods);
            model.addObject("goodsId", goods.getId());
            model.setViewName("admin_goods_update");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("lang", lang);
        modelAndView.addObject("goodsId", goods.getId());
        modelAndView.setViewName("redirect:/" +  "mall/goods/toUpdate.action");
        return modelAndView;
    }

    /**
     * 是否首页推荐
     * @param request
     * @return
     */
    @RequestMapping(value =  "/buyMinUpdate.action")
    public ModelAndView buyMinUpdate(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String buyMin = request.getParameter("buyMin");
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            adminMallGoodsService.updateBuyMin(sellerGoodsId,Integer.parseInt(buyMin));
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
        return modelAndView;
    }

    private String verification(SystemGoods goods, String name, String content, String unit) {
        if(StringUtils.isEmptyString(name)){
            throw new BusinessException("产品名称不能为空");
        }
        if(StringUtils.isEmptyString(unit)){
            throw new BusinessException("产品单位不能为空");
        }
        if(StringUtils.isEmptyString(goods.getSystemPrice().toString())){
            throw new BusinessException("请输入市场价格");
        }
        if(StringUtils.isEmptyString(content)){
            throw new BusinessException("请输入商品描述");
        }
        return null;
    }


    protected LinkedHashMap<String,String> attributeSelect(String attributeId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        // 公共商品属性记录，几万条数据，可以考虑放到公共缓存里
        List<GoodsAttributeCategory> data = goodsAttributeCategoryService.findAllAttributeCategory();
        for (GoodsAttributeCategory c : data) {
            map.put((String) c.getId(), c.getName());
        }
        return map;
    }

    private LinkedHashMap<String,String> dataStructure(String categoryId){
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        LinkedHashMap<String, String> categoryList = new LinkedHashMap<>();
        List<CategoryLang> categories = adminCategoryService.findLanByCategoryId(null,"cn" );
        for (CategoryLang category : categories) {
            map.put((String) category.getCategoryId(),category.getName());
        }
        categoryList.put(categoryId,map.get(categoryId));
        map.remove(categoryId);
        categoryList.putAll(map);
        return categoryList;
    }


    /**
     * 是否首页推荐
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateStatus.action")
    public ModelAndView updateStatus(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        String type = request.getParameter("type");
        String duration = request.getParameter("duration") == null ? "0" : request.getParameter("duration");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            adminMallGoodsService.updateStatus(id,Integer.parseInt(status),Integer.parseInt(type),Integer.parseInt(duration));
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
        return modelAndView;
    }

    /**
     * 批量更新折扣和利润比例
     *
     * @param request
     * @return
     */
    @RequestMapping("updateCheckFlag.action")
    public ModelAndView updateCheckFlag(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String checkFlag = request.getParameter("checkFlag");

        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(id);
            sellerGoods.setCheckFlag(checkFlag);
            if(checkFlag.equals("1")){
                sellerGoods.setIsShelf(0);
            }
            sellerGoodsService.updateSellerGoods(sellerGoods);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
        return modelAndView;
    }

    @RequestMapping(value =  "/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();

        try {
            String login_safeword = request.getParameter("login_safeword");
            if (StringUtils.isNullOrEmpty(login_safeword)) {
                model.addObject("error", "请输入登录人资金密码");
                model.setViewName("redirect:/" +  "invest/goods/list.action");
                return model;
            }
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
            checkLoginSafeword(sec,this.getUsername_login(), login_safeword);

            String id = request.getParameter("id");
            List<SystemGoodsLang> lanByGoodsId = adminMallGoodsService.findLanByGoodsId(id, null);
            this.adminMallGoodsService.delete(id,lanByGoodsId);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setPartyId(sec.getPartyId());
            log.setOperator(this.getUsername_login());
            lanByGoodsId.forEach(e ->{
                if(e.getLang().equals("cn")){
                    log.setLog("管理员手动删除商品["+e.getName() +"] 操作ip:["+this.getIp(getRequest())+"]" + "Time [" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss")+ "]");
                }
            });

            logService.saveSync(log);
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "mall/goods/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/goods/list.action");
            return model;
        }
    }

    @RequestMapping(value =  "/importUrl.action")
    public ModelAndView importUrl(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        try {
            JSONObject json = new JSONObject();
            String url = request.getParameter("url");
            String categoryId = request.getParameter("categoryId");
            json.put("category",categoryId);
            json.put("url",url);
            String post = HttpUtil.post(AutoConfig.getAdminUrl()+"/api/item/collect", json.toJSONString(),20000);
            JSONObject jsonObject = JSON.parseObject(post);
            int code = (int)jsonObject.get("code");
            if(code!=200){
                throw new BusinessException("导入失败");
            }
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "mall/goods/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.setViewName("redirect:/" +  "mall/goods/list.action");
            model.addObject("error", "程序错误");
            return model;
        }
    }


    /**
     * 商家店铺商品列表
     * @param request
     * @return
     */
    @RequestMapping("/sellerGoodsList.action")
    public ModelAndView sellerGoodsList(HttpServletRequest request,@RequestParam Map<String, String> allParams) {
        this.pageSize = 20;
        String error = request.getParameter("error");
        String goodName = request.getParameter("goodName");
        String PName = request.getParameter("PName");
        String goodId = request.getParameter("goodId");
        String sellerName = request.getParameter("sellerName");
        String categoryId = request.getParameter("categoryId");// 一级分类
        String secondaryCategoryId = request.getParameter("secondaryCategoryId");
        String sellerRoleName = request.getParameter("sellerRoleName");
        String message = request.getParameter("message");
        String messages = request.getParameter("messages");

        ModelAndView model = new ModelAndView("admin_system_goods_list");
        model.addObject("autoShelveUrl",this.getAutoShelveUrl(request));
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            // 待取代 TODO
            //this.page = adminMallGoodsService.pagedQuerySellerGoods1(this.pageNo, this.pageSize, goodId, goodName, PName, sellerName, categoryId, secondaryCategoryId,getLoginPartyId());
            this.page = adminMallGoodsService.pagedQuerySellerGoods(this.pageNo, this.pageSize, goodId, goodName, PName, sellerName, categoryId, secondaryCategoryId,getLoginPartyId(),sellerRoleName);
            List<Map> list = this.page.getElements();
            for (int i = 0; i < list.size(); i++) {
                Map map = list.get(i);
                Long systemNewTime = (Long) map.get("systemNewTime");
                if (systemNewTime > 0){
                    map.put("systemNewTime", DateUtils.getLongDate(new Date(systemNewTime)));
                }
                Long systemRecTime = (Long) map.get("systemRecTime");
                if (systemRecTime > 0){
                    map.put("systemRecTime", DateUtils.getLongDate(new Date(systemRecTime)));
                }
                Long sellWellTime = (Long) map.get("sellWellTime");
                if (sellWellTime > 0){
                    map.put("sellWellTime", DateUtils.getLongDate(new Date(sellWellTime)));
                }
            }
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("page",this.page);
        model.addObject("goodName",goodName);
        model.addObject("goodId",goodId);
        model.addObject("PName",PName);
        model.addObject("sellerName",sellerName);
        model.addObject("message",message);
        if(!StringUtils.isEmptyString(messages)){
            model.addObject("message",messages);
        }
        model.addObject("pageNo",pageNo);
        model.addObject("PName",PName);
        model.addObject("error",error);
        model.addObject("categoryId",categoryId);
        model.addObject("secondaryCategoryId",secondaryCategoryId);
        model.addObject("categoryLevel1Map",adminCategoryService.getParentCategory(null,1));
        model.addObject("categoryLevel2Map",adminCategoryService.getParentCategory(null,2));
        model.addObject("sellerRoleName",sellerRoleName);
        return model;
    }

    private String getAutoShelveUrl(HttpServletRequest request) {
        return this.getExtUrl(request,sysparaService,"/goods/settings");
    }


    /**
     * 商家店铺商品评论列表
     * @param request
     * @return
     */
    @RequestMapping("/evaluation.action")
    public ModelAndView evaluation(HttpServletRequest request) {
        this.pageSize = 20;
        String error = request.getParameter("error");
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        String sellerId = request.getParameter("sellerId");
        String userName = request.getParameter("userName");
        Integer evaluationType = request.getParameter("evaluationType") == null ? -2 : Integer.parseInt(request.getParameter("evaluationType"));
        ModelAndView model = new ModelAndView("admin_goods_evaluation");
        model.addObject("sellerGoodsId",sellerGoodsId);
        model.addObject("message",message);
        model.addObject("pageNo",pageNo);
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = adminMallGoodsService.pagedQueryEvaluation(this.pageNo, this.pageSize, sellerGoodsId, sellerId,userName, evaluationType);
            model.addObject("page",this.page);
            return model;
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
    }

    @RequestMapping(value =  "/deleteEvaluation.action")
    public ModelAndView deleteEvaluation(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();

        try {
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());

            String id = request.getParameter("id");
            Evaluation e = adminMallGoodsService.findEvaluationById(id);
            this.adminMallGoodsService.deleteEvaluation(e);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setPartyId(sec.getPartyId());
            log.setOperator(this.getUsername_login());
            log.setLog("管理员手动删除评论["+e.getContent() +"] 操作ip:["+this.getIp(getRequest())+"]" + "Time [" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss")+ "]");
            logService.saveSync(log);
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "mall/goods/evaluation.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/evaluation.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/goods/evaluation.action");
            return model;
        }
    }

    /**
     * 批量设置店铺商品上下架
     * @param ids
     * @return
     */
    @RequestMapping(value = "/shelfBatch.action")
    public Map shelfBatch(@RequestParam String[] ids, @RequestParam int isShelf) {

        Map map = new HashMap(2);
        try {
            List<String> sellerGoodsIdList = Arrays.stream(ids).filter(id -> !id.equals("on")).collect(Collectors.toList());

            adminMallGoodsService.adminShelfBatch(sellerGoodsIdList,isShelf);

            // 发布商品信息变更的事件
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            for (String oneSellerGoodId : sellerGoodsIdList) {
                SellerGoodsUpdateInfo info = new SellerGoodsUpdateInfo();
                info.setSellerGoodsId(oneSellerGoodId);
                info.setUpdateTime(System.currentTimeMillis());
                wac.publishEvent(new SellerGoodsUpdateEvent(this, info));
            }
            map.put("message","操作成功");
            map.put("code", 200);
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }

    /**
     * 用户评论禁用启用
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateEvaluationStatus.action")
    public ModelAndView updateEvaluationStatus(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            adminMallGoodsService.updateEvaluationStatus(id,Integer.parseInt(status));
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/evaluation.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/goods/evaluation.action");
            return model;
        }
        model.addObject("message", "操作成功");
        model.addObject("pageNo", pageNo);
        model.setViewName("redirect:/" +  "mall/goods/evaluation.action");
        return model;
    }


    /**
     * 验证登录人资金密码
     * @param operatorUsername
     * @param loginSafeword
     */
    protected void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
        String sysSafeword = secUser.getSafeword();
        String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("登录人资金密码错误");
        }
    }
}