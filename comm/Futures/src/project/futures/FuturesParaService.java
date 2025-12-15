package project.futures;

import java.util.List;
import java.util.Map;

public interface FuturesParaService {

	public FuturesPara cacheGet(String id);

	public List<FuturesPara> cacheGetBySymbol(String symbol);

	public List<FuturesPara> cacheGetBySymbolSort(String symbol);

	public Map<String, Object> bulidOne(FuturesPara order);

	public void update(FuturesPara source);

	public void add(FuturesPara source);
	
	public void delete(FuturesPara source);

}
