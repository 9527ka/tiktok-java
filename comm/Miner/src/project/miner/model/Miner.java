package project.miner.model;

import java.util.Comparator;

import kernel.bo.EntityObject;

/**
 * 矿机
 *
 */
public class Miner extends EntityObject<String> implements Comparator<Miner> {

	private static final long serialVersionUID = 1639941028310043811L;

	/**
	 * 矿机名称(简体中文)
	 */
	private String name;

	/**
	 * 矿机名称(英文)
	 */
	private String name_en;
	/**
	 * 矿机名称(繁体)
	 */
	private String name_cn;

	/**
	 * 周期-天数(现做矿机等级判定)
	 */
	private int cycle;
	/**
	 * ----可解锁周期-- 截止后方可解锁 CYCLE_CLOSE
	 */
	private int cycle_close;

	/**
	 * 日利率(%)
	 */
	private double daily_rate;
	/**
	 * 展示日利率(%)
	 */
	private double show_daily_rate;

	/**
	 * 最低投资金额(USDT)
	 */
	private double investment_min;
	/**
	 * 最高投资金额(USDT)
	 */
	private double investment_max;

	/**
	 * 上下架。0 下架， 1 上架,
	 */
	private String state = "0";
	/**
	 * 是否自主购买 0需管理员手动增加，1可自行购买
	 */
	private String on_sale = "0";
	/**
	 * ---是否是体验产品
	 */
	private boolean test = false;

	/**
	 * 适用算法
	 */
	private String algorithm;
	/**
	 * 算力
	 */
	private double computing_power;
	/**
	 * 算力单位
	 */
	private String computing_power_unit;
	/**
	 * 功耗
	 */
	private double power;
	/**
	 * 生产厂家
	 */
	private String product_factory;
	/**
	 * 外箱尺寸
	 */
	private String product_size;
	/**
	 * 整机重量
	 */
	private double weight;
	/**
	 * 工作温度区间（最小值）
	 */
	private double work_temperature_min;
	/**
	 * 工作温度区间（最大值）
	 */
	private double work_temperature_max;
	/**
	 * 工作湿度区间（最小值）
	 */
	private double work_humidity_min;
	/**
	 * 工作湿度区间（最大值）
	 */
	private double work_humidity_max;
	/**
	 * 网络连接
	 */
	private String internet;
	/**
	 * 基础计息金额
	 */
	private double base_compute_amount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public double getDaily_rate() {
		return daily_rate;
	}

	public void setDaily_rate(double daily_rate) {
		this.daily_rate = daily_rate;
	}

	public double getInvestment_min() {
		return investment_min;
	}

	public void setInvestment_min(double investment_min) {
		this.investment_min = investment_min;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getName_cn() {
		return name_cn;
	}

	public void setName_cn(String name_cn) {
		this.name_cn = name_cn;
	}

	public boolean getTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public String getOn_sale() {
		return on_sale;
	}

	public void setOn_sale(String on_sale) {
		this.on_sale = on_sale;
	}

	public double getInvestment_max() {
		return investment_max;
	}

	public void setInvestment_max(double investment_max) {
		this.investment_max = investment_max;
	}

	public double getShow_daily_rate() {
		return show_daily_rate;
	}

	public void setShow_daily_rate(double show_daily_rate) {
		this.show_daily_rate = show_daily_rate;
	}

	public int compare(Miner o1, Miner o2) {
		return o1.getInvestment_min() < o2.getInvestment_min() ? -1 : 1;
	}

	public int getCycle_close() {
		return cycle_close;
	}

	public void setCycle_close(int cycle_close) {
		this.cycle_close = cycle_close;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public double getPower() {
		return power;
	}

	public String getProduct_factory() {
		return product_factory;
	}

	public String getProduct_size() {
		return product_size;
	}

	public double getWeight() {
		return weight;
	}

	public String getInternet() {
		return internet;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public void setProduct_factory(String product_factory) {
		this.product_factory = product_factory;
	}

	public void setProduct_size(String product_size) {
		this.product_size = product_size;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setInternet(String internet) {
		this.internet = internet;
	}

	public double getWork_temperature_min() {
		return work_temperature_min;
	}

	public double getWork_temperature_max() {
		return work_temperature_max;
	}

	public double getWork_humidity_min() {
		return work_humidity_min;
	}

	public double getWork_humidity_max() {
		return work_humidity_max;
	}

	public void setWork_temperature_min(double work_temperature_min) {
		this.work_temperature_min = work_temperature_min;
	}

	public void setWork_temperature_max(double work_temperature_max) {
		this.work_temperature_max = work_temperature_max;
	}

	public void setWork_humidity_min(double work_humidity_min) {
		this.work_humidity_min = work_humidity_min;
	}

	public void setWork_humidity_max(double work_humidity_max) {
		this.work_humidity_max = work_humidity_max;
	}

	public double getComputing_power() {
		return computing_power;
	}

	public String getComputing_power_unit() {
		return computing_power_unit;
	}

	public void setComputing_power(double computing_power) {
		this.computing_power = computing_power;
	}

	public void setComputing_power_unit(String computing_power_unit) {
		this.computing_power_unit = computing_power_unit;
	}

	public double getBase_compute_amount() {
		return base_compute_amount;
	}

	public void setBase_compute_amount(double base_compute_amount) {
		this.base_compute_amount = base_compute_amount;
	}

}
