package project.data.loadcache;

import data.loadcache.WalletLoadCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.cms.data.loadcache.CmsLoadCacheService;
import project.contract.data.loadcache.ContractLoadCacheService;
import project.ddos.data.loadcache.IpHandleCacheService;
import project.finance.data.loadcache.FinanceLoadCacheService;
import project.futures.data.loadcache.FuturesLoadCacheService;
import project.mall.loadcache.MallLoadCacheService;
import project.miner.data.loadcache.MinerLoadCacheService;
import project.monitor.pledgegalaxy.data.loadcache.PledgeGalaxyLoadCacheService;
import project.user.data.loadcache.UserLoadCacheService;

//import project.news.data.loadcache.NewsLoadCacheService;

public class LoadCacheService {
	private static final Logger logger = LoggerFactory.getLogger(LoadCacheService.class);

	protected ItemLoadCacheService itemLoadCacheService;
	protected PartyLoadCacheService partyLoadCacheService;
	protected SysparaLoadCacheService sysparaLoadCacheService;
	protected UserLoadCacheService userLoadCacheService;
	protected ContractLoadCacheService contractLoadCacheService;
	protected MinerLoadCacheService minerLoadCacheService;
	protected PledgeGalaxyLoadCacheService pledgeGalaxyLoadCacheService;
	protected FinanceLoadCacheService financeLoadCacheService;
	protected CmsLoadCacheService cmsLoadCacheService;
	//protected NewsLoadCacheService newsLoadCacheService;
	protected FuturesLoadCacheService futuresLoadCacheService;
	protected WalletLoadCacheService walletLoadCacheService;
	protected MallLoadCacheService mallLoadCacheService;
	private IpHandleCacheService ipHandleCacheService;

	public void loadcache() {
		logger.info("开始初始化redis数据........");
		sysparaLoadCacheService.loadcache();
		partyLoadCacheService.loadcache();
		userLoadCacheService.loadcache();
		// 交易所的代码，废弃掉，caster 注释 2023-12-18
		 itemLoadCacheService.loadcache();
		ipHandleCacheService.loadIpMenu();
		//contractLoadCacheService.loadcache();
		//minerLoadCacheService.loadcache();
		//pledgeGalaxyLoadCacheService.loadcache();
		//financeLoadCacheService.loadcache();
//		cmsLoadCacheService.loadcache();
		//newsLoadCacheService.loadcache();
		//futuresLoadCacheService.loadcache();
		walletLoadCacheService.loadcache();
		logger.info("完成数据redis加载。");
		mallLoadCacheService.loadcache();
	}

	public void setItemLoadCacheService(ItemLoadCacheService itemLoadCacheService) {
		this.itemLoadCacheService = itemLoadCacheService;
	}

	public void setPartyLoadCacheService(PartyLoadCacheService partyLoadCacheService) {
		this.partyLoadCacheService = partyLoadCacheService;
	}

	public void setSysparaLoadCacheService(SysparaLoadCacheService sysparaLoadCacheService) {
		this.sysparaLoadCacheService = sysparaLoadCacheService;
	}

	public void setUserLoadCacheService(UserLoadCacheService userLoadCacheService) {
		this.userLoadCacheService = userLoadCacheService;
	}

	public void setContractLoadCacheService(ContractLoadCacheService contractLoadCacheService) {
		this.contractLoadCacheService = contractLoadCacheService;
	}

	public void setFuturesLoadCacheService(FuturesLoadCacheService futuresLoadCacheService) {
		this.futuresLoadCacheService = futuresLoadCacheService;
	}

	public void setCmsLoadCacheService(CmsLoadCacheService cmsLoadCacheService) {
		this.cmsLoadCacheService = cmsLoadCacheService;
	}

//	public void setNewsLoadCacheService(NewsLoadCacheService newsLoadCacheService) {
//		this.newsLoadCacheService = newsLoadCacheService;
//	}

	public void setWalletLoadCacheService(WalletLoadCacheService walletLoadCacheService) {
		this.walletLoadCacheService = walletLoadCacheService;
	}

	public MinerLoadCacheService getMinerLoadCacheService() {
		return minerLoadCacheService;
	}

	public void setMinerLoadCacheService(MinerLoadCacheService minerLoadCacheService) {
		this.minerLoadCacheService = minerLoadCacheService;
	}

	public FinanceLoadCacheService getFinanceLoadCacheService() {
		return financeLoadCacheService;
	}

	public void setFinanceLoadCacheService(FinanceLoadCacheService financeLoadCacheService) {
		this.financeLoadCacheService = financeLoadCacheService;
	}

	public PledgeGalaxyLoadCacheService getPledgeGalaxyLoadCacheService() {
		return pledgeGalaxyLoadCacheService;
	}

	public void setPledgeGalaxyLoadCacheService(PledgeGalaxyLoadCacheService pledgeGalaxyLoadCacheService) {
		this.pledgeGalaxyLoadCacheService = pledgeGalaxyLoadCacheService;
	}


	public void setMallLoadCacheService(MallLoadCacheService mallLoadCacheService) {
		this.mallLoadCacheService = mallLoadCacheService;
	}

	public void setIpHandleCacheService(IpHandleCacheService ipHandleCacheService) {
		this.ipHandleCacheService = ipHandleCacheService;
	}
}
