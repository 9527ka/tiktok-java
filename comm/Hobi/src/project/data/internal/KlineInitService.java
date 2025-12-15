package project.data.internal;

public interface KlineInitService {
	/**
	 * 初始化K线数据，初始化前会删除旧数据
	 * 
	 * @param symbol 指定产品代码，多个用逗号分割
	 */
	public void klineInit(String symbols);
}
