package kernel.sessiontoken.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kernel.sessiontoken.SessionTokenService;
import kernel.util.StringUtils;
import kernel.util.UUIDGenerator;

public class SessionTokenServiceImpl implements SessionTokenService {

	private volatile Map<String, String> cache = new ConcurrentHashMap<String, String>();

	public String savePut(String partyId) {
		String session_token = UUIDGenerator.getUUID();
		cache.put(session_token, partyId);
		return session_token;
	}

	public String cacheGet(String session_token) {
		if (StringUtils.isNullOrEmpty(session_token)) {
			return null;
		}
		return cache.get(session_token);
	}

	@Override
	public void delete(String session_token) {
		if (StringUtils.isNullOrEmpty(session_token)) {
			return;
		}
		cache.remove(session_token);
	}

}
