package project.ddos.data.loadcache;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import project.ddos.DdosRedisKeys;
import project.ddos.model.IpMenu;
import project.ddos.model.UrlSpecial;
import project.redis.RedisHandler;

public class IpHandleCacheService extends HibernateDaoSupport {
    private static final Log logger = LogFactory.getLog(IpHandleCacheService.class);

    private RedisHandler redisHandler;

    public void loadcache() {
        loadIpMenu();
        loadUrlSpecial();
        logger.info("完成IP处理 数据加载redis");
    }

    public void loadIpMenu() {
        StringBuffer queryString = new StringBuffer(" FROM IpMenu where type = 'black' ");
        List<IpMenu> list = (List<IpMenu>) this.getHibernateTemplate().find(queryString.toString());

        for (IpMenu ipMenu : list) {
            redisHandler.setSync(DdosRedisKeys.IP_MENU_IP + ipMenu.getIp(), ipMenu);
            redisHandler.sadd(DdosRedisKeys.IP_MENU_IP_BLACK, ipMenu.getIp());
        }
    }

    public void loadUrlSpecial() {
        StringBuffer queryString = new StringBuffer(" FROM UrlSpecial ");
        List<UrlSpecial> list = (List<UrlSpecial>) this.getHibernateTemplate().find(queryString.toString());

        Map<String, String> cacheMap = new ConcurrentHashMap<String, String>();

        for (UrlSpecial urlSpecial : list) {
            cacheMap.put(urlSpecial.getId().toString(), urlSpecial.getUrl());
            redisHandler.setSync(DdosRedisKeys.URL_SPECIAL_ID + urlSpecial.getId().toString(), urlSpecial);
        }
        redisHandler.setSync(DdosRedisKeys.URL_SPECIAL_URL_MAP, cacheMap);
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

}
