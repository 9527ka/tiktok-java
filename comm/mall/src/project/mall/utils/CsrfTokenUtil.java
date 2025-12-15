package project.mall.utils;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author axing
 * @since 2023/7/28
 **/
public class CsrfTokenUtil {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static void saveTokenInSession(HttpSession session, String token) {
        session.setAttribute("session_token", token);
    }

    public static boolean isTokenValid(String sessionToken, String token) {
        return sessionToken != null && sessionToken.equals(token);
    }

    public static void removeTokenFromSession(HttpSession session) {
        session.removeAttribute("session_token");
    }
}
