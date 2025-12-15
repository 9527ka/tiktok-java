package project.mall.notification.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import kernel.bo.EntityObject;
import lombok.Data;

import java.util.List;
import com.alibaba.fastjson.JSONArray;
import project.mall.orders.model.MallOrdersPrize;

/**
 * 消息模板，类型包括：短信消息模板，email消息模板，站内信消息模板
 */
@Data
public class NotificationTemplate extends EntityObject<String> {
    private static final long serialVersionUID = 8096944949372440876L;

//    /**
//     * 用于支持同条通知多语言，逻辑上同条通知该值相同
//     */
//    private String templateGroupKey;

    /**
     * 语言类型：zh_CN: 中文，en_US: 英文
     */
    private String language;

    // 模板编码，全局唯一
    private String templateCode;

    /**
     * 模板大类：
     * 1 - 短信模板
     * 2 - email模板
     * 3 - 站内消息模板
     */
    private Integer type;

    /**
     * 模板标题，说明
     */
    private String title;

    /**
     * 模板状态：1-审核中，2-正常，2-下架锁定（提前一天强制下架锁定，该状态的模板不能发起短信，针对并发情况）,3-废弃
     */
    private Integer status;


    /**
     * 模板平台：
     * inbox - 站内信
     * aliSms - 阿里短信
     * googleEmail - 谷歌邮箱
     *
     */
    private String platform;

    /**
     * 业务类型：
     * 参考枚举类型：NotificationBizTypeEnum
     */
    private String bizType;

    /**
     * 消息处理器名称
     */
    private String handler;

    /**
     * 模块：
     */
    private Integer module;


    /**
     * 模板内容
     */
    private String content;

    private String varPlaceholdersJson;

    /**
     * 对应解析类：ContentPlaceHolder
     * 消息模板变量占位符信息，例如：[{"index":1, "code":"text", "format":"yyyy年MM月dd日","value":"10月20号"}]
     */
    private List<ContentPlaceHolder> varPlaceholders;

    /**
     * 基于数据库里存储的 varPlaceholdersJson 信息构建并返回 varPlaceholders
     * @return
     */
    public List<ContentPlaceHolder> getVarPlaceholders() {
        if (this.varPlaceholders != null) {
            return this.varPlaceholders;
        }
        if (this.varPlaceholdersJson == null || this.varPlaceholdersJson.trim().isEmpty()) {
            return null;
        }

        this.varPlaceholders = JSONObject.parseObject(this.varPlaceholdersJson.trim(), new TypeReference<List<ContentPlaceHolder>>(){});
        return this.varPlaceholders;
    }

    /**
     * 在业务逻辑中更新 varHolders 值时，刷新用于存库的字段 varPlaceholdersJson
     * @param varHolders
     */
    public void setVarPlaceholders(List<ContentPlaceHolder> varHolders) {
        this.varPlaceholders = varHolders;
        if (varHolders == null || varHolders.isEmpty()) {
            this.varPlaceholdersJson = null;
            return;
        }

        this.varPlaceholdersJson = JSONArray.toJSON(varHolders).toString();
    }

}
