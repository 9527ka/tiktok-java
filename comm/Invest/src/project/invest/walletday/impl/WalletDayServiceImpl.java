package project.invest.walletday.impl;

import kernel.util.UUIDGenerator;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.invest.walletday.WalletDayService;
import project.wallet.WalletDay;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WalletDayServiceImpl extends HibernateDaoSupport implements WalletDayService {

    private Logger log = LoggerFactory.getLogger(WalletDayServiceImpl.class);
    private PagedQueryDao pagedQueryDao;

    @Override
    public Page pagedQuery(int pageNo, int pageSize) {
        StringBuffer queryString = new StringBuffer();
        queryString.append("SELECT t.AMOUNT amount, t.CREATE_TIME createTime FROM T_WALLET_DAY t ORDER BY t.CREATE_TIME DESC ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        Page page = pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

        List<Map> list = page.getElements();
        for (Map map : list) {
            map.put("amount", new BigDecimal((Double) map.get("amount")).setScale(8, RoundingMode.FLOOR).toPlainString());
        }
        return page;
    }

    /**
     * 每日凌晨更新汇总用户余额
     * @param amount
     */
    @Override
    public void updateWalletDay(double amount) {
        WalletDay walletDay = new WalletDay();
        walletDay.setId(UUIDGenerator.getUUID());
        walletDay.setAmount(amount);
        walletDay.setCreateTime(new Date());
        getHibernateTemplate().save(walletDay);
    }

    public PagedQueryDao getPagedQueryDao() {
        return pagedQueryDao;
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}