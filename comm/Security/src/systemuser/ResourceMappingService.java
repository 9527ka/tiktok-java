package systemuser;

import java.util.List;

import systemuser.model.ResourceMapping;

public interface ResourceMappingService {
	
	public List<ResourceMapping> findBySetIds(List<String> ids);

}
