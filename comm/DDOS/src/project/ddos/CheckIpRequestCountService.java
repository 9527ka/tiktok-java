package project.ddos;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public interface CheckIpRequestCountService {

    /**
     * 返回true:ip已被封， false：ip正常
     *
     * @param ip
     * @return
     */
    boolean chcekIp(String ip, String url);

    public Map<String, AtomicLong> getIpCountCache();

    public Map<String, Date> getIpTimeCacheCache();

    public void clearIpCountCache();

    public void clearIpTimeCacheCache();

    /**
     * 分页获取ip计数相关信息，（防止因跨服务请求导致网络带宽不足而响应慢）
     *
     * @param pageNo
     * @param pageSize
     * @param ip_para       ip
     * @param type_para     黑白名单
     * @param limit_count   最低访问量
     * @param isExcludeMenu 是否排除名单 true:排除名单
     * @return
     */
    public List<Map<String, Object>> cachePagedQueryIpCount(int pageNo, int pageSize, String ip_para, String type_para,
                                                            Long limit_count, boolean isExcludeMenu);

    /**
     * 是否开启检测功能
     *
     * @return
     */
    public boolean checkButton();

    /**
     * 根据ip,返回具体链接的请求数
     *
     * @param ip
     * @return
     */
    public Map<String, AtomicLong> cacheUrlCount(String ip);

    /**
     * 根据ips批量获取对应的请求数据，减少交互
     *
     * @param ips
     * @return
     */
    public Map<String, Long> ipCountByIps(List<String> ips);

    /**
     * 获取ip请求汇总数据
     *
     * @return
     */
    public Map<String, Object> ipCountSumDates();

    /**
     * 登录页相关所需的urls
     */
    public List<String> loginPageRelationAction();

    /**
     * ip请求清除不在名单里的数据
     */
    public void cacheRequestClearNoMenu();

    /**
     * ip是否锁定
     *
     * @param ip
     * @return
     */
    public boolean isLock(String ip);

    /**
     * ip请求数相关缓存清除
     *
     * @param ip
     */
    public void cacheRemoveIp(String ip);

    /**
     * 检查IP是否黑名单
     * 返回true:ip已被封， false：ip正常
     *
     * @param ip IP地址
     * @return 结果值
     */
    boolean checkBlackIp(String ip);
}