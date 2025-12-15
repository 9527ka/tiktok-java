package project.mall.loan;

import project.mall.loan.model.LoanConfig;

public interface AdminMallLoanConfigService {

    LoanConfig findLoanConfig();

    void updateById(LoanConfig model);
}
