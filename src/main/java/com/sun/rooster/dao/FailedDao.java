package com.sun.rooster.dao;


import com.sun.rooster.entity.FailedDO;
import com.sun.rooster.entity.param.AddMessageParam;

import java.util.List;

/**
 * DataDAO
 */
public interface FailedDao {

    /**
     * 新增数据
     *
     * @param amp
     * @return
     */
    int insertItem(FailedDO amp);


    List<FailedDO> selectFailedListByUniquelyID(String uniquelyID);


}
