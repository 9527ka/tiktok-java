package project.web.api.kyc;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.party.PartyService;
import project.party.model.Party;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;

/**
 * 实名认证
 */
@RestController
@CrossOrigin
public class KycController extends BaseAction {

	private Logger logger = LogManager.getLogger(KycController.class);
	
	@Autowired
	private KycService kycService;
	@Autowired
	private TipService tipService;
	@Autowired
	private PartyService partyService;
	
	private final String action = "/api/kyc!";

	/**
	 * 获取实名认证信息
	 */
	@RequestMapping(action + "get.action")
	public Object get() throws IOException {

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {
			
			String partyId = this.getLoginPartyId();
			Kyc kyc = this.kycService.get(partyId);
			
			if (!StringUtils.isNullOrEmpty(kyc.getIdimg_1())) {
				String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + kyc.getIdimg_1();
				kyc.setIdimg_1_path(path);
			}

			if (!StringUtils.isNullOrEmpty(kyc.getIdimg_2())) {
				String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + kyc.getIdimg_2();
				kyc.setIdimg_2_path(path);
			}
			
			if (!StringUtils.isNullOrEmpty(kyc.getIdimg_3())) {
				String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + kyc.getIdimg_3();
				kyc.setIdimg_3_path(path);
			} else {
//				kyc.setIdimg_3("qr/id_img3.jpg");
//				String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=qr/id_img3.jpg";
//				kyc.setIdimg_3_path(path);
			}

			resultObject.setData(kyc);
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
	 * 实名认证申请
	 */
	@RequestMapping(action + "apply.action")
	public Object apply(HttpServletRequest request) throws IOException {
		String idimg_1 = request.getParameter("idimg_1");
		String idimg_2 = request.getParameter("idimg_2");
		String idimg_3 = request.getParameter("idimg_3");
		String idname = request.getParameter("idname");
		String name = request.getParameter("name");
		String idnumber = request.getParameter("idnumber");
		String nationality = request.getParameter("nationality");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);		
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {
			
			String partyId = this.getLoginPartyId();

			String checkApplyResult = this.kycService.checkApplyResult(partyId);
			if (StringUtils.isNotEmpty(checkApplyResult)) {
				resultObject.setMsg(checkApplyResult);				
			}
			
//			idname = URLDecoder.decode(idname, "utf-8");
//			name = URLDecoder.decode(name, "utf-8");
			
			Kyc entity = new Kyc();
			entity.setPartyId(partyId);
			entity.setStatus(1);
			entity.setIdimg_1(idimg_1);
			entity.setIdimg_2(idimg_2);
			entity.setIdimg_3(idimg_3);
			entity.setIdname(idname);
			entity.setIdnumber(idnumber);
			entity.setName(name);
			entity.setNationality(nationality);
			entity.setSex("");
			entity.setBorth_date("");
			this.kycService.save(entity);
			
			Party party = this.partyService.cachePartyBy(partyId, false);
			if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
				this.tipService.saveTip(entity.getId().toString(), TipConstants.KYC);
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

}
