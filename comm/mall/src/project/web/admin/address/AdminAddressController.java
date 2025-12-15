package project.web.admin.address;

import kernel.util.JsonUtils;
import kernel.web.PageActionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.blockchain.ChannelBlockchain;
import project.blockchain.ChannelBlockchainService;
import project.mall.area.MallAddressAreaService;
import project.mall.area.model.MallCity;
import project.mall.area.model.MallState;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AdminAddressController extends PageActionSupport {

    @Autowired
    protected MallAddressAreaService mallAddressAreaService;

    @Autowired
    private ChannelBlockchainService channelBlockchainService;

    @RequestMapping("/findStatesByContrtyId.action")
    public String findStatesByContrtyId(HttpServletRequest request) {
        String countryId = request.getParameter("countryId");

        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        LinkedHashMap<Object, String> province = new LinkedHashMap<>();

        List<MallState> statesList = mallAddressAreaService.listAllState(Long.valueOf(countryId));

        if (statesList.size() > 0){
            statesList.stream().forEach(e ->{province.put(e.getId() ,e.getStateNameCn());});
        }

        resultMap.put("province",province);
        resultMap.put("code",200);
        return JsonUtils.getJsonString(resultMap);
    }


    @RequestMapping("/findCityByStateId.action")
    public String findCityByStateId(HttpServletRequest request) {
        String statesId = request.getParameter("statesId");

        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        LinkedHashMap<Object, Object> cityMap = new LinkedHashMap<>();

        List<MallCity> cityList = mallAddressAreaService.listAllCity(Long.valueOf(statesId));

        if (cityList.size() > 0){
            cityList.forEach(e ->{ cityMap.put(e.getId() ,e.getCityNameCn()); });
        }

        resultMap.put("city",cityMap);
        resultMap.put("code",200);
        return JsonUtils.getJsonString(resultMap);
    }


    @RequestMapping("/findWithdrawNameByCoin.action")
    public String findWithdrawNameByCoin(HttpServletRequest request) {
        String withdrawCoinType = request.getParameter("withdrawCoinType");

        HashMap<String, Object> resultMap = new HashMap<String, Object>();

        LinkedHashMap<Object, Object> withdrawChainNames = new LinkedHashMap<>();
        List<ChannelBlockchain> channelList = channelBlockchainService.findAll();
        if (channelList.size() > 0){
            for (ChannelBlockchain channelBlockchain : channelList) {
                if (withdrawCoinType.equals(channelBlockchain.getCoin())){
                    withdrawChainNames.put(channelBlockchain.getBlockchain_name(),channelBlockchain.getBlockchain_name());
                }
            }
        }
        resultMap.put("withdrawChainNames",withdrawChainNames);
        resultMap.put("code",200);
        return JsonUtils.getJsonString(resultMap);
    }

}
