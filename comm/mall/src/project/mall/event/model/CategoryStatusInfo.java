package project.mall.event.model;

import lombok.Data;

@Data
public class CategoryStatusInfo {
    private String categoryId;

    private int oriStatus;

    private int newStatus;
}
