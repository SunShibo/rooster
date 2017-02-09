package com.sun.rooster.entity.param;

import com.sun.rooster.common.base.BaseModel;

/**
 * 添加消息参数
 * Created by Shibo on 17/1/23.
 */
public class AddMessageParam extends BaseModel {

    private Integer id;

    /**
     * 唯一ID
     */
    private String uniquelyID;

    /**
     * 触发模式 single(一次)  cycle(循环)
     */
    private String triggerMode;

    /**
     * 触发时间 格式   yyyy-MM-dd HH:mm:ss
     */
    private String triggerTime;

    /**
     * 回调地址
     */
    private String callbackURL;

    /**
     * 回调方式  http / dubbo(暂时不行)
     */
    private String callbackMethod;

    /**
     * 回调信息
     */
    private String callbackData;

    public String getTriggerMode() {
        return triggerMode;
    }

    public void setTriggerMode(String triggerMode) {
        this.triggerMode = triggerMode;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public String getCallbackURL() {
        return callbackURL;
    }

    public void setCallbackURL(String callbackURL) {
        this.callbackURL = callbackURL;
    }

    public String getCallbackMethod() {
        return callbackMethod;
    }

    public void setCallbackMethod(String callbackMethod) {
        this.callbackMethod = callbackMethod;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
    }

    public String getUniquelyID() {
        return uniquelyID;
    }

    public void setUniquelyID(String uniquelyID) {
        this.uniquelyID = uniquelyID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
