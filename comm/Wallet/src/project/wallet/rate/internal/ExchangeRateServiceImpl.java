package project.wallet.rate.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.RedisKeys;
import project.invest.goods.model.Useraddress;
import project.invest.project.model.ExchangeOrder;
import project.invest.project.model.Project;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.redis.RedisHandler;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import project.wallet.rate.ExchangeRate;
import project.wallet.rate.ExchangeRateService;
import project.wallet.rate.PaymentMethod;

public class ExchangeRateServiceImpl extends HibernateDaoSupport implements ExchangeRateService {

	private PagedQueryDao pagedQueryDao;

	private RedisHandler redisHandler;

	private WalletService walletService;

	private MoneyLogService moneyLogService;

	private PartyService partyService;

	private KycService kycService;

	@Override
	public ExchangeRate findById(String id) {
		return this.getHibernateTemplate().get(ExchangeRate.class, id);
	}


	@Override
	public List<ExchangeRate> listExchangeRates(int pageNum, int pageSize) {
		DetachedCriteria query = DetachedCriteria.forClass(ExchangeRate.class);
		query.add( Property.forName("status").eq(0) );
		query.addOrder(Order.asc("sort"));
		return (List<ExchangeRate>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
	}

	@Override
	public List<ExchangeOrder> listExchangeRecords(String partyId, int pageNum, int pageSize) {
		DetachedCriteria query = DetachedCriteria.forClass(ExchangeOrder.class);
		query.add( Property.forName("partyId").eq(partyId) );
		query.addOrder(Order.desc("createTime"));
		return (List<ExchangeOrder>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
	}

	@Override
	public String updateExchange(String partyId, ExchangeRate exchangeRate, double amount, Kyc kyc ,String bankName, String bankAccount) {
		Wallet wallet = walletService.saveWalletByPartyId(partyId);
		double amount_before = wallet.getMoney();
		if(amount>amount_before){
			throw new BusinessException("余额不足");
		}

		wallet.setMoney(Arith.roundDown(Arith.sub(wallet.getMoney(), amount),2));

		walletService.update(wallet);


		ExchangeOrder orders = new ExchangeOrder();
		orders.setPartyId(partyId);
		orders.setSymbol("USDT");
		orders.setSymbolValue(amount);
		orders.setOrderPriceType(exchangeRate.getCurrency());
		orders.setOrderPriceAmount(Arith.mul(amount,exchangeRate.getRata()));
		orders.setStaus(0);
		orders.setCreateTime(new Date());
		orders.setRealAmount(Arith.mul(amount,exchangeRate.getRata()));
		orders.setPayType(0);
		orders.setRata(exchangeRate.getRata());
		orders.setBankName(bankName);
		orders.setBankAccount(bankAccount);
		orders.setCurrency_symbol(exchangeRate.getCurrency_symbol());
		orders.setRata(exchangeRate.getRata());
		this.getHibernateTemplate().save(orders);

		MoneyLog moneyLog = new MoneyLog();
		moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
		moneyLog.setAmount_before(amount_before);
		moneyLog.setAmount(Arith.sub(0, amount));
		moneyLog.setAmount_after(wallet.getMoney());

		moneyLog.setLog("otc兑换[" + orders.getId() + "]");
		moneyLog.setPartyId(partyId);
		moneyLog.setWallettype(Constants.WALLET);
		moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_OTC_IN);

		moneyLogService.save(moneyLog);

		return orders.getId().toString();
	}

	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name, String startTime, String endTime, Integer status) {
		StringBuffer queryString = new StringBuffer();
		queryString.append(" SELECT * FROM T_EXCHANGE_RATE where 1 = 1 ");

		Map<String, Object> parameters = new HashMap();

		if (StringUtils.isNotEmpty(name)) {
			queryString.append(" AND NAME=:name ");
			parameters.put("name", name);

		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append(" AND DATE(CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime", DateUtils.toDate(startTime));
		}
		if (-2 != status) {
			queryString.append(" and STATUS =:status");
			parameters.put("status", status);
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append(" AND DATE(CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}
		queryString.append(" ORDER BY UUID desc");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		return page;
	}

	@Override
	public double getExchangeRate(String exchangeCurrency, String baseCurrency) {
		StringBuffer queryString = new StringBuffer(" FROM ExchangeRate WHERE currency = '"+ exchangeCurrency+"'");
		List<ExchangeRate> list =   (List<ExchangeRate>) this.getHibernateTemplate().find(queryString.toString());
		if(list == null || list.size()==0){
			throw new RuntimeException("没有配置汇率! 货币："+exchangeCurrency);
		}
		return list.get(0).getRata();
	}

	@Override
	public void update(ExchangeRate entity) {
		this.getHibernateTemplate().update(entity);
	}



	@Override
	public ExchangeRate get(String id) {
		return (ExchangeRate) redisHandler.get(RedisKeys.EXCHANGE_RATE_ID + id);
//		return this.getHibernateTemplate().get(ExchangeRate.class, id);
	}


	@Override
	public void savePaymentMethod(String partyId, int use, int payType, String bankName, String bankAccount, Kyc kyc) {

		List<PaymentMethod> list = listPaymentMethod(partyId);
		if(list.size()>5){
			throw new BusinessException("卡号达上限");
		}

		if(use==1){
			for(PaymentMethod b: listPaymentMethod(partyId)){
				if(b.getStatus()==1){
					b.setStatus(0);
					getHibernateTemplate().update(b);
				}

			}
		}

		PaymentMethod addRess = new PaymentMethod();
		addRess.setPartyId(partyId);
		addRess.setPayType(payType);
		addRess.setStatus(use);
		addRess.setBankName(bankName);
		addRess.setBankAccount(bankAccount);
		addRess.setStatus(use);
		addRess.setCreateTime(new Date());
		addRess.setRealName(kyc.getName());
		getHibernateTemplate().save(addRess);

	}

	@Override
	public void updatePaymentMethod(String id, String partyId, int use, String bankName, String bankAccount) {
		PaymentMethod addRess = this.getHibernateTemplate().get(PaymentMethod.class, id);
		if(addRess==null||!partyId.equals(addRess.getPartyId())){
			return;
		}
		addRess.setStatus(use);
		addRess.setBankName(bankName);
		addRess.setBankAccount(bankAccount);
		this.getHibernateTemplate().update(addRess);
		if(use==1){
			for(PaymentMethod b: listPaymentMethod(partyId)){
				if(id.equals(b.getId())){
					continue;
				}
				b.setStatus(0);
				getHibernateTemplate().update(b);
			}
		}
	}

	@Override
	public void removePaymentMethod(String id) {
		PaymentMethod address = this.getHibernateTemplate().get(PaymentMethod.class, id);
		if(address==null){
			return;
		}
		getHibernateTemplate().delete(address);
	}

	@Override
	public List<PaymentMethod> listPaymentMethod(String partyId) {
		DetachedCriteria query = DetachedCriteria.forClass(PaymentMethod.class);
		query.add( Property.forName("partyId").eq(partyId) );
		query.addOrder(Order.desc("createTime"));
		return (List<PaymentMethod>) getHibernateTemplate().findByCriteria(query,0,10);
	}

	@Override
	public PaymentMethod getDefaultPaymentMethod(String partyId) {
		DetachedCriteria query = DetachedCriteria.forClass(PaymentMethod.class);
		query.add( Property.forName("partyId").eq(partyId) );
		query.add( Property.forName("status").eq(1) );
		List l = getHibernateTemplate().findByCriteria(query,0,1);
		if(l.size()>0){
			return (PaymentMethod) l.get(0);
		}
		return null;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setKycService(KycService kycService) {
		this.kycService = kycService;
	}
}
