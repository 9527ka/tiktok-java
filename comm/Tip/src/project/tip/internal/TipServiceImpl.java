package project.tip.internal;

import kernel.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.tip.model.Tip;
import security.Resource;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

import javax.servlet.http.HttpServletRequest;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TipServiceImpl extends HibernateDaoSupport implements TipService {

	private static Logger logger = LoggerFactory.getLogger(TipServiceImpl.class);
	private SecUserService secUserService;
	private JdbcTemplate jdbcTemplate;
	/**
	 * key：业务id
	 */
	private Map<String, Tip> tipCache = new ConcurrentHashMap<String, Tip>();
	private SysparaService sysparaService;
	/**
	 * key:username,value:上次获取数据新数据的时间
	 */
//	private Map<String, Date> cacheDate = new ConcurrentHashMap<String, Date>();

	/**
	 * 初始化数据
	 */
	public void init() {
		List<Tip> list =   (List<Tip>) this.getHibernateTemplate().find(" FROM Tip");
		for (Tip tip : list) {
			tipCache.put(tip.getBusiness_id(), tip);
		}
	}

	/**
	 * 新增通知
	 *
	 * @param businessId 业务id(唯一性)
	 * @param model      模块
	 */
	public void saveTip(String businessId, String model) {
		try {
			Tip tip = tipCache.get(businessId);
			if (null == tip) {
				tip = new Tip();
			}

			tip.setBusiness_id(businessId);
			tip.setCreate_time(new Date());
			tip.setModel(model);
			tip.setTime_stamp(new Date().getTime());

			this.getHibernateTemplate().saveOrUpdate(tip);
			tipCache.put(businessId, tip);
		} catch (Exception e) {
			logger.error("fail put tip businessId:{" + businessId + "},model:{" + model + "},e:{}", e);
		}
	}

	/**
	 * 新增通知
	 *
	 * @param tip 消息通知
	 */
	public void saveTip(Tip tip) {
		try {
			tip.setCreate_time(new Date());
			tip.setTime_stamp(new Date().getTime());
			this.getHibernateTemplate().save(tip);
			tipCache.put(tip.getBusiness_id(), tip);
		} catch (Exception e) {
			logger.error("fail put tip businessId:{" + tip.getBusiness_id() + "},model:{" + tip.getModel() + "},e:{}", e);
		}
	}

	/**
	 * 移除通知
	 *
	 * @param businessId
	 */
	public void deleteTip(String businessId) {
		try {
			Tip tip = tipCache.get(businessId);
			if (tip != null) {
				this.getHibernateTemplate().delete(tip);
				tipCache.remove(businessId);
			}
		} catch (Exception e) {
			logger.error("fail remove tip businessId:{" + businessId + "},e:{}", e);
		}
	}

	/**
	 * 批量移除通知
	 *
	 * @param businessId
	 */
	public void deleteTip(List<String> businessIds) {
		deleteBatchTip(businessIds);// 解决幂等性性问题
		for (String id : businessIds) {
			tipCache.remove(id);
		}
	}

	/**
	 * 批量更新订单收益
	 *
	 * @param orderList
	 */
	protected void deleteBatchTip(final List<String> idList) {
		String sql = "DELETE FROM T_TIP WHERE BUSINESS_ID=?";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, idList.get(i));
			}

			@Override
			public int getBatchSize() {
				return idList.size();
			}
		});

	}

	/**
	 * 获取总数 数据
	 *
	 * @param username
	 * @return
	 */
	public List<Map<String, Object>> getCacheSumTips(String username) {

		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();

		// 根据权限列表，判定是否有模块的通知权限
		List<String> resourceIds = this.userAuth(username);
		List<Tip> filterTips = filterTips(username, null);
		if (CollectionUtils.isEmpty(filterTips)) {
			// logger.info("根据用户名获取的通知数据:{}", filterTips);
			return result;
		}
		// 构建模块的通知数据
		Map<String, List<Tip>> modelMap = modelData(filterTips);
		// logger.info("构建模块的通知数据:{}", modelMap);

		for (Entry<String, List<Tip>> entry : modelMap.entrySet()) {
//			if (resourceIds.contains(entry.getKey())) {// 有权限则返回通知
				Map<String, Object> map = tipData(entry.getKey(), entry.getValue());
				result.add(map);
//			}
		}
		// logger.info("返回数据:{}", result);
		return result;
	}

	/**
	 * 获取指定模块的新通知数据
	 *
	 * @param username
	 * @return
	 */
	public List<Map<String, Object>> cacheNewTipsByModel(String username, Long lastTimeStamp, String model) {

		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();

		// 根据权限列表，判定是否有模块的通知权限
		List<String> resourceIds = this.userAuth(username);
		List<Tip> filterNewTips = filterTips(username, lastTimeStamp);

		if (CollectionUtils.isEmpty(filterNewTips)) {
			return result;
		}
		// 构建模块的通知数据
		Map<String, List<Tip>> modelMap = modelData(filterNewTips);
		List<Tip> tipList = modelMap.get(model);
		if (CollectionUtils.isEmpty(tipList)) {
			return result;
		} else {
			result.add(tipNewData(model, tipList));
		}
		return result;
	}

	/**
	 * 获取新通知数据
	 *
	 * @param username
	 * @return
	 */
	public List<Map<String, Object>> getCacheNewTips(String username, Long lastTimeStamp) {

		List<Map<String, Object>> result = new LinkedList<Map<String, Object>>();

		// 根据权限列表，判定是否有模块的通知权限
		List<String> resourceIds = this.userAuth(username);
		List<Tip> filterNewTips = filterTips(username, lastTimeStamp);

		if (CollectionUtils.isEmpty(filterNewTips)) {
			return result;
		}
		// 构建模块的通知数据
		Map<String, List<Tip>> modelMap = modelData(filterNewTips);

		for (Entry<String, List<Tip>> entry : modelMap.entrySet()) {
			if (resourceIds.contains(entry.getKey())) {// 有权限则返回通知
				Map<String, Object> map = tipNewData(entry.getKey(), entry.getValue());
				result.add(map);
			}
		}
		return result;
	}

	private List<Tip> filterTips(final String username, final Long lastTimeStamp) {
		ArrayList<Tip> tipList = new ArrayList<Tip>(tipCache.values());
		CollectionUtils.filter(tipList, new Predicate() {// 过滤查找新生成的通知数据
			@Override
			public boolean evaluate(Object paramObject) {
				// TODO Auto-generated method stub
				Tip tip = (Tip) paramObject;
				if (TipConstants.MUST_USERNAME_MODEL.containsKey(tip.getModel())) {
					if (StringUtils.isNotEmpty(tip.getTarget_username()) && username.equals(tip.getTarget_username())) {
						return lastTimeStamp == null || tip.getTime_stamp() > lastTimeStamp;// 时间戳为空则直接返回
					} else {
						return false;
					}
				}
				return lastTimeStamp == null || ((Tip) paramObject).getTime_stamp() > lastTimeStamp;
			}
		});
		return tipList;
	}

	private String getPath(HttpServletRequest request) {
		return String.format("%s://%s:%s%s", request.getScheme(), request.getServerName(),
				request.getServerPort() == 80?"":request.getServerPort(),
				request.getContextPath());
	}

    /**
     * 用户权限列表，根据权限判定对应的模块提醒
     *
     * @param username
     * @return
     */
    private List<String> userAuth(String username) {
        // 这块可做成缓存
//		SecUser user = this.secUserService.findUserByLoginName(username);
        List<String> resourceIds = jdbcTemplate.queryForList("SELECT rr.RESOURCE_UUID FROM SCT_USER u " +
                "LEFT JOIN SCT_USER_ROLE ur ON u.UUID = ur.USER_UUID  " +
                "LEFT JOIN SCT_ROLE_RESOURCE rr ON rr.ROLE_UUID = ur.ROLE_UUID " +
                "WHERE u.USERNAME= ? ", String.class, username);
//        Role role = user.getRoles().toArray(new Role[0])[0];
//        Set<Resource> resources = role.getResources();
//        List<String> resourceIds = new ArrayList<String>();
//        for (Resource r : resources) {
//            resourceIds.add(r.getId().toString());
//        }
        return resourceIds;
    }

	/**
	 * 每个模块对应的提醒数据
	 *
	 * @return
	 */
	private Map<String, List<Tip>> modelData(Collection<Tip> tips) {
		// 构建需要返回对应权限的数据
		ArrayList<Tip> tipList = new ArrayList<Tip>(tips);
		Map<String, List<Tip>> modelMap = new HashMap<String, List<Tip>>();
		// 构建各个模块的数据
		for (Tip tip : tipList) {
			List<Tip> list = modelMap.get(tip.getModel());
			if (list == null) {
				list = new ArrayList<Tip>();
			}
			list.add(tip);
			modelMap.put(tip.getModel(), list);
		}
		return modelMap;
	}

	/**
	 * 构建模块的通知数据
	 *
	 * @param lastDate 登录人上次获取的时间
	 * @param model    模块
	 * @param list     模块列表数据
	 * @return
	 */
	private Map<String, Object> tipNewData(String model, final List<Tip> list) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 模块新增数
		resultMap.put("tip_content_num", list.size());
		try {
			// 生成html通知
			String htmlMessage = MessageFormat.format(TipConstants.MESSAGE_MAP.get(model),
					MessageFormat.format("<span class=\"label label-danger\">{0}</span>", list.size()));
			resultMap.put("tip_message", list.size() > 0 ? htmlMessage : "");
			// 模块请求url
			resultMap.put("tip_url", list.size() > 0 ? TipConstants.ACTION_MAP.get(model) : "");
			// 是否右下角提示
			resultMap.put("tip_show",isShowTip(model));

		}catch (Exception e){
			logger.error("消息数据格式错误");
		}

		// 模块提示消息
		return resultMap;
	}
	/**
	 * 是否右下角提示，true：是，false：否
	 * @param model
	 * @return
	 */
	private boolean isShowTip(String model) {
		String value = sysparaService.find("tip_noshow_models").getValue();
		return value.indexOf(model)==-1;
	}
	private Map<String, Object> tipData(String model, final List<Tip> list) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 页面上对应的classname(可自定义)
		resultMap.put("tip_dom_name", TipConstants.DOM_MAP.get(model));
		// 模块总数
		resultMap.put("tip_content_sum", list.size());
		// 模块新增数
		return resultMap;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}


}
