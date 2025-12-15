package project.mall.area;

import kernel.web.Page;
import project.mall.area.model.MallCountry;

import java.util.List;

public interface AdminMallCountryService {

    Page pagedQueryCountry(int pageNo, int pageSize, String countryName, Integer flag);

    void saveCountry(MallCountry country);

    MallCountry findCountryById(Long id);

    List<MallCountry> findAllCountry();


    void updateCountry(MallCountry country);

    void updateCountryStatus(Long id, Integer flag);

    void deleteCountry(Long id);
}