package com.sun.rooster.service;

import com.sun.rooster.dao.TaskDao;
import com.sun.rooster.entity.TaskDO;
import com.sun.rooster.entity.bo.TaskBO;
import com.sun.rooster.entity.param.AddMessageParam;
import com.sun.rooster.entity.vo.TaskMonitorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Shibo on 17/2/6.
 */
@Service("taskService")
public class TaskService {

    @Autowired
    private TaskDao taskDao;

    public int createItem (AddMessageParam amp ) {
        return taskDao.insertTask(amp);
    }

    public TaskDO queryTask(String uniquelyID) {
        return taskDao.selectTask(uniquelyID);
    }

    public void cancel(String uniquelyID) {
        taskDao.cancel(uniquelyID);
    }

    public void complete(String uniquelyID) {
        taskDao.complete(uniquelyID);
    }

    public List<TaskBO> findNoCompleteTask() {
        List<TaskBO> taskDOs = taskDao.selectNoCompleteTask();
        return taskDOs;
    }

    public TaskMonitorVO findTaskNum () {
        int all = taskDao.selectTaskNumByStatus(null);
        int wait = taskDao.selectTaskNumByStatus("wait");
        int failed = taskDao.selectTaskNumByStatus("failed_wait");
        return new TaskMonitorVO(all ,failed , wait) ;
    }

    public TaskDO findTask(String uniquelyID) {
        TaskDO taskDO = taskDao.selectTask(uniquelyID);
        return taskDO;
    }

    public List<TaskDO> findAllFailedItem() {
        return taskDao.selectAllFailedItem();
    }
}
