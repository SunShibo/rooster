package com.sun.rooster.entity.bo;

import com.sun.rooster.entity.param.AddMessageParam;

/**
 * Created by Shibo on 17/2/6.
 */
public class TaskBO extends AddMessageParam {

    /**
     * 失败次数
     */
    private Integer failedTimes;


    public Integer getFailedTimes() {
        return failedTimes;
    }

    public void setFailedTimes(Integer failedTimes) {
        this.failedTimes = failedTimes;
    }
}
