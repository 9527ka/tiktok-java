package kernel.dao;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Query;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.util.CollectionUtils;


public abstract class HibernateUtils extends SQLUtils {

    public static Object resolveRealObject(Object proxy) {
        if (proxy == null) {
            return null;
        }
        if (proxy instanceof HibernateProxy) {
            return ((HibernateProxy) proxy).getHibernateLazyInitializer().getImplementation();
        }
        return proxy;
    }

    public static void applyParameters(Query queryObject, Map<String, Object> parameters) {
        if (!CollectionUtils.isEmpty(parameters)) {
            for (Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof Collection) {
                    queryObject.setParameterList(key, (Collection<?>) value);
                }
                else if (value instanceof Object[]) {
                    queryObject.setParameterList(key, (Object[]) value);
                }
                else {
                    queryObject.setParameter(key, value);
                }
            }
        }
    }
}
