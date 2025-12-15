package security;

import java.util.List;

public interface ResourceService {
    
  public   Resource get(String id);
  
  /**
   * 根据id列表批量获取
   * @param ids
   * @return
   */
  public List<Resource> getByIds(List<String> ids);

}
