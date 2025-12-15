package security.web;

import kernel.util.StringUtils;
import security.SecUser;
import security.SecurityAppUserHolder;

/**
 * 登录后的第一个默认action，做为基类使用
 */
public class LoginSuccessAction extends BaseSecurityAction {

	public String COOKIE_USERNAME_NAME = "SECURITY_USER_CORRELATION";

	/**
	 * Member Description
	 */

	private static final long serialVersionUID = -457151315942685113L;

	public String view() {
		SecUser secUser = (SecUser)super.readSecurityContextFromSession().getPrincipal();
		this.saveLoginCookies(secUser.getUsername());

		String cookie_username = this.getCookie(COOKIE_USERNAME_NAME);

		if (!StringUtils.isNullOrEmpty(cookie_username) && cookie_username.length() >= 4000) {
			cookie_username = cookie_username.substring(0, 3999);
		}

		return "index";

	}

	/**
	 * 这个方法在Nginx下失效
	 */
	public String getRemoteHost(javax.servlet.http.HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	/**
	 * 将登录关联信息保存到cookies
	 */
	private void saveLoginCookies(String username) {
		username = username.replaceAll("\\s*", "");
		username = username.toLowerCase();

		String username_cookie = this.getCookie(COOKIE_USERNAME_NAME);
		boolean find = false;
		if (!StringUtils.isNullOrEmpty(username_cookie)) {
			String[] array = username_cookie.split(",");

			for (int i = 0; i < array.length; i++) {
				if (username.equals(array[i])) {
					find = true;
					break;
				}

			}

		}
		if (!find) {

			if (StringUtils.isNullOrEmpty(username_cookie)) {
				addCookie(COOKIE_USERNAME_NAME, username);
			} else {
				username = username_cookie + "," + username;
				addCookie(COOKIE_USERNAME_NAME, username);
			}
		}
	}

}
