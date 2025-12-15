package project.invest.vip;

import project.invest.platform.BrushClient;
import project.invest.vip.model.Vip;
import project.party.model.Party;
import util.TwoValues;

import java.util.List;
import java.util.Map;


public interface VipService {

    List<Vip> listVip();


    Vip selectById(int vip_level);

    TwoValues<Integer,Double> getInvestPromotion(String partyId);

    void updatePartyVip(String partyId);


    BrushClient getBrushClient(String id);


}
