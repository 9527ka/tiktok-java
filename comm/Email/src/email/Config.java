package email;

public class Config {
	/**
	 * host
	 */
	public static final String host = EmailPropertiesUtil.getProperty("email.host");
	/**
	 *username
	 */
	public static final String username = EmailPropertiesUtil.getProperty("email.username");
	/**
	 * password
	 */
	public static final String password = EmailPropertiesUtil.getProperty("email.password");
	/**
	 * from
	 */
	public static final String from = EmailPropertiesUtil.getProperty("email.from");


}
