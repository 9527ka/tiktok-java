package project.mall.event;

import kernel.util.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import project.mall.event.message.CategoryStatusChangeEvent;
import project.mall.event.model.CategoryStatusInfo;
import project.mall.type.CategoryService;

/**
 * 修改了分类状态后，有一些关联业务会同步受到影响
 * 目前可见受影响的业务数据：
 * 1. 如果当前分类是一级分类，则还会影响到其下的二级分类：
 *
 * 2. ....
 *
 */
public class ModifyCategoryStatusEventListener implements ApplicationListener<CategoryStatusChangeEvent> {
    private Log logger = LogFactory.getLog(this.getClass());

    private CategoryService categoryService;

    @Override
    public void onApplicationEvent(CategoryStatusChangeEvent event) {
        CategoryStatusInfo changeInfo = event.getChangeInfo();
        logger.info("监听到分类记录:" + changeInfo.getCategoryId() + " 修改了状态：" + changeInfo.getOriStatus() + " ---> " + changeInfo.getNewStatus());

        try {
            //Category category = categoryService.getById(changeInfo.getCategoryId());
            int oriStatus = changeInfo.getOriStatus();
            int newStatus = changeInfo.getNewStatus();
            if (oriStatus == newStatus) {
                return;
            }

            categoryService.updateStatus(changeInfo.getCategoryId(), newStatus);
        } catch (Exception e) {
            logger.error("修改商品分类状态后，同步修改子分类状态报错，变更信息为: " + JsonUtils.getJsonString(changeInfo), e);
        }

    }


    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

}
