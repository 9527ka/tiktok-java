package project.mall.credit;

import kernel.web.Page;
import project.mall.credit.model.Credit;
import project.mall.loan.model.LoanConfig;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CreditService {

    public LoanConfig queryLoanConfig();

    public void updateLoanConfig(LoanConfig loanConfig);

    public List<Credit> getOnUseCreditById(String partyId);

    //    创建贷款单
    public void saveCreate(Credit credit);

    public Credit findCreditsById(String id);

    //    分页查询我的贷款
    public Page pagedQuery(int pageNo, int pageSize, String partyId);

    //    后台分页查询贷款记录
    Page pagedQuery(int pageNo, int pageSize, String userCode_para, String username_para, String identification_para, String status_para,
                    String customerSubmitTime_para_start, String customerSubmitTime_para_end, String partyId);

    List<Credit> queryBillCredit(String partyId);

    //    更新逾期状态方法
    public Integer updateOverDue();

    void updateCreditOrder(String partyId, String creditId, String safeword);


    void updateCreditStatus(String creditId, String operateType, String rejectReason,String manualRepay,String safeword, String operator_username);

    String updateCreditPass(String creditId,String safeword, String operator_username);

    void saveCreditPic(String creditId, String imgId, String img);

    Map<String, Object> calculate(Date date, Credit credit);
}
