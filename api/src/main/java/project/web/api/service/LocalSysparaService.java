package project.web.api.service;

import java.util.Map;

public interface LocalSysparaService {
	/**
	 * 可逗号相隔，查询多个参数值。
	 * 
	 */
	public Map<String, Object> find(String code);
}
