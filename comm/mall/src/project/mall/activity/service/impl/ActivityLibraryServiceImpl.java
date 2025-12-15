package project.mall.activity.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.service.ActivityLibraryService;
import project.mall.auto.AutoConfig;
import project.syspara.SysparaService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

//import project.mall.activity.model.lottery.Lottery;
//import project.mall.activity.model.lottery.LotteryInfoPrize;
//import project.mall.activity.model.lottery.LotteryPrize;

public class ActivityLibraryServiceImpl extends HibernateDaoSupport implements ActivityLibraryService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
@Autowired
private SysparaService sysparaService;
    @Override
    public String saveActivity(ActivityLibrary activityEntity) {
        Date now = new Date();
        if (activityEntity.getCreateTime() == null) {
            activityEntity.setCreateTime(now);
        }
        activityEntity.setUpdateTime(now);
        activityEntity.setDeleted(0);

        if (activityEntity.getId() == null
                || StrUtil.isBlank(activityEntity.getId().toString())
                || Objects.equals(activityEntity.getId().toString(), "0")) {
            activityEntity.setId(null);
            this.getHibernateTemplate().save(activityEntity);
        } else {
            this.getHibernateTemplate().update(activityEntity);
        }

        return activityEntity.getId().toString();
    }

    @Override
    public void delete(String id) {
        ActivityLibrary lottery = this.getHibernateTemplate().get(ActivityLibrary.class, id);

        if (Objects.isNull(lottery)) {
            throw new BusinessException("活动记录不存在");
        }

        getHibernateTemplate().delete(lottery);
    }

    @Override
    public void deleteLogic(String id) {
        ActivityLibrary lottery = this.getHibernateTemplate().get(ActivityLibrary.class, id);

        if (Objects.isNull(lottery)) {
            throw new BusinessException("活动记录不存在");
        }
        lottery.setDeleted(1);

        getHibernateTemplate().update(lottery);
    }

    @Override
    public ActivityLibrary findById(String id) {
        return getHibernateTemplate().get(ActivityLibrary.class, id);
    }

    @Override
    public ActivityLibrary findByTemplate(String templateId) {
        if (StrUtil.isBlank(templateId)) {
            throw new BusinessException("缺失 templateId 参数");
        }

        DetachedCriteria query = DetachedCriteria.forClass(ActivityLibrary.class);
        query.add(Property.forName("templateId").eq(templateId));
        query.add(Property.forName("deleted").eq(0));

        List<ActivityLibrary> list = (List<ActivityLibrary>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public ActivityLibrary findByType(String activityType) {
        if (StrUtil.isBlank(activityType)) {
            throw new BusinessException("缺失 activityType 参数");
        }

        DetachedCriteria query = DetachedCriteria.forClass(ActivityLibrary.class);
        query.add(Property.forName("type").eq(activityType));
        query.add(Property.forName("deleted").eq(0));

        List<ActivityLibrary> list = (List<ActivityLibrary>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public List<ActivityLibrary> getShowActivity(String type) {
        DetachedCriteria query = DetachedCriteria.forClass(ActivityLibrary.class);
        query.add(Property.forName("isShow").eq(1));
        query.add(Property.forName("type").eq(type));
        query.add(Property.forName("deleted").eq(0));

        List<ActivityLibrary> list = (List<ActivityLibrary>) getHibernateTemplate().findByCriteria(query);

        return list;
    }

    @Override
    public void updateShow(String id, int show) {
        ActivityLibrary library = (ActivityLibrary) getHibernateTemplate().get(ActivityLibrary.class, id);
        if (library == null) {
            throw new BusinessException("活动不存在" + id);
        }
        if (show != 0 && show != 1) {
            throw new BusinessException("请设置正确的状态值");
        }

        library.setIsShow(show);
        getHibernateTemplate().update(library);
    }

    @Override
    public Page listActivity(String title, Integer status, String startTime, String endTime, int pageNum, int pageSize) {
        Page page = new Page();

        DetachedCriteria query = DetachedCriteria.forClass(ActivityLibrary.class);
        query.add(Property.forName("deleted").eq(0));
        if (Objects.nonNull(status)) {
            query.add(Property.forName("status").eq(status));
        }

        if (StringUtils.isNotEmpty(title)) {
            Disjunction titleOr = Restrictions.disjunction();
            titleOr.add(Restrictions.like("titleCn", title.trim(), MatchMode.ANYWHERE));
            titleOr.add(Restrictions.like("titleEn", title.trim(), MatchMode.ANYWHERE));

            query.add(titleOr);
        }

        if (StringUtils.isNotEmpty(startTime)) {
            // 此处不能填字符串类的日期值，必须是 Date 类型的
            Date time = DateUtil.parseDateTime(startTime);
            query.add(Property.forName("startTime").gt(time));
        }
        if (StringUtils.isNotEmpty(endTime)) {
            // 此处不能填字符串类的日期值，必须是 Date 类型的
            Date time = DateUtil.parseDateTime(endTime);
            query.add(Property.forName("startTime").lt(time));
        }

        query.addOrder(Order.desc("createTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        List<?> resultList = getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);

        page.setElements(resultList);
        page.setThisPageNumber(pageNum);
        page.setTotalElements(totalCount.intValue());
        page.setPageSize(pageSize);
        return page;
    }

    @Override
    public List<ActivityLibrary> listRunningActivity() {
        Date now = new Date();
        DetachedCriteria query = DetachedCriteria.forClass(ActivityLibrary.class);
        query.add(Property.forName("status").eq(1));
        query.add(Property.forName("startTime").le(now));
        query.add(Property.forName("endTime").gt(now));
        query.add(Property.forName("deleted").eq(0));

        query.addOrder(Order.asc("startTime"));

        List<ActivityLibrary> list = (List<ActivityLibrary>) getHibernateTemplate().findByCriteria(query);
        return list;
    }

    @Override
    public String updateLotteryActivityUrl(String id) {
        ActivityLibrary entity = findById(id);
        if (entity == null) {
            return null;
        }
        if (StrUtil.isNotBlank(entity.getDetailUrl())) {
            return entity.getDetailUrl();
        }

        String baseDomain = AutoConfig.getBaseUrl();
        if (StrUtil.isBlank(baseDomain)) {
            logger.error("[ActivityLibraryServiceImpl updateActivityUrl] 未读取到当前应用的基础域名，无法组装活动详情地址");
            return null;
        }
        baseDomain = baseDomain.trim();
        if (!baseDomain.endsWith("/")) {
            baseDomain = baseDomain + "/";
        }

        String detailUrl = baseDomain + "seller-h5/#/activity/turntable?id=" + id;
        entity.setDetailUrl(detailUrl);
        this.getHibernateTemplate().update(entity);

        return detailUrl;
    }

}
