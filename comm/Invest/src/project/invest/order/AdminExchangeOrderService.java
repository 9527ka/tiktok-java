package project.invest.order;

import kernel.web.Page;

public interface AdminExchangeOrderService {

    /**
     * 获取 otc订单 列表
     */
    Page pagedQuery(int pageNo, int pageSize, String name_para, String phone, String id, String roleName, Integer status, String startTime, String endTime);

    /**
     * 通过申请
     * @param id
     * @param safeword
     * @param username_login
     */
    void saveSucceeded(String id, String safeword, String username_login);

    /**
     * 驳回申请
     * @param id
     * @param failure_msg
     * @param username_login
     */
    void saveReject(String id, String failure_msg, String username_login);
}