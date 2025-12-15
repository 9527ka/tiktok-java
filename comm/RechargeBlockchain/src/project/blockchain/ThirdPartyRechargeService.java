package project.blockchain;

import project.blockchain.event.model.RechargeInfo;
import project.blockchain.vo.GCash2NotifyVo;
import project.blockchain.vo.GCash3NotifyVo;
import project.blockchain.vo.GCashPayNotifyVo;
import project.blockchain.vo.ThirdPartyPayRespVo;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ThirdPartyRechargeService {

    /**
     * 发起银行卡充值订单
     *
     * @param partyId        用户Id
     * @param amount         金额
     * @param frenchCurrency 法币
     * @return 支付地址
     */
    String saveApplyRecharge(String partyId, String amount, String frenchCurrency);

    /**
     * 异步通知回调
     *
     * @param payRespVo 订单信息
     * @return
     */
    RechargeInfo saveSucceeded(ThirdPartyPayRespVo payRespVo);

    /**
     * 查询与同步第三方订单
     */
    void updateThirdPartyOrder();

    /**
     * 发起PHP充值订单
     *
     * @param partyId 用户Id
     * @param amount  金额
     * @return 支付地址
     */
    String saveApplyRecharge(String partyId, String amount);

    /**
     * 发起PHP充值订单2.0
     *
     * @param partyId 用户Id
     * @param amount  金额
     * @param pageUrl 回跳地址
     * @return 支付地址
     */
    String saveApplyRecharge2(String partyId, String amount, String pageUrl);

    /**
     * 2.0异步通知回调
     *
     * @param request
     * @return
     */
    RechargeInfo saveSucceeded2(GCash2NotifyVo request);

    /**
     * 查询与同步第三方2.0订单
     */
    void updateThirdPartyOrder2();

    /**
     * 发起PHP充值订单3.0
     *
     * @param partyId     用户Id
     * @param amount      金额
     * @param callBackUrl 回跳地址
     * @return 支付地址
     */
    String saveApplyRecharge3(String partyId, String amount, String callBackUrl);

    /**
     * 3.0、payMaya异步通知回调
     *
     * @param notifyVo
     * @return
     */
    RechargeInfo saveSucceeded3(GCash3NotifyVo notifyVo);

    /**
     * 发起PHP充值订单PayMaya
     *
     * @param partyId     用户Id
     * @param amount      金额
     * @param callBackUrl 回跳地址
     * @return
     */
    String saveApplyRecharge4(String partyId, String amount, String callBackUrl);

    /**
     * 查询与同步第三方3.0订单
     */
    void updateThirdPartyOrder3();

    /**
     * 发起PHP充值订单 GCash2.0
     * @param partyId
     * @param amount
     * @param callBackUrl
     * @return
     */
    String saveApplyRecharge5(String partyId, String amount, String callBackUrl) throws NoSuchAlgorithmException, InvalidKeyException, UnknownHostException;
    /**
     * GCash pay异步通知回调
     * @param notifyVo
     * @return
     */
    RechargeInfo saveSucceeded4(GCashPayNotifyVo notifyVo);

    /**
     * 查询与同步第三方GCash pay订单
     */
    void updateThirdPartyOrder4();

    /**
     * 发起三方支付(V11)
     *
     * @param request
     * @param partyId
     * @param channel 渠道，wxpay | alipay
     * @param amount  充值金额
     */
    String submitTopUpRequest(HttpServletRequest request, String partyId, String channel, String amount);

    /**
     * 更新支付成功状态
     * @param orderNo 订单号
     * @return RechargeInfo
     */
    RechargeInfo updatePaymentSuccessStatus(String orderNo);

    /**
     * 提交手动充值请求(客服审核)
     * @param partyId
     * @param channel
     * @param amount
     */
    void submitManualRequest( String partyId, String channel, String amount);

}
