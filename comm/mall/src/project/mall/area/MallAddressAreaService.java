package project.mall.area;

import project.mall.area.model.MallCity;
import project.mall.area.model.MallCountry;
import project.mall.area.model.MallState;
import project.mall.area.model.MobilePrefix;

import java.util.List;

public interface MallAddressAreaService {

    List<MallCountry> listAllCountry();

    List<MallCountry> listCountry(String countryName,String language);

    List<MallState> listAllState(Long countryId);

    List<MallState> listState(String stateName, Long countryId,String language);

    List<MallCity> listAllCity(Long stateId);

    List<MallCity> listCity(String cityName, Long stateId,String language);

    MallCountry findCountryById(Long id);

    MallState findMallStateById(Long id);

    MallCity findCityById(Long id);

    List<MobilePrefix> listAllMobilePrefix();

//    根据国家省市code和语言查询对应语种返回对应语种下的国家省市名称
    List<String> findAddressWithCodeAndLanguage(Long countryId,Long stateId,Long cityId,String language);
}