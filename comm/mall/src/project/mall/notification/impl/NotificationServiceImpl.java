package project.mall.notification.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.query.NativeQuery;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.mall.notification.NotificationService;
import project.mall.notification.model.Notification;

import java.util.*;

public class NotificationServiceImpl extends HibernateDaoSupport implements NotificationService {

//    private JdbcTemplate jdbcTemplate;
//
//    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

    private PagedQueryDao pagedDao;

    public void setPagedDao(PagedQueryDao pagedDao) {
        this.pagedDao = pagedDao;
    }

    @Override
    @Transactional
    public Notification save(Notification entity) {
        if (entity.getId() != null && !entity.getId().toString().trim().isEmpty()) {
            throw new RuntimeException("新增记录不能有主键值");
        }
        entity.setId(null);

//        SessionFactory sessionFactory = getSessionFactory();
//        Session session = sessionFactory.getCurrentSession();
//        session.setHibernateFlushMode(FlushMode.AUTO);

        getHibernateTemplate().save(entity);
        return entity;
    }

    @Override
    public Notification update(Notification entity) {
        if (entity.getId() == null || entity.getId().toString().trim().isEmpty()) {
            throw new RuntimeException("没有主键值");
        }

        getHibernateTemplate().update(entity);
        return entity;
    }

    @Override
    public Page pagedListUserNotification(int pageNum, int pageSize, String targetUserId, int type, int module, String bizType, String language, int status) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Notification.class);

//        DetachedCriteria query = DetachedCriteria.forClass(Notification.class);
//        query.add(Property.forName("type").eq(type));
        criteria.add(Property.forName("type").eq(type));
        criteria.add(Property.forName("module").eq(module));

        if (targetUserId != null && !targetUserId.trim().isEmpty()) {
            criteria.add(Restrictions.or(Restrictions.eq("targetUserId", targetUserId), Restrictions.eq("targetUserId", "0")));
        }
        if (bizType != null && !bizType.trim().isEmpty()) {
            criteria.add(Property.forName("bizType").eq(bizType.trim()));
        }
        if (language != null && !language.trim().isEmpty()) {
            criteria.add(Property.forName("language").eq(language.trim()));
        }
        if (status > 0) {
            criteria.add(Property.forName("status").eq(status));
        }
//        List<String> goodsIds = jdbcTemplate.queryForList("SELECT DISTINCT " +
//                "m.GOODS_ID  " +
//                "FROM " +
//                "T_MALL_SELLER_GOODS m " +
//                "LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID  " +
//                "WHERE " +
//                "s.LANG = '" + lang + "'  " +
//                "AND s.TYPE = 0 " +
//                "AND m.IS_SHELF = 1 ", String.class);

        criteria.addOrder(Order.desc("sendTime"));

        int total = 0;
        Object rowCountInfo = criteria.setProjection(Projections.rowCount()).uniqueResult();
        if (rowCountInfo != null) {
            total = ((Long) (rowCountInfo)).intValue();
        }
        criteria.setProjection(null);

        Page page = new Page(pageNum, pageSize, Integer.MAX_VALUE);

        //HibernateUtils.applyParameters(query, parameters);
        criteria.setFirstResult(page.getFirstElementNumber());
        criteria.setMaxResults(pageSize);
        List list = criteria.list();
        page.setElements(list);
        page.setThisPageNumber(list.size());
        page.setTotalElements(total);

        return page;
    }

    public List<Notification> getSlideListUserNotification(long lastLocation, int pageSize, String targetUserId, int type, int module, String bizType, String language, int status) {
        DetachedCriteria query = DetachedCriteria.forClass(Notification.class);
        query.add(Property.forName("type").eq(type));

        if (targetUserId != null && !targetUserId.trim().isEmpty()) {
            query.add(Restrictions.or(Restrictions.eq("targetUserId", targetUserId), Restrictions.eq("targetUserId", "0")));
        }
        if (module > 0) {
            query.add(Property.forName("module").eq(module));
        }
        if (lastLocation > 0) {
            query.add(Property.forName("location").lt(lastLocation));
        }
        if (bizType != null && !bizType.trim().isEmpty()) {
            query.add(Property.forName("bizType").eq(bizType.trim()));
        }
        if (language != null && !language.trim().isEmpty()) {
            query.add(Property.forName("language").eq(language.trim()));
        }
        if (status > 0) {
            query.add(Property.forName("status").eq(status));
        }
        query.addOrder(Order.desc("location"));

        return (List<Notification>) getHibernateTemplate().findByCriteria(query, 0, pageSize);
    }

    @Override
    public int getUnReadCount(String targetUserId, int type, int module, String bizType, String language) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Notification.class);

        criteria.add(Property.forName("type").eq(type));
        criteria.add(Restrictions.or(Restrictions.eq("targetUserId", targetUserId), Restrictions.eq("targetUserId", "0")));

        criteria.add(Property.forName("module").eq(module));
        if (bizType != null && !bizType.trim().isEmpty()) {
            criteria.add(Property.forName("bizType").eq(bizType.trim()));
        }
        if (language != null && !language.trim().isEmpty()) {
            criteria.add(Property.forName("language").eq(language.trim()));
        }
        criteria.add(Property.forName("status").eq(1));

        int total = ((Long) (criteria.setProjection(Projections.rowCount())).uniqueResult()).intValue();
        return total;
    }

    public Notification getById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        return getHibernateTemplate().get(Notification.class, id);
    }

    public void updateStatus(String id, int status) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }

//        Session session = HibernateUitl.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        Query query = session.createQuery("update Teacher t set t.name = 'yangtianb' where id = 3");
//        query.executeUpdate();
//        session.getTransaction().commit();

        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = " update T_NOTIFICATION set STATUS= :status where UUID= :id ";
        NativeQuery query = currentSession.createSQLQuery(sql);

        query.setParameter("status", status);
        query.setParameter("id", id);

        query.executeUpdate();
    }

    public static void main(String[] args) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 67; i++) {
            ids.add("id_" + i);
        }

        StringBuilder idsIn = new StringBuilder();
        List<String> oriIdList = new ArrayList<>(ids);
        List<String> pageIdList = null;
        int pageSize = 20;
        int offset = 0;
        while (offset < ids.size()) {
            if (offset + pageSize <= ids.size()) {
                pageIdList = oriIdList.subList(offset, offset + pageSize);
            } else {
                pageIdList = oriIdList.subList(offset, ids.size());
            }

            for (String oneId : pageIdList) {
                idsIn.append("'").append(oneId).append("',");
            }
            idsIn.deleteCharAt(idsIn.length() - 1);

            System.out.println("----> ids in :" + idsIn.toString());

            offset = offset + pageIdList.size();
            if (idsIn.length() > 0) {
                idsIn.delete(0, idsIn.length());
            }
            // 此处不能 clear，否则会导致 oriIdList 的元素被清理
            //pageIdList.clear();
        }
    }

    public void updateBatchRead(Set<String> ids, String partyId) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        StringBuilder idsIn = new StringBuilder();
        List<String> oriIdList = new ArrayList<>(ids);
        List<String> pageIdList = new ArrayList<>();
        int pageSize = 20;
        int offset = 0;
        while (offset < ids.size()) {
            if (offset + pageSize <= ids.size()) {
                pageIdList = oriIdList.subList(offset, offset + pageSize);
            } else {
                pageIdList = oriIdList.subList(offset, ids.size());
            }

            for (String oneId : pageIdList) {
                idsIn.append("'").append(oneId).append("',");
            }
            idsIn.deleteCharAt(idsIn.length() - 1);

            Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
            String sql = " update T_NOTIFICATION set STATUS= 2 where UUID in(" + idsIn.toString() + ") and TARGET_USER_ID = :partyId ";
            NativeQuery query = currentSession.createSQLQuery(sql);

            query.setParameter("partyId", partyId);

            query.executeUpdate();

            offset = offset + pageIdList.size();
            if (idsIn.length() > 0) {
                idsIn.delete(0, idsIn.length());
            }
            // 此处不能 clear，否则会导致 oriIdList 的元素被清理
            //pageIdList.clear();
        }
    }

    @Override
    public int deleteOldNotification(String targetUserId, List<Integer> statusList, Date limitTime) {
        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = " delete from T_NOTIFICATION where SEND_TIME <= :limitTime ";

        if (StrUtil.isNotBlank(targetUserId)) {
            sql += " and TARGET_USER_ID = :targetUserId ";
        }
        if (CollectionUtil.isNotEmpty(statusList)) {
            String statusIn = CollUtil.join(statusList, ",");
            sql += " and STATUS in(" + statusIn + ") ";
        }
        NativeQuery query = currentSession.createSQLQuery(sql);
        query.setParameter("limitTime", DateUtil.format(limitTime, "yyyy-MM-dd HH:mm:ss"));

        if (StrUtil.isNotBlank(targetUserId)) {
            query.setParameter("targetUserId", targetUserId);
        }

        return query.executeUpdate();
    }


//    public Page pagedQuery(int pageNo, int pageSize, String language, String title, String startTime, String endTime, Integer type, Integer status) {
//        StringBuffer queryString = new StringBuffer(" SELECT ");
//        queryString.append(" UUID id, TITLE title, CONTENT content, CREATE_TIME createTime, LANGUAGE language, TYPE type, STATUS status FROM T_CMS WHERE 1=1 ");
//        Map<String, Object> parameters = new HashMap();
//        if (StringUtils.isNotEmpty(language)) {
//            queryString.append("AND LANGUAGE=:language ");
//            parameters.put("language", language);
//        }
//        if (StringUtils.isNotEmpty(title)) {
//            queryString.append("AND TITLE like:title ");
//            parameters.put("title", "%" + title + "%");
//        }
//        if (-2 != status) {
//            queryString.append(" and STATUS =:status");
//            parameters.put("status", status);
//        }
//        if (-2 != type) {
//            queryString.append(" and TYPE =:type");
//            parameters.put("type", type);
//        }
//        if (StringUtils.isNotEmpty(startTime)) {
//            queryString.append(" AND DATE(CREATE_TIME) >= DATE(:startTime)  ");
//            parameters.put("startTime", DateUtils.toDate(startTime));
//        }
//
//        if (StringUtils.isNotEmpty(endTime)) {
//            queryString.append(" AND DATE(CREATE_TIME) <= DATE(:endTime)  ");
//            parameters.put("endTime", DateUtils.toDate(endTime));
//        }
//        queryString.append(" order by CREATE_TIME desc ");
//        Page page = this.pagedDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//        return page;
//    }

}
