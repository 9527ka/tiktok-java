package project.hobi;

import java.util.List;

import project.data.model.Depth;
import project.data.model.Kline;
import project.data.model.Realtime;
import project.data.model.Symbols;
import project.data.model.Trade;

public interface HobiDataService {

	/**
	 * 实时价格。所有交易对的最新 Tickers
	 * 
	 */
	public List<Realtime> realtime(int maximum);

	/**
	 * K线
	 * 
	 * @param period 1day, 1mon, 1week, 1year
	 * 
	 */
	public List<Kline> kline(String symbol, String period, Integer num, int maximum);

	/**
	 * 市场深度数据（20档）
	 */

	public Depth depth(String symbol, int maximum);

	/**
	 * 市场深度数据（20档）,包装，数据本地化处理
	 */
	public Depth depthDecorator(String symbol, int maximum);

	/**
	 * 获得近期交易记录
	 */
	public Trade trade(String symbol, int maximum);

	/**
	 * 获得近期交易记录,包装，数据本地化处理
	 */
	public Trade tradeDecorator(String symbol, int maximum);

	public List<Symbols> symbols();

	String getSymbolRealPrize(String symbol);

	void putSymbolRealCache(String symbol,String val);

}
