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
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.kyc.Kyc;
import project.user.kyc.KycHighLevel;
import project.user.kyc.KycHighLevelService;
import project.user.kyc.KycService;

/**
 * 用户高级认证
 */
@RestController
@CrossOrigin
public class KycHighLevelController extends BaseAction {

	private Logger logger = LogManager.getLogger(KycHighLevelController.class);
	
	@Autowired
	private KycHighLevelService kycHighLevelService;
	@Autowired
	private KycService kycService;
	@Autowired
	private TipService tipService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private SysparaService sysparaService;
	
	private final String action = "/api/kycHighLevel!";

	/**
	 * 获取 用户高级认证 信息
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

			KycHighLevel kycHighLevel = this.kycHighLevelService.get(partyId);
			if (kycHighLevel != null) {
				
				Kyc kyc = this.kycService.get(partyId);
				kycHighLevel.setName(kyc.getName());
				
				if (!StringUtils.isNullOrEmpty(kycHighLevel.getIdimg_1())) {
					String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + kycHighLevel.getIdimg_1();
					kycHighLevel.setIdimg_1_path(path);
				}

				if (!StringUtils.isNullOrEmpty(kycHighLevel.getIdimg_2())) {
					String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + kycHighLevel.getIdimg_2();
					kycHighLevel.setIdimg_2_path(path);
				}
				
				if (!StringUtils.isNullOrEmpty(kycHighLevel.getIdimg_3())) {
					String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + kycHighLevel.getIdimg_3();
					kycHighLevel.setIdimg_3_path(path);
				} else {
					kycHighLevel.setIdimg_3("qr/id_img3.jpg");
					String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=qr/id_img3.jpg";
					kycHighLevel.setIdimg_3_path(path);
				}				
				
				resultObject.setData(kycHighLevel);
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
	 * 用户高级认证 申请
	 * 
	 * work_place 工作地址
	 * home_place 家庭地址
	 * relatives_relation 亲属关系
	 * relatives_name 亲属名称
	 * relatives_place 亲属地址
	 * relatives_phone 亲属电话
	 * idimg_1 证件正面照
	 * idimg_2 证件背面照
	 * idimg_3 手持证件
	 */
	@RequestMapping(action + "apply.action")
	public Object apply(HttpServletRequest request) throws IOException {
		String work_place = request.getParameter("work_place");
		String home_place = request.getParameter("home_place");
		String relatives_name = request.getParameter("relatives_name");
		String relatives_relation = request.getParameter("relatives_relation");
		String relatives_place = request.getParameter("relatives_place");
		String relatives_phone = request.getParameter("relatives_phone");
		String idimg_1 = request.getParameter("idimg_1");
		String idimg_2 = request.getParameter("idimg_2");
		String idimg_3 = request.getParameter("idimg_3");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);		
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		try {
			
			String partyId = this.getLoginPartyId();
			
			String error = this.verify(work_place, home_place, relatives_name, relatives_relation, relatives_place, relatives_phone);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			if (!this.kycService.isPass(partyId)) {
				throw new BusinessException("实名认证未通过，无法进行高级认证");
			}

			String checkApplyResult = this.kycHighLevelService.checkApplyResult(partyId);
			if (StringUtils.isNotEmpty(checkApplyResult)) {
				resultObject.setMsg(checkApplyResult);
			}
			
			KycHighLevel entity = new KycHighLevel();
			entity.setPartyId(partyId);
			entity.setIdimg_1(idimg_1);
			entity.setIdimg_2(idimg_2);
			entity.setIdimg_3(idimg_3);
			entity.setStatus(1);
			entity.setHome_place(URLDecoder.decode(home_place, "utf-8"));
			entity.setWork_place(URLDecoder.decode(work_place, "utf-8"));
			entity.setRelatives_name(URLDecoder.decode(relatives_name, "utf-8"));
			entity.setRelatives_phone(URLDecoder.decode(relatives_phone, "utf-8"));
			entity.setRelatives_place(URLDecoder.decode(relatives_place, "utf-8"));
			entity.setRelatives_relation(URLDecoder.decode(relatives_relation, "utf-8"));

			this.kycHighLevelService.save(entity);
			Party party = this.partyService.cachePartyBy(partyId, false);
			if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
				this.tipService.saveTip(entity.getId().toString(), TipConstants.KYC_HIGH_LEVEL);
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

	private String verify(String work_place, String home_place, String relatives_name, String relatives_relation, 
			String relatives_place, String relatives_phone) {
		
		if (StringUtils.isEmptyString(work_place)) {
			return "工作地址不能为空";
		}
		
		if (StringUtils.isEmptyString(home_place)) {
			return "家庭地址不能为空";
		}
		
		String projectType = this.sysparaService.find("project_type").getValue();
		if (StringUtils.isEmptyString(projectType)) {
			return "系统参数错误";
		}
		if (projectType.equals("EXCHANGE_DELENO")) {
			// 选填
		} else {
			if (StringUtils.isEmptyString(relatives_name)) {
				return "亲属姓名不能为空";
			}
			
			if (StringUtils.isEmptyString(relatives_relation)) {
				return "亲属关系不能为空";
			}
			
			if (StringUtils.isEmptyString(relatives_place)) {
				return "亲属地址不能为空";
			}
			
			if (StringUtils.isEmptyString(relatives_phone)) {
				return "亲属电话不能为空";
			}
		}
		
		return null;
	}

}
