package project.monitor.etherscan;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Gwei
 * 
 * @author User
 *
 */
public class GasOracle implements Serializable{
	/**
	 * 正常速率
	 */
	public static final String GAS_PRICE_NORMAL="normal";
	/**
	 * 一倍系数加成
	 */
	public static final String GAS_PRICE_FAST="fast";
	/**
	 * 双倍系数加成
	 */
	public static final String GAS_PRICE_SUPER="super";
	/**
	 * 十倍系数加成
	 */
	public static final String GAS_PRICE_TEN_TIMES="ten_times";
	/**
	 * 二十倍系数加成
	 */
	public static final String GAS_PRICE_TWENTY_TIMES="twenty_times";
	/**
	 * 
	 */
	private static final long serialVersionUID = -6908340048454924756L;
	/**
	 * 最慢
	 */
	private Double safeGasPrice;
	/**
	 * 普通
	 */
	private Double proposeGasPrice;
	/**
	 * 最快
	 */
	private Double fastGasPrice;

	/**
	 * 建议
	 */
	private Double suggestBaseFee;

	/**
	 * 最慢GWei
	 */
	private BigInteger safeGasPriceGWei;
	/**
	 * 普通GWei
	 */
	private BigInteger proposeGasPriceGWei;
	/**
	 * 最快GWei
	 */
	private BigInteger fastGasPriceGWei;
	/**
	 * 建议GWei
	 */
	private BigInteger suggestBaseFeeGWei;

	public Double getSafeGasPrice() {
		return safeGasPrice;
	}

	public void setSafeGasPrice(Double safeGasPrice) {
		this.safeGasPrice = safeGasPrice;
	}

	public Double getProposeGasPrice() {
		return proposeGasPrice;
	}

	public void setProposeGasPrice(Double proposeGasPrice) {
		this.proposeGasPrice = proposeGasPrice;
	}

	public Double getFastGasPrice() {
		return fastGasPrice;
	}

	public void setFastGasPrice(Double fastGasPrice) {
		this.fastGasPrice = fastGasPrice;
	}

	public Double getSuggestBaseFee() {
		return suggestBaseFee;
	}

	public void setSuggestBaseFee(Double suggestBaseFee) {
		this.suggestBaseFee = suggestBaseFee;
	}

	public BigInteger getSafeGasPriceGWei() {
		return safeGasPriceGWei;
	}

	public BigInteger getProposeGasPriceGWei() {
		return proposeGasPriceGWei;
	}

	public BigInteger getFastGasPriceGWei() {
		return fastGasPriceGWei;
	}

	public void setSafeGasPriceGWei(BigInteger safeGasPriceGWei) {
		this.safeGasPriceGWei = safeGasPriceGWei;
	}

	public void setProposeGasPriceGWei(BigInteger proposeGasPriceGWei) {
		this.proposeGasPriceGWei = proposeGasPriceGWei;
	}

	public void setFastGasPriceGWei(BigInteger fastGasPriceGWei) {
		this.fastGasPriceGWei = fastGasPriceGWei;
	}

	public BigInteger getSuggestBaseFeeGWei() {
		return suggestBaseFeeGWei;
	}

	public void setSuggestBaseFeeGWei(BigInteger suggestBaseFeeGWei) {
		this.suggestBaseFeeGWei = suggestBaseFeeGWei;
	}

}
