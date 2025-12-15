package project.user.token;

public interface TokenService {

	public String savePut(String partyId);

	public String platFromSavePut(String partyId);


	public String cacheGet(String token);

	public void delete(String token);

	public void removePlatFromToken(String token);

	public void removeLoginToken(String partyId);

	public Token find(String partyId) ;
}
