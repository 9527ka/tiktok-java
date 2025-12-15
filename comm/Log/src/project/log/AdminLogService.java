package project.log;

import java.util.Date;

import kernel.web.Page;

public interface AdminLogService {
	/**
	 * 
	 * 资金变更日志
	 * @param log_para 日志 like查询
	 * @param wallettype 账户类型
	 */
	public  Page pagedQueryMoneyLog(int pageNo, int pageSize, String log_para,String name_para,String loginPartyId,String rolename_para,String startTime,String endTime,String frozenState);

	/**
	 * 
	 * 操作日志
	 * @param log_para 日志 like查询
	 * @param wallettype 账户类型
	 */
	public  Page pagedQueryLog(int pageNo, int pageSize, String log_para,String name_para,String category,String operator,Date createTime_begin,
			Date createTime_end, String loginPartyId,String loginUsername);
	
	
	/**
	 * 
	 * 系统日志
	 * @param log_para 日志 like查询
	 * @param wallettype 账户类型
	 */
	public  Page pagedQuerySysLog(int pageNo, int pageSize, String log_para,String level_para,String category_para,Date createTime_begin,
			Date createTime_end);
	
	
	/**
	 * 
	 * 发送验证码日志
	 * @param log_para 日志 like查询
	 * @param wallettype 账户类型
	 */
	public  Page pagedQueryCodeLog(int pageNo, int pageSize, String log_para,String name_para,String target,Date createTime_begin,
			Date createTime_end, String loginPartyId,String loginUsername,String id_para);
	
}
