package project.data.internal;

import java.util.List;

import project.data.model.Trend;

public class TrendTimeObject  extends TimeObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4078190280148061255L;


	private List<Trend> trend;


	public List<Trend> getTrend() {
		return trend;
	}

	public void setTrend(List<Trend> trend) {
		this.trend = trend;
	}

}
