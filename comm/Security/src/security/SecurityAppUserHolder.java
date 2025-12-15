package security;

import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

public class SecurityAppUserHolder {

	public static SecUser getCurrentUser() {
		Authentication authentication = getAuthentication();
		if (null != authentication) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				return (SecUser) principal;
			}
		}
		return null;
	}
	
	public static String gettUsername() {
        Authentication authentication = getAuthentication();
        if (null != authentication) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((SecUser) principal).getUsername();
            }
        }
        return null;
    }

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
