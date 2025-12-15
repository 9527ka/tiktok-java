package project.web.api;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import ext.Strings;
import ext.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.blockchain.ChannelBlockchain;
import project.blockchain.ChannelBlockchainService;
import project.blockchain.PartyBlockchain;
import project.hobi.HobiDataService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;

/**
 * 区块链
 */
@RestController
@CrossOrigin
public class ChannelBlockchainController extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(ChannelBlockchainController.class);

	@Autowired
	private ChannelBlockchainService channelBlockchainService;
	@Autowired
	private SysparaService sysparaService;

	@Resource
	private HobiDataService hobiDataService;

	@Resource
	private PartyService partyService;

	@Resource
	private UserRecomService userRecomService;

	private final String action = "/api/channelBlockchain!";

	/**
	 * 获取所有链地址
	 */
	@RequestMapping(action + "list.action")
	public Object list() throws IOException {
		List<ChannelBlockchain> data = new ArrayList<>();

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			resultObject.setData(data);
			return resultObject;
		}

		try {
			Integer canRecharge = Types.orValue( this.sysparaService.find("can_recharge").getInteger(),0);
			Double rechargeLimitMin = sysparaService.find("recharge_limit_min").getDouble();
			Double rechargeLimitMax = sysparaService.find("recharge_limit_max").getDouble();
			int agentReceiptEnabled = sysparaService.find("recharge_agent_receipt_enabled").getInteger();

			String partyId = this.getLoginPartyId();
			Party party = this.partyService.cachePartyBy(partyId, true);
			data = this.channelBlockchainService.findAll().stream().filter(a-> !Strings.isNullOrEmpty(a.getAddress()) &&
					!"-".equals(a.getAddress())).collect(Collectors.toList());
			PartyBlockchain personBlockchain = null;
			//Party agent = party.get

			Party agentParty = null;
			if(agentReceiptEnabled == 1) {
				/**
				 * 代理充值，查询代理信息
				 * select * FROM PAT_USER_RECOM WHERE party_id='4021d52a96d72aab0196ef64f3e0183d' limit 1;
				 * select * FROM pat_party where uuid='4021d52a96d72af00196ef5dba74006c';
				 * select * FROM T_PARTY_BLOCKCHAIN where user_name='xxu62420011@gmail.com';
				 */
				agentParty = userRecomService.getAgentParty(partyId);
			}

			for (int i = 0; i < data.size(); i++) {				
				if (canRecharge == 1) {
					// 允许在线充值，展示二维码
					if (!StringUtils.isNullOrEmpty(data.get(i).getImg())) {
						String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath="
								+ data.get(i).getImg();
						data.get(i).setImg(path);
					}
					double fee = 1;

					if (data.get(i).getCoin().equalsIgnoreCase("BTC")) {
						fee = Double.parseDouble(hobiDataService.getSymbolRealPrize("btc"));
						if(Objects.nonNull(agentParty)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(agentParty.getUsername(),"BTC", null);
						} else if (Objects.nonNull(party)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(party.getUsername(),"BTC", null);
						}
						if(Objects.nonNull(agentParty)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(agentParty.getUsername(),"BTC", null);
						}
					} else if(data.get(i).getCoin().equalsIgnoreCase("ETH")) {
						fee = Double.parseDouble(hobiDataService.getSymbolRealPrize("eth"));
						if(Objects.nonNull(agentParty)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(agentParty.getUsername(), "ETH", null);
						}else if (Objects.nonNull(party)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(party.getUsername(),"ETH", null);
						}
					} else if(data.get(i).getCoin().equalsIgnoreCase("USDC")){
						if(Objects.nonNull(agentParty)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(agentParty.getUsername(),"USDC", null);
						}else if (Objects.nonNull(party)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(party.getUsername(),"USDC", null);
						}
						if(Objects.nonNull(agentParty)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(agentParty.getUsername(),"USDC",null);
						}
					} else if(data.get(i).getCoin().equalsIgnoreCase("USDT")){
						String blockchainName = data.get(i).getBlockchain_name();
						if(Objects.nonNull(agentParty)){
							personBlockchain = channelBlockchainService.findPersonBlockchain(agentParty.getUsername(),"USDT",blockchainName);
						}else if (Objects.nonNull(party)) {
							personBlockchain = channelBlockchainService.findPersonBlockchain(party.getUsername(),"USDT",blockchainName);
						}
						if(Objects.nonNull(agentParty)){
							personBlockchain = channelBlockchainService.findPersonBlockchain(agentParty.getUsername(),"USDT",blockchainName);
						}
					}

					data.get(i).setFee(fee);
					data.get(i).setRecharge_limit_max(rechargeLimitMax);
					data.get(i).setRecharge_limit_min(rechargeLimitMin);
					if (Objects.nonNull(personBlockchain)) {
						if (StringUtils.isNotEmpty(personBlockchain.getAddress())) {
							data.get(i).setAddress(personBlockchain.getAddress());
						}
						if (StringUtils.isNotEmpty(personBlockchain.getQrImage())) {
							String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath="
									+ personBlockchain.getQrImage();
							data.get(i).setImg(path);
						}
					}
				} else {
					data.get(i).setImg(null);
					data.get(i).setAddress(null);
				}
			}

			resultObject.setData(data);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
			logger.error("---> ChannelBlockchainController.list error:", e);
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("---> ChannelBlockchainController.list error:", t);
		}

		return resultObject;
	}

	/**
	 * 根据币种获取链地址
	 */
	@RequestMapping(action + "getBlockchainName.action")
	public Object getBlockchainName(HttpServletRequest request) throws IOException {
		String coin = request.getParameter("coin");

		List<ChannelBlockchain> data = new ArrayList<ChannelBlockchain>();
		
		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			resultObject.setData(data);
			return resultObject;
		}

		try {

			data = this.channelBlockchainService.findByCoin(coin);
			
			for (int i = 0; i < data.size(); i++) {
				if (1 == this.sysparaService.find("can_recharge").getInteger()) {
					
					if (!StringUtils.isNullOrEmpty(data.get(i).getImg())) {
						
						String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + data.get(i).getImg();
						data.get(i).setImg_str("/public/showimg!showImg.action?imagePath=" + data.get(i).getImg());
						data.get(i).setImg(path);
					}
				} else {
					data.get(i).setImg(null);
					data.get(i).setImg_str(null);
					data.get(i).setAddress(null);
				}
			}

			resultObject.setData(data);
			
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
