package project.mall.log.impl;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.log.OrderLogService;
import project.mall.log.model.OrderLog;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderLogServiceImpl extends HibernateDaoSupport implements OrderLogService {

    private Logger logger = LogManager.getLogger(OrderLogServiceImpl.class);

    private PagedQueryDao pagedQueryDao;

    @Override
    public List<OrderLog> listByOrderId(String orderId) {

        StringBuffer queryString = new StringBuffer(" FROM OrderLog where 1 = 1 ");
        Map parameters = new HashMap();

        if (StringUtils.isNotEmpty(orderId)) {
            queryString.append(" and orderId =  :orderId");
            parameters.put("orderId", orderId);
        }

        queryString.append(" order by createTime ");
        Page page = this.pagedQueryDao.pagedQueryHql(1, 1000, queryString.toString(), parameters);

        List<OrderLog> elements = page.getElements();

        return elements;

    }

    @Override
    public void saveSync(OrderLog entity) {

        logger.info("同步保存订单日志:" + entity);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        this.getHibernateTemplate().save(entity);
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

}
