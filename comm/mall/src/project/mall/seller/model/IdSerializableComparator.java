package project.mall.seller.model;

import java.io.Serializable;
import java.util.Comparator;

public class IdSerializableComparator implements Comparator<MallLevel> {
    @Override
    public int compare(MallLevel level1, MallLevel level2) {
        String id1 = level1.getId();
        String id2 = level2.getId();

//        // 使用序列化表示进行比较
//        String serializedId1 = serializeId(id1);
//        String serializedId2 = serializeId(id2);

        return id1.compareTo(id2);
    }

    private String serializeId(Serializable id) {
        // 在这里实现将Serializable对象转换为字符串的逻辑
        // 可以使用对象流等方法将id序列化为字符串
        // 这里只是一个示例，具体实现需要根据您的数据类型进行调整
        return id.toString();
    }
}
