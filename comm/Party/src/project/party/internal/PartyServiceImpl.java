package project.party.internal;

import cn.hutool.core.util.StrUtil;
import kernel.cache.RedisLocalCache;
import kernel.constants.LocalCacheBucketKey;
import kernel.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;
import project.RedisKeys;
import project.mall.MallRedisKeys;
import project.mall.orders.model.MallAddress;
import project.party.PartyRedisKeys;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import security.SaltSigureUtils;
import security.SecUser;
import security.internal.SecUserService;
import util.cache.CacheOperation;
import util.concurrent.gofun.core.FunParams;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PartyServiceImpl extends HibernateDaoSupport implements PartyService {
    /**
     * partyid Party
     */
    private PasswordEncoder passwordEncoder;

    private RedisHandler redisHandler;
    private RedisLocalCache redisLocalCache;

    private SecUserService secUserService;
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取用户等级 1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
     */
    public int getUserLevelByAuth(Party party) {
        SecUser secUser = this.secUserService.findUserByPartyId(party.getId().toString());
        int userLevel = 1;
        if (party.getEmail_authority() || party.getPhone_authority() || secUser.isGoogle_auth_bind()) {
            if (party.getKyc_authority()) {
                if (party.isKyc_highlevel_authority()) {
                    userLevel = 4;
                } else {
                    userLevel = 3;
                }
            } else {
                userLevel = 2;
            }
        } else {
            userLevel = 1;
        }
        return userLevel;
    }

    /**
     * 根据电话号码查询用户
     *
     * @param phone
     * @return
     */
    public String selectUuidByPhone(String phone) {
        String sql = "SELECT * FROM PAT_PARTY WHERE PHONE = '" + phone + "'";
        return this.jdbcTemplate.queryForObject(sql, String.class);
    }

    public Party cachePartyBy(Serializable partyId, boolean localcache) {
        if (Objects.isNull(partyId)) {
            return null;
        }

        Party party = (Party) redisHandler.get(PartyRedisKeys.PARTY_ID + partyId);
        if (party == null) {
            party = this.getById(partyId.toString());
            if (party != null) {
                redisHandler.setSync(PartyRedisKeys.PARTY_ID + party.getId(), party);
                redisHandler.setSync(PartyRedisKeys.PARTY_USERNAME + party.getUsername(), party);
            }
        }

        return party;
    }

    public Party cachePartyByUsername(String username, boolean localcache) {
        Party party = null;
        if (localcache) {
            /**
             * 非必须，可读缓存
             */
            party = (Party) redisLocalCache.get(PartyRedisKeys.PARTY_USERNAME + username);
        } else {
            /**
             * 读数据库
             */
            party = (Party) redisHandler.get(PartyRedisKeys.PARTY_USERNAME + username);
        }
        if (party == null) {
            party = findPartyByUsername(username);
            if (party != null) {
                redisHandler.setSync(PartyRedisKeys.PARTY_ID + party.getId(), party);
                redisHandler.setSync(PartyRedisKeys.PARTY_USERNAME + party.getUsername(), party);
            }
        }

        return party;
    }

    public Party save(Party entity) {
        entity.setCreateTime(new Date());
        this.getHibernateTemplate().save(entity);
        redisHandler.setSync(PartyRedisKeys.PARTY_ID + entity.getId(), entity);
        redisHandler.setSync(PartyRedisKeys.PARTY_USERNAME + entity.getUsername(), entity);
        return entity;
    }

    public void update(Party entity) {
        this.getHibernateTemplate().merge(entity);
        redisHandler.setSync(PartyRedisKeys.PARTY_ID + entity.getId(), entity);
        redisHandler.setSync(PartyRedisKeys.PARTY_USERNAME + entity.getUsername(), entity);
    }

    public Party findPartyByUsername(String username) {
        List<Party> list = (List<Party>) this.getHibernateTemplate().find(" FROM Party WHERE username = ?0", username);
        if (list.size() > 0) {
            return (Party) list.get(0);
        }
        return null;
    }

    public Party getById(String id) {
        if (StrUtil.isBlank(id)) {
            return null;
        }

        return this.getHibernateTemplate().get(Party.class, id);
    }

    /**
     * 获取PAT_PARTY 根据已验证的电话号码
     */
    @Override
    public Party findPartyByVerifiedPhone(String phone) {
//		List<Party> list = (List<Party>) this.getHibernateTemplate().find(" FROM Party WHERE phone = ?0 AND phone_authority = 'Y' ", phone);
        List<Party> list = (List<Party>) this.getHibernateTemplate().find(" FROM Party WHERE phone = ?0", phone);
        if (list.size() > 0) {
            return (Party) list.get(0);
        }
        return null;
    }

    /**
     * 获取PAT_PARTY 根据已验证的邮箱
     */
    @Override
    public Party findPartyByVerifiedEmail(String email) {
        List<Party> list = (List<Party>) this.getHibernateTemplate().find(" FROM Party WHERE email = ?0 AND email_authority = 'Y' ", email);
        if (list.size() > 0) {
            return (Party) list.get(0);
        }
        return null;
    }

    public Party getPartyByEmail(String email) {
        List<Party> list = (List<Party>) this.getHibernateTemplate().find(" FROM Party WHERE email = ?0 ", email);
        if (list.size() > 0) {
            return (Party) list.get(0);
        }
        return null;
    }

    public List<Party> getAll() {
        return (List<Party>) this.getHibernateTemplate().find(" FROM Party ");
    }

    public List<Party> getGuestAll() {
        String hql = "FROM Party WHERE rolename = ?0 AND create_time BETWEEN '2023-12-24 00:00:00' AND '2024-01-01 23:59:59'";
        List<Party> result = (List<Party>) this.getHibernateTemplate().find(hql, "GUEST");
        return result;
    }

    @Override
    public Party findPartyByUsercode(String usercode) {
        // List<Party> list = (List<Party>) this.getHibernateTemplate().find(" FROM Party WHERE usercode = ?0");
        List<Party> list = (List<Party>) this.getHibernateTemplate().find("FROM Party WHERE usercode = ?0", new Object[]{usercode});

        if (list.size() > 0) {
            return (Party) list.get(0);
        }

        return null;
    }

    public void updateSafeword(Party party, String safeword) {
        String safeword_md5 = passwordEncoder.encodePassword(safeword, SaltSigureUtils.saltfigure);
        party.setSafeword(safeword_md5);
        this.update(party);

        //资金密码更新  重置校验密码次数
        String lockPassworkErrorKey = MallRedisKeys.MALL_PASSWORD_ERROR_LOCK + party.getId();
        redisHandler.remove(lockPassworkErrorKey);
    }

    public boolean checkSafeword(String safeword, String partyId) {
        if (StringUtils.isEmpty(safeword))
            return Boolean.FALSE;
        Party party = this.cachePartyBy(partyId, false);
        if (party == null) {
            logger.error(MessageFormat.format("party is null,id:{0}", partyId));
            return Boolean.FALSE;
        }
        if (StringUtils.isEmpty(party.getSafeword()))
            return Boolean.FALSE;
        String md5 = passwordEncoder.encodePassword(safeword, SaltSigureUtils.saltfigure);
        return md5.equals(party.getSafeword());
    }

    @Override
    public void updateWithdrawDepositPasswordFailedNumber(String partyId, Boolean bool) {
        // 密码正确 删除 redis key
        // 密码错误 先去 redis 获取
        // 如果存在key 原来的failed number + 1 并修改过期时间三分钟
        // 如果不存在则设置一个新的key failed number = 1 设置过期时间三分钟
        String key = RedisKeys.WITHDRAW_DEPOSIT_PASSWORD_FAILD_NUMBER + partyId;
        if (bool) {
            redisHandler.remove(key);
        } else {
            int failedNumber;
            int expirationTime = 3 * 60;
            String value = redisHandler.getString(key);
            if (value == null) {
                failedNumber = 1;
                redisHandler.setSyncStringEx(key, String.valueOf(failedNumber), expirationTime);
            } else {
                failedNumber = Integer.parseInt(value) + 1;
                redisHandler.setSyncStringEx(key, String.valueOf(failedNumber), expirationTime);
            }
        }
    }

    @Override
    public Boolean getWithdrawDepositPasswordFailedNumberStatus(String partyId) {
        // 获取redis key 若不存在 返回true
        //若存在 判断是否大于3  大于等于 false 小于 true
        String key = RedisKeys.WITHDRAW_DEPOSIT_PASSWORD_FAILD_NUMBER + partyId;
        String value = redisHandler.getString(key);
        boolean status;
        if (value == null) {
            status = true;
        } else {
            status = Integer.parseInt(value) < 3;
        }
        return status;
    }

    @Override
    public void updateCache(Party party) {
        redisHandler.setSync(PartyRedisKeys.PARTY_ID + party.getId(), party);
        redisHandler.setSync(PartyRedisKeys.PARTY_USERNAME + party.getUsername(), party);
    }

    @Override
    public List<MallAddress> findUserAddressByPartyId(String id) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallAddress.class);
        criteria.add(Restrictions.eq("partyId", id));
        criteria.add(Restrictions.eq("status", 1));
        if (CollectionUtils.isNotEmpty(criteria.list())) {
            return criteria.list();
        }
        return null;
    }

    @Override
    public List<Party> getPartyBatch(List<String> idList) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Party.class);
        criteria.add(Restrictions.in("id", idList));
        List<Party> list = (List<Party>) this.getHibernateTemplate().findByCriteria(criteria);
        return list;
    }

    @Override
    public Integer getCacheCountLoginByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.CountLoginByDay);

        FunParams funParams = FunParams.newParam()
                .set("partyService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        Integer result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            PartyService partyService = params.get("partyService").getAs(PartyService.class);
            Integer getResult = partyService.getCountLoginByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }

    public Integer getCountLoginByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder();

        countSql.append("select count(1) from PAT_PARTY  where ROLENAME <> 'GUEST'   ");

        List<Object> params = new ArrayList<>();
        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND LAST_LOGIN_TIME >= ?  ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND LAST_LOGIN_TIME < ? ");
        }

        Integer result = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Integer.class);


        return result;
    }

    @Override
    public Integer getCountRegisterByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder();

        countSql.append("select count(1) from PAT_PARTY where ROLENAME <> 'GUEST'   ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATE_TIME >= ?  ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATE_TIME < ? ");
        }

        Integer result = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Integer.class);
        return result;
    }

    @Override
    public Integer getCacheCountRegisterByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.CountRegisterByDay);

        FunParams funParams = FunParams.newParam()
                .set("partyService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        Integer result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            PartyService partyService = params.get("partyService").getAs(PartyService.class);
            Integer getResult = partyService.getCountRegisterByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }

    @Override
    public Integer getCountAllUser() {

        StringBuilder countSql = new StringBuilder();

        countSql.append("select count(1) from PAT_PARTY  where ROLENAME <> 'GUEST' ");
        Integer result = jdbcTemplate.queryForObject(countSql.toString(), Integer.class);

        return result;
    }

    @Override
    public Integer getCacheCountAllUser() {

        String funKey = LocalCacheBucketKey.CountAllUser;

        FunParams funParams = FunParams.newParam()
                .set("partyService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        Integer result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            PartyService partyService = params.get("partyService").getAs(PartyService.class);
            Integer getResult = partyService.getCountAllUser();
            return getResult;
        });
        return result;
    }

    @Override
    public Integer getCountAllSeller() {

        StringBuilder countSql = new StringBuilder();

        countSql.append("select count(1) from T_MALL_SELLER t1 LEFT JOIN PAT_PARTY t2 on t1.UUID = t2.UUID ");
        countSql.append("LEFT JOIN T_KYC t3 ON t1.UUID = t3.PARTY_ID  ");
        countSql.append("WHERE t3.`STATUS` = 2 and t2.ROLENAME <> 'GUEST' ");
        Integer result = jdbcTemplate.queryForObject(countSql.toString(), Integer.class);

        return result;
    }

    @Override
    public Integer getCacheCountAllSeller() {

        String funKey = LocalCacheBucketKey.CountAllSeller;

        FunParams funParams = FunParams.newParam()
                .set("partyService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        Integer result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            PartyService partyService = params.get("partyService").getAs(PartyService.class);
            Integer getResult = partyService.getCountAllSeller();
            return getResult;
        });
        return result;
    }

    @Override
    public Integer getCountRegisterSellerByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder();

        countSql.append("select count(1) from T_MALL_SELLER t1 LEFT JOIN PAT_PARTY t2 on t1.UUID = t2.UUID ");
        countSql.append("LEFT JOIN T_KYC t3 ON t1.UUID = t3.PARTY_ID  ");
        countSql.append("WHERE t3.`STATUS` = 2 and t2.ROLENAME <> 'GUEST' ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND t1.CREATE_TIME >= ?  ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND t1.CREATE_TIME < ? ");
        }

        Integer result = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Integer.class);

        return result;
    }

    @Override
    public Integer getCacheCountRegisterSellerByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.CountRegisterSellerByDay);

        FunParams funParams = FunParams.newParam()
                .set("partyService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        Integer result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            PartyService partyService = params.get("partyService").getAs(PartyService.class);
            Integer getResult = partyService.getCountRegisterSellerByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }

    @Override
    public Integer getCountOrderByDay(String startTime, String endTime) {

        StringBuilder countSql = new StringBuilder();

        countSql.append("select count(1) from T_MALL_ORDERS_PRIZE where 1= 1 and RETURN_STATUS = 0  and FLAG in(0,1) ");

        List<Object> params = new ArrayList<>();

        if (kernel.util.StringUtils.isNotEmpty(startTime)) {
            params.add(startTime);
            countSql.append("AND CREATE_TIME >= ?  ");
        }

        if (kernel.util.StringUtils.isNotEmpty(endTime)) {
            params.add(endTime);
            countSql.append("AND CREATE_TIME < ? ");
        }

        Integer result = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Integer.class);

        return result;
    }

    @Override
    public Integer getCacheCountOrderByDay(String startTime, String endTime) {

        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.CountOrderByDay);

        FunParams funParams = FunParams.newParam()
                .set("partyService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        Integer result = CacheOperation.execute(funKey, true, 600L * 1000L, funParams, (params) -> {
            PartyService partyService = params.get("partyService").getAs(PartyService.class);
            Integer getResult = partyService.getCountOrderByDay(startTime, endTime);
            return getResult;
        });
        return result;
    }

    @Override
    public void updateUserRemark(String partyId, String remarks) {
        Party party = this.getById(partyId);
        party.setRemarks(remarks);
        this.update(party);
    }


    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setRedisLocalCache(RedisLocalCache redisLocalCache) {
        this.redisLocalCache = redisLocalCache;
    }

    public void setSecUserService(SecUserService secUserService) {
        this.secUserService = secUserService;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getKey(String fromTime, String toTime, String key) {
        String from = fromTime.replace("-", "")
                .replace(":", "")
                .replace("000000", "")
                .replace(" ", "");

        String to = toTime.replace("-", "")
                .replace(":", "")
                .replace("000000", "")
                .replace(" ", "");

        String funKey = key + ":" + from + "-" + to;

        return funKey;
    }
}
