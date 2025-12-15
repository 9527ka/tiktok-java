package project.invest.project.model;

import kernel.bo.EntityObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Project extends EntityObject<String> {

    /**
     * 分红比例
     */
    private double bonusRate;

    /**
     * 锁仓期限
     */
    private Integer bonus;

    /**
     * 状态：0-进行中 1-结束
     */
    private Integer ending;

    /**
     * 状态：0-启用 1-禁用
     */
    private Integer status;

    /**
     * 1=按小时付收益，到期返本；
     * 2=按小时算收益，到期返本+分红；
     * 3=按天付收益，到期返本；
     * 4=按天算收益，到期返本+分红
     */
    private Integer type;

    /**
     * 父类（类型）ID
     */
    private String baseId;

    /**
     * 图标
     */
    private String iconImg;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 投资规模
     */
    private double investSize;

    /**
     * 投资进度
     */
    private double investProgress;

    /**
     * 虚假进度
     */
    private double investProgressMan;

    /**
     * 已售增量
     */
    private double investSellAdd;

    /**
     * 起投金额
     */
    private double investMin;

    /**
     * 最大投资
     */
    private double investMax;

    /**
     * 积分赠送比例
     */
    private double pointRate;

    /**
     * 推荐时间（0=不推荐）
     */
    private long recTime;

    /**
     * 是否重投
     */
    private boolean repeating;

    /**
     * 更新时间
     */
    private long upTime;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public double getBonusRate() {
        return bonusRate;
    }

    public void setBonusRate(double bonusRate) {
        this.bonusRate = bonusRate;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public double getInvestSize() {
        return investSize;
    }

    public void setInvestSize(double investSize) {
        this.investSize = investSize;
    }

    public double getInvestProgress() {
        return investProgress;
    }

    public void setInvestProgress(double investProgress) {
        this.investProgress = investProgress;
    }

    public double getInvestProgressMan() {
        return investProgressMan;
    }

    public void setInvestProgressMan(double investProgressMan) {
        this.investProgressMan = investProgressMan;
    }

    public double getInvestSellAdd() {
        return investSellAdd;
    }

    public void setInvestSellAdd(double investSellAdd) {
        this.investSellAdd = investSellAdd;
    }

    public double getInvestMin() {
        return investMin;
    }

    public void setInvestMin(double investMin) {
        this.investMin = investMin;
    }

    public double getInvestMax() {
        return investMax;
    }

    public void setInvestMax(double investMax) {
        this.investMax = investMax;
    }

    public double getPointRate() {
        return pointRate;
    }

    public void setPointRate(double pointRate) {
        this.pointRate = pointRate;
    }

    public long getRecTime() {
        return recTime;
    }

    public void setRecTime(long recTime) {
        this.recTime = recTime;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getEnding() {
        return ending;
    }

    public void setEnding(Integer ending) {
        this.ending = ending;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }
}