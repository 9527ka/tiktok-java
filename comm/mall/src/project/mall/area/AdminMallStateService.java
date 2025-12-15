package project.mall.area;

import kernel.web.Page;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;

public interface AdminMallStateService {


    Page pagedQueryState(int pageNo, int pageSize, String stateName,Integer flag);
    void saveState(MallState state);

    MallState findStateById(Long id);

    void updateState(MallState state);

    void updateStateStatus(Long id,Integer flag);
    void deleteState(Long id);
}