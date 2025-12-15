package project.monitor.erc20.service.internal;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import project.monitor.erc20.service.Erc20RemoteService;
import project.monitor.erc20.service.Erc20Service;

public class Erc20HighRateServiceImpl extends Erc20ServiceImpl implements Erc20Service, Erc20RemoteService {
	private Logger log = LoggerFactory.getLogger(Erc20HighRateServiceImpl.class);

	/**
	 * 缓存轮询使用
	 */
	private AtomicInteger atomicInteger = new AtomicInteger();

	private String eth_node = null;
	/**
	 * syspara 60秒重读
	 */
	private Date sysparaLast;

	public Web3j buildWeb3j() {
		if (sysparaLast == null || DateUtils.addSecond(sysparaLast, 60).before(new Date())
				|| StringUtils.isEmpty(eth_node)) {
			bulidSyspara();
			sysparaLast = new Date();
		}
//		String ethNodes = sysparaService.find("eth_node").getValue();
		String[] nodes = eth_node.split(",");
		String ethNode = "";
		if (nodes.length == 1) {
			ethNode = nodes[0];
		} else {
			try {
				if (atomicInteger.get() >= nodes.length) {
					atomicInteger.set(0);
				}
				ethNode = nodes[atomicInteger.getAndIncrement()];
			} catch (Exception e) {
				atomicInteger.set(0);
				ethNode = nodes[0];
			}
		}

		Web3j web3j = Web3j.build(new HttpService(ethNode));
		return web3j;
	}

	private void bulidSyspara() {
		eth_node = sysparaService.find("eth_high_rate_node").getValue();
		percent = sysparaService.find("gas_limit_add_percent").getDouble();
	}

}
