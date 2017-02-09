package com.sun.rooster.system.systemConfig;

import com.sun.rooster.core.DefaultTaskScheduler;
import com.sun.rooster.service.TaskService;

import javax.annotation.Resource;

/**
 * 系统初始化基本配置器
 * Created by Shibo
 */
public class SystemInitializationConfigurer {



    @Resource(name = "defaultTaskScheduler")
    private DefaultTaskScheduler defaultTaskScheduler;

    @Resource(name = "taskService")
    private TaskService taskService;

    /**
     * 系统初始化时做基础配置
     */
    public void init () {
        loadNoCompleteTask();
    }

    public void loadNoCompleteTask(){
        // 启动时从数据库重新加载
        defaultTaskScheduler.reloadTaskFromDB();
    }
}
