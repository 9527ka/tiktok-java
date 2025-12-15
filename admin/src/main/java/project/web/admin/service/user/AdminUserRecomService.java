package project.web.admin.service.user;

import kernel.web.Page;
import project.party.model.UserRecom;

public interface AdminUserRecomService {

    public Page pagedQuery(int pageNo, int pageSize, String usernameOrUid,String parentUsername,String login_partyId);
    
    public UserRecom get(String id);
    
    /**
     * 被修改用户partyId
     * 修改后的推荐人reco_username
     * 操作者用户名operator
     */
    public void update(String partyId, String reco_username,String operator_name,String ip,String loginSafeword);
}
