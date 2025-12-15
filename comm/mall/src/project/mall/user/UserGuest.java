package project.mall.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class UserGuest extends BaseRowModel {
    private static final long serialVersionUID = -3166620145120075722L;

    @ExcelProperty(index = 0)
    private String userName;

    @ExcelProperty(index = 1)
    private String password;

    @ExcelProperty(index = 2)
    private double money;
}
