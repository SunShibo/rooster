package com.sun.rooster.dao;


import com.sun.rooster.entity.TaskDO;
import com.sun.rooster.entity.bo.TaskBO;
import com.sun.rooster.entity.param.AddMessageParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DataDAO
 */
public interface TaskDao {

    /**
     * 新增数据
     * @param amp
     * @return
     */
    int insertTask(AddMessageParam amp) ;

    /**
     * 查找任务
     * @param uniquelyID
     * @return
     */
    TaskDO selectTask(@Param("uniquelyID") String uniquelyID);

    /**
     * 取消任务
     * @param uniquelyID
     */
    void cancel(String uniquelyID);

    /**
     * 任务完成
     * @param uniquelyID
     */
    void complete(String uniquelyID);

    /**
     * 修改失败等待状态
     * @param uniquelyID
     */
    void failedWait(String uniquelyID);

    /**
     * 查找没有完成的任务
     * @return
     */
    List<TaskBO> selectNoCompleteTask();

    /**
     * 更具状态查记录数
     * @param status
     * @return
     */
    int selectTaskNumByStatus(@Param("status") String status);

    List<TaskDO> selectAllFailedItem();

}
