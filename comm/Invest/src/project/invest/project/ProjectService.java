package project.invest.project;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import project.invest.project.model.InvestOrders;
import project.invest.project.model.InvestRebate;
import project.invest.project.model.Project;

import java.util.List;

public interface ProjectService {

//    List<Category> listCategorys(String lang);


    List<Project>  listProjectSell(String baseId, int pageNum, int pageSize);

    List<Project>  listProjectHome( int pageNum, int pageSize);

    Project getProject(String projectId);


    Double updateBuyProject(String partyId,String projectId,double amount);


    List<InvestOrders>  listProjectMy(String partyId, int pageNum, int pageSize);


    JSONObject getMyInvestInfo(String partyId);



    List<InvestRebate>  listProjectIncome(String orderId, int pageNum, int pageSize);


    List<InvestRebate>  listInvestRebate(String partyId, int pageNum, int pageSize);


    JSONObject getProjectIncome(String orderId);


   List<InvestOrders> listWaiteSettlements();

    void updateSettlementsOrders(String investOrdersId);


    List<InvestRebate> listWaiteRebate();

    boolean updateRebate(String rebateId);

    JSONObject getTeamInfo(String partyId);

    JSONArray listRebateByLevel(String partyId, int level, int pageNum, int pageSize);

}
