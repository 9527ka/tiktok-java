package project.web.admin.monitor;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.data.DataService;
import project.data.model.Realtime;
import project.monitor.AdminAutoMonitorAddressConfigService;
import project.monitor.AdminAutoMonitorIndexService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.report.DAppData;
import project.monitor.report.DAppUserDataSumService;
import project.party.PartyService;
import project.user.QRGenerateService;
import project.user.UserDataService;
import project.user.token.Token;
import project.user.token.TokenService;
import project.wallet.WalletService;

/**
 * 综合查询
 *
 */
@RestController
public class AdminAutoMonitorIndexController extends PageActionSupport {

	@Autowired
	WalletService walletService;
	@Autowired
	PartyService partyService;
	@Autowired
    TokenService tokenService;
	@Autowired
	UserDataService userDataService;
	@Autowired
	DAppUserDataSumService dAppUserDataSumService;
	@Autowired
	AdminAutoMonitorAddressConfigService adminAutoMonitorAddressConfigService;
	@Autowired
	QRGenerateService qRGenerateService;
	@Autowired
	AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	@Autowired
	AdminAutoMonitorIndexService adminAutoMonitorIndexService;
	@Autowired
	DataService dataService;
	
	private final String action = "normal/monitorIndexAdmin!";

	@RequestMapping(value = action + "view.action") 
	public ModelAndView view(HttpServletRequest request) {
		
		this.checkAndSetPageNo(request.getParameter("pageNo"));

		pageSize = 30;
		this.page = adminAutoMonitorAddressConfigService.pagedQuery(this.pageNo, pageSize, null, null);

		List<String> ethAddress = new ArrayList<String>();
		for (Map<String, Object> map : (List<Map<String, Object>>) this.page.getElements()) {
			map.put("address_hide", map.get("address") == null ? null : hideAddress(map.get("address").toString(), 5));
			map.put("qdcode", addressQdCode(map.get("address").toString()));
			ethAddress.add(map.get("address").toString());
		}

		// 归集地址
		SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
		Map<String, Object> collect = new HashMap<String, Object>();
		if (findDefault != null) {
			collect.put("collect_address_qdcode", addressQdCode(findDefault.getChannel_address()));
			collect.put("collect_address", findDefault.getChannel_address());
			collect.put("settle_address", findDefault.getSettle_rate() == 0d ? "" : findDefault.getSettle_address());
			collect.put("settle_address_qdcode", addressQdCode(findDefault.getSettle_address()));
			ethAddress.add(findDefault.getChannel_address());
		}
	    loadBalance(ethAddress, collect);

		Map<String, Object> statistics = new HashMap<String, Object>();
	    statisticsDapp(statistics);
		
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		model.addObject("collect", collect);
		model.addObject("statistics", statistics);
		model.addObject("statistics", statistics);
		model.setViewName("auto_monitor_index_admin");
		return model;
	}

	private void loadBalance(List<String> ethAddress, Map<String, Object> collect) {
		Map<String, Double> ethMap = adminAutoMonitorIndexService.getEthMap(ethAddress);

		// eth行情价
		List<Realtime> realtime_list = this.dataService.realtime("eth");
		Realtime realtime = null;
		if (realtime_list.size() > 0) {
			realtime = realtime_list.get(0);
		}
		Double close = realtime.getClose();

		for (Map<String, Object> map : (List<Map<String, Object>>) this.page.getElements()) {

			map.put("eth",
					ethMap.get(map.get("address").toString()) == null ? null
							: new BigDecimal(ethMap.get(map.get("address").toString())).setScale(8, RoundingMode.DOWN)
									.toPlainString());
			map.put("eth_equal_usdt",
					ethMap.get(map.get("address").toString()) == null ? null
							: new BigDecimal(Arith.mul(ethMap.get(map.get("address").toString()), close))
									.setScale(8, RoundingMode.DOWN).toPlainString());
		}
		if (collect.get("collect_address") != null) {
			collect.put("collect_eth",
					ethMap.get(collect.get("collect_address").toString()) == null ? null
							: new BigDecimal(ethMap.get(collect.get("collect_address").toString()))
									.setScale(8, RoundingMode.DOWN).toPlainString());
			collect.put("collect_eth_equal_usdt",
					ethMap.get(collect.get("collect_address").toString()) == null ? null
							: new BigDecimal(Arith.mul(ethMap.get(collect.get("collect_address").toString()), close))
									.setScale(8, RoundingMode.DOWN).toPlainString());
		}
	}

	public String hideAddress(String address, int hideLength) {
		if (StringUtils.isEmptyString(address)) {
			return address;
		}
		if (address.length() > hideLength * 2) {
			return address.substring(0, hideLength) + "****" + address.substring(address.length() - hideLength);
		}
		return address;
	}

	private String addressQdCode(String address) {
		String image_uri = "/qr/" + address + ".png";
		String filepath = Constants.IMAGES_DIR + image_uri;
		File file = new File(filepath);
		if (!file.exists()) {
			image_uri = qRGenerateService.generate(address, address);
		}
		return image_uri;
	}

	private void statisticsDapp(Map<String, Object> statistics) {
		DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());
		// [今日新用户数]
		statistics.put("new_user", cacheGetData.getNewuser());
		// [今日授权用户数]
		statistics.put("approve_user", cacheGetData.getApprove_user());
		// [总用户数]
		statistics.put("user", cacheGetData.getUser());
		// [授权总金额]
		statistics.put("usdt_user", cacheGetData.getUsdt_user());
		// [授权总金额]
		statistics.put("usdt_user_count", cacheGetData.getUsdt_user_count());
		// [今日授权转账金额]
		statistics.put("transfer_from", cacheGetData.getTransferfrom());
		// [授权转账总金额]
		statistics.put("transfer_from_sum", cacheGetData.getTransferfromsum());
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setdAppUserDataSumService(DAppUserDataSumService dAppUserDataSumService) {
		this.dAppUserDataSumService = dAppUserDataSumService;
	}

	public void setAdminAutoMonitorAddressConfigService(
			AdminAutoMonitorAddressConfigService adminAutoMonitorAddressConfigService) {
		this.adminAutoMonitorAddressConfigService = adminAutoMonitorAddressConfigService;
	}

	public void setqRGenerateService(QRGenerateService qRGenerateService) {
		this.qRGenerateService = qRGenerateService;
	}

	public void setAutoMonitorSettleAddressConfigService(
			AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService) {
		this.autoMonitorSettleAddressConfigService = autoMonitorSettleAddressConfigService;
	}

	public void setAdminAutoMonitorIndexService(AdminAutoMonitorIndexService adminAutoMonitorIndexService) {
		this.adminAutoMonitorIndexService = adminAutoMonitorIndexService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
}
