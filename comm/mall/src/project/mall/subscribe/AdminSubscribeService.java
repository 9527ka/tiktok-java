package project.mall.subscribe;

import kernel.web.Page;

public interface AdminSubscribeService{

    Page pagedQuery(int pageNo, int pageSize, String startTime, String endTime, String email);

    void delete(String id);
}
