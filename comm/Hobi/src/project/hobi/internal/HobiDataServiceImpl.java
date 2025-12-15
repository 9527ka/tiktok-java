package project.hobi.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.data.model.Depth;
import project.data.model.DepthEntry;
import project.data.model.Kline;
import project.data.model.Realtime;
import project.data.model.Symbols;
import project.data.model.Trade;
import project.data.model.TradeEntry;
import project.hobi.Config;
import project.hobi.HobiDataService;
import project.hobi.http.HttpHelper;
import project.hobi.http.HttpMethodType;
import project.invest.InvestRedisKeys;
import project.item.ItemService;
import project.item.model.Item;
import project.mall.MallRedisKeys;

public class HobiDataServiceImpl implements HobiDataService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 接口调用间隔（毫秒）
	 */
	private int interval = 100;
	private int sleep = 100;
	/**
	 * 最后一次访问接口时间
	 */
	private volatile Date last_time = new Date();

	private volatile boolean lock = false;

	private ItemService itemService;

	@Override
	public List<Realtime> realtime(int maximum) {
		List<Realtime> list = new ArrayList<Realtime>();
		boolean current_lock = false;
		if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
			ThreadUtils.sleep(sleep);
			if (maximum >= 100) {
				logger.warn("---------> 超过 100 次的轮询，未能竞争到查询实时汇率的处理机会，返回空集合。");
				return list;
			} else {
				return realtime(++maximum);
			}
		} else {
			try {
				current_lock = true;
				lock = true;
				Map<String, Object> param = new HashMap<String, Object>();
				String result = HttpHelper.getJSONFromHttp(Config.url + Config.tickers, param, HttpMethodType.GET);
				JSONObject resultJson = JSON.parseObject(result);
				String status = resultJson.getString("status");
				if ("ok".equals(status)) {
					JSONArray dataArray = resultJson.getJSONArray("data");
					if (dataArray == null) {
						return new ArrayList<>();
					}
					Long ts = resultJson.getLongValue("ts");
					for (int i = 0; i < dataArray.size(); i++) {
						JSONObject realtimeJson = dataArray.getJSONObject(i);
						Realtime realtime = new Realtime();
						Item item = itemService.cacheBySymbolData(realtimeJson.getString("symbol"));
						if (item == null) {
							continue;
						}
						realtime.setSymbol(item.getSymbol());
						realtime.setName(item.getName());
						realtime.setTs(ts);
						realtime.setOpen(realtimeJson.getDouble("open"));
						realtime.setClose(realtimeJson.getDouble("close"));
						realtime.setHigh(realtimeJson.getDouble("high"));
						realtime.setLow(realtimeJson.getDouble("low"));
						realtime.setAmount(realtimeJson.getDouble("amount"));
						realtime.setVolume(realtimeJson.getDouble("vol"));
						list.add(realtime);
					}
				} else {
					logger.error(" realtime()error, resultJson [ " + resultJson.toJSONString() + " ]");
				}
			} catch (Exception e) {
				logger.error("realtime error:", e);
			} finally {
				if (current_lock) {
					lock = false;
					last_time = new Date();
				}

			}
		}
		return list;
	}

	@Override
	public List<Kline> kline(String symbol, String period, Integer num, int maximum) {
		List<Kline> list = new ArrayList<Kline>();
		Item item = itemService.cacheBySymbolData(symbol);
		if (item == null) {
			return list;
		}
		boolean current_lock = false;
		if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
			ThreadUtils.sleep(sleep);
			if (maximum >= 100) {
				return list;
			} else {
				return this.kline(symbol, period, num, ++maximum);
			}

		} else {
			try {
				current_lock = true;
				lock = true;
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("symbol", symbol);
				param.put("period", period);
				if (num == null) {
					if (Kline.PERIOD_1MIN.equals(period)) {
						param.put("size", 1440);
					}
					if (Kline.PERIOD_5MIN.equals(period)) {
						param.put("size", 576);
					}
					if (Kline.PERIOD_15MIN.equals(period)) {
						param.put("size", 576);
					}
					if (Kline.PERIOD_30MIN.equals(period)) {
						param.put("size", 576);
					}
					if (Kline.PERIOD_60MIN.equals(period)) {
						param.put("size", 576);
					}

					if (Kline.PERIOD_4HOUR.equals(period)) {
						param.put("size", 576);
					}
					if (Kline.PERIOD_1DAY.equals(period)) {
						param.put("size", 500);
					}
					if (Kline.PERIOD_1MON.equals(period)) {
						param.put("size", 500);
					}
					if (Kline.PERIOD_1WEEK.equals(period)) {
						param.put("size", 500);
					}

				} else {
					param.put("size", num);
				}

				String result = HttpHelper.getJSONFromHttp(Config.url + Config.kline, param, HttpMethodType.GET);
				JSONObject resultJson = JSON.parseObject(result);
				String status = resultJson.getString("status");
				if ("ok".equals(status)) {
					JSONArray dataArray = resultJson.getJSONArray("data");
					/**
					 * 丢弃第一行数据
					 */
					int start = 1;
					if (num != null && num == 1)
						start = 0;
					for (int i = start; i < dataArray.size(); i++) {
						JSONObject realtimeJson = dataArray.getJSONObject(i);
						Kline kline = new Kline();
						kline.setSymbol(item.getSymbol());
						kline.setPeriod(period);
						kline.setTs(Long.valueOf(realtimeJson.getString("id") + "000"));
						kline.setOpen(realtimeJson.getDouble("open"));
						kline.setClose(realtimeJson.getDouble("close"));
						kline.setHigh(realtimeJson.getDouble("high"));
						kline.setLow(realtimeJson.getDouble("low"));
						kline.setVolume(realtimeJson.getDouble("vol"));
						list.add(kline);
					}

				}
			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				if (current_lock) {
					lock = false;
					last_time = new Date();
				}

			}
		}
		return list;
	}

	/**
	 * 市场深度数据（20档）,包装，数据本地化处理
	 */
	public Depth depthDecorator(String symbol, int maximum) {
		Depth depth = this.depth(symbol, maximum);
		Item item = itemService.cacheBySymbolData(symbol);
		item = itemService.cacheBySymbol(item.getSymbol(), false);
		if ((depth == null || item.getAdjustment_value() == null || item.getAdjustment_value() == 0) &&
				(item.getMultiple() == 0 || item.getMultiple() == 1)) {
			return depth;
		}

		List<DepthEntry> asks = depth.getAsks();
		for (int i = 0; i < asks.size(); i++) {
			DepthEntry depthEntry = asks.get(i);

			/**
			 * 调整交易量倍数和 行情值
			 */
			if (item.getMultiple() > 0) {
				depthEntry.setAmount(Arith.mul(depthEntry.getAmount(), item.getMultiple()));
			}else {
				depthEntry.setAmount(depthEntry.getAmount());
			}
			depthEntry.setPrice(Arith.add(depthEntry.getPrice(), item.getAdjustment_value()));
		}

		List<DepthEntry> bids = depth.getBids();
		for (int i = 0; i < bids.size(); i++) {
			DepthEntry depthEntry = bids.get(i);
			/**
			 * 调整交易量倍数和 行情值
			 */
			if (item.getMultiple() > 0) {
				depthEntry.setAmount(Arith.mul(depthEntry.getAmount(), item.getMultiple()));
			}else {
				depthEntry.setAmount(depthEntry.getAmount());
			}
			depthEntry.setPrice(Arith.add(depthEntry.getPrice(), item.getAdjustment_value()));
		}

		return depth;
	}

	@Override
	public Depth depth(String symbol, int maximum) {
		boolean current_lock = false;
		if (StringUtils.isNullOrEmpty(symbol)) {
			return null;
		}
		if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
			ThreadUtils.sleep(sleep);
			if (maximum >= 100) {
				return null;
			} else {
				return this.depth(symbol, ++maximum);
			}
		} else {
			try {
				current_lock = true;
				lock = true;
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("symbol", symbol);
				param.put("type", "step2");

				String result = HttpHelper.getJSONFromHttp(Config.url + Config.depth, param, HttpMethodType.GET);
				JSONObject resultJson = JSON.parseObject(result);
				String status = resultJson.getString("status");
				if ("ok".equals(status)) {
					JSONObject dataJson = resultJson.getJSONObject("tick");
					Long ts = resultJson.getLongValue("ts");
					Depth depth = new Depth();

					Item item = itemService.cacheBySymbolData(symbol);
					if (item == null) {
						return null;
					}
					depth.setSymbol(item.getSymbol());
					depth.setTs(ts);

					JSONArray bidsArray = dataJson.getJSONArray("bids");
					for (int i = 0; i < bidsArray.size(); i++) {

						JSONArray object = (JSONArray) bidsArray.get(i);
						DepthEntry depthEntry = new DepthEntry();
						depthEntry.setPrice(object.getDouble(0));
						depthEntry.setAmount(object.getDouble(1));
						depth.getBids().add(depthEntry);

					}

					JSONArray asksArray = dataJson.getJSONArray("asks");
					for (int i = 0; i < asksArray.size(); i++) {
						JSONArray object = (JSONArray) asksArray.get(i);
						DepthEntry depthEntry = new DepthEntry();
						depthEntry.setPrice(object.getDouble(0));
						depthEntry.setAmount(object.getDouble(1));
						depth.getAsks().add(depthEntry);

					}

					return depth;
				}

			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				if (current_lock) {
					lock = false;
					last_time = new Date();
				}

			}
		}
		return null;
	}

	/**
	 * 获得近期交易记录,包装，数据本地化处理
	 */
	public Trade tradeDecorator(String symbol, int maximum) {
		Trade trade = this.trade(symbol, maximum);
		Item item = itemService.cacheBySymbolData(symbol);
		item = itemService.cacheBySymbol(item.getSymbol(), false);
		if ((trade == null || item.getAdjustment_value() == null || item.getAdjustment_value() == 0) &&
				(item.getMultiple() == 0 || item.getMultiple() == 1)) {
			return trade;
		}
		List<TradeEntry> data = trade.getData();
		for (int i = 0; i < data.size(); i++) {
			TradeEntry tradeEntry = data.get(i);

			/**
			 * 调整交易量倍数和 行情值
			 */
			if (item.getMultiple() > 0) {
				tradeEntry.setAmount(Arith.mul(tradeEntry.getAmount(), item.getMultiple()));
			}else {
				tradeEntry.setAmount(tradeEntry.getAmount());
			}
			tradeEntry.setPrice(Arith.add(tradeEntry.getPrice(), item.getAdjustment_value()));
		}
		return trade;

	}

	@Override
	public Trade trade(String symbol, int maximum) {
		boolean current_lock = false;
		if (StringUtils.isNullOrEmpty(symbol)) {
			return null;
		}
		if (lock || (new Date().getTime() - last_time.getTime()) < interval) {
			ThreadUtils.sleep(sleep);
			if (maximum >= 100) {

				return null;
			} else {
				return this.trade(symbol, ++maximum);
			}
		} else {
			try {
				current_lock = true;
				lock = true;
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("symbol", symbol);

				String result = HttpHelper.getJSONFromHttp(Config.url + Config.trade, param, HttpMethodType.GET);
				JSONObject resultJson = JSON.parseObject(result);
				String status = resultJson.getString("status");
				if ("ok".equals(status)) {
					JSONObject dataJson = resultJson.getJSONObject("tick");
					Long ts = resultJson.getLongValue("ts");

					Trade trade = new Trade();

					Item item = itemService.cacheBySymbolData(symbol);
					if (item == null) {
						return null;
					}
					trade.setSymbol(item.getSymbol());
					trade.setTs(ts);

					JSONArray dataArray = dataJson.getJSONArray("data");
					for (int i = 0; i < dataArray.size(); i++) {
						JSONObject object = dataArray.getJSONObject(i);
						TradeEntry tradeEntry = new TradeEntry();
						tradeEntry.setTs(object.getLong("ts"));
						tradeEntry.setPrice(object.getDouble("price"));
						tradeEntry.setAmount(object.getDouble("amount"));
						tradeEntry.setDirection(object.getString("direction"));
						trade.getData().add(tradeEntry);

					}
					return trade;
				}

			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				if (current_lock) {
					lock = false;
					last_time = new Date();
				}

			}
		}
		return null;
	}

	@Override
	public List<Symbols> symbols() {
		List<Symbols> list = new ArrayList<Symbols>();
		boolean current_lock = false;
		if (lock || (new Date().getTime() - last_time.getTime()) < interval) {

			return list;
		} else {
			try {
				current_lock = true;
				lock = true;
				Map<String, Object> param = new HashMap<String, Object>();
				String result = HttpHelper.getJSONFromHttp(Config.url + Config.symbols, param, HttpMethodType.GET);
				JSONObject resultJson = JSON.parseObject(result);
				String status = resultJson.getString("status");
				if ("ok".equals(status)) {
					JSONArray dataArray = resultJson.getJSONArray("data");
					for (int i = 0; i < dataArray.size(); i++) {
						JSONObject realtimeJson = dataArray.getJSONObject(i);
						Symbols symbols = new Symbols();
						symbols.setBase_currency(realtimeJson.getString("base-currency"));
						symbols.setQuote_currency(realtimeJson.getString("quote-currency"));
						symbols.setLeverage_ratio(realtimeJson.getDouble("leverage-ratio"));
						symbols.setPrice_precision(realtimeJson.getIntValue("price-precision"));
						symbols.setState(realtimeJson.getString("state"));
						symbols.setSymbol(realtimeJson.getString("symbol"));
						list.add(symbols);
					}

				} else {
					logger.error(" symbols()error, resultJson [ " + resultJson.toJSONString() + " ]");
				}
			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				if (current_lock) {
					lock = false;
					last_time = new Date();
				}

			}
		}
		return list;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	@Override
	public String getSymbolRealPrize(String symbol) {
		return itemService.getRedisHandler().getString(MallRedisKeys.BRUSH_VIRTUAL_CURRENCY_PRICE+symbol);
	}

	@Override
	public void putSymbolRealCache(String symbol, String val) {
		itemService.getRedisHandler().setSyncString(MallRedisKeys.BRUSH_VIRTUAL_CURRENCY_PRICE+symbol,val);
	}

}
