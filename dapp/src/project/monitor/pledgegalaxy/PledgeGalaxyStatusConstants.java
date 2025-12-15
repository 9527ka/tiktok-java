package project.monitor.pledgegalaxy;

/**
 * 质押2.0状态常量类
 *
 */
public class PledgeGalaxyStatusConstants {

	/**
	 * 质押提交申请
	 */
	public static final int PLEDGE_APPLY = 0;
	
	/**
	 * 质押成功
	 */
	public static final int PLEDGE_SUCCESS = 1;
	
	/**
	 * 质押失败
	 */
	public static final int PLEDGE_FAIL = 2;
	
	/**
	 * 赎回申请
	 */
	public static final int RETURN_APPLY = 3;
	
	/**
	 * 赎回成功
	 */
	public static final int RETURN_SUCCESS = 4;
	
	/**
	 * 赎回失败
	 */
	public static final int RETURN_FAIL = 5;
	
	/**
	 * 0 待领取 1 待审核 2 已通过 3 不通过 4 已过期
	 */
	public static final int PROFIT_PENDING = 0;
	
	/**
	 *  1 待审核 
	 */
	public static final int PROFIT_AUTID = 1;
	
	/**
	 *  2 已通过 
	 */
	public static final int PROFIT_PASSED = 2;
	
	/**
	 *  3 不通过
	 */
	public static final int PROFIT_FAIL = 3;
	
	/**
	 * 4 已过期
	 */
	public static final int PROFIT_EXPIRED = 4;
}
