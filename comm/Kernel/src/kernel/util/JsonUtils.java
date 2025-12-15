package kernel.util;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:json工具類
 * </p>

 */
public class JsonUtils {
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * 转成JSON字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String getJsonString(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("---> JsonUtils.getJsonString error: ", e);
			return "";
		}
	}

	/**
	 * 
	 * @param <T>
	 * @param jsonStr
	 * @param T
	 * @return
	 */
	public static <T> T readJsonEntity(String jsonStr, Class<T> T) {
		try {
			return mapper.readValue(jsonStr, T);
		} catch (Exception e) {
			logger.error("---> JsonUtils.readJsonEntity read:{}, error: ", jsonStr, e);
			return null;
		}
	}

	/**
	 * JsonNode
	 * 
	 * @param jsonStr
	 * @see { 代码示例： String resultJson
	 *      ="{'playerResults':[{'playerId':'111','gameId':'','tee':'0,0,0'},{'playerId':'ff80808137f7daac0137f7dd1ab80001','gameId':'','tee':'255,255,255'}]}";
	 *      JsonNode jn=readJsonEntity(resultJson); jn=jn.get("playerResults");
	 *      for (int i = 0; i < jn.size(); i++){ String
	 *      playerId=jn.get(i).get("playerId").asText();
	 *      logger.info("playerId="+playerId); } }
	 * @return
	 */
	public static JsonNode readJsonEntity(String jsonStr) {
		return readJsonEntity(jsonStr, JsonNode.class);
	}
	
	/**
	 * 将POJO转换成JSON 
	 * @param object
	 * @return
	 * @throws Exception 
	 */ 
	 public static String bean2Json(Object object) {
	 	if (object == null) {
	 		return null;
		}

		 String json = null;
//		 ObjectMapper mapper = new ObjectMapper();
		 try {
			 json = mapper.writeValueAsString(object);
		 } catch (Exception e) {
			 logger.error("---> JsonUtils.bean2Json error: ", e);
			 return null;
		 }
		 return json;
	 } 
	
	 /**
	  * 将JSON转换成POJO
	 * @param <T>
	  * @param json
	  * @param beanClz POJO的Class
	  * @return
	 *  @throws Exception 
	  */
	 public static <T> T json2Object(String json, Class<T> beanClz) {
//		 ObjectMapper mapper = new ObjectMapper();
//		 mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//		 mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
//		 mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
//		 mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		 T t = null;
		 try {
			 t = mapper.readValue(json, beanClz);
		 } catch (Exception e) {
			 try {
			 	t = beanClz.newInstance();
			 } catch (Exception e1) {}
			 logger.error("---> JsonUtils.json2Object parse:{}, error: ", json, e);
			 return null;
		 }
		 return t;
	 } 
	 
	 /** 
	     * json数组转List 
	     * @param jsonStr 
	     * @param valueTypeRef 
	     * @return 
	     */  
	    public static <T> T readValue(String jsonStr, TypeReference<T> valueTypeRef) {  
//	    	ObjectMapper objectMapper = new ObjectMapper();
	        try {  
	            return mapper.readValue(jsonStr, valueTypeRef);
	        } catch (Exception e) {
				logger.error("---> JsonUtils.readValue read:{} error: ", jsonStr, e);
	        }  
	  
	        return null;  
	    }  
}
