package project.mall.loadcache;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import kernel.util.JsonUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.RedisKeys;
import project.log.MallLogRedisKeys;
import project.log.MoneyLog;
import project.mall.MallRedisKeys;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;
import project.mall.combo.model.ComboLang;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.model.SystemGoodsLang;
import project.mall.seller.model.Seller;
import project.mall.type.model.CategoryLang;
import project.party.PartyRedisKeys;
import project.redis.RedisHandler;
import project.syspara.SysparaService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MallLoadCacheService extends HibernateDaoSupport {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected RedisHandler redisHandler;
    private SysparaService sysparaService;
    private SellerGoodsService sellerGoodsService;

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setSellerGoodsService(SellerGoodsService sellerGoodsService) {
        this.sellerGoodsService = sellerGoodsService;
    }


    public void loadcache() {
        loadGoodslang();
        loadTypelang();
        loadCombolang();
        loadBlackSeller();
        loadCountries();
        loadState();
        loadCities();
        //loadNewSellerGoods(); // 记录较多导致启动耗时较长，已经修改成其他方案
        loadMoneyLog();
        logger.info("[MallLoadCacheService loadcache] 完成MallLoadCacheService数据加载redis");
    }

    public void loadGoodslang() {
        try {
            DetachedCriteria query = DetachedCriteria.forClass(SystemGoodsLang.class);
            List<SystemGoodsLang> list = (List<SystemGoodsLang>) getHibernateTemplate().findByCriteria(query, 0, 100000);
            for (SystemGoodsLang gl : list) {
                redisHandler.setSyncString(MallRedisKeys.MALL_GOODS_LANG + gl.getLang() + ":" + gl.getGoodsId(), JSON.toJSONString(gl));
            }
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadGoodslang] 加载缓存报错: ", e);
        }
    }

    public void loadTypelang() {
        try {
            DetachedCriteria query = DetachedCriteria.forClass(CategoryLang.class);
            List<CategoryLang> list = (List<CategoryLang>) getHibernateTemplate().findByCriteria(query, 0, 100000);
            for (CategoryLang gl : list) {
                redisHandler.setSyncString(MallRedisKeys.TYPE_LANG + gl.getLang() + ":" + gl.getCategoryId(), JSON.toJSONString(gl));
            }
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadTypelang] 加载缓存报错: ", e);
        }
    }

    public void loadCombolang() {
        try {
            DetachedCriteria query = DetachedCriteria.forClass(ComboLang.class);
            List<ComboLang> list = (List<ComboLang>) getHibernateTemplate().findByCriteria(query, 0, 100000);
            for (ComboLang gl : list) {
                redisHandler.setSyncString(MallRedisKeys.MALL_COMBO_LANG + gl.getLang() + ":" + gl.getComboId(), JSON.toJSONString(gl));
            }
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadCombolang] 加载缓存报错: ", e);
        }
    }

    public void loadBlackSeller() {
        try {
            DetachedCriteria query = DetachedCriteria.forClass(Seller.class);
            query.add(Property.forName("black").eq(1));
            List<Seller> list = (List<Seller>) getHibernateTemplate().findByCriteria(query, 0, 100000);
            for (Seller seller : list) {
                if (1 == seller.getBlack()) {
                    redisHandler.setSyncString(PartyRedisKeys.PARTY_ID_SELLER_BLACK + seller.getId(), "1");
                }
            }
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadBlackSeller] 加载缓存报错: ", e);
        }
    }

    public void loadCountries() {
        try {
            DetachedCriteria query = DetachedCriteria.forClass(MallCountry.class);
            List<MallCountry> list = (List<MallCountry>) getHibernateTemplate().findByCriteria(query, 0, 100000);
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            for (MallCountry mallCountry : list) {
                params.put(MallRedisKeys.MALL_COUNTRY + mallCountry.getId(), mallCountry);
            }
            redisHandler.setBatchSync(params);
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadCountries] 加载缓存报错: ", e);
        }
    }

    public void loadState() {
        try {
            DetachedCriteria query = DetachedCriteria.forClass(MallState.class);
            List<MallState> list = (List<MallState>) getHibernateTemplate().findByCriteria(query, 0, 100000);
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            for (MallState mallState : list) {
                params.put(MallRedisKeys.MALL_STATE + mallState.getId(), mallState);
            }
            redisHandler.setBatchSync(params);
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadState] 加载缓存报错: ", e);
        }
    }

    public void loadCities() {
        try {
            DetachedCriteria query = DetachedCriteria.forClass(MallCity.class);
            List<MallCity> list = (List<MallCity>) getHibernateTemplate().findByCriteria(query, 0, 200000);
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            for (MallCity mallCity : list) {
                params.put(MallRedisKeys.MALL_CITY + mallCity.getId(), mallCity);
            }
            redisHandler.setBatchSync(params);
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadCities] 加载缓存报错: ", e);
        }
    }


    public void loadMoneyLog() {
        try {

            long searchEndTime = System.currentTimeMillis();

            DetachedCriteria query = DetachedCriteria.forClass(MoneyLog.class);
            query.add(Property.forName("notify").eq(1));
            query.add(Property.forName("amount").gt(0.0));
            List<MoneyLog> list = (List<MoneyLog>) getHibernateTemplate().findByCriteria(query, 0, 200000);
            long searchBeginTime = System.currentTimeMillis();
            logger.info("->>>>>> [MallLoadCacheService loadMoneyLog] 搜索耗时：{}ms",(searchBeginTime - searchEndTime));
            for (MoneyLog log : list) {
                redisHandler.sadd(RedisKeys.SELLER_MONEY_ADD_NOTIFY + log.getPartyId(),log.getId().toString());
            }
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadMoneyLog] 加载缓存报错: ", e);
        }
    }

    public void loadNewSellerGoods() {
        try {
//            int newSellerGoodsDayLimit = 7;
//            Syspara newSellerGoodsDayLimitParam = sysparaService.find(SysParaCode.NEW_SELLER_GOODS_DAY_LIMIT.getCode());
//            if (newSellerGoodsDayLimitParam != null) {
//                String value = newSellerGoodsDayLimitParam.getValue().trim();
//                newSellerGoodsDayLimit = Integer.parseInt(value);
//            }
            int total = 0;
            int currentPage = 1;
            int pageSize = 100;
            while (true) {
                List<SellerGoods> pageList = sellerGoodsService.pagedNewSellerGoods(0L, currentPage, pageSize);
                if (CollectionUtil.isEmpty(pageList)) {
                    break;
                }
                currentPage++;

                for (SellerGoods oneGoods : pageList) {
                    if (oneGoods.getFirstShelfTime() == null || oneGoods.getFirstShelfTime().longValue() <= 0) {
                        continue;
                    }

                    redisHandler.zadd(RedisKeys.SELLER_GOODS_FIRST_SHELF_TIME, oneGoods.getFirstShelfTime(), oneGoods.getId().toString());
                    total++;
                }
            }

            logger.info("[MallLoadCacheService loadNewSellerGoods] 完成:{} 个商品新品缓存的加载", total);
        } catch (Exception e) {
            logger.error("[MallLoadCacheService loadNewSellerGoods] 加载缓存报错: ", e);
        }
    }

}
