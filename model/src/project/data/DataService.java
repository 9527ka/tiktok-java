package project.data;

import java.util.List;

import project.data.model.Depth;
import project.data.model.Kline;
import project.data.model.Realtime;
import project.data.model.Trade;
import project.data.model.Trend;

public interface DataService {

	/**
	 * 实时价格
	 * 
	 * @param symbol 指定产品代码，多个用逗号分割，最大100个
	 * 
	 */
	public List<Realtime> realtime(String symbol);

	/**
	 * 市场深度数据
	 */
	public Depth depth(String symbol);

	/**
	 * 分时
	 */
	public List<Trend> trend(String symbol);

	/**
	 * K线
	 * 
	 */
	public List<Kline> kline(String symbol, String line);

	/**
	 * 获得近期交易记录
	 */
	public Trade trade(String symbol);

}
