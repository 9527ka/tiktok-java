package project.data.internal;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import project.data.AdjustmentValue;
import project.data.AdjustmentValueCache;
import project.data.AdjustmentValueService;
import project.data.DataService;
import project.data.model.Realtime;
import project.item.ItemService;
import project.item.model.Item;

public class AdjustmentValueServiceImpl extends HibernateDaoSupport implements AdjustmentValueService {
	
	private DataService dataService;
	private ItemService itemService;

	public void adjust(String symbol, double value, double second) {
		if (value == 0.0D) {
			return;
		}

		Realtime realtime = dataService.realtime(symbol).get(0);
		double new_price = realtime.getClose();
		double plus = Math.abs(value);
		if (Arith.div(plus, new_price) > 0.1D) {
			throw new BusinessException("调整偏差过大，超过10%");
		}

		if (second <= 0) {
			/**
			 * 即时生效
			 */

			Double currentValue = AdjustmentValueCache.getCurrentValue().get(symbol);

			if (currentValue == null) {
				AdjustmentValueCache.getCurrentValue().put(symbol, value);
			} else {
				AdjustmentValueCache.getCurrentValue().put(symbol, Arith.add(currentValue, value));
			}
			/*
			 * 持久化缓存
			 */
			Item item = this.itemService.cacheBySymbol(symbol, false);
			if (item.getAdjustment_value() != AdjustmentValueCache.getCurrentValue().get(symbol)) {
				item.setAdjustment_value(AdjustmentValueCache.getCurrentValue().get(symbol));
				itemService.update(item);
			}

		} else {
			AdjustmentValue adjustmentValue = new AdjustmentValue();
			adjustmentValue.setSymbol(symbol);
			adjustmentValue.setValue(value);
			adjustmentValue.setSecond(second);
			AdjustmentValueCache.getDelayValue().put(symbol, adjustmentValue);
		}
	}

	@Override
	public Double getCurrentValue(String symbol) {
		return AdjustmentValueCache.getCurrentValue().get(symbol);
	}

	@Override
	public AdjustmentValue getDelayValue(String symbol) {
		return AdjustmentValueCache.getDelayValue().get(symbol);
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

}
