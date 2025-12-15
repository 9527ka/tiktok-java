package project.data.internal;

import java.util.ArrayList;
import java.util.List;

import project.data.model.Kline;

public class KlineTimeObject  extends TimeObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5777137609729197999L;


	private List<Kline> kline=new ArrayList<Kline>();



	/**
	 * @return the kline
	 */
	public List<Kline> getKline() {
		return kline;
	}

	/**
	 * @param kline the kline to set
	 */
	public void setKline(List<Kline> kline) {
		this.kline = kline;
	}

}
