package project.web.api;

import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.Constants;
import project.mall.seller.ComplaintService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Complaint;
import project.party.PartyService;
import project.party.model.Party;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.token.TokenService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@CrossOrigin
public class ComplaintController extends BaseAction {
    private static Log logger = LogFactory.getLog(ComplaintController.class);

    @Resource
    protected SellerService sellerService ;
    @Resource
    protected PartyService partyService ;
    @Resource
    protected ComplaintService complaintService ;
    @Resource
    protected TokenService tokenService ;

    @Resource
    protected TipService tipService;

    private final String action = "/api/comp";

    /**
     * 新增投诉
     * @param request
     * @return
     */
    @PostMapping(action + "/add.action")
    public Object info(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String token = request.getParameter("token");
        if (StringUtils.isNullOrEmpty(token)) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }
        String partyId = tokenService.cacheGet(token);
        if (StringUtils.isNullOrEmpty(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }

        String userId = getLoginPartyId();
        String sellerId = request.getParameter("sellerId");
        String complaintStatus = request.getParameter("complaintStatus");
        String content = request.getParameter("content");
        String imgUrl1 = request.getParameter("imgUrl1");
        String imgUrl2 = request.getParameter("imgUrl2");
        String imgUrl3 = request.getParameter("imgUrl3");
        String imgUrl4 = request.getParameter("imgUrl4");
        String imgUrl5 = request.getParameter("imgUrl5");
        String imgUrl6 = request.getParameter("imgUrl6");
        String imgUrl7 = request.getParameter("imgUrl7");
        String imgUrl8 = request.getParameter("imgUrl8");
        String imgUrl9 = request.getParameter("imgUrl9");
        try {
            Party party = partyService.getById(userId);
            Party seller = partyService.getById(sellerId);
            Complaint complaint = Complaint.builder().complaintStatus(Integer.valueOf(complaintStatus))
                    .content(content)
                    .partyId(party.getId().toString())
                    .userCode(party.getUsercode())
                    .storeId(seller.getId().toString())
                    .storeCode(seller.getUsercode())
                    .auditStatus(0)
                    .createTime(new Date()).build();
            if (StringUtils.isNotEmpty(imgUrl1)) {
                complaint.setImgUrl1(imgUrl1);
            }
            if (StringUtils.isNotEmpty(imgUrl2)) {
                complaint.setImgUrl2(imgUrl2);
            }
            if (StringUtils.isNotEmpty(imgUrl3)) {
                complaint.setImgUrl3(imgUrl3);
            }
            if (StringUtils.isNotEmpty(imgUrl4)) {
                complaint.setImgUrl4(imgUrl4);
            }
            if (StringUtils.isNotEmpty(imgUrl5)) {
                complaint.setImgUrl5(imgUrl5);
            }
            if (StringUtils.isNotEmpty(imgUrl6)) {
                complaint.setImgUrl6(imgUrl6);
            }
            if (StringUtils.isNotEmpty(imgUrl7)) {
                complaint.setImgUrl7(imgUrl7);
            }
            if (StringUtils.isNotEmpty(imgUrl8)) {
                complaint.setImgUrl8(imgUrl8);
            }
            if (StringUtils.isNotEmpty(imgUrl9)) {
                complaint.setImgUrl9(imgUrl9);
            }
            this.complaintService.saveComplaint(complaint);
            this.tipService.saveTip(complaint.getId().toString(), TipConstants.COMPLAINT);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("程序异常");
            logger.error("新增投诉信息报错",e);
        }
        return resultObject;
    }

}
