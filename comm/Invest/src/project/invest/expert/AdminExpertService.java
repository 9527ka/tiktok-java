package project.invest.expert;

import kernel.web.Page;
import project.invest.expert.model.Expert;

public interface AdminExpertService {

    Page pagedQuery(int pageNo, int pageSize, String title, String lang, String startTime, String endTime, Integer status);

    void save(Expert expert);

    Expert findById(String id);

    void update(Expert expert);

    void delete(Expert expert);
}