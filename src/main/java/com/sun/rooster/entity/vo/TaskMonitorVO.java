package com.sun.rooster.entity.vo;

import com.sun.rooster.common.base.BaseModel;

/**
 * Created by Shibo on 17/2/8.
 */
public class TaskMonitorVO extends BaseModel{

    private Integer allTask ;

    private Integer failedTask;

    private Integer waitTask;

    public Integer getAllTask() {
        return allTask;
    }

    public void setAllTask(Integer allTask) {
        this.allTask = allTask;
    }

    public Integer getFailedTask() {
        return failedTask;
    }

    public void setFailedTask(Integer failedTask) {
        this.failedTask = failedTask;
    }

    public Integer getWaitTask() {
        return waitTask;
    }

    public TaskMonitorVO(Integer allTask, Integer failedTask, Integer waitTask) {
        this.allTask = allTask;
        this.failedTask = failedTask;
        this.waitTask = waitTask;
    }
    public TaskMonitorVO(){

    }

    public void setWaitTask(Integer waitTask) {

        this.waitTask = waitTask;
    }
}
