package project.party;

import project.mall.utils.MallPageInfo;
import project.party.model.UserMetrics;


public interface UserMetricsService {

    UserMetrics save(UserMetrics entity);

    UserMetrics getByPartyId(String partyId);

    void update(UserMetrics entity);

}
