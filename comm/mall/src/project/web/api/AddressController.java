package project.web.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.MallRedisKeys;
import project.mall.area.MallAddressAreaService;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallAddress;
import project.redis.RedisHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@CrossOrigin
public class AddressController extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Resource
    private GoodsOrdersService goodsOrdersService;

    @Resource
    private MallAddressAreaService mallAddressAreaService;

    @Resource
    private RedisHandler redisHandler;

    private final String action = "/api/address!";

    /**
     * 添加
     *
     * @return
     */
    @PostMapping(action + "add.action")
    public Object add(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        String use = request.getParameter("use");
        int isUse = 0;
        if (use != null) {
            isUse = Integer.parseInt(use);
        }
        String phone = request.getParameter("phone");

        if (StringUtils.isEmptyString(phone)) {
            resultObject.setCode("1");
            resultObject.setMsg("手机号不能为空");
            return resultObject;
        }

        String contacts = request.getParameter("contacts");

        if (StringUtils.isEmptyString(contacts)) {
            resultObject.setCode("1");
            resultObject.setMsg("联系人不能为空");
            return resultObject;
        }
        String address = request.getParameter("address");

        if (StringUtils.isEmptyString(address)) {
            resultObject.setCode("1");
            resultObject.setMsg("地址不能为空");
            return resultObject;
        }

        if (address.length() > 255) {
            resultObject.setCode("1");
            resultObject.setMsg("地址过长");
            return resultObject;
        }
        if (contacts.length()>64) {
            resultObject.setCode("1");
            resultObject.setMsg("收货人姓名不能超过64个字符");
            return resultObject;
        }


        String email = request.getParameter("email");
        String postcode = request.getParameter("postcode");
        String country = request.getParameter("country");
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String countryId = request.getParameter("countryId");
        String provinceId = request.getParameter("provinceId");
        String cityId = request.getParameter("cityId");
        if (Objects.nonNull(postcode)&&postcode.length()>32) {
            resultObject.setCode("1");
            resultObject.setMsg("邮码不能超过32个字符");
            return resultObject;
        }
        try {
            goodsOrdersService.saveAddress(partyId, isUse, phone, email, postcode, contacts, country, province, city, address,
                    Integer.parseInt(countryId),Integer.parseInt(provinceId),Integer.parseInt(cityId));
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("添加失败");
        }

        return resultObject;
    }

    /**
     * 修改
     *
     * @return
     */
    @PostMapping(action + "edit.action")
    public Object edit(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String id = request.getParameter("id");
        if (StringUtils.isEmptyString(id)) {
            resultObject.setCode("1");
            resultObject.setMsg("非法请求");
            return resultObject;
        }

        String partyId = this.getLoginPartyId();
        String use = request.getParameter("use");
        int isUse = 0;
        if (use != null) {
            isUse = Integer.parseInt(use);
        }
        String phone = request.getParameter("phone");

        if (StringUtils.isEmptyString(phone)) {
            resultObject.setCode("1");
            resultObject.setMsg("手机号不能为空");
            return resultObject;
        }

        String contacts = request.getParameter("contacts");

        if (StringUtils.isEmptyString(contacts)) {
            resultObject.setCode("1");
            resultObject.setMsg("联系人不能为空");
            return resultObject;
        }
        String address = request.getParameter("address");

        if (StringUtils.isEmptyString(address)) {
            resultObject.setCode("1");
            resultObject.setMsg("地址不能为空");
            return resultObject;
        }

        if (address.length() > 255) {
            resultObject.setCode("1");
            resultObject.setMsg("地址过长");
            return resultObject;
        }

        String email = request.getParameter("email");
        String postcode = request.getParameter("postcode");
        String country = request.getParameter("country");
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String countryId = request.getParameter("countryId");
        String provinceId = request.getParameter("provinceId");
        String cityId = request.getParameter("cityId");
        int countryNumber=0;
        int provinceNumber=0;
        int cityNumber=0;
        try {
            countryNumber= Integer.parseInt(countryId);
            provinceNumber= Integer.parseInt(provinceId);
            cityNumber = Integer.parseInt(cityId);
        } catch (NumberFormatException e) {
            logger.error("修改地址失败，错误原因地址参数格式不正确");
            resultObject.setCode("1");
            resultObject.setMsg("参数错误");
            return resultObject;
        }
        try {
            goodsOrdersService.updateAddress(id,partyId,isUse,phone,email,postcode,
                    contacts,country,province,city, address,countryNumber,provinceNumber,cityNumber);
        }catch (BusinessException e){
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("系统错误");
        }
        return resultObject;
    }

    /**
     * 删除
     *
     * @return
     */
    @PostMapping(action + "del.action")
    public Object del(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String id = request.getParameter("id");
        if (StringUtils.isEmptyString(id)) {
            resultObject.setCode("1");
            resultObject.setMsg("地址不存在,或者已删除");
            return resultObject;
        }

        try {
            goodsOrdersService.removeAddress(id);
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("删除失败");
        }

        return resultObject;
    }

    /**
     * 列表地址
     *
     * @return
     */
    @PostMapping(action + "listCountry.action")
    public Object listCountry(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        JSONArray jsonArray = new JSONArray();
        String countryName = request.getParameter("countryName");
        String lang = request.getParameter("lang");
        for (MallCountry country : mallAddressAreaService.listCountry(countryName, lang)) {
            JSONObject o = new JSONObject();
            o.put("id", country.getId().toString());
            if ("cn".equalsIgnoreCase(lang)) {
                o.put("countryName", country.getCountryNameCn());
            } else if ("en".equalsIgnoreCase(lang)) {
                o.put("countryName", country.getCountryNameEn());
            } else if ("tw".equalsIgnoreCase(lang)) {
                o.put("countryName", country.getCountryNameTw());
            } else {
/*                log.error("listCountry 不支持的语言：" + lang);
                resultObject.setCode("-1");
                resultObject.setMsg("不支持的语言");
                return resultObject;*/
//              其他语言的暂时显示英文
                o.put("countryName", country.getCountryNameEn());
            }
            jsonArray.add(o);
        }
        JSONObject object = new JSONObject();
        object.put("data", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "listState.action")
    public Object listState(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        Long countryId = null;
        try {
            countryId = request.getParameter("countryId") == null ? null : Long.valueOf(request.getParameter("countryId"));
        } catch (NumberFormatException e) {
            resultObject.setData("");
            return resultObject;
        }
        if (countryId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("请先选择国家及州");
            return resultObject;
        }
        String stateName = request.getParameter("stateName");
        String language = request.getParameter("lang");
        JSONArray jsonArray = new JSONArray();
        for (MallState state : mallAddressAreaService.listState(stateName, countryId,language)) {
            JSONObject o = new JSONObject();
            o.put("id", state.getId().toString());
            if ("cn".equalsIgnoreCase(language)) {
                o.put("stateName", state.getStateNameCn());
            } else if ("en".equalsIgnoreCase(language)) {
                o.put("stateName", state.getStateNameEn());
            } else if ("tw".equalsIgnoreCase(language)) {
                o.put("stateName", state.getStateNameTw());
            } else {
//                log.error("listCountry 不支持的语言：" + language);
//                resultObject.setCode("-1");
//                resultObject.setMsg("不支持的语言");
//                return resultObject;
                //              其他语言的暂时显示英文
                o.put("stateName", state.getStateNameEn());
            }
            jsonArray.add(o);
        }
        JSONObject object = new JSONObject();
        object.put("data", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "listCity.action")
    public Object listCity(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        String queryType = request.getParameter("queryType");
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        Long stateId = null;
        try {
            stateId = request.getParameter("stateId") == null ? null : Long.valueOf(request.getParameter("stateId"));
        } catch (NumberFormatException e) {
            resultObject.setData("");
            return resultObject;
        }
        if (stateId == null) {
            if ("noShow".equals(queryType)) {
                return resultObject;
            }
            resultObject.setCode("1");
            resultObject.setMsg("请先选择州");
            return resultObject;
        }
        String cityName = request.getParameter("cityName");
        String language = request.getParameter("lang");
        JSONArray jsonArray = new JSONArray();
        for (MallCity city : mallAddressAreaService.listCity(cityName, stateId,language)) {
            JSONObject o = new JSONObject();
            o.put("id", city.getId().toString());
            if ("cn".equalsIgnoreCase(language)) {
                o.put("cityName", city.getCityNameCn());
            } else if ("en".equalsIgnoreCase(language)) {
                o.put("cityName", city.getCityNameEn());
            } else if ("tw".equalsIgnoreCase(language)) {
                o.put("cityName", city.getCityNameTw());
            } else {
                /*log.error("listCountry 不支持的语言：" + language);
                resultObject.setCode("-1");
                resultObject.setMsg("不支持的语言");
                return resultObject;*/
                o.put("cityName", city.getCityNameEn());
            }
            jsonArray.add(o);
        }
        JSONObject object = new JSONObject();
        object.put("data", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 列表地址
     *
     * @return
     */
    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String lang = request.getParameter("lang");
        String partyId = this.getLoginPartyId();
        JSONArray jsonArray = new JSONArray();
        for (MallAddress address : goodsOrdersService.listAddress(partyId)) {
            JSONObject o = new JSONObject();
            o.put("id", address.getId().toString());
            o.put("use", address.getStatus());
            o.put("phone", address.getPhone());
            o.put("contacts", address.getContacts());
            o.put("address", address.getAddress());

            o.put("email", address.getEmail());
            o.put("postcode", address.getPostcode());
//            2023-04-21 国家省市增加多语言显示
            List<String> detailAddress= findAddressWithCodeAndLanguage(Long.valueOf(address.getCountryId()),
                    Long.valueOf(address.getProvinceId()), Long.valueOf(address.getCityId()), lang);
            o.put("country", StringUtils.isEmptyString(detailAddress.get(0))?address.getCountry():detailAddress.get(0));
            o.put("province",StringUtils.isEmptyString(detailAddress.get(0))?address.getProvince():detailAddress.get(1));
            o.put("city", StringUtils.isEmptyString(detailAddress.get(0))?address.getCity():detailAddress.get(2));
            o.put("countryId",address.getCountryId());
            o.put("provinceId",address.getProvinceId());
            o.put("cityId",address.getCityId());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    public List<String> findAddressWithCodeAndLanguage(Long countryId, Long stateId, Long cityId, String language) {
        String countryName = "";
        String stateName = "";
        String cityName = "";
        MallCountry mallCountry = (MallCountry) redisHandler.get(MallRedisKeys.MALL_COUNTRY + countryId);
        MallState mallState = (MallState) redisHandler.get(MallRedisKeys.MALL_STATE + stateId);
        MallCity mallCity = (MallCity) redisHandler.get(MallRedisKeys.MALL_CITY + cityId);
        if ("cn".equalsIgnoreCase(language)) {
            if (Objects.nonNull(mallCountry)) countryName=mallCountry.getCountryNameCn();
            if (Objects.nonNull(mallState)) stateName=mallState.getStateNameCn();
            if (Objects.nonNull(mallCity)) cityName=mallCity.getCityNameCn();
        } else if ("tw".equalsIgnoreCase(language)) {
            if (Objects.nonNull(mallCountry)) countryName=mallCountry.getCountryNameTw();
            if (Objects.nonNull(mallState)) stateName=mallState.getStateNameTw();
            if (Objects.nonNull(mallCity)) cityName=mallCity.getCityNameTw();
        } else {//其他语言默认英语
            if (Objects.nonNull(mallCountry)) countryName=mallCountry.getCountryNameEn();
            if (Objects.nonNull(mallState)) stateName=mallState.getStateNameEn();
            if (Objects.nonNull(mallCity)) cityName=mallCity.getCityNameEn();
        }
        return Arrays.asList(countryName,stateName,cityName);
    }
}
