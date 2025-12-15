package security.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import security.Resource;

public class SecurityAuthoritiesHolderImpl extends HibernateDaoSupport
		implements SecurityAuthoritiesHolder {

	private Map<String, Map<String, String>> cache = new HashMap<String, Map<String, String>>();

	@SuppressWarnings("unchecked")
	private List<Resource> getResourcesByType(String resType) {
		return (List<Resource>) this.getHibernateTemplate().find("FROM Resource WHERE resType = ?0",new Object[] {resType});

	}

	public Map<String, String> loadAuthorities(String resType) {
		Map<String, String> authorities = cache.get(resType);
		if (authorities == null) {
			authorities = new LinkedHashMap<String, String>();
			List<Resource> urlResources = getResourcesByType(resType);
			
			Collections.sort(urlResources,new Comparator<Resource>() {
	            //升序排序
	            public int compare(Resource o1, Resource o2) {
	            	if (o1.getResString().length()<o2.getResString().length()) {
						return 1;
					} else if (o1.getResString().length()==o2.getResString().length()){
						return 0;
					}
	            	return -1;
	            }
			  });
			
			for (Resource resource : urlResources) {
				authorities.put(resource.getResString(),
						resource.getRoleAuthorities());
			}
			cache.put(resType, authorities);
		}
		

		return authorities;
	}

    @Override
    public void clean() {
        cache = new HashMap<String, Map<String, String>>();
        
    }
	
	
	

}
