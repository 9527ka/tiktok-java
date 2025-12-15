package project.exchange.job;

import project.data.model.Realtime;
import project.exchange.ExchangeApplyOrder;

public interface ExchangeApplyOrderHandleJobService {
	public void handle(ExchangeApplyOrder applyOrder, Realtime realtime);
}
