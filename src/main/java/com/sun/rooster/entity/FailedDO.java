package com.sun.rooster.entity;

import com.sun.rooster.common.base.BaseModel;

import java.util.Date;

/**
 * Created by Shibo on 17/2/6.
 */
public class FailedDO extends BaseModel {

    private Integer id;

    /**
     * 唯一ID
     */
    private String uniquelyID;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 失败信息
     */
    private String failedInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUniquelyID() {
        return uniquelyID;
    }

    public void setUniquelyID(String uniquelyID) {
        this.uniquelyID = uniquelyID;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFailedInfo() {
        return failedInfo;
    }

    public void setFailedInfo(String failedInfo) {
        this.failedInfo = failedInfo;
    }
}
