package project.invest.platform;

import kernel.web.Page;

import java.util.List;


public interface AdminPlatformService {

    /**
     * 分页查询平台
     * @param
     * @return
     */
    Page findPlatformList(int pageNo, int pageSize, String name, String startTime, String endTime);

    /**
     * 新增或编辑
     * @param
     */
    void addOrModify(String id, String name, String createTime, Integer status);

    /**
     * 删除平台
     * @param id
     */
    void delete(String id);

    List<Platfrom> findAllPlatfrom();

    Platfrom findById(String id);

}