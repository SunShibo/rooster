package com.sun.rooster.service;

import com.sun.rooster.dao.FailedDao;
import com.sun.rooster.dao.TaskDao;
import com.sun.rooster.entity.FailedDO;
import com.sun.rooster.entity.TaskDO;
import com.sun.rooster.entity.param.AddMessageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Shibo on 17/2/6.
 */
@Service("failedService")
public class FailedService {

    @Autowired
    private FailedDao failedDao;

    @Autowired
    private TaskDao taskDao;


    public int createItem (String id , String failedInfo ) {
        FailedDO failedDO = new FailedDO();
        failedDO.setUniquelyID(id);
        failedDO.setCreateTime(new Date());
        failedDO.setFailedInfo(failedInfo);
        taskDao.failedWait(id);
        return failedDao.insertItem(failedDO);
    }


}
