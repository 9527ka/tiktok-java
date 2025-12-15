package project.user.idcode;

public interface IdentifyingCodeTimeWindowService {

	public String getAuthCode(String key);

	public void putAuthCode(String key, String authcode);
	
	public void delAuthCode(String key);
}
