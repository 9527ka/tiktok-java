package project.mall.utils;

import java.util.List;
import java.util.Objects;

public class MallPageInfoUtil {

    public static MallPageInfo getMallPage(int pageSize, int pageNum, Long totalElements, List elements) {
        return new MallPageInfo(pageSize, pageNum, Objects.isNull(totalElements) ? 0 : totalElements.intValue(), elements);
    }
}
