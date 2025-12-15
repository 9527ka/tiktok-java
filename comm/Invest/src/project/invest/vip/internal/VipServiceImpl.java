package project.invest.vip.internal;

import kernel.util.Arith;
import org.hibernate.criterion.DetachedCriteria;

import org.hibernate.query.NativeQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Service;
import project.invest.platform.BrushClient;
import project.invest.vip.VipService;
import project.invest.vip.model.Vip;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.user.ChildrenLever;
import project.user.UserDataService;
import project.wallet.WalletService;
import util.TwoValues;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class VipServiceImpl extends HibernateDaoSupport implements VipService {

    private JdbcTemplate jdbcTemplate;

    private WalletService walletService;

    private MoneyLogService moneyLogService;

    private PartyService partyService;

    private UserDataService userDataService;


    @Override
    public List<Vip> listVip() {
        DetachedCriteria query = DetachedCriteria.forClass(Vip.class);
        return (List<Vip>) getHibernateTemplate().findByCriteria(query);
    }

    @Override
    public Vip selectById(int vip_level) {
        Vip vip = this.getHibernateTemplate().get(Vip.class, vip_level + "");
        return vip;
    }

    @Override
    public TwoValues<Integer, Double> getInvestPromotion(String partyId) {
        ChildrenLever childrenLever = userDataService.getCacheChildrenLever4(partyId);
        TwoValues<Integer, Double> rn = new TwoValues<Integer, Double>();
        rn.setOne(childrenLever.getLever1().size());

        String sql = "select IFNULL(sum(AMOUNT),0) from T_INVEST_REBATE where PARTY_ID=?0  and `LEVEL`= 1";
        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
        nativeQuery.setParameter(0,partyId);
        Object result = nativeQuery.getSingleResult();
        rn.setTwo(Arith.round(Double.parseDouble(result.toString()),2));

        return rn;
    }

    @Override
    public void updatePartyVip(String partyId) {
        Party party = partyService.cachePartyBy(partyId, false);

        Vip v = selectById(party.getVip_level()+1);
        if(v==null){
            return;
        }

        TwoValues<Integer, Double> in = getInvestPromotion(partyId);
        if(in.getOne()>=v.getSubCount()&&in.getTwo()>=v.getSubSales()){
            party.setVip_level(party.getVip_level()+1);
            partyService.update(party);
        }
    }

    @Override
    public BrushClient getBrushClient(String id) {
        BrushClient client = this.getHibernateTemplate().get(BrushClient.class, id);
        return client;
    }

    public static Date beforeOrAfterNumberDay(int day) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public WalletService getWalletService() {
        return walletService;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public MoneyLogService getMoneyLogService() {
        return moneyLogService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public PartyService getPartyService() {
        return partyService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }
}
