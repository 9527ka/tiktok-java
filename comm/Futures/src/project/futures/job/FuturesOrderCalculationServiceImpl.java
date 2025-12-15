package project.futures.job;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import project.data.DataCache;
import project.data.model.Realtime;
import project.futures.FuturesOrder;
import project.futures.FuturesOrderService;

public class FuturesOrderCalculationServiceImpl implements FuturesOrderCalculationService {
	private static Log logger = LogFactory.getLog(FuturesOrderCalculationServiceImpl.class);
	private FuturesOrderService futuresOrderService;

	public void saveCalculation(FuturesOrder order) {

		try {

			Realtime realtime = DataCache.getRealtime(order.getSymbol());
			if (null == realtime) {
				return;
			}

			double close = realtime.getClose();
			futuresOrderService.refreshCache(order, close);// 更新订单信息并纪录到缓存

			if (order.getSettlement_time().before(new Date())) {
				futuresOrderService.saveClose(order, realtime);

			}

		} catch (Throwable e) {
			logger.error("FuturesOrderCalculationServiceImpl run fail", e);
		}
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.futuresOrderService = futuresOrderService;
	}

}
