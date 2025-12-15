package project.wallet.rate;

import java.util.List;
import java.util.Map;

import kernel.web.Page;
import project.invest.goods.model.Useraddress;
import project.invest.project.model.ExchangeOrder;
import project.user.kyc.Kyc;

public interface ExchangeRateService {


	public ExchangeRate findById(String id);

	List<ExchangeRate> listExchangeRates(int pageNum, int pageSize);


	List<ExchangeOrder> listExchangeRecords(String partyId,int pageNum, int pageSize);

	String updateExchange(String partyId, ExchangeRate exchangeRate, double usdt, Kyc kyc,String bankName, String bankAccount);

	void savePaymentMethod(String partyId,int use,  int payType,String bankName,String bankAccount, Kyc kyc);

	void updatePaymentMethod(String id,String partyId,int use,String bankName,String bankAccount);

	void removePaymentMethod(String id);

	List<PaymentMethod> listPaymentMethod(String partyId);

	PaymentMethod getDefaultPaymentMethod(String partyId);

	public ExchangeRate get(String id);

	public void update(ExchangeRate entity);

	Page pagedQuery(int pageNo, int pageSize, String name, String startTime, String endTime, Integer status);

	/**
	 * 获取汇率
	 * @param exchangeCurrency 兑换货币
	 * @param baseCurrency 基准货币
	 * @return
	 */
    double getExchangeRate(String exchangeCurrency, String baseCurrency);
}
