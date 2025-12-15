package project.invest.project.internal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.query.NativeQuery;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.invest.InvestRedisKeys;
import project.invest.project.ProjectService;
import project.invest.project.model.InvestOrders;
import project.invest.project.model.InvestRebate;
import project.invest.project.model.Project;
import project.invest.vip.VipService;
import project.invest.vip.model.Vip;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.user.ChildrenLever;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProjectServiceImpl extends HibernateDaoSupport implements ProjectService {

    private WalletService walletService;

    private MoneyLogService moneyLogService;

    private PartyService partyService;

    private VipService vipService;

    private UserRecomService userRecomService;

    private UserDataService userDataService;

    private RedisHandler redisHandler;

    private static Log logger = LogFactory.getLog(ProjectServiceImpl.class);

//    @Override
//    public List<Category> listCategorys(String lang) {
//        DetachedCriteria query = DetachedCriteria.forClass(Category.class);
//        query.add( Property.forName("lang").eq(lang) );
//        query.addOrder(Order.asc("sort"));
//        return (List<Category>) getHibernateTemplate().findByCriteria(query);
//    }

    @Override
    public List<Project> listProjectSell(String baseId, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(Project.class);
        query.add( Property.forName("baseId").eq(baseId) );
        query.add( Property.forName("status").eq(0) );
        query.addOrder(Order.asc("sort"));
        query.addOrder(Order.desc("createTime"));
        return (List<Project>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public List<Project> listProjectHome(int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(Project.class);
        query.add( Property.forName("recTime").gt(0L) );
        query.add( Property.forName("status").eq(0) );
        query.addOrder(Order.desc("recTime"));
        return (List<Project>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public Project getProject(String projectId) {
        return this.getHibernateTemplate().get(Project.class, projectId);
    }

    @Override
    public List<InvestOrders> listProjectMy(String partyId, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(InvestOrders.class);
        query.add( Property.forName("partyId").eq(partyId) );
        query.addOrder(Order.desc("createTime"));
        return (List<InvestOrders>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public JSONObject getMyInvestInfo(String partyId) {
        JSONObject o = new JSONObject();
        String sql = "select IFNULL(sum(INCOME_WILL),0), IFNULL(sum(INCOME),0) from T_INVEST_ORDERS where PARTY_ID=?0 and  `STATUS` > -1";
        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
        nativeQuery.setParameter(0,partyId);
        Object[] result = nativeQuery.getSingleResult();
        double  incomeWill = (double) result[0];
        double  incomeReal = (double) result[1];
        o.put("incomeWill",Arith.sub(incomeWill,incomeReal));
        o.put("incomeReal",incomeReal);
        return o;
    }


    @Override
    public List<InvestRebate> listProjectIncome(String orderId, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(InvestRebate.class);
        query.add( Property.forName("orderId").eq(orderId) );
        query.add( Property.forName("status").eq(0) );
        query.add( Property.forName("level").eq(0) );
        query.addOrder(Order.desc("realTime"));
        return (List<InvestRebate>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public List<InvestRebate> listInvestRebate(String partyId, int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(InvestRebate.class);
        query.add( Property.forName("partyId").eq(partyId) );
        query.add( Property.forName("status").eq(0) );
        query.addOrder(Order.desc("realTime"));
        return (List<InvestRebate>) getHibernateTemplate().findByCriteria(query,(pageNum-1)*pageSize,pageSize);
    }

    @Override
    public JSONObject getProjectIncome(String orderId) {
        InvestOrders orders =  this.getHibernateTemplate().get(InvestOrders.class, orderId);
        JSONObject o = new JSONObject();
        o.put("status", orders.getStatus());
        o.put("type", orders.getType());
        o.put("incomeReal",orders.getIncome());
        o.put("amount",orders.getAmount());
        double valDay = Arith.mul(orders.getAmount(),orders.getBonusRateVip());
        o.put("valDay", Arith.roundDown(valDay,2));
        o.put("bonusRate", orders.getBonusRate());
        o.put("bonusRateVip", orders.getBonusRateVip());
        long pass = System.currentTimeMillis()- orders.getCreateTime().getTime();
        if(orders.getType()<=2){
            pass /= 3600000L;
        }else{
            pass /= 86400000L;
        }
        if(pass>orders.getBonus()){
            pass = orders.getBonus();
        }
        o.put("remainDays",orders.getBonus()-pass) ;
        o.put("passDays",pass) ;
        o.put("incomeWill",orders.getIncomeWill());
        return o;
    }

    @Override
    public Double updateBuyProject(String partyId,String projectId,double amount) {
        Project project = getProject(projectId);
        if(project.getStatus()==1||project.getEnding()==1||project.getInvestProgressMan()>=1){
            throw new BusinessException("该项目已经结束");
        }
        if(amount<project.getInvestMin()){
            throw new BusinessException("输入金额低于起投金额");
        }

        if(amount>project.getInvestMax()){
            throw new BusinessException("输入金额高于最高投资");
        }

        if(!project.isRepeating()){
            DetachedCriteria query = DetachedCriteria.forClass(InvestOrders.class);
            query.add( Property.forName("projectId").eq(projectId) );
            query.add( Property.forName("partyId").eq(partyId) );
            if(getHibernateTemplate().findByCriteria(query).size()>0){
                throw new BusinessException("您已经投资该项目");
            }
        }

        Wallet wallet = walletService.saveWalletByPartyId(partyId);
        double amount_before = wallet.getMoney();
        if(amount>amount_before){
            throw new BusinessException("余额不足");
        }

        wallet.setMoney(Arith.roundDown(Arith.sub(wallet.getMoney(), amount),2));

        walletService.update(wallet);


        InvestOrders orders = new InvestOrders();
        orders.setProjectId(projectId);
        orders.setAmount(amount);
        orders.setCreateTime(new Date());
        orders.setPartyId(partyId);
        orders.setStatus(0);
        orders.setUpTime(0);

        long nextWill = orders.getCreateTime().getTime();
        long sucessWill = nextWill;

        if(project.getType()==1){
            nextWill += 3600000L;
            sucessWill +=  3600000L*project.getBonus();
        }else if(project.getType()==2){
            nextWill += 3600000L;
            sucessWill +=  3600000L*project.getBonus();
        }else if(project.getType()==3){
            nextWill += 3600000L*24;
            sucessWill +=  3600000L*24*project.getBonus();
        }else if(project.getType()==4){
            nextWill +=  3600000L*24;
            sucessWill +=   3600000L*24*project.getBonus();
        }
        orders.setSucessWill(sucessWill);
        orders.setNextWill(nextWill);
        orders.setBonus(project.getBonus());
        orders.setType(project.getType());
        orders.setBonusRate(project.getBonusRate());

        orders.setIncome(0D);
        orders.setIncomeWill(Arith.mul(project.getBonusRate(),amount));
        orders.setIncomeWill(Arith.mul(orders.getIncomeWill(),orders.getBonus()));
        orders.setIncomeWill(Arith.roundDown(orders.getIncomeWill(),2));
        Party party = partyService.cachePartyBy(partyId, false);
        Vip vip = vipService.selectById(party.getVip_level());
        if(vip!=null){
            orders.setBonusRateVip(project.getBonusRate()+vip.getRebate0());
        }
        getHibernateTemplate().save(orders);


        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(Arith.sub(0, amount));
        moneyLog.setAmount_after(wallet.getMoney());

        moneyLog.setLog("支付订单[" + orders.getId() + "]");
        moneyLog.setPartyId(partyId);
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_PAY_ORDER);

        moneyLogService.save(moneyLog);

        Double point = project.getPointRate()*amount;
        walletService.updateInvestPoint(partyId,point.intValue());

        InvestRebate rabate1 = null;
        InvestRebate rabate2 = null;
        String nowString = DateUtils.getLongDate(new Date());
        UserRecom recom1 = userRecomService.findByPartyId(partyId);
        if(recom1!=null){
            rabate1 = new InvestRebate();
            rabate1.setPartyId(recom1.getReco_id().toString());
            rabate1.setRebate(0);
            rabate1.setStatus(1);
            rabate1.setCreateTime(nowString);
            rabate1.setRealTime(nowString);
            rabate1.setLevel(1);
            rabate1.setAmount(amount);
            rabate1.setOrderId(orders.getId().toString());
            rabate1.setOrderPartyId(orders.getPartyId());


            UserRecom recom2 = userRecomService.findByPartyId(recom1.getReco_id().toString());
            if(recom2!=null){
                rabate2 = new InvestRebate();
                rabate2.setPartyId(recom2.getReco_id().toString());
                rabate2.setRebate(0);
                rabate2.setStatus(1);
                rabate2.setCreateTime(nowString);
                rabate2.setRealTime(nowString);
                rabate2.setLevel(2);
                rabate2.setAmount(amount);
                rabate2.setOrderId(orders.getId().toString());
                rabate2.setOrderPartyId(orders.getPartyId());

            }
        }

        if(rabate1!=null){
            getHibernateTemplate().save(rabate1);
        }
        if(rabate2!=null){
            getHibernateTemplate().save(rabate2);
        }

        double add = Arith.div(amount,project.getInvestSize());
        add = Arith.roundDown(add,4);
        project.setInvestProgress(Arith.roundDown(Arith.add(project.getInvestProgress(),add),4));
        project.setInvestProgressMan(Arith.roundDown(Arith.add(project.getInvestProgressMan(),add),4));
        getHibernateTemplate().save(project);

        userDataService.saveInsvestBuy(partyId,amount);

        return 0d;
    }

    @Override
    public List<InvestOrders> listWaiteSettlements() {
        DetachedCriteria query = DetachedCriteria.forClass(InvestOrders.class);
        query.add( Property.forName("status").eq(0) );
        query.add( Property.forName("nextWill").lt(System.currentTimeMillis()) );
        return (List<InvestOrders>) getHibernateTemplate().findByCriteria(query,0,100);
    }

    @Override
    public void updateSettlementsOrders(String investOrdersId) {
        InvestOrders orders= this.getHibernateTemplate().get(InvestOrders.class, investOrdersId);
        if(orders==null||orders.getStatus()!=0){
            return;
        }

        if(orders.getType()==2&&orders.getNextWill()<orders.getSucessWill()){
            orders.setNextWill(orders.getNextWill()+3600000L);
            this.getHibernateTemplate().update(orders);
            return;
        }

        if(orders.getType()==4&&orders.getNextWill()<orders.getSucessWill()){
            orders.setNextWill(orders.getNextWill()+3600000L*24);
            this.getHibernateTemplate().update(orders);
            return;
        }

        String lockKey = InvestRedisKeys.INVEST_ORDER_USER_LOCK+orders.getPartyId();
        if(!redisHandler.lock(lockKey,20)){
            return  ;
        }

        try {
            double ben = 0;
            double prize ;
            double rebate ;
            if(orders.getType()==1){
                prize = Arith.mul(orders.getAmount(),orders.getBonusRateVip());
                prize = Arith.roundDown(prize,2);
                rebate = prize;
                if(orders.getNextWill()>=orders.getSucessWill()){
                    prize = Arith.add(orders.getAmount(),prize);
                    orders.setStatus(2);
                    ben = orders.getAmount();
                }else{
                    orders.setNextWill(orders.getNextWill()+3600000L);
                }
            }else if(orders.getType()==3){
                prize = Arith.mul(orders.getAmount(),orders.getBonusRateVip());
                prize = Arith.roundDown(prize,2);
                rebate = prize;
                if(orders.getNextWill()>=orders.getSucessWill()){
                    prize = Arith.add(orders.getAmount(),prize);
                    orders.setStatus(2);
                    ben = orders.getAmount();
                }else{
                    orders.setNextWill(orders.getNextWill()+3600000L*24);
                }
            }else {
                prize = orders.getIncomeWill();
                rebate = prize;
                prize = Arith.add(orders.getAmount(),prize);
                orders.setStatus(2);
                ben = orders.getAmount();

            }
            orders.setIncome(Arith.add(orders.getIncome(),rebate));
            orders.setUpTime(System.currentTimeMillis());
            this.getHibernateTemplate().update(orders);


            Wallet wallet = walletService.saveWalletByPartyId(orders.getPartyId());

            double amount_before = wallet.getMoney();

            wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), prize),2));
            wallet.setRebate(Arith.add(wallet.getRebate(), prize));
            walletService.update(wallet);



            InvestRebate rabate0 = new InvestRebate();
            rabate0.setPartyId(orders.getPartyId());
            rabate0.setRebate(rebate);
            rabate0.setStatus(0);
            rabate0.setCreateTime(DateUtils.getLongDate(new Date()));
            rabate0.setRealTime(rabate0.getCreateTime());
            rabate0.setLevel(0);
            rabate0.setOrderId(orders.getId().toString());
            rabate0.setOrderPartyId(orders.getPartyId());
            rabate0.setAmount(orders.getAmount());
            this.getHibernateTemplate().save(rabate0);

            double add = Arith.sub(prize,ben);
            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(amount_before);
            moneyLog.setAmount(add);
            moneyLog.setAmount_after(Arith.add(amount_before,add));

            moneyLog.setLog("投资收益[" + orders.getId() + "]");
            moneyLog.setPartyId(orders.getPartyId());
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_BONUS);
            moneyLogService.save(moneyLog);

            if(ben>0){
                amount_before = moneyLog.getAmount_after();
                moneyLog = new MoneyLog();
                moneyLog.setAmount_before(amount_before);
                moneyLog.setAmount(ben);
                moneyLog.setAmount_after(wallet.getMoney());

                moneyLog.setLog("投资返本[" + orders.getId() + "]");
                moneyLog.setPartyId(orders.getPartyId());
                moneyLog.setWallettype(Constants.WALLET);
                moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_RETURNS);
                moneyLogService.save(moneyLog);
            }

            userDataService.saveBrushRebate(orders.getPartyId(),add,0,ben);


        }catch (Exception e){
            logger.error("分红失败"+orders,e);
        }finally {
            redisHandler.remove(lockKey);
        }
    }

    @Override
    public List<InvestRebate> listWaiteRebate() {
        DetachedCriteria query = DetachedCriteria.forClass(InvestRebate.class);
        query.add( Property.forName("status").eq(1) );
        return (List<InvestRebate>) getHibernateTemplate().findByCriteria(query,0,100);
    }

    @Override
    public boolean updateRebate(String rebateId) {
        InvestRebate rebate =  this.getHibernateTemplate().get(InvestRebate.class, rebateId);
        if(rebate==null||rebate.getStatus()!=1){
            logger.error("发佣状态不对");
            return false;
        }
        String lockKey = InvestRedisKeys.INVEST_ORDER_USER_LOCK+rebate.getPartyId();
        if(!redisHandler.lock(lockKey,20)){
            logger.error("发佣操作并发");
            return  false;
        }
        try {

            InvestOrders orders =  this.getHibernateTemplate().get(InvestOrders.class, rebate.getOrderId());
            if(orders ==null){
                logger.error("原始订单为空");
                return  false;
            }

            Party party =  partyService.cachePartyBy(rebate.getPartyId(),false);

            Vip vip = vipService.selectById(party.getVip_level());
            if(vip==null){
                return  false;
            }
            double prize ;
            if(rebate.getLevel()==1){
                prize = Arith.mul(orders.getAmount(),vip.getRebate1());
            }else{
                prize = Arith.mul(orders.getAmount(),vip.getRebate2());
            }
            String nowString = DateUtils.getLongDate(new Date());
            rebate.setRealTime(nowString);
            rebate.setStatus(0);
            rebate.setRebate(prize);
            getHibernateTemplate().update(rebate);

            Wallet wallet = walletService.saveWalletByPartyId(rebate.getPartyId());

            double amount_before = wallet.getMoney();

            wallet.setMoney(Arith.roundDown(Arith.add(wallet.getMoney(), prize),2));
            wallet.setRebate(Arith.add(wallet.getRebate(), prize));
            walletService.update(wallet);

            MoneyLog moneyLog = new MoneyLog();
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(amount_before);
            moneyLog.setAmount(Arith.add(0, prize));
            moneyLog.setAmount_after(wallet.getMoney());

            moneyLog.setLog(rebate.getLevel()+"级佣金[" + rebate.getOrderId() + "]");
            moneyLog.setPartyId(rebate.getPartyId());
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_REBATE);
            moneyLogService.save(moneyLog);

            userDataService.saveBrushRebate(rebate.getPartyId(),prize,rebate.getLevel(),0);

            if(rebate.getLevel()==1){
                vipService.updatePartyVip(rebate.getPartyId());
            }

            return  true;

        }catch ( Exception e){
            throw new BusinessException(e);
        }
        finally {
            redisHandler.remove(lockKey);
        }
    }

    @Override
    public JSONObject getTeamInfo(String partyId) {
        ChildrenLever childrenLever = userDataService.getCacheChildrenLever4(partyId);
        JSONObject object = new JSONObject();
        String sql = "select IFNULL(sum(REBATE),0) from T_INVEST_REBATE where PARTY_ID=?0 and  `STATUS` = 0 and `LEVEL`>0";
        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
        nativeQuery.setParameter(0,partyId);
        Object result = nativeQuery.getSingleResult();
        object.put("income_already",  Arith.round(Double.parseDouble(result.toString()),2));

        Date d = new Date();
        sql = "select IFNULL(sum(REBATE),0) from T_INVEST_REBATE where PARTY_ID=?0 and  `STATUS` = 0 and `LEVEL`>0 and REAL_TIME >?1 and REAL_TIME <=?2";
        nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
        nativeQuery.setParameter(0,partyId);
        nativeQuery.setParameter(1,DateUtils.getDayStartString(d));
        nativeQuery.setParameter(2,DateUtils.getDayEndString(d));
        object.put("income_today", nativeQuery.getSingleResult());

        sql = "select count(DISTINCT ORDER_ID) from T_INVEST_REBATE where PARTY_ID=?0 and `LEVEL`>0";
        nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
        nativeQuery.setParameter(0,partyId);

        object.put("count_order", nativeQuery.getSingleResult());
        object.put("count_people", childrenLever.getLever1().size() + childrenLever.getLever2().size());

        object.put("count_people_level1", childrenLever.getLever1().size() );
        object.put("count_people_level2", childrenLever.getLever2().size());

        sql = "select IFNULL(sum(REBATE),0) from T_INVEST_REBATE where PARTY_ID=?0 and  `LEVEL`= 1 ";
        nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
        nativeQuery.setParameter(0,partyId);
        result = nativeQuery.getSingleResult();
        object.put("rebate_level1", Arith.round(Double.parseDouble(result.toString()),2));
        sql = "select IFNULL(sum(REBATE),0) from T_INVEST_REBATE where PARTY_ID=?0 and `LEVEL`= 2 ";
        nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql);
        nativeQuery.setParameter(0,partyId);
        result = nativeQuery.getSingleResult();
        object.put("rebate_level2", Arith.round(Double.parseDouble(result.toString()),2));

        return object;
    }

    @Override
    public JSONArray listRebateByLevel(String partyId, int level, int pageNum, int pageSize) {
        JSONArray jsonArray = new JSONArray();
        for(Map<String, Object> rebateMap : userDataService.getChildrenLevelPagedForBrush(pageNum,pageSize,partyId,level)){
            JSONObject o = new JSONObject();
            String username = rebateMap.get("username").toString();
            if(username.length()>3){
                username = username.substring(0, 3) + "***" + username.substring(username.length() - 3);
            }
            o.put("username", username);
            o.put("regTime",rebateMap.get("createTime"));
            o.put("rebate",Arith.roundDown(Double.parseDouble(rebateMap.get("rebate").toString()),2));
            o.put("avatar", rebateMap.get("avatar"));
            o.put("countOrder",rebateMap.get("countOrder"));
            jsonArray.add(o);
        }
        return jsonArray;
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

    public void setVipService(VipService vipService) {
        this.vipService = vipService;
    }
    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }
}
