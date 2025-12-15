package project.mall.seller;

import kernel.web.Page;
import project.mall.seller.model.Complaint;

public interface ComplaintService {
    void saveComplaint(Complaint complaint);

    Page pagedQuery(int pageNo, int pageSize, String userCode, String storeCode, String storeName, String auditStatus, String startTime, String endTime);

    Complaint getComplaintById(String id);
}
