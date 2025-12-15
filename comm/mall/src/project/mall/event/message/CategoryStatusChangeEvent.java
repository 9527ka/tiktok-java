package project.mall.event.message;

import org.springframework.context.ApplicationEvent;
import project.mall.event.model.CategoryStatusInfo;

public class CategoryStatusChangeEvent extends ApplicationEvent {
    private CategoryStatusInfo info;

    public CategoryStatusChangeEvent(Object source, CategoryStatusInfo info) {
        super(source);
        this.info = info;
    }

    public CategoryStatusInfo getChangeInfo() {
        return this.info;
    }
}
