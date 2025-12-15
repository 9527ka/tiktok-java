package project.mall.seller.impl;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.seller.ComplaintService;
import project.mall.seller.model.Complaint;
import project.mall.seller.model.Seller;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ComplaintServiceImpl extends HibernateDaoSupport implements ComplaintService {
    private PagedQueryDao pagedQueryDao;

    @Override
    public void saveComplaint(Complaint complaint) {
        getHibernateTemplate().saveOrUpdate(complaint);
    }

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String userCode, String storeCode, String storeName, String auditStatus, String startTime, String endTime) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT com.UUID,com.USER_CODE ,party.USERNAME,com.STORE_CODE,seller.NAME,com.COMPLAINT_STATUS,com.CONTENT,com.AUDIT_STATUS,com.REMARK,com.CREATE_TIME,IMG_URL_1,IMG_URL_2,IMG_URL_3,IMG_URL_4,IMG_URL_5,IMG_URL_6,IMG_URL_7,IMG_URL_8,IMG_URL_9, ");
        queryString.append(" (\n" +
                "        CASE WHEN COALESCE(IMG_URL_1, '') <> '' THEN 1 ELSE 0 END +\n" +
                "        CASE WHEN COALESCE(IMG_URL_2, '') <> '' THEN 1 ELSE 0 END +\n" +
                "        CASE WHEN COALESCE(IMG_URL_3, '') <> '' THEN 1 ELSE 0 END +\n" +
                "        CASE WHEN COALESCE(IMG_URL_4, '') <> '' THEN 1 ELSE 0 END +\n" +
                "        CASE WHEN COALESCE(IMG_URL_5, '') <> '' THEN 1 ELSE 0 END +\n" +
                "        CASE WHEN COALESCE(IMG_URL_6, '') <> '' THEN 1 ELSE 0 END +\n" +
                "        CASE WHEN COALESCE(IMG_URL_7, '') <> '' THEN 1 ELSE 0 END +\n" +
                "        CASE WHEN COALESCE(IMG_URL_8, '') <> '' THEN 1 ELSE 0 END +\n" +
                "        CASE WHEN COALESCE(IMG_URL_9, '') <> '' THEN 1 ELSE 0 END\n" +
                "    ) AS total, AUDIT_TIME auditTime ");
        queryString.append(" FROM T_MALL_COMPLAINT com LEFT JOIN T_MALL_SELLER seller ON com.STORE_ID = seller.UUID");
        queryString.append(" LEFT JOIN PAT_PARTY party ON com.PARTY_ID = party.UUID");
        queryString.append(" WHERE 1=1");

        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(userCode)) {
            queryString.append(" AND party.USERCODE =:userCode");
            parameters.put("userCode", userCode);
        }
        if (!StringUtils.isNullOrEmpty(storeCode)) {
            queryString.append(" AND com.STORE_CODE =:storeCode");
            parameters.put("storeCode", storeCode);
        }
        if (!StringUtils.isNullOrEmpty(storeName)) {
            queryString.append(" AND seller.NAME LIKE:storeName");
            parameters.put("storeName", storeName);
        }
        if (!StringUtils.isNullOrEmpty(auditStatus)) {
            queryString.append(" AND com.AUDIT_STATUS =:auditStatus");
            parameters.put("auditStatus", auditStatus);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND com.CREATE_TIME >=:startTime");
            parameters.put("startTime", startTime);
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND com.CREATE_TIME <=:endTime");
            parameters.put("endTime", endTime);
        }
        queryString.append(" ORDER BY com.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Complaint getComplaintById(String id) {
        return this.getHibernateTemplate().get(Complaint.class, id);
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}
