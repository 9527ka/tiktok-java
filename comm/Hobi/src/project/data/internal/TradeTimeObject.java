package project.data.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import project.data.model.Trade;
import project.data.model.TradeEntry;

public class TradeTimeObject  extends TimeObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8540786365761455345L;


	private Trade trade;

	public void put(String symbol, List<TradeEntry> data) {
		if (trade == null) {
			trade = new Trade();
			trade.setSymbol(symbol);
		}

		trade.getData().addAll(data);
		Collections.sort(trade.getData());

		if (trade.getData().size() > 50) {
			List<TradeEntry> data_50 = new ArrayList<TradeEntry>();
			for (int i = 0; i < 50; i++) {
				data_50.add(trade.getData().get(i));
			}
			trade.setData(data_50);
		}

	}



	public Trade getTrade() {
		return trade;
	}

}
