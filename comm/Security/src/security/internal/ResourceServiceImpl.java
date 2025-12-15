package security.internal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import security.Resource;
import security.ResourceService;

public class ResourceServiceImpl  extends HibernateDaoSupport implements ResourceService{

    @Override
    public Resource get(String id) {
        return this.getHibernateTemplate().get(Resource.class, id);
    }
    
    public List<Resource> getByIds(List<String> ids) {
    	List<String> param_ids = new ArrayList<String>();
    	for(String id:ids) {
    		param_ids.add("'"+id+"'");
    	}
        return (List<Resource>) this.getHibernateTemplate().find(" FROM Resource WHERE id in("+String.join(",", param_ids)+")");
    }
}
