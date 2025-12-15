package project.web.admin.controller.mall;

import ext.Strings;
import ext.Types;
import ext.utils.OsUtils;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import project.mall.auto.AutoConfig;
import project.syspara.SysparaService;

import java.io.IOException;


@Slf4j
@RestController
@RequestMapping("/mall/ext/")
public class ExtController extends PageActionSupport {
    /**
     * V3 Pos
     */
    @RequestMapping("/goods/gather.action")
    private void v3GoodsGather(HttpServletRequest request, HttpServletResponse resp) throws IOException {
        String modules = Types.orValue(System.getProperty("TK_MODULES"),System.getenv("TK_MODULES"));
        if(modules == null){
            modules = "";
        }
        String roles = Strings.join( this.readSecurityContextFromSession().getRoles(),",");
        String accessToken = this.getUsername_login()+ ":"+roles;
        String url = String.format("tks/index.html?token=ag001&accessToken=%s#/goods/gather",accessToken);

        if (!OsUtils.detectPort("localhost", 8011) || !modules.contains("GATHER")) {
            resp.getWriter().write("<h1>此版本不支持采集功能,请联系管理员开通!</h1>");
            resp.setContentType("text/html; charset=utf-8");
            return;
        }
        String host = request.getHeader("HOST");
        if (host.startsWith("localhost")) {
            resp.getWriter().write("<script>location.replace('http://localhost:8011/"+url+"');</script>");
        } else {
            String prefix = AutoConfig.getBaseUrl();
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }
            resp.getWriter().write("<script>location.replace('" + prefix + url+"');</script>");
        }
        resp.setContentType("text/html; charset=utf-8");
    }
}
