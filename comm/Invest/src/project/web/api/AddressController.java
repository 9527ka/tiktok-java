//package project.web.api;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import kernel.exception.BusinessException;
//import kernel.util.StringUtils;
//import kernel.web.BaseAction;
//import kernel.web.ResultObject;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//import project.invest.goods.GoodsService;
//import project.invest.goods.model.Useraddress;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//@Slf4j
//@RestController
//@CrossOrigin
//public class AddressController extends BaseAction {
//
//
//    @Resource
//    private GoodsService goodsService;
//
//    private static Log logger = LogFactory.getLog(AddressController.class);
//    private final String action = "/api/address!";
//
//    /**
//     * 添加
//     * @return
//     */
//    @PostMapping( action+"add.action")
//    public Object add(HttpServletRequest request){
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
//        String use = request.getParameter("use");
//        int isUse = 0;
//        if(use!=null){
//            isUse = Integer.parseInt(use);
//        }
//        String phone = request.getParameter("phone");
//
//        if(StringUtils.isEmptyString(phone)){
//            resultObject.setCode("1");
//            resultObject.setMsg("手机号不能为空");
//            return resultObject;
//        }
//
//        String contacts = request.getParameter("contacts");
//
//        if(StringUtils.isEmptyString(contacts)){
//            resultObject.setCode("1");
//            resultObject.setMsg("联系人不能为空");
//            return resultObject;
//        }
//        String address = request.getParameter("address");
//
//        if(StringUtils.isEmptyString(address)){
//            resultObject.setCode("1");
//            resultObject.setMsg("地址不能为空");
//            return resultObject;
//        }
//
//        if(address.length()>100){
//            resultObject.setCode("1");
//            resultObject.setMsg("地址过长");
//            return resultObject;
//        }
//
//        try {
//            goodsService.saveAddress(partyId,isUse,phone,contacts,address);
//        }catch (BusinessException e){
//            e.printStackTrace();
//            resultObject.setCode("1");
//            resultObject.setMsg(e.getMessage());
//            return resultObject;
//        }catch (Exception e1){
//            e1.printStackTrace();
//            resultObject.setCode("1");
//            resultObject.setMsg("添加失败");
//        }
//
//        return resultObject;
//    }
//
//    /**
//     * 修改
//     * @return
//     */
//    @PostMapping( action+"edit.action")
//    public Object edit(HttpServletRequest request){
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String id = request.getParameter("id");
//        if(StringUtils.isEmptyString(id)){
//            resultObject.setCode("1");
//            resultObject.setMsg("非法请求");
//            return resultObject;
//        }
//
//        String partyId = this.getLoginPartyId();
//        String use = request.getParameter("use");
//        int isUse = 0;
//        if(use!=null){
//            isUse = Integer.parseInt(use);
//        }
//        String phone = request.getParameter("phone");
//
//        if(StringUtils.isEmptyString(phone)){
//            resultObject.setCode("1");
//            resultObject.setMsg("手机号不能为空");
//            return resultObject;
//        }
//
//        String contacts = request.getParameter("contacts");
//
//        if(StringUtils.isEmptyString(contacts)){
//            resultObject.setCode("1");
//            resultObject.setMsg("联系人不能为空");
//            return resultObject;
//        }
//        String address = request.getParameter("address");
//
//        if(StringUtils.isEmptyString(address)){
//            resultObject.setCode("1");
//            resultObject.setMsg("地址不能为空");
//            return resultObject;
//        }
//
//        if(address.length()>100){
//            resultObject.setCode("1");
//            resultObject.setMsg("地址过长");
//            return resultObject;
//        }
//        goodsService.updateAddress(id,partyId,isUse,phone,contacts,address);
//
//        return resultObject;
//    }
//
//    /**
//     * 删除
//     * @return
//     */
//    @PostMapping( action+"del.action")
//    public Object del(HttpServletRequest request){
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//
//        String id = request.getParameter("id");
//        if(StringUtils.isEmptyString(id)){
//            resultObject.setCode("1");
//            resultObject.setMsg("地址不存在,或者已删除");
//            return resultObject;
//        }
//
//        try {
//            goodsService.removeAddress(id);
//        }catch (BusinessException e){
//            e.printStackTrace();
//            resultObject.setCode("1");
//            resultObject.setMsg(e.getMessage());
//            return resultObject;
//        }catch (Exception e1){
//            e1.printStackTrace();
//            resultObject.setCode("1");
//            resultObject.setMsg("删除失败");
//        }
//
//        return resultObject;
//    }
//
//    /**
//     * 列表地址
//     * @return
//     */
//    @PostMapping( action+"list.action")
//    public Object list(){
//        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
//        if (!"0".equals(resultObject.getCode())) {
//            return resultObject;
//        }
//        String partyId = this.getLoginPartyId();
//        JSONArray jsonArray = new JSONArray();
//        for(Useraddress address: goodsService.listAddress(partyId)){
//            JSONObject o = new JSONObject();
//            o.put("id", address.getId().toString());
//            o.put("use", address.getStatus());
//            o.put("phone", address.getPhone());
//            o.put("contacts", address.getContacts());
//            o.put("address", address.getAddress());
//            jsonArray.add(o);
//        }
//
//        JSONObject object = new JSONObject();
//        object.put("pageList", jsonArray);
//        resultObject.setData(object);
//        return resultObject;
//    }
//}
