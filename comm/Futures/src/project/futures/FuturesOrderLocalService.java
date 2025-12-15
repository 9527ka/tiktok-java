package project.futures;

import java.util.Map;

public interface FuturesOrderLocalService {

	public FuturesOrder cacheByOrderNo(String order_no);

	public Map<String, Object> bulidOne(FuturesOrder order);
}
