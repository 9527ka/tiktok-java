package kernel.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jackson读写工具类.
 */
@Slf4j
public class JacksonUtils {

    /**
     * jackson对象.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule timeModule = new JavaTimeModule();

        timeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        timeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        timeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        MAPPER
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(timeModule)
                .registerModule(new ParameterNamesModule())
                .registerModule(new JSR310Module())
                .registerModule(new Jdk8Module());
        // .registerModule(new JtsModule());
    }

    /**
     * 将对象转换成json字符串.
     */
    public static String objectToJson(Object data) {
        try {
            if (data == null) {
                return null;
            }
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("异常打印", e);
        }
        return null;
    }

    /**
     * 将对象转换成json字符串.
     *
     * @param data .
     * @return
     */
    public static String objToJson(Object data) {
        if (data == null) {
            return null;
        }

        String json = null;
        try {
            json = MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            //ignore
        }
        return json;
    }

    /**
     * 将json结果集转化为对象.
     */
    public static <T> T jsonToObject(String jsonData, Class<T> beanType) {
        if (ObjectUtils.isEmpty(jsonData) || beanType == null) {
            return null;
        }
        if (beanType == String.class) {
            return (T)jsonData;
        }

        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (IOException e) {
            log.error("数据转换失败:", e);
        }
        return null;
    }

    /**
     * 基于 json 字符串做反序列化.
     * @param jsonStr .
     * @param typeReference .
     * @return
     */
    public static <T> T jsonToObject(String jsonStr, TypeReference typeReference) {
        if (StrUtil.isBlank(jsonStr)) {
            return null;
        }
        try {
            // 示例：TypeReference newType = new TypeReference<List<NationalCityCode>>() {};
            return (T)(MAPPER.readValue(jsonStr, typeReference));
        } catch (IOException e) {
            log.error("异常打印{}", e);
        }
        return null;
    }

    /**
     * 基于 json 做反序列化.
     * @param jsonStr .
     * @param type .
     * @return
     */
    public static <T> T jsonToObject(String jsonStr, Type type) {
        if (StrUtil.isBlank(jsonStr)) {
            return null;
        }
        try {
            JavaType javaType = MAPPER.constructType(type);
            return (T)(MAPPER.readValue(jsonStr, javaType));
        } catch (IOException e) {
            log.error("异常打印{}", e);
        }
        return null;
    }

    /**
     * 读取json中某个字段.
     */
    public static String readField(String json, String field) throws IOException {
        if (Strings.isNullOrEmpty(field)) {
            return "";
        } else {
            JsonNode node = MAPPER.readValue(json, ObjectNode.class).get(field);
            return (node == null ? null : node.textValue());
        }
    }

    /**
     * 读取json中某个字段.
     */
    public static String readNodeField(String json, String field) throws IOException {
        if (Strings.isNullOrEmpty(field)) {
            return null;
        } else {
            JsonNode node = MAPPER.readValue(json, ObjectNode.class).get(field);
            return node == null ? null : node.toString();
        }
    }

    /**
     * 读取json中Decimal类型字段.
     */
    public static BigDecimal readBigDecimalField(String json, String field) throws IOException {
        if (Strings.isNullOrEmpty(field)) {
            return null;
        } else {
            JsonNode node = MAPPER.readValue(json, ObjectNode.class).get(field);
            return (node == null ? null : node.decimalValue());
        }
    }

    /**
     * 读取json中Long类型字段.
     */
    public static Long readLongField(String json, String field) throws IOException {
        if (Strings.isNullOrEmpty(field)) {
            return null;
        } else {
            JsonNode node = MAPPER.readValue(json, ObjectNode.class).get(field);
            return (node == null ? null : node.longValue());
        }
    }

    /**
     * 读取json中Int类型字段.
     */
    public static int readIntField(String json, String field) throws IOException {
        if (Strings.isNullOrEmpty(field)) {
            return -1;
        } else {
            JsonNode node = MAPPER.readValue(json, ObjectNode.class).get(field);
            return (node == null ? null : node.intValue());
        }
    }

    /**
     * 读取json中Boolean类型字段.
     */
    public static Boolean readBooleanField(String json, String field) throws IOException {
        if (Strings.isNullOrEmpty(field)) {
            return false;
        } else {
            JsonNode node = MAPPER.readValue(json, ObjectNode.class).get(field);
            return (node == null ? null : node.booleanValue());
        }
    }

    /**
     * 读取json中Boolean类型字段.
     *
     * @param json .
     * @return
     */
    public static JsonNode readString(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            JsonNode node = MAPPER.readValue(json, ObjectNode.class);
            return node;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json转list.
     */
    public static <T> List<T> jsonToList(String json, Class<T> c) throws IOException {
        if (StrUtil.isBlank(json)) {
            return new ArrayList<>();
        }

        TypeFactory t = TypeFactory.defaultInstance();
        return MAPPER.readValue(json, t.constructCollectionType(ArrayList.class, c));
    }

    /**
     * 将 json 字符串转换为 map 对象，其中，key 是字符串类型， value 是参数指定的类型.
     *
     * @param json .
     * @param type .
     * @throws IOException .
     */
    public static <V> Map<String, V> jsonToMap(String json, Class<V> type) throws IOException {
        if (StrUtil.isBlank(json)) {
            return new HashMap<>();
        }

        if (type == String.class
                || ClassTools.isBaseType(type)
                || ClassTools.isPrimitiveWrapper(type)) {
            return jsonToMap(json);
        }

        Map<String, Map<String, Object>> map = MAPPER.readValue(json,
            new TypeReference<Map<String, Map<String, Object>>>() {});
        Map<String, V> result = new HashMap<String, V>();
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            if (type == String.class) {
                result.put(entry.getKey(), (V)String.valueOf(entry.getValue()));
            } else {
                result.put(entry.getKey(), mapToPojo(entry.getValue(), type));
            }
        }
        return result;
    }

    /**
     * 将 json 字符串转换成 map 返回.
     * 注意：经测试，当 V 是一个 javabean 类型时，此方法返回的 V 将是一个 LinkedHashMap 对象.
     *
     * @param json .
     * @return
     */
    public static <K, V> Map<K, V> jsonToMap(String json) throws IOException {
        if (StrUtil.isBlank(json)) {
            return new HashMap<>();
        }

        return MAPPER.readValue(json, new TypeReference<Map<K, V>>() {
        });
    }

    /**
     * map  转JavaBean.
     */
    public static <T> T mapToPojo(Map map, Class<T> clazz) {
        return MAPPER.convertValue(map, clazz);
    }


    /**
     * 获取泛型的Collection Type.
     *
     * @param collectionClass 泛型的Collection .
     * @param elementClasses  元素类 .
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

     /**
     * main方法.
     *
     * @param args .
     * @throws Exception Exception .
     */
    public static void main(String[] args) throws Exception {
        String groupInRedis = "{\"pc\":\"http://www.zxjlbvip.org,http://bob1.zxjlbvip.org,http://bob2.zxjlbvip.org\",\"h5\":\"http://h5.zxjlbvip.org,http://h51.zxjlbvip.org,http://h52.zxjlbvip.org\",\"api\":\"http://api.zxjlbvip.org,http://apibob1.zxjlbvip.org,http://apibob2.zxjlbvip.org\",\"cdn\":\"http://dl1.zxjlbvip.org,http://dl2.zxjlbvip.org\",\"img_url\":\"http://dl1.zxjlbvip.org\"}";
        groupInRedis = "{\"pc\":1, \"xx\":\"ok\"}";
        Map<String, String> urlsByTypeInRedis = JacksonUtils.jsonToMap(groupInRedis);
        System.out.println("------> urlsByTypeInRedis: " + urlsByTypeInRedis);

        Map<String, List<String>> mapListMap = new HashMap<>();
        List<String> tmpList1 = new ArrayList<>();
        mapListMap.put("k1", tmpList1);

        tmpList1.add("v1");
        tmpList1.add("v2");
        tmpList1.add("v3");

        String json = JacksonUtils.objectToJson(mapListMap);

        Map<String, List<String>> mapListMap2 = JacksonUtils.jsonToMap(json);
        System.out.println("====> list1:" + mapListMap2.get("k1"));
        TypeReference newType2 = new TypeReference<Map<String, List<String>>>() {};
        Map<String, List<String>> mapListMap23 = JacksonUtils.jsonToObject(json, newType2);
        System.out.println("====> list1:" + mapListMap23.get("k1"));

        //Map<String, List<String>> mapListMap3 = JacksonUtils.jsonToMap(json);
        //System.out.println("====> list1:" + mapListMap2.get("k1"));

    }

}
