package security.web;

import kernel.util.ServletUtil;
import kernel.web.BaseAction;

/** 
 *  判断是否登录
 */
public class IsLoginAction extends BaseSecurityAction {

    /**	
     * Member Description
     */
    
    private static final long serialVersionUID = 1L;

    public String execute() throws Exception {
        String partyId =this.getLoginPartyId();
        ServletUtil.outputXML(getResponse(), partyId!=null ? "0" : "1");
        return null;

    }


}
