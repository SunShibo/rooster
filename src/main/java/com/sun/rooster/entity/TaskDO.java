package com.sun.rooster.entity;

import com.sun.rooster.common.base.BaseModel;

/**
 * Created by Shibo on 17/2/6.
 */
public class TaskDO extends BaseModel {

    private Integer id;

    /**
     * 唯一ID
     */
    private String uniquelyID;

    /**
     * 触发模式
     */
    private String triggerMode;

    /**
     * 触发时间
     */
    private String triggerTime;

    /**
     * 回调地址
     */
    private String callbackURL;

    /**
     * 回调方式  http/dubbo(暂时不行)
     */
    private String callbackMethod;

    /**
     * 回调信息
     */
    private String callbackData;

    /**
     * 状态
     */
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
