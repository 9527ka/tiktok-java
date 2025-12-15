package project.ddos.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import kernel.util.StringUtils;
import project.ddos.CheckIpRequestCountService;
import project.ddos.IpMenuService;
import project.ddos.UrlSpecialService;
import project.ddos.model.IpMenu;
import project.ddos.utils.BlacklistIpSerivceTimeWindow;
import project.ddos.utils.IpLockRequestTimeWindow;
import project.ddos.utils.IpRequestTimeWindow;
import project.ddos.utils.IpSpecialRequestTimeWindow;
import project.ddos.utils.LocklistIpSerivceTimeWindow;
import project.ddos.utils.SystemParaReadRequestTimeWindow;
import project.redis.RedisHandler;
import project.syspara.SysparaService;

public class CheckIpRequestCountServiceImpl implements CheckIpRequestCountService, InitializingBean {
    private Logger log = LoggerFactory.getLogger(CheckIpRequestCountServiceImpl.class);
    private IpRequestTimeWindow ipRequestTimeWindow;
    private IpSpecialRequestTimeWindow ipSpecialRequestTimeWindow;
    private BlacklistIpSerivceTimeWindow blacklistIpSerivceTimeWindow;

    private IpLockRequestTimeWindow ipLockRequestTimeWindow;
    private LocklistIpSerivceTimeWindow locklistIpSerivceTimeWindow;

    private SystemParaReadRequestTimeWindow systemParaReadRequestTimeWindow;
    private SysparaService sysparaService;
    private IpMenuService ipMenuService;
    private UrlSpecialService urlSpecialService;
    private RedisHandler redisHandler;

    /**
     * 是否开启ip检测
     */
    private String request_check_ip;
    /**
     * 验证ip请求数量限制（十分钟内）
     */
    private Integer request_check_ip_count;
    /**
     * 特殊url验证ip请求数量限制（一分钟内）
     */
    private Integer request_check_ip_special_count;
    /**
     * 是否只有白名单才可以访问
     */
    private boolean request_only_white_menu;
//	private String request_check_ip = "false";
//	private Integer request_check_ip_count = 500;
//	private Integer request_check_ip_special_count = 20;

    private Map<String, Integer> ipCache = new ConcurrentHashMap<String, Integer>();
    /**
     * 特殊地址ip处理
     */
    private Map<String, Integer> ipSpecialCache = new ConcurrentHashMap<String, Integer>();
    /**
     * 登录相关地址ip请求数，判定锁定处理
     */
    private Map<String, Integer> ipLockCache = new ConcurrentHashMap<String, Integer>();
    /**
     * ip请求数本地缓存，从服务器启动时开始计数
     */
    private Map<String, AtomicLong> ipCountCache = new ConcurrentHashMap<String, AtomicLong>();
    /**
     * ip请求时间本地缓存，从服务器启动时开始
     */
    private Map<String, Date> ipTimeCacheCache = new ConcurrentHashMap<String, Date>();
    /**
     * ip urls请求数本地缓存，从服务器启动时开始计数
     */
    private Map<String, Map<String, AtomicLong>> ipUrlCountCache = new ConcurrentHashMap<String, Map<String, AtomicLong>>();
    /**
     * 登录页相关所需的urls
     */
    private List<String> urls = new ArrayList<String>();

    /**
     * ip访问计数
     *
     * @param ip
     */
    private void ipCount(String ip, String url) {
        AtomicLong atomicLong = ipCountCache.get(ip);
        Map<String, AtomicLong> urlCountMap = ipUrlCountCache.get(ip);
        if (urlCountMap == null) {
            urlCountMap = new ConcurrentHashMap<String, AtomicLong>();
        }
        if (atomicLong == null) {
            atomicLong = new AtomicLong();
            ipTimeCacheCache.put(ip, new Date());
            atomicLong.incrementAndGet();
            ipCountCache.put(ip, atomicLong);
            // 累计url次数

            AtomicLong atomicUrlLong = new AtomicLong();
            atomicUrlLong.incrementAndGet();
            urlCountMap.put(url, atomicUrlLong);
            ipUrlCountCache.put(ip, urlCountMap);
        } else {
            atomicLong.incrementAndGet();
            // 累计url次数
            AtomicLong atomicUrlLong = urlCountMap.get(url);
            if (atomicUrlLong == null) {
                atomicUrlLong = new AtomicLong();
                atomicUrlLong.incrementAndGet();
                urlCountMap.put(url, atomicUrlLong);
                ipUrlCountCache.put(ip, urlCountMap);
            } else {
                atomicUrlLong.incrementAndGet();
            }
//			System.out.println("ip:" + ip + ",url:" + url + ",count:" + atomicUrlLong.get());
        }
//		System.out.println("ip:" + ip + ",count:" + atomicLong.get());
    }

    /**
     * 是否开启检测功能
     *
     * @return
     */
    public boolean checkButton() {
        if (StringUtils.isEmptyString(systemParaReadRequestTimeWindow.get("systemParaRead"))) {
            readPara();
            systemParaReadRequestTimeWindow.put("systemParaRead", "1");
        }
        return "true".equals(request_check_ip);
    }

    public void readPara() {
        String old_request_check_ip = request_check_ip;
        int old_request_check_ip_count = request_check_ip_count;
        int old_request_check_ip_special_count = request_check_ip_special_count;
        boolean old_request_only_white_menu = request_only_white_menu;
        request_check_ip = sysparaService.find("request_check_ip").getValue();
        request_check_ip_count = sysparaService.find("request_check_ip_count").getInteger();
        request_check_ip_special_count = sysparaService.find("request_check_ip_special_count").getInteger();
        request_only_white_menu = sysparaService.find("request_only_white_menu").getBoolean();
        if (!old_request_check_ip.equals(request_check_ip)) {
            log.info("request_check_ip 修改，原值:[{}],新值[{}]", old_request_check_ip, request_check_ip);
        }
        if (old_request_check_ip_count != request_check_ip_count) {
            log.info("request_check_ip_count 修改，原值:[{}],新值[{}]", old_request_check_ip_count, request_check_ip_count);
        }
        if (old_request_check_ip_special_count != request_check_ip_special_count) {
            log.info("request_check_ip_special_count 修改，原值:[{}],新值[{}]", old_request_check_ip_special_count,
                    request_check_ip_special_count);
        }
        if (old_request_only_white_menu != request_only_white_menu) {
            log.info("request_only_white_menu 修改，原值:[{}],新值[{}]", old_request_only_white_menu, request_only_white_menu);
        }

    }

    /**
     * 返回true:ip已被封， false：ip正常
     *
     * @param ip
     * @return
     */
    @Override
    public boolean chcekIp(String ip, String url) {
//		String request_check_ip = sysparaService.find("request_check_ip").getValue();
//		Integer request_check_ip_count = sysparaService.find("request_check_ip_count").getInteger();
        if (!"true".equals(request_check_ip))
            return false;// 不为1时 未开启，直接返回false不做处理
        ipCount(ip, url);
        IpMenu ipMenu = ipMenuService.cacheByIp(ip);
        if (ipMenu != null && ipMenu.getDelete_status() != -1 && IpMenu.IP_WHITE.equals(ipMenu.getType())) {// 白名单直接过
            return false;
        } else if (request_only_white_menu) {
            return true;
        }
        if (checkLockUrl(ip, url)) {// 先判断是否锁定
            return true;
        }
        if (blacklistIpSerivceTimeWindow.getBlackIp(ip) != null)
            return true;// ip被封，不发送
        if (ipMenu != null && ipMenu.getDelete_status() != -1 && IpMenu.IP_BLACK.equals(ipMenu.getType())) {// 黑名单已过期的
            ipMenu.setDelete_status(-1);
            ipMenuService.update(ipMenu);
        }
        if (ipRequestTimeWindow.getIpSend(ip) != null) {
            Integer count = ipCache.get(ip);
//			System.out.println("ip:" + ip + ",count:" + count);
            count++;
            if (count >= request_check_ip_count) {// 从ip发送第一条开始
                // System.out.println("ip:" + ip + ",count:" + count);
//				blacklistIpSerivceTimeWindow.putBlackIp(ip, ip);
                ipCache.remove(ip);
                ipRequestTimeWindow.delIpSend(ip);

                if (null == ipMenu) {
                    ipMenu = new IpMenu();
                    ipMenu.setCreate_time(new Date());
                    ipMenu.setDelete_status(0);
                    ipMenu.setLast_opera_time(new Date());
                    ipMenu.setType(IpMenu.IP_BLACK);
                    ipMenu.setIp(ip);
                    ipMenuService.save(ipMenu);
                } else {
                    ipMenu.setDelete_status(0);
                    ipMenu.setLast_opera_time(new Date());
                    ipMenu.setType(IpMenu.IP_BLACK);
                    ipMenuService.update(ipMenu);
                }
                return true;
            } else {
                ipCache.put(ip, count++);
            }

        } else {
            ipCache.put(ip, 1);
            ipRequestTimeWindow.putIpSend(ip, ip);
        }
        if (checkSpecialUrl(ip, url)) {
            return true;
        }

        return false;

    }

    /**
     * 检验特殊url处理 返回true:ip已被封， false：ip正常
     *
     * @param ip
     * @return
     */
    public boolean checkSpecialUrl(String ip, String url) {
//		Integer request_check_ip_special_count = sysparaService.find("request_check_ip_special_count").getInteger();
        if (!urlSpecialService.cacheAllUrls().contains(url)) {
            return false;
        }
        if (ipSpecialRequestTimeWindow.getIpSend(ip) != null) {
            Integer count = ipSpecialCache.get(ip);
//			System.out.println("ip:" + ip + ",count:" + count);
            count++;
            if (count >= request_check_ip_special_count) {// 从ip发送第一条开始

//				blacklistIpSerivceTimeWindow.putBlackIp(ip, ip);
                ipSpecialCache.remove(ip);
                ipSpecialRequestTimeWindow.delIpSend(ip);

                IpMenu ipMenu = ipMenuService.cacheByIp(ip);
                if (null == ipMenu) {
                    ipMenu = new IpMenu();
                    ipMenu.setCreate_time(new Date());
                    ipMenu.setDelete_status(0);
                    ipMenu.setLast_opera_time(new Date());
                    ipMenu.setType(IpMenu.IP_BLACK);
                    ipMenu.setIp(ip);
                    ipMenuService.save(ipMenu);
                } else {
                    ipMenu.setDelete_status(0);
                    ipMenu.setLast_opera_time(new Date());
                    ipMenu.setType(IpMenu.IP_BLACK);
                    ipMenuService.update(ipMenu);
                }
                return true;
            } else {
                ipSpecialCache.put(ip, count++);
            }

        } else {
            ipSpecialCache.put(ip, 1);
            ipSpecialRequestTimeWindow.putIpSend(ip, ip);
        }
        return false;
    }

    /**
     * 检验锁定相关url处理 返回true:ip已被封， false：ip正常
     *
     * @param ip
     * @return
     */
    public boolean checkLockUrl(String ip, String url) {
        if (!urls.contains(url)) {
            return false;
        }
        if (ipLockRequestTimeWindow.getIpSend(ip) != null) {
            Integer count = this.ipLockCache.get(ip);
//			System.out.println("ip:" + ip + ",count:" + count);
            count++;
            if (count >= request_check_ip_count) {// 从ip发送第一条开始

//				blacklistIpSerivceTimeWindow.putBlackIp(ip, ip);
                ipLockCache.remove(ip);
                ipLockRequestTimeWindow.delIpSend(ip);

                IpMenu ipMenu = ipMenuService.cacheByIp(ip);
                if (null == ipMenu) {
                    ipMenu = new IpMenu();
                    ipMenu.setCreate_time(new Date());
                    ipMenu.setDelete_status(0);
                    ipMenu.setLast_opera_time(new Date());
                    ipMenu.setType(IpMenu.IP_LOCK);
                    ipMenu.setIp(ip);
                    ipMenuService.save(ipMenu);
                } else {
                    ipMenu.setDelete_status(0);
                    ipMenu.setLast_opera_time(new Date());
                    ipMenu.setType(IpMenu.IP_LOCK);
                    ipMenuService.update(ipMenu);
                }
                return true;
            } else {
                ipLockCache.put(ip, count++);
            }

        } else {
            ipLockCache.put(ip, 1);
            ipLockRequestTimeWindow.putIpSend(ip, ip);
        }
        return false;
    }

    public Map<String, AtomicLong> getIpCountCache() {
        return ipCountCache;
    }

    public Map<String, Date> getIpTimeCacheCache() {
        return ipTimeCacheCache;
    }

    public void clearIpCountCache() {
        ipCountCache.clear();
    }

    public void clearIpTimeCacheCache() {
        ipTimeCacheCache.clear();
    }

    /**
     * 分页获取ip计数相关信息，（防止因跨服务请求导致网络带宽不足而响应慢）
     *
     * @param pageNo
     * @param pageSize
     * @param ip_para
     * @param type_para
     * @param limit_count
     * @param isExcludeBlack true:排除黑名单
     * @return
     */
    public List<Map<String, Object>> cachePagedQueryIpCount(int pageNo, int pageSize, String ip_para, String type_para,
                                                            Long limit_count, boolean isExcludeMenu) {
        Map<String, AtomicLong> ipCountCache = this.getIpCountCache();
        Map<String, Date> ipTimeCacheCache = this.getIpTimeCacheCache();
        List<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
        for (Entry<String, AtomicLong> entry : ipCountCache.entrySet()) {
            if (StringUtils.isNotEmpty(ip_para) && entry.getKey().indexOf(ip_para) == -1) {// ip赛选条件
                continue;
            }
            IpMenu ipMenu = ipMenuService.cacheByIp(entry.getKey());
            if (StringUtils.isNotEmpty(type_para) && ipMenu != null && !type_para.equals(ipMenu.getType())
                    && ipMenu.getDelete_status() == 0) {// 类型赛选条件
                continue;
            }
            if (isExcludeMenu && ipMenu != null && ipMenu.getDelete_status() == 0) {// 是否排除黑名单
                continue;
            }
            if (limit_count != null && limit_count > entry.getValue().get()) {
                continue;
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("ip", entry.getKey());
            data.put("count", entry.getValue().get());
            data.put("create_time", ipTimeCacheCache.get(entry.getKey()));
            data.put("type", ipMenu == null || ipMenu.getDelete_status() == -1 ? null : ipMenu.getType());
            pageList.add(data);
        }
        if (pageList.isEmpty()) {
            return new LinkedList<Map<String, Object>>();
        }
        java.util.Collections.sort(pageList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> paramT1, Map<String, Object> paramT2) {
                // TODO Auto-generated method stub
                Long t1 = Long.valueOf(paramT1.get("count").toString());
                Long t2 = Long.valueOf(paramT2.get("count").toString());
                return -t1.compareTo(t2);
            }
        });

        int start = (pageNo - 1) * pageSize;
        start = start <= 0 ? 0 : start;//
        int end = start + pageSize;

        if (start >= pageList.size()) {// 起始数据大于总量，返回空
            return new LinkedList<Map<String, Object>>();
        }
        if (pageList.size() <= end)
            end = pageList.size();

        List<Map<String, Object>> list = pageList.subList(start, end);
        return list;
    }

    /**
     * 根据ips批量获取对应的请求数据，减少交互
     *
     * @param ips
     * @return
     */
    public Map<String, Long> ipCountByIps(List<String> ips) {
        Map<String, Long> map = new HashMap<String, Long>();
        for (String ip : ips) {
            map.put(ip, !this.ipCountCache.containsKey(ip) ? 0L : ipCountCache.get(ip).get());
        }

        return map;
    }

    /**
     * 获取ip请求汇总数据
     *
     * @return
     */
    public Map<String, Object> ipCountSumDates() {
        Map<String, Object> map = new HashMap<String, Object>();
        Long sumRequest = 0l;
        Long sumIp = 0l;
        for (Entry<String, AtomicLong> entry : ipCountCache.entrySet()) {
            IpMenu ipMenu = ipMenuService.cacheByIp(entry.getKey());
            if (ipMenu != null && ipMenu.getDelete_status() == 0) {// 排除名单
                continue;
            }
            sumIp++;
            sumRequest += entry.getValue().get();
        }
        map.put("ip_request_sum", sumRequest);
        map.put("ip_sum", sumIp);
        return map;
    }

    public void afterPropertiesSet() throws Exception {
        request_check_ip = sysparaService.findByDB("request_check_ip").getValue();
        request_check_ip_count = sysparaService.findByDB("request_check_ip_count").getInteger();
        request_check_ip_special_count = sysparaService.findByDB("request_check_ip_special_count").getInteger();
        request_only_white_menu = sysparaService.findByDB("request_only_white_menu").getBoolean();
        initLoginPageRelationAction();
    }

    public void initLoginPageRelationAction() {
//		/pages/login/index登录页：1、api/syspara.action 2、api/user!login.action 
        urls.add("/api/syspara.action");
        urls.add("/api/user!login.action");
//		/pages/login/forget找回密码：1、api/syspara.action 2、api/user!resetpswByGoogle 3、api/user!resetpsw.action 4、api/idcode!execute.action 5、api/user!getUserNameVerifTarget
//		urls.add("/api/syspara.action");
        urls.add("/api/user!resetpswByGoogle.action");
        urls.add("/api/user!resetpsw.action");
        urls.add("/api/idcode!execute.action");
        urls.add("/api/api/user!getUserNameVerifTarget.action");
//		/pages/login/register-new注册页面：1、api/localuser!getImageCode 2、api/idcode!execute 3、api/localuser!register_username 4、api/localuser!register
        urls.add("/api/localuser!getImageCode.action");
        urls.add("/api/idcode!execute.action");
        urls.add("/api/localuser!register_username.action");
        urls.add("/api/localuser!register.action");
//		/pages/user/server客服 1、api/onlinechat!list.action 2、api/onlinechat!send 3、api/onlinechat!unread
        urls.add("/api/onlinechat!list.action");
        urls.add("/api/onlinechat!send.action");
        urls.add("/api/onlinechat!unread.action");
        urls.add("/api/linelogin!lineurl.action");
        urls.add("/api/linelogin!login.action");
    }

    /**
     * 登录页相关所需的urls
     */
    public List<String> loginPageRelationAction() {
        return urls;
    }

    public Map<String, AtomicLong> cacheUrlCount(String ip) {
        return ipUrlCountCache.get(ip);
    }

    /**
     * ip请求数相关缓存清除
     *
     * @param ip
     */
    public void cacheRemoveIp(String ip) {
        this.ipCountCache.remove(ip);
        this.ipTimeCacheCache.remove(ip);
        this.ipUrlCountCache.remove(ip);

        /**
         * 对应的时间窗ip清除
         */
        ipRequestTimeWindow.delIpSend(ip);
        ipSpecialRequestTimeWindow.delIpSend(ip);
        ipLockRequestTimeWindow.delIpSend(ip);

        ipLockCache.remove(ip);
        ipCache.remove(ip);
        ipSpecialCache.remove(ip);

    }

    /**
     * ip请求清除不在名单里的数据
     */
    public void cacheRequestClearNoMenu() {
        for (String key : ipCountCache.keySet()) {
            IpMenu ipMenu = ipMenuService.cacheByIp(key);
            if (ipMenu != null && ipMenu.getDelete_status() == 0) {// 排除名单
                continue;
            }
//			this.ipCountCache.remove(key);
//			this.ipTimeCacheCache.remove(key);
//			this.ipUrlCountCache.remove(key);
            cacheRemoveIp(key);
        }
    }

    @Override
    public boolean checkBlackIp(String ip) {
//        log.info("检查IP黑名单：{}", ip);
        if (!"true".equals(request_check_ip)) {
            return false;// 不为1时 未开启，直接返回false不做处理}
        }
//        ipCount(ip, url);
        IpMenu ipMenu = ipMenuService.cacheByIp(ip);
        // 黑名单已过期的
        return ipMenu != null && ipMenu.getDelete_status() != -1 && IpMenu.IP_BLACK.equals(ipMenu.getType());// ip被封，不发送

    }

    /**
     * ip是否锁定
     *
     * @param ip
     * @return
     */
    public boolean isLock(String ip) {
        return locklistIpSerivceTimeWindow.getLockIp(ip) != null;
    }

    public void setIpRequestTimeWindow(IpRequestTimeWindow ipRequestTimeWindow) {
        this.ipRequestTimeWindow = ipRequestTimeWindow;
    }

    public void setBlacklistIpSerivceTimeWindow(BlacklistIpSerivceTimeWindow blacklistIpSerivceTimeWindow) {
        this.blacklistIpSerivceTimeWindow = blacklistIpSerivceTimeWindow;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setIpMenuService(IpMenuService ipMenuService) {
        this.ipMenuService = ipMenuService;
    }

    public void setIpSpecialRequestTimeWindow(IpSpecialRequestTimeWindow ipSpecialRequestTimeWindow) {
        this.ipSpecialRequestTimeWindow = ipSpecialRequestTimeWindow;
    }

    public void setUrlSpecialService(UrlSpecialService urlSpecialService) {
        this.urlSpecialService = urlSpecialService;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setSystemParaReadRequestTimeWindow(SystemParaReadRequestTimeWindow systemParaReadRequestTimeWindow) {
        this.systemParaReadRequestTimeWindow = systemParaReadRequestTimeWindow;
    }

    public void setIpLockRequestTimeWindow(IpLockRequestTimeWindow ipLockRequestTimeWindow) {
        this.ipLockRequestTimeWindow = ipLockRequestTimeWindow;
    }

    public void setLocklistIpSerivceTimeWindow(LocklistIpSerivceTimeWindow locklistIpSerivceTimeWindow) {
        this.locklistIpSerivceTimeWindow = locklistIpSerivceTimeWindow;
    }

}
