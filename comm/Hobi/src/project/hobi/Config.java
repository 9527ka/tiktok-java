package project.hobi;

public class Config {

	/**
	 * API域名
	 */
	public static String url = "https://api.huobi.pro";

	/**
	 * 所有交易对的最新 Tickers
	 */
	public static String tickers = "/market/tickers";

	/**
	 * 市场深度数据
	 */
	public static String depth = "/market/depth";

	/**
	 * 最近市场成交记录
	 */
	public static String trade = "/market/trade";

	/**
	 * K 线数据（蜡烛图）
	 */
	public static String kline = "/market/history/kline";

	public static String symbols = "/v1/common/symbols";

}
