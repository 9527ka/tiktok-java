package systemuser.internal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import systemuser.ResourceMappingService;
import systemuser.model.ResourceMapping;

public class ResourceMappingServiceImpl extends HibernateDaoSupport implements ResourceMappingService {

	public List<ResourceMapping> findBySetIds(List<String> ids){
		List<String> param_ids = new ArrayList<String>();
    	for(String id:ids) {
    		param_ids.add("'"+id+"'");
    	}
        return  (List<ResourceMapping>) this.getHibernateTemplate().find(" FROM ResourceMapping WHERE set_id in("+String.join(",", param_ids)+")");
	}
}
