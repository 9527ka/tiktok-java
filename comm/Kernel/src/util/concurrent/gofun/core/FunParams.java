package util.concurrent.gofun.core;

import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于仿照 go 协程方法的参数传值.
 */
public class FunParams {
    /**
     * 参数集合.
     */
    Map<String, ValueOptional> params = new HashMap<>();

    /**
     * 新建对象.
     *
     * @return
     */
    public static FunParams newParam() {
        FunParams object = new FunParams();
        return object;
    }

    /**
     * 记录参数值.
     *
     * @param paramName .
     * @param paramValue .
     * @return
     */
    public FunParams set(String paramName, Object paramValue) {
        if (StrUtil.isBlank(paramName)) {
            return this;
        }
        ValueOptional option = new ValueOptional(paramValue);
        this.params.put(paramName, option);

        return this;
    }

    /**
     * 获取参数值.
     *
     * @param paramName .
     * @return
     */
    public ValueOptional get(String paramName) {
        if (StrUtil.isBlank(paramName)) {
            return null;
        }

        return this.params.get(paramName);
    }
}

