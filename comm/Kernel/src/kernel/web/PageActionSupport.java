package kernel.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ext.Strings;
import ext.Types;
import ext.utils.OsUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import kernel.util.StringUtils;
import project.mall.auto.AutoConfig;
import project.syspara.SysparaService;
import security.web.BaseSecurityAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageActionSupport extends BaseSecurityAction {

    /**	
     * Member Description
     */

    private static final long serialVersionUID = -27853059031238999L;

    protected Page page;

    protected List<Integer> tabs;

    protected int pageNo = 1;

    protected int pageSize = 10;
    
	/**
	 * 检查并设置pageNo
	 */
    public void checkAndSetPageNo(String pageNoStr) {

    	if (StringUtils.isNullOrEmpty(pageNoStr)) {
    		this.pageNo = 1;
    		return;
    	}
    	
    	Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
    	Matcher isNum = pattern.matcher(pageNoStr);
    	if (!isNum.matches()) {
    		this.pageNo = 1;
    		return;
    	}
    	
    	int pageNo = Integer.valueOf(pageNoStr).intValue();
    	if (pageNo <= 0) {
    		this.pageNo = 1;
    	} else {
    		this.pageNo = pageNo;
    	}    	
		return;
    }

    public List<Integer> bulidTabs() {
        List<Integer> tabs = new ArrayList<Integer>();
        if (page == null) {
            return tabs;
        }
        int total = page.getTotalPage();
        int cur = page.getThisPageNumber();
        if (total <= 1) {
            if (total == 1) tabs.add(1);
            return tabs;
        }
        if (total <= 10) {
            for (int i = 1; i <= total; i++) tabs.add(i);
            return tabs;
        }
        // total > 10: 头 5 + 当前 ±1 + 尾 3, 之间用 -1 占位省略号
        java.util.TreeSet<Integer> set = new java.util.TreeSet<Integer>();
        for (int i = 1; i <= 5; i++) set.add(i);
        for (int i = Math.max(1, total - 2); i <= total; i++) set.add(i);
        for (int i = Math.max(1, cur - 1); i <= Math.min(total, cur + 1); i++) set.add(i);
        Integer prev = null;
        for (Integer p : set) {
            if (prev != null && p - prev > 1) {
                tabs.add(-1);
            }
            tabs.add(p);
            prev = p;
        }
        return tabs;
    }

    public List<Integer> getTabs() {
        return tabs;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    protected void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    protected String getExtUrl(HttpServletRequest request, SysparaService sysparaService, String path)  {
        String roles = Strings.join( this.readSecurityContextFromSession().getRoles(),",");
        String accessToken = this.getUsername_login()+ ":"+roles;
        if(path.startsWith("/")){
            path = path.substring(1);
        }
        String url = String.format("tks/index.html?token=ag001&accessToken=%s#/%s",accessToken,path);

        if (!OsUtils.detectPort("localhost", 8011)){
            // 未开启
            return null;
        }

        String host = request.getHeader("HOST");
        if (host.startsWith("localhost")) {
            return "http://localhost:8011/"+url;
        }
        String prefix = AutoConfig.getBaseUrl();
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + url;
    }
}
