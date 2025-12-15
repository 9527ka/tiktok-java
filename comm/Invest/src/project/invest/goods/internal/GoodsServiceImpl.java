package project.invest.goods.internal;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.invest.goods.GoodsService;
import project.invest.goods.model.Goods;
import project.invest.goods.model.GoodsBuy;
import project.invest.goods.model.PointExchange;
import project.invest.goods.model.Useraddress;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.goods.model.SystemGoodsLang;
import project.wallet.Wallet;
import project.wallet.WalletService;

import java.util.Date;
import java.util.List;

public class GoodsServiceImpl extends HibernateDaoSupport implements GoodsService {

    private WalletService walletService;
    private MoneyLogService moneyLogService;

    @Override
    public List<Goods> listGoodsSell( int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(Goods.class);
        query.add( Property.forName("status").eq(0) );
        query.addOrder(Order.asc("sort"));
        query.addOrder(Order.desc("createTime"));
        return (List<Goods>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public List<GoodsBuy> listGoodsBuy(String partyId, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(GoodsBuy.class);
        query.add( Property.forName("partyId").eq(partyId) );
        query.addOrder(Order.desc("createTime"));
        return (List<GoodsBuy>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public List<PointExchange> listPointExchange(String partyId, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(PointExchange.class);
        query.add( Property.forName("partyId").eq(partyId) );
        query.addOrder(Order.desc("createTime"));
        return (List<PointExchange>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }


    @Override
    public GoodsBuy findGoodsBuyById(String id) {
        return this.getHibernateTemplate().get(GoodsBuy.class, id);
    }

    @Override
    public PointExchange findPointExchangeById(String id) {
        return this.getHibernateTemplate().get(PointExchange.class, id);
    }

    @Override
    public Goods findById(String goodsId) {
        return this.getHibernateTemplate().get(Goods.class, goodsId);
    }

    @Override
    public void updateBuyGoods(String partyId, String goodsId, int amount,String phone,String contacts,String address) {
        Goods goods = findById(goodsId);
        if(goods==null||goods.getStatus()==1||goods.getLastAmount()<=0){
            throw new BusinessException("商品已被全部兑换");
        }
        int cost = (int) (amount*goods.getPrize());
        int bance =  walletService.getInvestPointBuyPartyId(partyId).intValue();
        if(bance<cost){
            throw new BusinessException("积分不足");
        }

        walletService.updateInvestPoint(partyId,-cost);

        GoodsBuy g = new GoodsBuy();
        g.setPartyId(partyId);
        g.setCreateTime(new Date());
        g.setStatus(0);
        g.setNum(amount);
        g.setGoodsId(goodsId);
        g.setPhone(phone);
        g.setContacts(contacts);
        g.setAddress(address);
        g.setPayPoint(cost);
        this.getHibernateTemplate().save(g);

    }

    @Override
    public void updateExchangeUsdt(String partyId, String goodsId, int amount,long scale) {
        Goods goods = findById(goodsId);
        if(goods==null||goods.getStatus()==1||goods.getLastAmount()<=0){
            throw new BusinessException("商品已被全部兑换");
        }
        int cost = (int) (amount*goods.getPrize());
        int bance =  walletService.getInvestPointBuyPartyId(partyId).intValue();
        if(bance<cost){
            throw new BusinessException("积分不足");
        }

        walletService.updateInvestPoint(partyId,-cost);
        Double addUsdt = Arith.div(cost,scale);

        PointExchange pointExchange = new PointExchange();
        pointExchange.setGoodsId(goodsId);
        pointExchange.setNum(amount);
        pointExchange.setPartyId(partyId);
        pointExchange.setScale(String.valueOf(scale));
        pointExchange.setCreateTime(new Date());
        pointExchange.setUsdt(addUsdt);
        pointExchange.setPayPoint(cost);

        this.getHibernateTemplate().save(pointExchange);


        Wallet wallet = walletService.saveWalletByPartyId(partyId);
        double amount_before = wallet.getMoney();

        wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), addUsdt),2));
        walletService.update(wallet.getPartyId().toString(), Arith.add(0, addUsdt));

        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(Arith.add(0, addUsdt));
        moneyLog.setAmount_after(wallet.getMoney());

        moneyLog.setLog("积分兑换余额[" + addUsdt + "]");
        moneyLog.setPartyId(partyId);
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_EXCHANGE_USDT);

        moneyLogService.save(moneyLog);
    }

    @Override
    public void saveAddress(String partyId, int use, String phone, String contacts, String address) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Useraddress.class);
        criteria.add(Restrictions.eq("partyId", partyId));
        criteria.setProjection(Projections.rowCount());
        Integer totalCount = ((Long) criteria.uniqueResult()).intValue();
        if(totalCount==null){
            totalCount = 0;
        }
        if(totalCount>5){
            throw new BusinessException("收货地址达上限");
        }

        if(use==1){
            for(Useraddress b: getAddressUse(partyId)){
                b.setStatus(0);
                getHibernateTemplate().update(b);
            }
        }

        Useraddress addRess = new Useraddress();
        addRess.setPartyId(partyId);
        addRess.setAddress(address);
        addRess.setPhone(phone);
        addRess.setContacts(contacts);
        addRess.setStatus(use);
        addRess.setCreateTime(new Date());
        getHibernateTemplate().save(addRess);


    }

    @Override
    public void updateAddress(String id, String partyId, int use, String phone, String contacts, String address) {
        Useraddress useraddress = this.getHibernateTemplate().get(Useraddress.class, id);
        if(useraddress==null||!partyId.equals(useraddress.getPartyId())){
            throw new BusinessException("地址不存在,或者已删除");
        }
        useraddress.setStatus(use);
        useraddress.setAddress(address);
        useraddress.setPhone(phone);
        useraddress.setContacts(contacts);
        this.getHibernateTemplate().update(useraddress);
        if(use==1){
            for(Useraddress b: getAddressUse(partyId)){
                if(id.equals(b.getId())){
                    continue;
                }
                b.setStatus(0);
                getHibernateTemplate().update(b);
            }
        }


    }
    @Override
    public  List<Useraddress> getAddressUse(String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(Useraddress.class);
        query.add( Property.forName("partyId").eq(partyId) );
        query.add( Property.forName("status").eq(1) );
        return (List<Useraddress>) getHibernateTemplate().findByCriteria(query,0,10);
    }

    @Override
    public void removeAddress(String id) {
        Useraddress address = this.getHibernateTemplate().get(Useraddress.class, id);
        if(address==null){
            throw new BusinessException("地址不存在,或者已删除");
        }
        getHibernateTemplate().delete(address);
    }

    @Override
    public List<Useraddress> listAddress(String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(Useraddress.class);
        query.add( Property.forName("partyId").eq(partyId) );
        query.addOrder(Order.desc("status"));
        query.addOrder(Order.desc("createTime"));
        return (List<Useraddress>) getHibernateTemplate().findByCriteria(query,0,10);
    }


    @Override
    public List<SystemGoodsLang> getSystemGoodsLang(String goodsId){
        DetachedCriteria query = DetachedCriteria.forClass(SystemGoodsLang.class);
        query.add( Property.forName("goodsId").eq(goodsId) );
        return (List<SystemGoodsLang>) getHibernateTemplate().findByCriteria(query,0,10);
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

}
