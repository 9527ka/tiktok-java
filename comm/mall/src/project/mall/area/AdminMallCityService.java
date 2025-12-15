package project.mall.area;

import kernel.web.Page;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;

public interface AdminMallCityService {

    Page pagedQueryCity(int pageNo, int pageSize, String cityName,Integer flag);


    void saveCity(MallCity city);


    MallCity findCityById(Long id);

    void updateCity(MallCity city);


    void updateCityStatus(Long id,Integer flag);

    void deleteCity(Long id);
}