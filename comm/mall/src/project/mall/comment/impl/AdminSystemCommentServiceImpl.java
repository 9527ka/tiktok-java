package project.mall.comment.impl;

import cn.hutool.core.util.StrUtil;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.*;
import org.springframework.beans.BeanUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.comment.AdminSystemCommentService;
import project.mall.goods.model.Evaluation;
import project.mall.goods.model.SystemComment;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;
import project.web.admin.dto.SystemCommentDto;
import project.web.admin.model.SystemCommentModel;
import util.DateUtil;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AdminSystemCommentServiceImpl extends HibernateDaoSupport implements AdminSystemCommentService {

    private PagedQueryDao pagedQueryDao;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, Integer status) {

        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" c.UUID id, c.SCORE score, c.STATUS status, c.CONTENT content, c.CREATE_TIME createTime");
        queryString.append(" FROM ");
        queryString.append(" T_SYSTEM_COMMENT c ");
        queryString.append(" WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();
        if (-2 != status) {
            queryString.append(" and c.STATUS =:status");
            parameters.put("status", status);
        }

        queryString.append(" ORDER BY c.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }


    @Override
    public Page listComment(int pageNo, int pageSize, Integer status,String systemGoodId) {
        DetachedCriteria query = DetachedCriteria.forClass(SystemComment.class);
        query.add(Property.forName("systemGoodId").eq(systemGoodId));

        if (-2 != status) {
            query.add(Property.forName("status").eq(status));
        }
        query.addOrder(Order.desc("createTime"));
        Page page =new Page();
                // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo=  MallPageInfoUtil.getMallPage(pageSize,pageNo,totalCount,getHibernateTemplate().findByCriteria(query, (pageNo - 1) * pageSize, pageSize));
        List<SystemCommentDto> resultList=new ArrayList<>();
        Iterator iterator=  mallPageInfo.getElements().iterator();
        while (iterator.hasNext()) {
            SystemComment systemComment = (SystemComment) iterator.next();
            SystemCommentDto systemCommentDto=new SystemCommentDto();

            BeanUtils.copyProperties(systemComment,systemCommentDto);
            systemCommentDto.setId(systemComment.getId().toString());
            if ( systemComment.getCreateTime()!=null){
                systemCommentDto.setCreateTime(DateUtils.format(systemComment.getCreateTime(),DateUtils.NORMAL_DATE_FORMAT));
            }
            resultList.add(systemCommentDto);
        }
        page.setElements(resultList);
        page.setTotalElements(mallPageInfo.getTotalElements());
        return page;
    }

    @Override
    public List<SystemComment> queryTop50Comments(String systemGoodId , String sellerGoodId ) {
        DetachedCriteria criteria= DetachedCriteria.forClass(SystemComment.class);

        criteria.add( Restrictions.eq("status",  0));
        criteria.add( Restrictions.eq("systemGoodId", systemGoodId));
        criteria.addOrder(Order.asc("createTime"));
        List<SystemComment> systemComments = (List<SystemComment>) getHibernateTemplate().findByCriteria(criteria, 0, 1000);

        if(CollectionUtils.isNotEmpty(systemComments)) {
            //如果这个商品已经用了这个系统评价了，下次就不能再使用了
            DetachedCriteria evaluation = DetachedCriteria.forClass(Evaluation.class);
            evaluation.add( Restrictions.eq("sellerGoodsId",  sellerGoodId));
            List<Evaluation>  evaluationList = (List<Evaluation>)getHibernateTemplate().findByCriteria(evaluation);

            if(CollectionUtils.isNotEmpty(evaluationList)) {
               List<String> systemCommentIds =  evaluationList.stream().map(Evaluation::getTemplate).collect(Collectors.toList());
                systemComments = systemComments.stream().filter(value -> !systemCommentIds.contains(value.getId())).collect(Collectors.toList());
            }
        }
        return systemComments;
    }

    @Override
    public void saveUpdate(SystemCommentModel model) {

        SystemComment systemComment=null;
        if (StrUtil.isNotEmpty(model.getId())){
            systemComment=findCommentById(model.getId());
        }
        if (systemComment==null){
            systemComment=new SystemComment();
        }
        systemComment.setContent(model.getContent());
        if (StrUtil.isNotEmpty(model.getCreateTime())){
            systemComment.setCreateTime(DateUtil.stringToDate(model.getCreateTime(),DateUtil.DATE_FORMAT));
        }
        systemComment.setImgUrl1(model.getImgUrl1());
        systemComment.setImgUrl2(model.getImgUrl2());
        systemComment.setImgUrl3(model.getImgUrl3());
        systemComment.setImgUrl4(model.getImgUrl4());
        systemComment.setImgUrl5(model.getImgUrl5());
        systemComment.setImgUrl6(model.getImgUrl6());
        systemComment.setImgUrl7(model.getImgUrl7());
        systemComment.setImgUrl8(model.getImgUrl8());
        systemComment.setImgUrl8(model.getImgUrl9());
        systemComment.setContent(model.getContent());
        systemComment.setSystemGoodId(model.getSystemGoodId());
        systemComment.setScore(model.getScore());
        systemComment.setStatus(model.getStatus());
        getHibernateTemplate().saveOrUpdate(systemComment);
    }

    @Override
    public void save(SystemComment comment) {
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        this.getHibernateTemplate().save(comment);
    }

    @Override
    public SystemComment findCommentById(String id) {
        return this.getHibernateTemplate().get(SystemComment.class, id);
    }

    @Override
    public void delete(String id) {
        if(StringUtils.isNotEmpty(id)){
            SystemComment comment = this.getHibernateTemplate().get(SystemComment.class, id);
            this.getHibernateTemplate().delete(comment);
        }
    }

    @Override
    public void deleteAll(String ids) {
       List<String>  list= StrUtil.split(ids,',');
       for (String id:list){
           delete(id);
       }
    }

    @Override
    public void updateStatus(String id, int status) {
        SystemComment comment = this.findCommentById(id);
        comment.setStatus(status);
        this.getHibernateTemplate().update(comment);

    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}
