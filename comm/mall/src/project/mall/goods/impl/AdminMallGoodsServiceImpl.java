package project.mall.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.invest.goods.model.Goods;
import project.mall.LanguageEnum;
import project.mall.MallRedisKeys;
import project.mall.goods.AdminMallGoodsService;
import project.mall.goods.GoodsAttributeService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.dto.GoodsAttributeValueDto;
import project.mall.goods.dto.SkuAttrDto;
import project.mall.goods.dto.SkuDto;
import project.mall.goods.model.*;
import project.mall.goods.vo.SellerGoodsSkuInfoVO;
import project.mall.type.CategoryLangService;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.wallet.Wallet;
import project.web.admin.dto.SystemGoodListDto;
import project.web.admin.model.SystemGoodModel;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品管理
 */
@Slf4j
public class AdminMallGoodsServiceImpl extends HibernateDaoSupport implements AdminMallGoodsService {

    private PagedQueryDao pagedQueryDao;

    protected RedisHandler redisHandler;

    protected GoodsAttributeService goodsAttributeService;

    private SellerGoodsService sellerGoodsService;

    protected CategoryLangService categoryLangService;

    private UserRecomService userRecomService;

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page pageQuery(int pageNo, int pageSize, String name, Integer isShelf, String categoryId, String secondaryCategoryId, String id, Integer updateStatusPara) {
        Page page = new Page();
        List<Object> params = new ArrayList<>();
        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
        categoryQuery.add(Property.forName("type").eq(1));
        categoryQuery.add(Property.forName("status").eq(1));
        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
        List<String> validCategoryIdList = validCategoryList.stream()
                // 只保留二级分类id
                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(validCategoryIdList)) {
            //page.setElements(new ArrayList());
            //page.setTotalElements(0);
            //return page;
        }
        StringBuffer categoryIds = new StringBuffer("(");
        for (String oneCategoryId : validCategoryIdList) {
            categoryIds.append("'").append(oneCategoryId).append("',");
        }
        categoryIds.deleteCharAt(categoryIds.length() - 1);
        categoryIds.append(")");

        StringBuffer queryString = new StringBuffer("SELECT sg.UUID AS 'id', sg.UPDATE_STATUS, sg.IS_SHELF, sg.IMG_URL_1, sg.CATEGORY_ID, sg.SECONDARY_CATEGORY_ID, sg.CREATE_TIME, sg.SYSTEM_PRICE ");
        queryString.append(" FROM T_MALL_SYSTEM_GOODS sg ");

        StringBuffer countQueryString = new StringBuffer("SELECT count(*)  FROM T_MALL_SYSTEM_GOODS sg ");
        if (StrUtil.isBlank(name)) {
            // 简单 sql

        } else {
            queryString.append(" INNER JOIN (");
            queryString.append(" SELECT NAME,GOODS_ID FROM T_MALL_SYSTEM_GOODS_LANG WHERE type=0 ");
            queryString.append(" AND NAME like ? ");
            queryString.append(") sgl ON sg.UUID=sgl.GOODS_ID ");

            countQueryString.append(" INNER JOIN (");
            countQueryString.append(" SELECT NAME,GOODS_ID FROM T_MALL_SYSTEM_GOODS_LANG WHERE type=0 ");
            countQueryString.append(" AND NAME like ? ");
            countQueryString.append(") sgl ON sg.UUID=sgl.GOODS_ID ");
            params.add("%" + name + "%");
        }
        queryString.append(" where 1=1 ");
        countQueryString.append(" where 1=1 ");

        //"LEFT JOIN (SELECT NAME AS 'categoryName', CATEGORY_ID categoryId FROM T_MALL_CATEGORY_LANG WHERE LANG = 'en') gc    ON sg.CATEGORY_ID = gc.categoryId WHERE 1=1 ");
        if (!StringUtils.isNullOrEmpty(categoryId) && !Objects.equals(categoryId, "0")) {
            queryString.append(" AND sg.CATEGORY_ID = ? ");
            countQueryString.append(" AND sg.CATEGORY_ID = ? ");
            params.add(categoryId);
        }
        if (!StringUtils.isNullOrEmpty(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
            queryString.append(" AND sg.SECONDARY_CATEGORY_ID =  ? ");
            countQueryString.append(" AND sg.SECONDARY_CATEGORY_ID = ? ");
            params.add(secondaryCategoryId);
        }
//        else {// caster 分类过滤延迟生效
//            queryString.append(" AND sg.SECONDARY_CATEGORY_ID in " + categoryIds);
//            countQueryString.append(" AND sg.SECONDARY_CATEGORY_ID in " + categoryIds);
//        }

        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" AND sg.UUID = ? ");
            countQueryString.append(" AND sg.UUID = ? ");
            params.add(id);
        }
        if (isShelf != null) {
            queryString.append(" AND sg.IS_SHELF = ? ");
            countQueryString.append(" AND sg.IS_SHELF = ? ");
            params.add(isShelf.intValue());
        }
        if (updateStatusPara != null) {
            queryString.append(" AND sg.UPDATE_STATUS = ? ");
            countQueryString.append(" AND sg.UPDATE_STATUS = ? ");
            params.add(updateStatusPara.intValue());
        }

        queryString.append(" ORDER BY sg.CREATE_TIME DESC ");
        queryString.append("   limit " + (pageNo - 1) * pageSize + "," + pageSize);
        logger.info("-----------> AdminMallGoodsServiceImpl.pageQuery sql:" + queryString.toString());

        List list = jdbcTemplate.queryForList(queryString.toString(), params.toArray());
        int totalCount = jdbcTemplate.queryForObject(countQueryString.toString(), params.toArray(), Integer.class);
        Iterator iterator = list.iterator();
        List<SystemGoodListDto> resultList = new ArrayList<>();

        List<String> categoryIdList = new ArrayList();
        List<String> goodsIdList = new ArrayList();
        while (iterator.hasNext()) {
            Map rowMap = (Map) iterator.next();
            String gid = (String) rowMap.getOrDefault("id", "");
            String goodName = (String) rowMap.getOrDefault("goodName", "");
            String ci = (String) rowMap.getOrDefault("CATEGORY_ID", "");
            String sci = (String) rowMap.getOrDefault("SECONDARY_CATEGORY_ID", "");
            Integer shelf = (Integer) rowMap.getOrDefault("IS_SHELF", "0");
            String imgUrl1 = (String) rowMap.getOrDefault("IMG_URL_1", "");
            Double systemPrice = (Double) rowMap.getOrDefault("SYSTEM_PRICE", "0");
            LocalDateTime createTime = (LocalDateTime) rowMap.getOrDefault("CREATE_TIME", "0");
            Integer updateStatus = (Integer) rowMap.getOrDefault("UPDATE_STATUS", "0");

            goodsIdList.add(gid);
            categoryIdList.add(ci);
            if (StrUtil.isNotBlank(sci) && !Objects.equals(sci, "0")) {
                categoryIdList.add(sci);
            }

            SystemGoodListDto dto = new SystemGoodListDto();
            dto.setCategoryId(ci);
            dto.setSecondaryCategoryId(sci);
            dto.setGoodName(goodName);
            dto.setImgUrl1(imgUrl1);
            dto.setIsShelf(shelf);
            dto.setSystemPrice(systemPrice);
            dto.setId(gid);
            dto.setUpdateStatus(updateStatus);
            Date date = Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant());
            dto.setCreateTime(DateUtils.format(date, DateUtils.NORMAL_DATE_FORMAT));
            resultList.add(dto);
        }

        // 填充 categoryName 和 goodName
        if (resultList.size() > 0) {
            DetachedCriteria categoryCriteria = DetachedCriteria.forClass(CategoryLang.class);
            // 给criteria添加条件，引号中status为实体中的字段，后一个为值
            categoryCriteria.add(Restrictions.in("categoryId", categoryIdList));
            categoryCriteria.add(Restrictions.eq("lang", "en"));
            List<CategoryLang> categoryList = (List<CategoryLang>) this.getHibernateTemplate().findByCriteria(categoryCriteria);
            Map<String, CategoryLang> categoryMap = categoryList.stream().collect(Collectors.toMap(CategoryLang::getCategoryId, entity -> entity, (value1, value2) -> {
                return value1;
            }));
            for (SystemGoodListDto dto : resultList) {
                CategoryLang category = categoryMap.get(dto.getSecondaryCategoryId());
                if (category == null) {
                    category = categoryMap.get(dto.getCategoryId());
                }
                if (category != null) {
                    dto.setCategoryName(category.getName());
                }
            }

            DetachedCriteria langCriteria = DetachedCriteria.forClass(SystemGoodsLang.class);
            // 给criteria添加条件，引号中status为实体中的字段，后一个为值
            langCriteria.add(Restrictions.in("goodsId", goodsIdList));
            langCriteria.add(Restrictions.eq("lang", "en"));
            List<SystemGoodsLang> langList = (List<SystemGoodsLang>) this.getHibernateTemplate().findByCriteria(langCriteria);
            Map<String, SystemGoodsLang> langMap = langList.stream().collect(Collectors.toMap(SystemGoodsLang::getGoodsId, entity -> entity, (value1, value2) -> {
                return value1;
            }));
            for (SystemGoodListDto dto : resultList) {
                SystemGoodsLang lang = langMap.get(dto.getId());
                if (lang != null) {
                    dto.setGoodName(lang.getName());
                }
            }
        }

        page.setElements(resultList);
        page.setTotalElements(totalCount);
        return page;
    }

    public Page pageQuery1(int pageNo, int pageSize, String name, Integer isShelf, String categoryId, String secondaryCategoryId, String id, Integer updateStatusPara) {
        Page page = new Page();
        List<Object> params = new ArrayList<>();
        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
        categoryQuery.add(Property.forName("type").eq(1));
        categoryQuery.add(Property.forName("status").eq(1));
        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
        List<String> validCategoryIdList = validCategoryList.stream()
                // 只保留二级分类id
                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(validCategoryIdList)) {
            //page.setElements(new ArrayList());
            //page.setTotalElements(0);
            //return page;
        }
        StringBuffer categoryIds = new StringBuffer("(");
        for (String oneCategoryId : validCategoryIdList) {
            categoryIds.append("'").append(oneCategoryId).append("',");
        }
        categoryIds.deleteCharAt(categoryIds.length() - 1);
        categoryIds.append(")");

        StringBuffer queryString = new StringBuffer("SELECT sg.UUID AS 'id', sg.UPDATE_STATUS, sg.IS_SHELF, sg.IMG_URL_1, sg.CATEGORY_ID, sg.SECONDARY_CATEGORY_ID, sg.CREATE_TIME, sg.SYSTEM_PRICE, sg.CN_TITLE, sg.EN_TITLE, sg.ZH_TITLE ");
        queryString.append(" FROM T_MALL_SYSTEM_GOODS sg WHERE 1=1 ");

        StringBuffer countQueryString = new StringBuffer("SELECT count(*) FROM T_MALL_SYSTEM_GOODS sg WHERE 1=1 ");
        if (StrUtil.isNotBlank(name)) {
            queryString.append(" AND (CN_TITLE like ? or EN_TITLE like ? or ZH_TITLE like ?) ");
            countQueryString.append(" AND (CN_TITLE like ? or EN_TITLE like ? or ZH_TITLE like ?) ");

            params.add("%" + name + "%");
            params.add("%" + name + "%");
            params.add("%" + name + "%");
        }

        if (!StringUtils.isNullOrEmpty(categoryId) && !Objects.equals(categoryId, "0")) {
            queryString.append(" AND sg.CATEGORY_ID = ? ");
            countQueryString.append(" AND sg.CATEGORY_ID = ? ");
            params.add(categoryId);
        }
        if (!StringUtils.isNullOrEmpty(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
            queryString.append(" AND sg.SECONDARY_CATEGORY_ID =  ? ");
            countQueryString.append(" AND sg.SECONDARY_CATEGORY_ID = ? ");
            params.add(secondaryCategoryId);
        }
//        else {// caster 分类过滤延迟生效
//            queryString.append(" AND sg.SECONDARY_CATEGORY_ID in " + categoryIds);
//            countQueryString.append(" AND sg.SECONDARY_CATEGORY_ID in " + categoryIds);
//        }

        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" AND sg.UUID = ? ");
            countQueryString.append(" AND sg.UUID = ? ");
            params.add(id);
        }
        if (isShelf != null) {
            queryString.append(" AND sg.IS_SHELF = ? ");
            countQueryString.append(" AND sg.IS_SHELF = ? ");
            params.add(isShelf.intValue());
        }
        if (updateStatusPara != null) {
            queryString.append(" AND sg.UPDATE_STATUS = ? ");
            countQueryString.append(" AND sg.UPDATE_STATUS = ? ");
            params.add(updateStatusPara.intValue());
        }

        queryString.append(" ORDER BY sg.CREATE_TIME DESC ");
        queryString.append("   limit " + (pageNo - 1) * pageSize + "," + pageSize);
        logger.info("-----------> AdminMallGoodsServiceImpl.pageQuery sql:" + queryString.toString());

        List list = jdbcTemplate.queryForList(queryString.toString(), params.toArray());
        int totalCount = jdbcTemplate.queryForObject(countQueryString.toString(), params.toArray(), Integer.class);
        Iterator iterator = list.iterator();
        List<SystemGoodListDto> resultList = new ArrayList<>();

        List<String> categoryIdList = new ArrayList();
        List<String> goodsIdList = new ArrayList();
        while (iterator.hasNext()) {
            Map rowMap = (Map) iterator.next();
            String gid = (String) rowMap.getOrDefault("id", "");
            String cnGoodName = (String) rowMap.getOrDefault("CN_TITLE", "");
            String enGoodName = (String) rowMap.getOrDefault("EN_TITLE", "");
            String zhGoodName = (String) rowMap.getOrDefault("ZH_TITLE", "");
            String goodName = cnGoodName;
            if (StrUtil.isBlank(goodName)) {
                goodName = enGoodName;
            }
            if (StrUtil.isBlank(goodName)) {
                goodName = zhGoodName;
            }

            String ci = (String) rowMap.getOrDefault("CATEGORY_ID", "");
            String sci = (String) rowMap.getOrDefault("SECONDARY_CATEGORY_ID", "");
            Integer shelf = (Integer) rowMap.getOrDefault("IS_SHELF", "0");
            String imgUrl1 = (String) rowMap.getOrDefault("IMG_URL_1", "");
            Double systemPrice = (Double) rowMap.getOrDefault("SYSTEM_PRICE", "0");
            LocalDateTime createTime = (LocalDateTime) rowMap.getOrDefault("CREATE_TIME", "0");
            Integer updateStatus = (Integer) rowMap.getOrDefault("UPDATE_STATUS", "0");

            goodsIdList.add(gid);
            categoryIdList.add(ci);
            if (StrUtil.isNotBlank(sci) && !Objects.equals(sci, "0")) {
                categoryIdList.add(sci);
            }

            SystemGoodListDto dto = new SystemGoodListDto();
            dto.setCategoryId(ci);
            dto.setSecondaryCategoryId(sci);
            dto.setGoodName(goodName);
            dto.setImgUrl1(imgUrl1);
            dto.setIsShelf(shelf);
            dto.setSystemPrice(systemPrice);
            dto.setId(gid);
            dto.setUpdateStatus(updateStatus);
            Date date = Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant());
            dto.setCreateTime(DateUtils.format(date, DateUtils.NORMAL_DATE_FORMAT));
            resultList.add(dto);
        }

        // 填充 categoryName 和 goodName
        if (resultList.size() > 0) {
            DetachedCriteria categoryCriteria = DetachedCriteria.forClass(CategoryLang.class);
            // 给criteria添加条件，引号中status为实体中的字段，后一个为值
            categoryCriteria.add(Restrictions.in("categoryId", categoryIdList));
            categoryCriteria.add(Restrictions.eq("lang", "en"));
            List<CategoryLang> categoryList = (List<CategoryLang>) this.getHibernateTemplate().findByCriteria(categoryCriteria);
            Map<String, CategoryLang> categoryMap = categoryList.stream().collect(Collectors.toMap(CategoryLang::getCategoryId, entity -> entity, (value1, value2) -> {
                return value1;
            }));
            for (SystemGoodListDto dto : resultList) {
                CategoryLang category = categoryMap.get(dto.getSecondaryCategoryId());
                if (category == null) {
                    category = categoryMap.get(dto.getCategoryId());
                }
                if (category != null) {
                    dto.setCategoryName(category.getName());
                }
            }

//            DetachedCriteria langCriteria = DetachedCriteria.forClass(SystemGoodsLang.class);
//            // 给criteria添加条件，引号中status为实体中的字段，后一个为值
//            langCriteria.add(Restrictions.in("goodsId", goodsIdList));
//            langCriteria.add(Restrictions.eq("lang", "en"));
//            List<SystemGoodsLang> langList = (List<SystemGoodsLang>) this.getHibernateTemplate().findByCriteria(langCriteria);
//            Map<String, SystemGoodsLang> langMap = langList.stream().collect(Collectors.toMap(SystemGoodsLang::getGoodsId, entity -> entity, (value1, value2) -> {
//                return value1;
//            }));
//            for (SystemGoodListDto dto : resultList) {
//                SystemGoodsLang lang = langMap.get(dto.getId());
//                if (lang != null) {
//                    dto.setGoodName(lang.getName());
//                }
//            }
        }

        page.setElements(resultList);
        page.setTotalElements(totalCount);
        return page;
    }

//    public  Page  pageQuery(int pageNo, int pageSize, String name, Integer isShelf, String categoryId,String id ,Integer updateStatusPara){
//        StringBuffer queryString=new StringBuffer("SELECT sg.UUID AS 'id' ,sg.UPDATE_STATUS,sg.IS_SHELF,sg.IMG_URL_1,sg.CATEGORY_ID,sg.CREATE_TIME,gc.categoryName,sg.SYSTEM_PRICE,sgl.`NAME` AS 'goodName'  FROM T_MALL_SYSTEM_GOODS   sg LEFT JOIN (SELECT *  FROM T_MALL_SYSTEM_GOODS_LANG WHERE type=0  AND LANG='en') sgl ON sg.UUID=sgl.GOODS_ID \n" +
//                "LEFT JOIN (SELECT NAME AS 'categoryName', CATEGORY_ID categoryId FROM T_MALL_CATEGORY_LANG WHERE LANG = 'en') gc    ON sg.CATEGORY_ID = gc.categoryId WHERE 1=1 ");
//        StringBuffer countQueryString=new StringBuffer("SELECT count(*)  FROM T_MALL_SYSTEM_GOODS   sg LEFT JOIN (SELECT *  FROM T_MALL_SYSTEM_GOODS_LANG WHERE type=0  AND LANG='en') sgl ON sg.UUID=sgl.GOODS_ID \n" +
//                "LEFT JOIN (SELECT NAME AS 'categoryName', CATEGORY_ID categoryId FROM T_MALL_CATEGORY_LANG WHERE LANG = 'en') gc    ON sg.CATEGORY_ID = gc.categoryId WHERE 1=1 ");
//        if (!StringUtils.isNullOrEmpty(name)) {
//            queryString.append(" AND sgl.NAME like '%"+name+"%'");
//            countQueryString.append(" AND sgl.NAME like '%"+name+"%'");
//        }
//        if (!StringUtils.isNullOrEmpty(categoryId)) {
//            queryString.append(" AND sg.CATEGORY_ID ='"+categoryId+"'");
//            countQueryString.append(" AND sg.CATEGORY_ID ='"+categoryId+"'");
//        }
//
//        if (!StringUtils.isNullOrEmpty(id)) {
//            queryString.append(" AND sg.UUID ='"+id+"'");
//            countQueryString.append(" AND sg.UUID ='"+id+"'");
//        }
//        if (isShelf!=null) {
//            queryString.append(" AND sg.IS_SHELF ="+isShelf.intValue()+"");
//            countQueryString.append(" AND sg.IS_SHELF ="+isShelf.intValue()+"");
//        }
//        if (updateStatusPara!=null) {
//            queryString.append(" AND sg.UPDATE_STATUS ="+updateStatusPara.intValue()+"");
//            countQueryString.append(" AND sg.UPDATE_STATUS ="+updateStatusPara.intValue()+"");
//        }
//
//        queryString.append(" ORDER BY sg.CREATE_TIME DESC ");
//        queryString.append("   limit " + (pageNo - 1) * pageSize + "," + pageSize);
//        List list=jdbcTemplate.queryForList(queryString.toString());
//        int totalCount = jdbcTemplate.queryForObject(countQueryString.toString(), Integer.class);
//        Iterator iterator=  list.iterator();
//        List<SystemGoodListDto> resultList=new ArrayList<>();
//        while (iterator.hasNext()) {
//            Map rowMap = (Map) iterator.next();
//            String gid = (String) rowMap.getOrDefault("id","");
//            String goodName = (String)  rowMap.getOrDefault("goodName","");
//            String ci = (String) rowMap.getOrDefault("CATEGORY_ID","");
//            Integer shelf = (Integer) rowMap.getOrDefault("IS_SHELF","0");
//            String imgUrl1 = (String) rowMap.getOrDefault("IMG_URL_1","");
//            Double systemPrice = (Double) rowMap.getOrDefault("SYSTEM_PRICE","0");
//            LocalDateTime createTime = (LocalDateTime) rowMap.getOrDefault("CREATE_TIME","0");
//            Integer updateStatus = (Integer) rowMap.getOrDefault("UPDATE_STATUS","0");
//
//            SystemGoodListDto dto=new SystemGoodListDto();
//            dto.setCategoryId(ci);
//            dto.setGoodName(goodName);
//            dto.setImgUrl1(imgUrl1);
//            dto.setIsShelf(shelf);
//            dto.setSystemPrice(systemPrice);
//            dto.setId(gid);
//            dto.setUpdateStatus(updateStatus);
//            Date date = Date.from( createTime.atZone( ZoneId.systemDefault()).toInstant());
//            dto.setCreateTime(DateUtils.format(date,DateUtils.NORMAL_DATE_FORMAT));
//            resultList.add(dto);
//        }
//        Page page=new Page();
//        page.setElements(resultList);
//        page.setTotalElements(totalCount);
//        return  page;
//    }

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name, Integer isShelf, String startTime, String endTime, String PName) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" g.UUID id, l.NAME name, l.LANG lang, g.GOODS_SORT sort, g.CATEGORY_ID categoryId, g.CREATE_TIME createTime, ");
        queryString.append(" c.PName, g.SYSTEM_PRICE systemPrice, g.IS_SHELF isShelf, g.BUY_MIN buyMin "); //g.REC_TIME recTime, g.NEW_TIME newTime,
        queryString.append(" FROM ");
        queryString.append(" T_MALL_SYSTEM_GOODS g ");
        queryString.append(" LEFT JOIN ( SELECT NAME AS PName, CATEGORY_ID categoryId FROM T_MALL_CATEGORY_LANG WHERE LANG = 'cn' ) c ON g.CATEGORY_ID = c.categoryId ");
        queryString.append(" LEFT JOIN T_MALL_SYSTEM_GOODS_LANG l ON g.UUID = l.GOODS_ID ");
        queryString.append(" WHERE 1=1 and IFNULL(l.LANG, 'en') = 'en' and l.TYPE = 0 ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(name)) {
            queryString.append(" AND l.NAME like:name ");
            parameters.put("name", "%" + name + "%");
        }
        if (!StringUtils.isNullOrEmpty(PName)) {
            queryString.append(" AND c.PName like :PName ");
            parameters.put("PName", "%" + PName + "%");
        }
        if (-2 != isShelf) {
            queryString.append(" AND g.IS_SHELF =:isShelf ");
            parameters.put("isShelf", isShelf);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(g.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(g.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY g.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Page pagedQuery1(int pageNo, int pageSize, String name, Integer isShelf, String startTime, String endTime, String PName) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" g.UUID id, g.GOODS_SORT sort, g.CATEGORY_ID categoryId, g.CREATE_TIME createTime, ");
        queryString.append(" CASE WHEN IFNULL(g.CN_TITLE, '') <> '' THEN g.CN_TITLE WHEN IFNULL(g.EN_TITLE, '') <> '' THEN g.EN_TITLE ELSE g.ZH_TITLE END AS name,");
        queryString.append(" CASE WHEN IFNULL(g.CN_TITLE, '') <> '' THEN 'cn' WHEN IFNULL(g.EN_TITLE, '') <> '' THEN 'en' ELSE 'tw' END AS lang,");
        queryString.append(" c.NAME PName, g.SYSTEM_PRICE systemPrice, g.IS_SHELF isShelf, g.BUY_MIN buyMin "); //g.REC_TIME recTime, g.NEW_TIME newTime,
        queryString.append(" FROM ");
        queryString.append(" T_MALL_SYSTEM_GOODS g ");
        queryString.append(" LEFT JOIN T_MALL_CATEGORY_LANG c ON g.CATEGORY_ID = c.CATEGORY_ID ");
        queryString.append(" WHERE IFNULL(g.MULTI_LANGS, '') <> '' ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(name)) {
            queryString.append(" AND (g.CN_TITLE like :name or g.EN_TITLE like :name or g.ZH_TITLE like :name) ");
            parameters.put("name", "%" + name + "%");
        }
        if (!StringUtils.isNullOrEmpty(PName)) {
            queryString.append(" AND c.NAME like :PName ");
            parameters.put("PName", "%" + PName + "%");
        } else {
            queryString.append(" AND c.LANG = 'cn' ");
        }
        if (-2 != isShelf) {
            queryString.append(" AND g.IS_SHELF =:isShelf ");
            parameters.put("isShelf", isShelf);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(g.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(g.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY g.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public void save(String name, String categoryId) {
        // 待取代 TODO
        // List<SystemGoodsLang> goodsLan = findGoodsByName1(name, "cn", 0);
        List<SystemGoodsLang> goodsLan = findGoodsByName(name, "cn", 0);
        if (CollectionUtils.isNotEmpty(goodsLan)) {
            throw new BusinessException("商品名称已存在");
        }

        SystemGoods goods = new SystemGoods();
        goods.setSystemPrice(0D);
        goods.setCategoryId(categoryId);
        goods.setCreateTime(new Date());
        goods.setGoodsSort(0);
        goods.setFreightAmount(0D);
        goods.setGoodsTax(0D);
        goods.setIsShelf(0);
        goods.setIsRefund(0);
        goods.setBuyMin(1);
        goods.setUpTime(System.currentTimeMillis());

        // 2023-12-16 新增
        goods.setCnTitle(name);
        this.getHibernateTemplate().save(goods);

        SystemGoodsLang goodsLang = new SystemGoodsLang();
        goodsLang.setName(name);
        // goodsLang.setLang("en"); // 2023-12-16 改成 cn
        goodsLang.setLang("cn");
        goodsLang.setType(0);
        goodsLang.setGoodsId(goods.getId().toString());
        getHibernateTemplate().save(goodsLang);
        redisHandler.setSyncString(MallRedisKeys.MALL_GOODS_LANG + goodsLang.getLang() + ":" + goods.getId(), JSON.toJSONString(goodsLang));
    }

    @Override
    public SystemGoods findById(String goodsId) {
        return this.getHibernateTemplate().get(SystemGoods.class, goodsId);
    }

    @Override
    public List<SystemGoodsLang> findLanByGoodsId(String goodsId, String lang) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(SystemGoodsLang.class);
        criteria.add(Restrictions.eq("goodsId", goodsId));
        if (!StringUtils.isEmptyString(lang)) {
            criteria.add(Restrictions.eq("lang", lang));
        }
        if (CollectionUtils.isNotEmpty(criteria.list())) {
            return criteria.list();
        }
        return null;
    }

    @Override
    public void update(SystemGoods bean, String name, String lang, String content, String content1, String unit, String goodsLanId, String attributeId) {
        List<SystemGoodsLang> lanByGoodsId = this.findLanByGoodsId(bean.getId().toString(), lang);
        SystemGoodsLang goodsLang = new SystemGoodsLang();
        if (CollectionUtils.isEmpty(lanByGoodsId)) {
            goodsLang = new SystemGoodsLang();
            goodsLang.setName(name);
            goodsLang.setDes(content);
            goodsLang.setImgDes(content1);
            goodsLang.setType(0);
            goodsLang.setUnit(unit);
            goodsLang.setGoodsId(bean.getId().toString());
            goodsLang.setLang(lang);
            getHibernateTemplate().save(goodsLang);
        } else {
            goodsLang = lanByGoodsId.get(0);
            goodsLang.setUnit(unit);
            goodsLang.setDes(content);
            goodsLang.setImgDes(content1);
            goodsLang.setName(name);
            getHibernateTemplate().update(goodsLang);
        }
        redisHandler.setSyncString(MallRedisKeys.MALL_GOODS_LANG + goodsLang.getLang() + ":" + bean.getId().toString(), JSON.toJSONString(goodsLang));
        goodsAttributeService.saveGenerateSku(bean.getId().toString(), attributeId);
        this.getHibernateTemplate().update(bean);
    }

    public void update1(SystemGoods bean, String name, String lang, String content, String content1, String unit, String goodsLanId, String attributeId) {
        List<SystemGoodsLang> lanByGoodsId = this.findLanByGoodsId(bean.getId().toString(), lang);
        SystemGoodsLang goodsLang = new SystemGoodsLang();
        if (CollectionUtils.isEmpty(lanByGoodsId)) {
            goodsLang = new SystemGoodsLang();
            goodsLang.setName(name);
            goodsLang.setDes(content);
            goodsLang.setImgDes(content1);
            goodsLang.setType(0);
            goodsLang.setUnit(unit);
            goodsLang.setGoodsId(bean.getId().toString());
            goodsLang.setLang(lang);
            getHibernateTemplate().save(goodsLang);
        } else {
            goodsLang = lanByGoodsId.get(0);
            goodsLang.setUnit(unit);
            goodsLang.setDes(content);
            goodsLang.setImgDes(content1);
            goodsLang.setName(name);
            getHibernateTemplate().update(goodsLang);
        }
        redisHandler.setSyncString(MallRedisKeys.MALL_GOODS_LANG + goodsLang.getLang() + ":" + bean.getId().toString(), JSON.toJSONString(goodsLang));
        goodsAttributeService.saveGenerateSku(bean.getId().toString(), attributeId);
        this.getHibernateTemplate().update(bean);
    }


    @Override
    public void updateStatus(String id, int status, int type, int duration) {
        SellerGoods goods = this.findSellerGoodsById(id);

        if (goods.getIsShelf() == 0) {
            throw new BusinessException("商家已下架该商品，请先上架该商品");
        }
        if (goods.getIsValid() == 0) {
            throw new BusinessException("平台商品库已下架该商品，请先上架该商品");
        }

        switch (type) {

            case 1:
                if (status != 0) {
                    goods.setSystemRecTime(new Date().getTime());
                    goods.setSysRecDuration(duration);
                    goods.setUpTime(new Date().getTime());
                } else {
                    goods.setSystemRecTime(0L);
                    goods.setUpTime(new Date().getTime());
                }
                break;

            case 2:
                if (status != 0) {
                    goods.setSystemNewTime(new Date().getTime());
                    goods.setSysNewDuration(duration);
                } else {
                    goods.setSystemNewTime(0L);
                }
                break;

            case 3:
                if (status != 0) {
                    goods.setSellWellTime(new Date().getTime());
                    goods.setSellWellDuration(duration);
                } else {
                    goods.setSellWellTime(0L);
                }
                break;
        }

        this.getHibernateTemplate().update(goods);
    }

    @Override
    public SellerGoods findSellerGoodsById(String id) {
        SellerGoods entity = this.getHibernateTemplate().get(SellerGoods.class, id);
        if (entity == null) {
            return entity;
        }

//        String skuInfoJson = entity.getSkuInfo();
//        if (StrUtil.isNotBlank(skuInfoJson)) {
//            TypeReference paramType = new TypeReference<List<SellerGoodsSkuInfoVO>>() {
//            };
//            List<SellerGoodsSkuInfoVO> skuInfoList = (List<SellerGoodsSkuInfoVO>) JsonUtils.readValue(skuInfoJson.trim(), paramType);
//            entity.setSkuInfoList(skuInfoList);
//        }

        return entity;
    }

    @Override
    public void delete(String id, List<SystemGoodsLang> goodsLangs) {
        SystemGoods goods = this.findById(id);
        if (goods == null) {
            throw new BusinessException("商品不存在!");
        }
        getHibernateTemplate().delete(goods);
        if (CollectionUtils.isNotEmpty(goodsLangs)) {
            goodsLangs.forEach(e -> {
                e.setType(1);
                getHibernateTemplate().update(e);
                redisHandler.setSyncString(MallRedisKeys.MALL_GOODS_LANG + e.getLang() + ":" + goods.getId(), JSON.toJSONString(e));
            });
        }

    }

    /**
     * 商家店铺商品列表
     *
     * @param pageNo
     * @param pageSize
     * @param goodId
     * @param goodName
     * @param PName
     * @return
     */
//    @Override
//    public Page pagedQuerySellerGoods(int pageNo, int pageSize, String goodId, String goodName, String PName,
//                                      String sellerName, String categoryId, String secondaryCategoryId, String loginPartyId) {
//        Page page = new Page();
//
//        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
//        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
//        categoryQuery.add(Property.forName("type").eq(1));
//        categoryQuery.add(Property.forName("status").eq(1));
//        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
//        List<String> validCategoryIdList = validCategoryList.stream()
//                // 只保留二级分类id
//                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
//                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(validCategoryIdList)) {
//            //page.setElements(new ArrayList());
//            //page.setTotalElements(0);
//            //return page;
//        }
//
//        StringBuffer categoryIds = new StringBuffer("(");
//        for (String oneCategoryId : validCategoryIdList) {
//            categoryIds.append("'").append(oneCategoryId).append("',");
//        }
//        categoryIds.deleteCharAt(categoryIds.length() - 1);
//        categoryIds.append(")");
//
//        StringBuffer queryString = new StringBuffer();
//        queryString.append(" SELECT ");
//        queryString.append(" g.UUID id, l.NAME name, g.CATEGORY_ID categoryId, g.SECONDARY_CATEGORY_ID secondaryCategoryId, s.NAME sellerName, g.SYSTEM_PRICE systemPrice, g.GOODS_ID goodsId , g.BUY_MIN buyMin, g.SELLER_ID sellerId, g.SELL_WELL_TIME sellWellTime, ");
//        queryString.append(" g.SOLD_NUM soldNum, g.SELLING_PRICE sellingPrice, g.SYSTEM_NEW_TIME systemNewTime, g.SYSTEM_REC_TIME systemRecTime, g.SELLER_ID sellerId, g.REC_TIME recTime, g.NEW_TIME newTime, g.IS_SHELF isShelf, ");
//        queryString.append(" g.SYS_NEW_DURATION sysNewDuration, g.SYS_REC_DURATION sysRecDuration, g.SELL_WELL_DURATION sellWellDuration ");
//        queryString.append(" FROM ");
//        queryString.append(" T_MALL_SELLER_GOODS g FORCE INDEX (idx_create_time) ");
//        queryString.append(" LEFT JOIN T_MALL_SYSTEM_GOODS_LANG l ON g.GOODS_ID = l.GOODS_ID ");
//        queryString.append(" LEFT JOIN T_MALL_SELLER s ON g.SELLER_ID = s.UUID ");
//        queryString.append(" WHERE 1=1 and IFNULL(l.LANG, 'en') = 'en' and l.TYPE = 0 and g.IS_VALID = 1 and s.status = 1");
//        Map<String, Object> parameters = new HashMap<String, Object>();
//        if (!StringUtils.isNullOrEmpty(goodName)) {
//            queryString.append(" AND l.NAME like:goodName ");
//            parameters.put("goodName", "%" + goodName + "%");
//        }
//        if (!StringUtils.isNullOrEmpty(goodId)) {
//            queryString.append(" AND g.GOODS_ID =:goodId ");
//            parameters.put("goodId", goodId);
//        }
//        if (!StringUtils.isNullOrEmpty(PName)) {
//            queryString.append(" AND c.PName like :PName ");
//            parameters.put("PName", PName);
//        }
//        if (!StringUtils.isNullOrEmpty(sellerName)) {
//            queryString.append(" AND trim(replace(s.NAME,' ','')) like:sellerName ");
//            sellerName = sellerName.replace(" ", "");
//            parameters.put("sellerName", "%" + sellerName + "%");
//        }
//        if (StrUtil.isNotBlank(categoryId) && !Objects.equals(categoryId, "0")) {
//            queryString.append(" AND g.CATEGORY_ID = :categoryId ");
//            parameters.put("categoryId", categoryId);
//        }
//        if (StrUtil.isNotBlank(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
//            queryString.append(" AND g.SECONDARY_CATEGORY_ID = :secondaryCategoryId ");
//            parameters.put("secondaryCategoryId", secondaryCategoryId);
//        }
//
//        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
//            List children = this.userRecomService.findChildren(loginPartyId);
//            if (children.size() == 0) {
////				return Page.EMPTY_PAGE;
//                return new Page();
//            }
//            queryString.append(" and s.UUID in (:children) ");
//            parameters.put("children", children);
//        }
////        else { // caster 分类过滤延迟生效
////            queryString.append(" AND g.SECONDARY_CATEGORY_ID in :categoryIds ");
////            parameters.put("categoryIds", categoryIds);
////        }
//
//        queryString.append(" ORDER BY g.CREATE_TIME DESC ");
//        page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//
//        List<Map<String, Object>> pageList = page.getElements();
//        if (CollectionUtil.isEmpty(pageList)) {
//            return page;
//        }
//
//        List<String> categoryIdList = new ArrayList();
//        //List<String> secondaryCategoryIdList = new ArrayList();
//        for (Map<String, Object> oneRecord : pageList) {
//            Object currentCategoryIdInfo = oneRecord.get("categoryId");
//            Object currentSecondaryCategoryIdInfo = oneRecord.get("secondaryCategoryId");
//
//            if (currentCategoryIdInfo != null && !currentCategoryIdInfo.toString().isEmpty()) {
//                categoryIdList.add(currentCategoryIdInfo.toString());
//            }
//            if (currentSecondaryCategoryIdInfo != null && !currentSecondaryCategoryIdInfo.toString().isEmpty()) {
//                categoryIdList.add(currentSecondaryCategoryIdInfo.toString());
//            }
//        }
//
//        List<CategoryLang> langList = categoryLangService.listCategoryLang(categoryIdList, "cn");
//        Map<String, CategoryLang> langMap = langList.stream().collect(Collectors.toMap(entity -> entity.getId().toString(), Function.identity(), (key1, key2) -> key2));
//        for (Map<String, Object> oneRecord : pageList) {
//            Object currentCategoryIdInfo = oneRecord.get("categoryId");
//            Object currentSecondaryCategoryIdInfo = oneRecord.get("secondaryCategoryId");
//            String currentCategoryId = "0";
//            String currentSecondayCategoryId = "0";
//
//            String categoryName = "";
//            if (currentCategoryIdInfo != null) {
//                currentCategoryId = currentCategoryIdInfo.toString();
//            }
//            if (currentSecondaryCategoryIdInfo != null) {
//                currentSecondayCategoryId = currentSecondaryCategoryIdInfo.toString();
//            }
//
////            CategoryLang currentLang = langMap.get(currentSecondayCategoryId);
////            if (currentLang == null) {
////                // 优先使用二级分类的名称
////                currentLang = langMap.get(currentCategoryId);
////            }
////            if (currentLang != null) {
////                oneRecord.put("PName", currentLang.getName());
////            }
//            // 分类名称使用格式：[一级分类名] - [二级分类名]
//            CategoryLang topCategoryLang = langMap.get(currentCategoryId);
//            if (topCategoryLang != null) {
//                categoryName = topCategoryLang.getName();
//            }
//            CategoryLang secondaryCategoryLang = langMap.get(currentSecondayCategoryId);
//            if (secondaryCategoryLang != null) {
//                categoryName = categoryName + " - " + secondaryCategoryLang.getName();
//            }
//            oneRecord.put("PName", categoryName);
//        }
//
//        return page;
//    }
    public Page pagedQuerySellerGoods(int pageNo, int pageSize, String goodId, String goodName, String PName, String sellerName,
                                      String categoryId, String secondaryCategoryId, String loginPartyId, String sellerRoleName) {
        Page page = new Page();

        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
        categoryQuery.add(Property.forName("type").eq(1));
        categoryQuery.add(Property.forName("status").eq(1));
        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
        List<String> validCategoryIdList = validCategoryList.stream()
                // 只保留二级分类id
                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(validCategoryIdList)) {
            //page.setElements(new ArrayList());
            //page.setTotalElements(0);
            //return page;
        }

        StringBuffer categoryIds = new StringBuffer("(");
        for (String oneCategoryId : validCategoryIdList) {
            categoryIds.append("'").append(oneCategoryId).append("',");
        }
        categoryIds.deleteCharAt(categoryIds.length() - 1);
        categoryIds.append(")");

        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" g.UUID id, COALESCE(l.EN_TITLE, l.CN_TITLE, l.ZH_TITLE) as name, g.CATEGORY_ID categoryId, g.SECONDARY_CATEGORY_ID secondaryCategoryId, s.NAME sellerName, g.SYSTEM_PRICE systemPrice, g.GOODS_ID goodsId , g.BUY_MIN buyMin, g.SELL_WELL_TIME sellWellTime, ");
        queryString.append(" g.SOLD_NUM soldNum, g.SELLING_PRICE sellingPrice, g.SYSTEM_NEW_TIME systemNewTime, g.SYSTEM_REC_TIME systemRecTime, g.SELLER_ID sellerId, g.REC_TIME recTime, g.NEW_TIME newTime, g.IS_SHELF isShelf, ");
        queryString.append(" g.SYS_NEW_DURATION sysNewDuration, g.SYS_REC_DURATION sysRecDuration, g.SELL_WELL_DURATION sellWellDuration, p.ROLENAME sellerRoleName,g.CHECK_FLAG checkFlag ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_SELLER_GOODS g FORCE INDEX (idx_create_time) ");
        //queryString.append(" LEFT JOIN T_MALL_SYSTEM_GOODS_LANG l ON g.GOODS_ID = l.GOODS_ID ");
        //queryString.append(" LEFT JOIN T_MALL_SELLER s ON g.SELLER_ID = s.UUID ");
        //queryString.append(" WHERE 1=1 and IFNULL(l.LANG, 'en') = 'en' and l.TYPE = 0 and g.IS_VALID = 1 and s.status = 1");
        queryString.append(" INNER JOIN T_MALL_SYSTEM_GOODS l ON g.GOODS_ID = l.UUID ");
        queryString.append(" INNER JOIN T_MALL_SELLER s ON g.SELLER_ID = s.UUID ");
        queryString.append(" INNER JOIN PAT_PARTY p ON s.UUID = p.UUID ");
        queryString.append(" WHERE 1=1 and g.IS_VALID = 1 and s.status = 1");
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(goodName)) {
            queryString.append(" AND (l.CN_TITLE like:goodName OR l.EN_TITLE like:goodName OR l.ZH_TITLE like:goodName) ");
            parameters.put("goodName", "%" + goodName + "%");
        }
        if (!StringUtils.isNullOrEmpty(goodId)) {
            queryString.append(" AND g.GOODS_ID =:goodId ");
            parameters.put("goodId", goodId);
        }
        // 2023-12-16 caster注释， PName 是谁的字段？
//        if (!StringUtils.isNullOrEmpty(PName)) {
//            queryString.append(" AND c.PName like :PName ");
//            parameters.put("PName", PName);
//        }
        if (!StringUtils.isNullOrEmpty(sellerRoleName)) {
            queryString.append(" AND p.ROLENAME =:sellerRoleName ");
            parameters.put("sellerRoleName", sellerRoleName);
        }
        if (!StringUtils.isNullOrEmpty(sellerName)) {
            queryString.append(" AND trim(replace(s.NAME,' ','')) like:sellerName ");
            sellerName = sellerName.replace(" ", "");
            parameters.put("sellerName", "%" + sellerName + "%");
        }
        if (StrUtil.isNotBlank(categoryId) && !Objects.equals(categoryId, "0")) {
            queryString.append(" AND g.CATEGORY_ID = :categoryId ");
            parameters.put("categoryId", categoryId);
        }
        if (StrUtil.isNotBlank(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
            queryString.append(" AND g.SECONDARY_CATEGORY_ID = :secondaryCategoryId ");
            parameters.put("secondaryCategoryId", secondaryCategoryId);
        }

        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            List children = this.userRecomService.findChildren(loginPartyId);
            if (children.size() == 0) {
//				return Page.EMPTY_PAGE;
                return new Page();
            }
            queryString.append(" and s.UUID in (:children) ");
            parameters.put("children", children);
        }
//        else { // caster 分类过滤延迟生效
//            queryString.append(" AND g.SECONDARY_CATEGORY_ID in :categoryIds ");
//            parameters.put("categoryIds", categoryIds);
//        }

        queryString.append(" ORDER BY g.CREATE_TIME DESC ");
        page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

        List<Map<String, Object>> pageList = page.getElements();
        if (CollectionUtil.isEmpty(pageList)) {
            return page;
        }

        List<String> categoryIdList = new ArrayList();
        //List<String> secondaryCategoryIdList = new ArrayList();
        for (Map<String, Object> oneRecord : pageList) {
            Object currentCategoryIdInfo = oneRecord.get("categoryId");
            Object currentSecondaryCategoryIdInfo = oneRecord.get("secondaryCategoryId");

            if (currentCategoryIdInfo != null && !currentCategoryIdInfo.toString().isEmpty()) {
                categoryIdList.add(currentCategoryIdInfo.toString());
            }
            if (currentSecondaryCategoryIdInfo != null && !currentSecondaryCategoryIdInfo.toString().isEmpty()) {
                categoryIdList.add(currentSecondaryCategoryIdInfo.toString());
            }
        }

        List<CategoryLang> langList = categoryLangService.listCategoryLang(categoryIdList, "cn");
        Map<String, CategoryLang> langMap = langList.stream().collect(Collectors.toMap(entity -> entity.getCategoryId(), Function.identity(), (key1, key2) -> key2));
        for (Map<String, Object> oneRecord : pageList) {
            Object currentCategoryIdInfo = oneRecord.get("categoryId");
            Object currentSecondaryCategoryIdInfo = oneRecord.get("secondaryCategoryId");
            String currentCategoryId = "0";
            String currentSecondayCategoryId = "0";

            String categoryName = "";
            if (currentCategoryIdInfo != null) {
                currentCategoryId = currentCategoryIdInfo.toString();
            }
            if (currentSecondaryCategoryIdInfo != null) {
                currentSecondayCategoryId = currentSecondaryCategoryIdInfo.toString();
            }

//            CategoryLang currentLang = langMap.get(currentSecondayCategoryId);
//            if (currentLang == null) {
//                // 优先使用二级分类的名称
//                currentLang = langMap.get(currentCategoryId);
//            }
//            if (currentLang != null) {
//                oneRecord.put("PName", currentLang.getName());
//            }
            // 分类名称使用格式：[一级分类名] - [二级分类名]
            CategoryLang topCategoryLang = langMap.get(currentCategoryId);
            if (topCategoryLang != null) {
                categoryName = topCategoryLang.getName();
            }
            CategoryLang secondaryCategoryLang = langMap.get(currentSecondayCategoryId);
            if (secondaryCategoryLang != null) {
                categoryName = categoryName + " - " + secondaryCategoryLang.getName();
            }
            oneRecord.put("PName", categoryName);
            log.info("---> AdminMallGoodsServiceImpl.pagedQuerySellerGoods 商品类型:{} -{} 的名称为:{}", currentCategoryId, currentSecondayCategoryId, categoryName);
        }

        return page;
    }

    @Override
    public Page pagedQueryEvaluation(int pageNo, int pageSize, String sellerGoodsId, String sellerId, String userName, Integer evaluationType) {

        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" e.USER_NAME userName, e.SELLER_GOODS_ID sellerGoodsId, e.SELLER_ID sellerId, e.EVALUATION_TYPE evaluationType, ");
        queryString.append(" e.RATING rating, e.CREATE_TIME createTime, s.NAME sellerName, e.CONTENT content, e.UUID id, e.STATUS status, ");
        queryString.append(" e.IMG_URL_1 imgUrl1, e.IMG_URL_2 imgUrl2, e.IMG_URL_3 imgUrl3, e.IMG_URL_4 imgUrl4, e.IMG_URL_5 imgUrl5, e.IMG_URL_6 imgUrl6,e.IMG_URL_7 imgUrl7, ");
        queryString.append(" e.IMG_URL_8 imgUrl8, e.IMG_URL_9 imgUrl9");
        queryString.append(" FROM T_MALL_EVALUATION e ");
        queryString.append(" LEFT JOIN T_MALL_SELLER s ON e.SELLER_ID = s.UUID ");
        queryString.append(" WHERE 1 = 1 ");
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(sellerGoodsId)) {
            queryString.append(" AND e.SELLER_GOODS_ID =:sellerGoodsId ");
            parameters.put("sellerGoodsId", sellerGoodsId);
        }
        if (!StringUtils.isNullOrEmpty(userName)) {
            queryString.append(" AND e.USER_NAME =:userName ");
            parameters.put("userName", userName);
        }
        if (-2 != evaluationType) {
            queryString.append(" AND e.EVALUATION_TYPE =:evaluationType ");
            parameters.put("evaluationType", evaluationType);
        }
        queryString.append(" ORDER BY e.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Integer getSystemGoodsNum(String lang) {
        return this.jdbcTemplate.queryForObject(
                "SELECT DISTINCT " +
                        "COUNT( distinct m.UUID)   " +
                        "FROM " +
                        "T_MALL_SYSTEM_GOODS m " +
                        "LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.UUID = s.GOODS_ID  " +
                        "WHERE " +
                        "s.LANG = '" + lang + "'  " +
                        "AND s.TYPE = 0 and m.IS_SHELF = 1 ", Integer.class);
    }

    @Override
    public Evaluation findEvaluationById(String id) {
        return this.getHibernateTemplate().get(Evaluation.class, id);
    }

    @Override
    public void deleteEvaluation(Evaluation e) {
        this.getHibernateTemplate().delete(e);
    }

    private List<SystemGoodsLang> findGoodsByName(String name, String lang, int type) {

        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(SystemGoodsLang.class);
        criteria.add(Restrictions.eq("name", name));
        criteria.add(Restrictions.eq("lang", lang));
        criteria.add(Restrictions.eq("type", 0));
        return criteria.list();
    }

    private List<SystemGoodsLang> findGoodsByName1(String name, String lang, int type) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(SystemGoods.class);
        if (lang.equals(LanguageEnum.CN.getLang())) {
            criteria.add(Restrictions.eq("cnTitle", name));
        } else if (lang.equals(LanguageEnum.EN.getLang())) {
            criteria.add(Restrictions.eq("enTitle", name));
        } else if (lang.equals(LanguageEnum.TW.getLang())) {
            criteria.add(Restrictions.eq("zhTitle", name));
        } else {
            throw new RuntimeException("未指定 lang 参数");
        }

        criteria.add(Restrictions.eq("type", 0));
        return criteria.list();
    }

    @Override
    public List<SellerGoods> findSellerGoodsBySystemGoodsId(String goodId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoods.class);
        criteria.add(Restrictions.eq("goodsId", goodId));
        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(criteria);
    }

    @Override
    public List<SystemGoods> findGoodsByCategoryId(String categoryId) {
//        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(SystemGoods.class);
//        criteria.add(Restrictions.eq("categoryId", categoryId));
//        if (CollectionUtils.isNotEmpty(criteria.list())) {
//            return criteria.list();
//        }
//
//        return null;

        // 这能行？ TODO
        String sql = "SELECT * FROM T_MALL_SYSTEM_GOODS WHERE CATEGORY_ID = '" + categoryId + "' or SECONDARY_CATEGORY_ID = '" + categoryId + "' ";
        // this.jdbcTemplate.queryForList(sql, SystemGoods.class); // 报错，此种情况下每个列都被当做一个对象解析，出现几十个列值的情况，而不是一个 SystemGoods 对象
        List<Map<String, Object>> rowList = (List<Map<String, Object>>)this.jdbcTemplate.queryForList(sql);
        return null;// TODO
    }

    @Override
    public int getGoodsCountByCategoryId(String categoryId) {
        String sql = "SELECT IFNULL(count(*), 0) FROM T_MALL_SYSTEM_GOODS WHERE CATEGORY_ID = '" + categoryId + "' or SECONDARY_CATEGORY_ID = '" + categoryId + "' ";
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public void updateBuyMin(String sellerGoodsId, int buyMin) {
        SellerGoods goods = this.findSellerGoodsById(sellerGoodsId);
        goods.setBuyMin(buyMin);
        getHibernateTemplate().update(goods);
    }

    @Override
    public void updateEvaluationStatus(String id, int parseInt) {
        Evaluation e = this.findEvaluationById(id);
        e.setStatus(parseInt);
        this.getHibernateTemplate().update(e);
    }


    public void clearSku(String goodId) {
        DetachedCriteria query = DetachedCriteria.forClass(GoodsSku.class);
        query.add(Property.forName("goodId").eq(goodId));
        List<GoodsSku> list = (List<GoodsSku>) this.getHibernateTemplate().findByCriteria(query);
        HibernateTemplate template = this.getHibernateTemplate();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> {
                e.setDeleted(1);
                template.update(e);
            });
        }
    }


    @Override
    public void updateShelf(String id, Integer isShelf) {
        SystemGoods systemGoods = getHibernateTemplate().get(SystemGoods.class, id);
        if (systemGoods == null) {
            throw new BusinessException("参数错误");
        }
        if (isShelf == null) {
            throw new BusinessException("参数错误");
        }
        systemGoods.setIsShelf(isShelf);
        systemGoods.setUpTime(System.currentTimeMillis());
        getHibernateTemplate().update(systemGoods);

        sellerGoodsService.updateSellerGoodsValid(id, isShelf);
    }

    @Override
    public void updateUpdateStatus(String id, Integer updateStatus) {
        SystemGoods systemGoods = getHibernateTemplate().get(SystemGoods.class, id);
        if (systemGoods == null) {
            throw new BusinessException("参数错误");
        }
        if (updateStatus == null) {
            throw new BusinessException("参数错误");
        }
        systemGoods.setUpdateStatus(updateStatus);
        getHibernateTemplate().update(systemGoods);
    }

    @Override
    public void deleteSku(String skuId) {
        GoodsSku goodsSku = this.getHibernateTemplate().get(GoodsSku.class, skuId);
        if (null != goodsSku) {
            String goodId = goodsSku.getGoodId();
            goodsSku.setDeleted(1);
            this.getHibernateTemplate().update(goodsSku);
//            DetachedCriteria query = DetachedCriteria.forClass(SellerGoodsSku.class);
//            query.add(Property.forName("systemGoodsId").eq(goodId));
//            query.add(Property.forName("skuId").eq(skuId));
//            // TODO
//            List<SellerGoodsSku> sellerGoodsSku = (List<SellerGoodsSku>) this.getHibernateTemplate().findByCriteria(query);
//            if (CollectionUtil.isNotEmpty(sellerGoodsSku)) {
//                for (SellerGoodsSku sku : sellerGoodsSku) {
//                    sku.setIsDelete(1);
//                    this.getHibernateTemplate().update(sku);
//                }
//            }
//
//            // TODO
//            deleteSku1(goodsSku.getGoodId(), skuId);
        }

    }

    public void deleteSku1(String systemGoodsId, String skuId) {
//        List<SellerGoods> sellerGoodsList = sellerGoodsService.listSellerGoodsBySku(systemGoodsId, skuId);
//        if (CollectionUtil.isEmpty(sellerGoodsList)) {
//            return;
//        }
//
//        for (SellerGoods oneSellerGoods : sellerGoodsList) {
//            List<SellerGoodsSkuInfoVO> oldSkuInfoList = oneSellerGoods.getSkuInfoList();
//            List<SellerGoodsSkuInfoVO> newSkuInfoList = new ArrayList<>();
//            for (SellerGoodsSkuInfoVO oneSkuInfo : oldSkuInfoList) {
//                if (oneSkuInfo.getSkuId().equals(skuId)) {
//                    continue;
//                }
//                newSkuInfoList.add(oneSkuInfo);
//            }
//
//            oneSellerGoods.setSkuInfo(JsonUtils.bean2Json(newSkuInfoList));
//        }
//
//        sellerGoodsService.updateBatchGoodsSku(sellerGoodsList);
    }

    @Transactional
    @Override
    public void adminShelfBatch(List<String> sellerGoodsIdList, Integer isShelf) {
        if (CollectionUtil.isEmpty(sellerGoodsIdList) || isShelf == null) {
            log.error("批量更新商家状态，传入参数不对:{},{}", sellerGoodsIdList, isShelf);
            return;
        }
        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("sellerGoodsIdList", sellerGoodsIdList);
        params.put("isShelf", isShelf);

        String sql = "update T_MALL_SELLER_GOODS set IS_SHELF = :isShelf where UUID in (:sellerGoodsIdList)";
        nameJdbc.update(sql, params);
    }

    @Override
    public void update(SystemGoodModel systemGoodModel) {
        boolean update = false;
        BigDecimal oldPrice = new BigDecimal(0.0);
        BigDecimal newPrice = new BigDecimal(systemGoodModel.getSystemPrice());
        String oldCategoryId = null;
        String newCategoryId = systemGoodModel.getCategoryId();
        String oldSecondaryCategoryId = null;
        String newSecondaryCategoryId = systemGoodModel.getSecondaryCategoryId();
        SystemGoods systemGoods = null;
        if (StringUtils.isNotEmpty(systemGoodModel.getId())) {
            systemGoods = getHibernateTemplate().get(SystemGoods.class, systemGoodModel.getId());
            update = true;
            oldPrice = new BigDecimal(systemGoods.getSystemPrice());
            oldCategoryId = systemGoods.getCategoryId();
            oldSecondaryCategoryId = systemGoods.getSecondaryCategoryId();
        }
        oldCategoryId = StrUtil.isBlank(oldCategoryId) ? "0" : oldCategoryId;
        newCategoryId = StrUtil.isBlank(newCategoryId) ? "0" : newCategoryId;
        oldSecondaryCategoryId = StrUtil.isBlank(oldSecondaryCategoryId) ? "0" : oldSecondaryCategoryId;
        newSecondaryCategoryId = StrUtil.isBlank(newSecondaryCategoryId) ? "0" : newSecondaryCategoryId;

        if (systemGoods == null) {
            systemGoods = new SystemGoods();
            systemGoods.setCreateTime(new Date());
            //throw new BusinessException("参数错误");
        }
        systemGoods.setAttributeCategoryId(systemGoodModel.getAttributeCategoryId());
//        系统商品价格保留俩位
        systemGoods.setSystemPrice(new BigDecimal(String.valueOf(systemGoodModel.getSystemPrice())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        if (StrUtil.isBlank(systemGoodModel.getCategoryId())) {
            systemGoods.setCategoryId("0");
        } else {
            systemGoods.setCategoryId(systemGoodModel.getCategoryId());
        }
        if (StrUtil.isBlank(systemGoodModel.getSecondaryCategoryId())) {
            systemGoods.setSecondaryCategoryId("0");
        } else {
            systemGoods.setSecondaryCategoryId(systemGoodModel.getSecondaryCategoryId());
        }
        systemGoods.setGoodsSort(systemGoodModel.getGoodsSort());
        systemGoods.setFreightAmount(systemGoodModel.getFreightAmount());
        systemGoods.setFreightType(systemGoodModel.getFreightType());
        systemGoods.setGoodsTax(systemGoodModel.getGoodsTax());
        systemGoods.setIsShelf(systemGoodModel.getIsShelf());
        systemGoods.setIsRefund(systemGoodModel.getIsRefund());
        systemGoods.setRemindNum(systemGoodModel.getRemindNum());
        systemGoods.setLastAmount(systemGoodModel.getLastAmount());
        systemGoods.setUpTime(System.currentTimeMillis());
        systemGoods.setImgUrl1(systemGoodModel.getImgUrl1());
        systemGoods.setImgUrl2(systemGoodModel.getImgUrl2());
        systemGoods.setImgUrl3(systemGoodModel.getImgUrl3());
        systemGoods.setImgUrl4(systemGoodModel.getImgUrl4());
        systemGoods.setImgUrl5(systemGoodModel.getImgUrl5());
        systemGoods.setImgUrl6(systemGoodModel.getImgUrl6());
        systemGoods.setImgUrl7(systemGoodModel.getImgUrl7());
        systemGoods.setImgUrl8(systemGoodModel.getImgUrl8());
        systemGoods.setImgUrl9(systemGoodModel.getImgUrl9());
        systemGoods.setImgUrl10(systemGoodModel.getImgUrl10());
        systemGoods.setLink(systemGoodModel.getLink());
        systemGoods.setBuyMin(systemGoodModel.getBuyMin());
        systemGoods.setAttributeCategoryId(systemGoodModel.getAttributeCategoryId());

        // 填充多语言标题
        fillMultiLang(systemGoods, systemGoodModel.getLang(), systemGoodModel.getName());
        if (systemGoods.getUpdateStatus() == null) {
            systemGoods.setUpdateStatus(0);
        }

        this.getHibernateTemplate().saveOrUpdate(systemGoods);

        List<SystemGoodsLang> goodsLan = findGoodsLang(systemGoods.getId(), systemGoodModel.getLang());
        if (CollectionUtils.isNotEmpty(goodsLan)) {
            SystemGoodsLang goodsLang = goodsLan.get(0);
            goodsLang.setName(systemGoodModel.getName());
            goodsLang.setDes(systemGoodModel.getDes());
            goodsLang.setImgDes(systemGoodModel.getImgDes());
            goodsLang.setUnit(systemGoodModel.getUnit());
            getHibernateTemplate().update(goodsLang);
            redisHandler.setSyncString(MallRedisKeys.MALL_GOODS_LANG + goodsLang.getLang() + ":" + systemGoods.getId().toString(), JSON.toJSONString(goodsLang));
        } else {
            SystemGoodsLang goodsLang = new SystemGoodsLang();
            goodsLang.setName(systemGoodModel.getName());
            goodsLang.setDes(systemGoodModel.getDes());
            goodsLang.setGoodsId(systemGoods.getId());
            goodsLang.setUnit(systemGoodModel.getUnit());

            goodsLang.setImgDes(systemGoodModel.getImgDes());
            goodsLang.setLang(systemGoodModel.getLang());
            goodsLang.setType(0);
            getHibernateTemplate().save(goodsLang);
            redisHandler.setSyncString(MallRedisKeys.MALL_GOODS_LANG + goodsLang.getLang() + ":" + systemGoods.getId(), JSON.toJSONString(goodsLang));
        }
        List<SkuDto> skuDtos = systemGoodModel.getSkus();
//        clearSku(systemGoodModel.getId());
        if (CollectionUtils.isNotEmpty(systemGoodModel.getSkus())) {
            for (SkuDto skuDto : skuDtos) {
                boolean updateSku = false;
                GoodsSku goodsSku = new GoodsSku();
                String skuId = skuDto.getSkuId();
                if (StringUtils.isNotEmpty(skuId)) {
                    goodsSku.setId(skuId);
                    updateSku = true;
                }
                goodsSku.setDeleted(0);
                goodsSku.setCoverImg(skuDto.getCoverImg());
                goodsSku.setPic(JSON.toJSONString(skuDto.getImg()));
                goodsSku.setGoodId(systemGoods.getId().toString());
                List<HashMap> list = new ArrayList<>();
                for (SkuAttrDto s : skuDto.getAttrs()) {
                    HashMap<String, Object> idMap = new HashMap<>();
                    idMap.put("attrId", s.getAttrId());
                    idMap.put("attrValueId", s.getAttrValueId());
                    idMap.put("is_icon", s.isIcon());
                    idMap.put("iconImg", s.getIconImg());
                    list.add(idMap);
                }
                goodsSku.setSpData(JSON.toJSONString(list));
                goodsSku.setPrice(new BigDecimal(skuDto.getPrice()));
                if (updateSku) {
                    // 取代 TODO
                    //this.updateSellerGoodsSku(goodsSku);
                    this.getHibernateTemplate().update(goodsSku);
                    this.updateSellerGoodsSku1(goodsSku);
                } else {
                    this.getHibernateTemplate().save(goodsSku);
                }
            }
        }

        try {
            if (update && oldPrice.compareTo(newPrice) != 0) {
                if (Objects.nonNull(systemGoodModel.getId())) {
                    sellerGoodsService.updateSellerPriceByGoodsId(systemGoodModel.getId(), systemGoodModel.getSystemPrice());
                }
            }
            if (update
                    && (!oldCategoryId.equals(newCategoryId) || !oldSecondaryCategoryId.equals(newSecondaryCategoryId))) {
                this.sellerGoodsService.updateSellerGoodsCategory(systemGoods.getId(), newCategoryId, newSecondaryCategoryId);
            }
        } catch (Exception e) {
            logger.error("更新商家销售价，折扣价格失败:" + e.getMessage());
        }
    }

//    private void updateSellerGoodsSku(GoodsSku goodsSku) {
//        DetachedCriteria query = DetachedCriteria.forClass(SellerGoodsSku.class);
//        query.add(Property.forName("skuId").eq(goodsSku.getId()));
//        query.add(Property.forName("isDelete").eq(0));
//        List<SellerGoodsSku> sellerGoodsSkuList = (List<SellerGoodsSku>) this.getHibernateTemplate().findByCriteria(query);
//        if (CollectionUtil.isNotEmpty(sellerGoodsSkuList)) {
//            for (SellerGoodsSku sellerGoodsSku : sellerGoodsSkuList) {
//                BigDecimal profitRatio = new BigDecimal(sellerGoodsSku.getProfitRatio()).add(new BigDecimal(1));
//                BigDecimal discountRatio = new BigDecimal(sellerGoodsSku.getDiscountRatio());
//                BigDecimal price = goodsSku.getPrice();
//                sellerGoodsSku.setSellingPrice(profitRatio.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//                if (discountRatio.compareTo(new BigDecimal(0.0)) > 0) {
//                    discountRatio = new BigDecimal(1.0).subtract(discountRatio);
//                    sellerGoodsSku.setDiscountPrice(discountRatio.multiply(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//                }
//                sellerGoodsSku.setSystemPrice(price.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
//                this.getHibernateTemplate().update(sellerGoodsSku);
//            }
//        }
////        this.getHibernateTemplate().flush();
//        //更新缓存
//        sellerGoodsService.updateCachedSellerGoodSku(sellerGoodsSkuList);
//    }

    private void updateSellerGoodsSku1(GoodsSku goodsSku) {
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("goodsId").eq(goodsSku.getGoodId()));
        List<SellerGoods> sellerGoodsList = (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(sellerGoodsList)) {
            return;
        }
        for (SellerGoods sellerGoods : sellerGoodsList) {
            sellerGoodsService.deleteCachedSellerGoodSku1(sellerGoods);
        }
    }

    private void fillMultiLang(SystemGoods systemGoods, String lang, String title) {
        String multiLangs = StrUtil.isBlank(systemGoods.getMultiLangs()) ? "" : systemGoods.getMultiLangs().trim();
        if (StrUtil.isNotBlank(multiLangs)) {
            multiLangs = multiLangs.trim();
        } else {
            multiLangs = "";
        }
        if (multiLangs.startsWith(",")) {
            multiLangs = multiLangs.substring(1);
        }
        if (StrUtil.isNotBlank(multiLangs) && !multiLangs.endsWith(",")) {
            multiLangs = multiLangs + ",";
        }
        if (lang.equals(LanguageEnum.CN.getLang())) {
            systemGoods.setCnTitle(title);
            if (!multiLangs.contains(LanguageEnum.CN.getLang())) {
                multiLangs = multiLangs + LanguageEnum.CN.getLang();
            }
        } else if (lang.equals(LanguageEnum.EN.getLang())) {
            systemGoods.setEnTitle(title);
            if (!multiLangs.contains(LanguageEnum.EN.getLang())) {
                multiLangs = multiLangs + LanguageEnum.EN.getLang();
            }
        } else if (lang.equals(LanguageEnum.TW.getLang())) {
            systemGoods.setZhTitle(title);
            if (!multiLangs.contains(LanguageEnum.TW.getLang())) {
                multiLangs = multiLangs + LanguageEnum.TW.getLang();
            }
        } else {
            log.warn("---> AdminSystemGoodsServiceController.fillMultiLang 暂不支持该语种:{}", lang);
        }

        systemGoods.setMultiLangs(multiLangs);
    }


    public List<SystemGoodsLang> findGoodsLang(String goodId, String lang) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SystemGoodsLang.class);
        criteria.add(Restrictions.eq("goodsId", goodId));
        criteria.add(Restrictions.eq("lang", lang));
        criteria.add(Restrictions.eq("type", 0));
        return (List<SystemGoodsLang>) getHibernateTemplate().findByCriteria(criteria);
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setRedisHandler(RedisHandler redisHandler) {

        this.redisHandler = redisHandler;
    }

    public void setGoodsAttributeService(GoodsAttributeService goodsAttributeService) {

        this.goodsAttributeService = goodsAttributeService;
    }

    public void setSellerGoodsService(SellerGoodsService sellerGoodsService) {
        this.sellerGoodsService = sellerGoodsService;
    }

    public void setCategoryLangService(CategoryLangService categoryLangService) {
        this.categoryLangService = categoryLangService;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }
}
