package project.web.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.goods.GoodsService;
import project.invest.goods.model.Useraddress;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.rate.ExchangeRateService;
import project.wallet.rate.PaymentMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@CrossOrigin
public class PaymentMethodController extends BaseAction {


    @Resource
    private ExchangeRateService exchangeRateService;

    private static Log logger = LogFactory.getLog(PaymentMethodController.class);

    @Resource
    protected KycService kycService;

    private final String action = "/api/p-method!";

    /**
     * 添加
     * @return
     */
    @PostMapping( action+"add.action")
    public Object add(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        int payType = 0;
        String use = request.getParameter("use");
        int isUse = 0;
        if(use!=null){
            isUse = Integer.parseInt(use);
        }
        String bankName = request.getParameter("bankName");

        if(StringUtils.isEmptyString(bankName)){
            resultObject.setCode("1");
            resultObject.setMsg("开户行不能为空");
            return resultObject;
        }

        String bankAccount = request.getParameter("bankAccount");

        if(StringUtils.isEmptyString(bankAccount)){
            resultObject.setCode("1");
            resultObject.setMsg("卡号不能为空");
            return resultObject;
        }

        Kyc kyc = this.kycService.get(partyId);
        if (null == kyc ) {
            resultObject.setCode("800");
            resultObject.setMsg("尚未KYC认证");
            return resultObject;
        }

        if ( kyc.getStatus() != 2) {
            resultObject.setCode("801");
            resultObject.setMsg("KYC认证尚未通过");
            return resultObject;
        }

        try {
            exchangeRateService.savePaymentMethod(partyId,isUse,payType,bankName,bankAccount,kyc);
        }catch (BusinessException e){
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("添加失败");
        }

        return resultObject;
    }

    /**
     * 修改
     * @return
     */
    @PostMapping( action+"edit.action")
    public Object edit(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String id = request.getParameter("id");
        if(StringUtils.isEmptyString(id)){
            resultObject.setCode("1");
            resultObject.setMsg("非法请求");
            return resultObject;
        }

        String partyId = this.getLoginPartyId();

        String use = request.getParameter("use");
        int isUse = 0;
        if(use!=null){
            isUse = Integer.parseInt(use);
        }
        String bankName = request.getParameter("bankName");

        if(StringUtils.isEmptyString(bankName)){
            resultObject.setCode("1");
            resultObject.setMsg("开户行不能为空");
            return resultObject;
        }

        String bankAccount = request.getParameter("bankAccount");

        if(StringUtils.isEmptyString(bankAccount)){
            resultObject.setCode("1");
            resultObject.setMsg("卡号不能为空");
            return resultObject;
        }

        exchangeRateService.updatePaymentMethod(id,partyId,isUse,bankName,bankAccount);

        return resultObject;
    }

    /**
     * 删除
     * @return
     */
    @PostMapping( action+"del.action")
    public Object del(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String id = request.getParameter("id");
        if(StringUtils.isEmptyString(id)){
            resultObject.setCode("1");
            resultObject.setMsg("非法请求");
            return resultObject;
        }

        try {
            exchangeRateService.removePaymentMethod(id);
        }catch (BusinessException e){
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("删除失败");
        }

        return resultObject;
    }

    /**
     * 列表地址
     * @return
     */
    @PostMapping( action+"list.action")
    public Object list(){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        JSONArray jsonArray = new JSONArray();
        for(PaymentMethod address: exchangeRateService.listPaymentMethod(partyId)){
            JSONObject o = new JSONObject();
            o.put("id", address.getId().toString());
            o.put("use", address.getStatus());
            o.put("payType", address.getPayType());
            o.put("realName", address.getRealName());
            o.put("bankName", address.getBankName());
            o.put("bankAccount", address.getBankAccount());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }
}
