package project.mall.activity.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import org.hibernate.criterion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityPrize;
import project.mall.activity.model.ActivityPrizePool;
import project.mall.activity.service.ActivityPrizePoolService;

import java.util.*;

public class ActivityPrizePoolServiceImpl extends HibernateDaoSupport implements ActivityPrizePoolService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void save(ActivityPrizePool prizePool) {
		prizePool.setUpdateTime(new Date());

		if (prizePool.getId() == null
				|| StrUtil.isBlank(prizePool.getId().toString())
				|| Objects.equals(prizePool.getId().toString(), "0")) {
			if (prizePool.getCreateTime() == null) {
				prizePool.setCreateTime(new Date());
			}
			prizePool.setId(null);
			prizePool.setDeleted(0);
			getHibernateTemplate().save(prizePool);
		} else {
			getHibernateTemplate().update(prizePool);
		}
	}

	@Override
	public void delete(String id) {
		ActivityPrizePool prizePool = this.getHibernateTemplate().get(ActivityPrizePool.class, id);

		if (Objects.isNull(prizePool)) {
			//throw new BusinessException("记录不存在");
			return;
		}

		getHibernateTemplate().delete(prizePool);
	}

	@Override
	public void deleteLogic(String id) {
		ActivityPrizePool prizePool = this.getHibernateTemplate().get(ActivityPrizePool.class, id);

		if (Objects.isNull(prizePool)) {
			//throw new BusinessException("记录不存在");
			return;
		}

		prizePool.setDeleted(1);
		prizePool.setStatus(0);
		getHibernateTemplate().update(prizePool);
	}

	@Override
	public void update(ActivityPrizePool prizePool, String lang) {
		prizePool.setUpdateTime(new Date());

//		if (lang.equals(LanguageEnum.CN.getLang())) {
//			prizePool.setPrizeNameCn(lotteryPrize.getPrizeNameCn());
//		} else {
//			prizePool.setPrizeNameEn(lotteryPrize.getPrizeNameEn());
//		}

		this.getHibernateTemplate().save(prizePool);
	}

	@Override
	public ActivityPrizePool detail(String id) {
		return this.getHibernateTemplate().get(ActivityPrizePool.class, id);
	}

	@Override
	public List<ActivityPrizePool> listAll() {
		DetachedCriteria query = DetachedCriteria.forClass(ActivityPrizePool.class);
		query.add(Property.forName("deleted").eq(0));

		return (List<ActivityPrizePool>) getHibernateTemplate().findByCriteria(query);
	}

	@Override
	public Page listPrize(String prizeName, int prizeType, int status, String startTime, String endTime, int pageNum, int pageSize) {
		Page page = new Page();
		DetachedCriteria query = DetachedCriteria.forClass(ActivityPrizePool.class);
		query.add(Property.forName("deleted").eq(0));
		if (prizeType > 0) {
			query.add(Property.forName("prizeType").eq(prizeType));
		}
		if (status >= 0) {
			query.add(Property.forName("status").eq(status));
		}
		if (StringUtils.isNotEmpty(prizeName)) {
			Disjunction titleOr = Restrictions.disjunction();
			titleOr.add(Restrictions.like("prizeNameCn", prizeName.trim(), MatchMode.ANYWHERE));
			titleOr.add(Restrictions.like("prizeNameEn", prizeName.trim(), MatchMode.ANYWHERE));

			query.add(titleOr);
		}

		if (StringUtils.isNotEmpty(startTime)) {
			// 此处不能填字符串类的日期值，必须是 Date 类型的
			Date time = DateUtil.parseDateTime(startTime);
			query.add(Property.forName("createTime").gt(time));
		}
		if (StringUtils.isNotEmpty(endTime)) {
			// 此处不能填字符串类的日期值，必须是 Date 类型的
			Date time = DateUtil.parseDateTime(endTime);
			query.add(Property.forName("createTime").lt(time));
		}

		query.addOrder(Order.desc("createTime"));

		// 查询总条数
		Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
		query.setProjection(null);

		List<?> resultList = getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);

		page.setThisPageNumber(pageNum);
		page.setTotalElements(totalCount.intValue());
		page.setElements(resultList);
		page.setPageSize(pageSize);

		return page;
	}

	/**
	 * 过滤掉：谢谢惠顾类型的奖品
	 *
	 * @param size
	 * @return
	 */
	@Override
	public List<ActivityPrizePool> listLotteryPrize(int size) {
		if (size <= 0) {
			size = 1;
		}

		DetachedCriteria query = DetachedCriteria.forClass(ActivityPrizePool.class);
		query.add(Property.forName("prizeType").in(1, 2));
		query.add(Property.forName("status").eq(1));
		query.add(Property.forName("deleted").eq(0));

		query.addOrder(Order.desc("createTime"));

		List<ActivityPrizePool> resultList = (List<ActivityPrizePool>) getHibernateTemplate().findByCriteria(query, 0, size);

		return resultList;
	}

	@Override
	public List<ActivityPrizePool> listByIds(List<String> ids) {
		DetachedCriteria query = DetachedCriteria.forClass(ActivityPrizePool.class);

		query.add(Property.forName("id").in(ids));

		List<ActivityPrizePool> results = (List<ActivityPrizePool>) getHibernateTemplate().findByCriteria(query);
		return results;
	}

	@Override
	public List<ActivityPrizePool> listDefaltPrize(int status, int size) {
		DetachedCriteria query = DetachedCriteria.forClass(ActivityPrizePool.class);
		query.add(Property.forName("deleted").eq(0));
		if (status >= 0) {
			query.add(Property.forName("status").eq(1));
		}
		// 谢谢惠顾类型的奖品
		query.add(Property.forName("prizeType").eq(3));

		List<ActivityPrizePool> results = null;
		if (size <= 0) {
			results = (List<ActivityPrizePool>) getHibernateTemplate().findByCriteria(query);
		} else {
			results = (List<ActivityPrizePool>) getHibernateTemplate().findByCriteria(query, 0, size);
		}

		return results;
	}

}
