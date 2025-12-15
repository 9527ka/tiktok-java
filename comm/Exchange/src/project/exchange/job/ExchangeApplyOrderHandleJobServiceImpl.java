package project.exchange.job;

import project.data.model.Realtime;
import project.exchange.ExchangeApplyOrder;

public class ExchangeApplyOrderHandleJobServiceImpl implements ExchangeApplyOrderHandleJobService{
	private ExchangeApplyOrderHandleJob exchangeApplyOrderHandleJob;
	
	public void handle(ExchangeApplyOrder applyOrder, Realtime realtime) {
		exchangeApplyOrderHandleJob.handle(applyOrder, realtime);
	}

	public void setExchangeApplyOrderHandleJob(ExchangeApplyOrderHandleJob exchangeApplyOrderHandleJob) {
		this.exchangeApplyOrderHandleJob = exchangeApplyOrderHandleJob;
	}
	
}
