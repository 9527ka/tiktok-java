package project.mall.loan.impl;

import org.hibernate.Criteria;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.loan.AdminMallLoanConfigService;
import project.mall.loan.model.LoanConfig;

/**
 * 借贷配置
 */
public class AdminMallLoanConfigServiceImpl extends HibernateDaoSupport implements AdminMallLoanConfigService {

    @Override
    public LoanConfig findLoanConfig() {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(LoanConfig.class);
        return (LoanConfig) criteria.list().get(0);
    }

    @Override
    public void updateById(LoanConfig model) {
        getHibernateTemplate().update(model);
    }


}
