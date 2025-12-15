package project.data;

import java.util.List;

import project.data.model.Kline;
import project.data.model.Realtime;

public interface KlineService {

	public void saveInit(String symbol);

	public void saveOne(String symbol, String line);

	public List<Kline> find(String symbol, String line, int pageSie);

	public void delete(String line, int days);

	public Kline bulidKline(Realtime realtime, Kline lastOne, Kline hobiOne, String line) ;

}
