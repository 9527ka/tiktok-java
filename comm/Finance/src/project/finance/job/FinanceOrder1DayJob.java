package project.finance.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.util.DateUtils;
import kernel.util.ThreadUtils;
import project.finance.FinanceOrder;
import project.finance.FinanceOrderLock;
import project.finance.FinanceOrderService;
import project.log.SysLog;
import project.log.SysLogService;

public class FinanceOrder1DayJob {
	private static Log logger = LogFactory.getLog(FinanceOrder1DayJob.class);
	protected FinanceOrderService financeOrderService;
	protected SysLogService sysLogService;
	
	public void taskJob() {
		try {
			List<FinanceOrder> financeOrder = financeOrderService.getAllStateBy_1();
			if(financeOrder != null) {
				for (int i = 0; i < financeOrder.size(); i++) {
					FinanceOrder order = financeOrder.get(i);
					boolean lock = false;
					try {

						if (!FinanceOrderLock.add(order.getOrder_no())) {
							continue;
						}
						lock = true;
						this.financeOrderService.addListProfit(order);

					} catch (Exception e) {
						logger.error("FinanceOrder1DayJob order profit fail，orderno:{"+order.getOrder_no()+"},e:", e);
					} finally {
						if (lock) {
							/**
							 * 每秒处理100个订单
							 */
							ThreadUtils.sleep(10);
							FinanceOrderLock.remove(order.getOrder_no());
						}

					}
				}
				logger.info(DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss)+" finance profit finished ,count:" + financeOrder.size());
			}
			
		
		} catch (Throwable e) {
			logger.error("FinanceOrder1DayJob run fail e:", e);
			SysLog entity = new SysLog();
			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setLog("FinanceOrder1DayJob 理财任务 执行失败 e:"+e);
			sysLogService.saveAsyn(entity);
		} finally {
			/**
			 * 暂停0.1秒
			 */
			ThreadUtils.sleep(1000);
		}

	}
	
	public void handleData(Date systemTime) {
		try {
			List<FinanceOrder> financeOrder = financeOrderService.getAllStateBy_1();
			if(financeOrder != null) {
				for (int i = 0; i < financeOrder.size(); i++) {
					FinanceOrder order = financeOrder.get(i);
					boolean lock = false;
					try {

						if (!FinanceOrderLock.add(order.getOrder_no())) {
							continue;
						}
						lock = true;
						this.financeOrderService.addListProfit(order,systemTime);

					} catch (Exception e) {
						logger.error("FinanceOrder1DayJob order profit fail，orderno:{"+order.getOrder_no()+"},e:", e);
					} finally {
						if (lock) {
							/**
							 * 每秒处理100个订单
							 */
							ThreadUtils.sleep(10);
							FinanceOrderLock.remove(order.getOrder_no());
						}

					}
				}
				logger.info(DateUtils.dateToStr(new Date(), DateUtils.DF_yyyyMMddHHmmss)+" finance profit finished ,count:" + financeOrder.size());
			}
			
		} catch (Throwable e) {
			logger.error("FinanceOrder1DayJob run fail e:", e);
			SysLog entity = new SysLog();
			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setLog("FinanceOrder1DayJob 理财任务 执行失败  e:"+e);
			sysLogService.saveAsyn(entity);
		} finally {
			/**
			 * 暂停0.1秒
			 */
			ThreadUtils.sleep(1000);
		}
	}

	public void setFinanceOrderService(FinanceOrderService financeOrderService) {
		this.financeOrderService = financeOrderService;
	}

	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}

	
}
