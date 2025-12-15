package project.party;

import java.io.Serializable;
import java.util.List;

import project.mall.orders.model.MallAddress;
import project.party.model.Party;

/**
 * Party CRUD
 */
public interface PartyService {

    Party getById(String id);

    /**
     * 获取用户等级 1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
     */
    public int getUserLevelByAuth(Party party);

    /**
     * @param partyId
     * @param localcache 是否读本地缓存
     * @return
     */
    public Party cachePartyBy(Serializable partyId, boolean localcache);

    /**
     * 根据电话号码查询用户uuid
     *
     * @param phone
     * @return
     */
    String selectUuidByPhone(String phone);

    /**
     * @param username
     * @param localcache 是否读本地缓存
     * @return
     */
    public Party cachePartyByUsername(String username, boolean localcache);

    public Party save(Party entity);

    public void update(Party entity);

    /**
     * 根据用户名
     */
    public Party findPartyByUsername(String username);

    /**
     * 获取PAT_PARTY 根据已验证的电话号码
     */
    public Party findPartyByVerifiedPhone(String phone);

    /**
     * 获取PAT_PARTY 根据已验证的邮箱
     */
    public Party findPartyByVerifiedEmail(String email);

    public Party getPartyByEmail(String email);

    /**
     * 根据用户名
     */
    public Party findPartyByUsercode(String usercode);

    public List<Party> getAll();

    public List<Party> getGuestAll();

    public void updateSafeword(Party party, String safeword);

    /**
     * 验证资金密码
     *
     * @param safeword
     * @param partyId
     * @return
     */
    public boolean checkSafeword(String safeword, String partyId);

    /**
     * 修改redis中 用户提现资金密码错误次数
     *
     * @param partyId partyId
     * @param bool    密码是否正确
     */
    void updateWithdrawDepositPasswordFailedNumber(String partyId, Boolean bool);

    /**
     * 获取用户是否满足提现错误次数状态
     *
     * @param partyId partyId
     * @return 是否超过3次
     */
    Boolean getWithdrawDepositPasswordFailedNumberStatus(String partyId);

    void updateCache(Party party);

    /**
     * 根据partyId获取用户默认发货地址
     *
     * @param id
     */
    List<MallAddress> findUserAddressByPartyId(String id);

    List<Party> getPartyBatch(List<String> idList);

    /**
     * 根据时间统计登录人数,緩存
     *
     * @param startTime
     * @param endTime
     * @return 统计总人数
     */
    Integer getCacheCountLoginByDay(String startTime, String endTime);


    /**
     * 根据时间统计登录人数
     *
     * @param startTime
     * @param endTime
     * @return 统计总人数
     */
    Integer getCountLoginByDay(String startTime, String endTime);

    /**
     * 根据时间统计注册人数
     *
     * @param startTime
     * @param endTime
     * @return 统计总人数
     */
    Integer getCountRegisterByDay(String startTime, String endTime);

    /**
     * 根据时间统计注册人数,緩存
     *
     * @param startTime
     * @param endTime
     * @return 统计总人数
     */
    Integer getCacheCountRegisterByDay(String startTime, String endTime);


    /**
     * @return 统计总人数
     */
    Integer getCountAllUser();

    /**
     * @return 统计总人数，缓存
     */
    Integer getCacheCountAllUser();


    /**
     * @return 统计总店铺数，缓存
     */
    Integer getCacheCountAllSeller();

    /**
     * @return 统计总店铺数
     */
    Integer getCountAllSeller();

    /**
     * 根据时间统计新增店铺
     *
     * @param startTime
     * @param endTime
     * @return 统计总店铺数
     */
    Integer getCacheCountRegisterSellerByDay(String startTime, String endTime);


    /**
     * 根据时间统计新增店铺
     *
     * @param startTime
     * @param endTime
     * @return 统计总店铺数
     */
    Integer getCountRegisterSellerByDay(String startTime, String endTime);


    /**
     * 根据时间统计新增订单数
     *
     * @param startTime
     * @param endTime
     * @return 统计订单数
     */
    Integer getCountOrderByDay(String startTime, String endTime);


    /**
     * 根据时间统计新增订单数
     *
     * @param startTime
     * @param endTime
     * @return 统计订单数
     */
    Integer getCacheCountOrderByDay(String startTime, String endTime);

    void updateUserRemark(String sellerId, String remarks);
}
