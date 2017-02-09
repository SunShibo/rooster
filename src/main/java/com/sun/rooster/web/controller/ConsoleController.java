package com.sun.rooster.web.controller;

import com.sun.rooster.core.DefaultTaskScheduler;
import com.sun.rooster.entity.TaskDO;
import com.sun.rooster.entity.dto.ResultDTO;
import com.sun.rooster.entity.dto.ResultDTOBuilder;
import com.sun.rooster.entity.param.AddMessageParam;
import com.sun.rooster.entity.vo.TaskMonitorVO;
import com.sun.rooster.service.TaskService;
import com.sun.rooster.util.JsonUtils;
import com.sun.rooster.util.redisUtils.RedisLock;
import com.sun.rooster.web.controller.base.BaseCotroller;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 控制台页面
 */
@Controller
@RequestMapping("/console")
public class ConsoleController extends BaseCotroller {

    static final Logger log = LoggerFactory.getLogger(ConsoleController.class);

    @Resource(name = "defaultTaskScheduler")
    private DefaultTaskScheduler defaultTaskScheduler;

    @Resource(name = "taskService")
    private TaskService taskService;


    /**
     * 主页
     * @param response
     */
    @RequestMapping("/home")
    public ModelAndView home(HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("home/home");
        return mav;
    }

    @RequestMapping("/monitor")
    public void taskMonitor(HttpServletResponse response) {
        TaskMonitorVO taskNum = taskService.findTaskNum();
        JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.success(taskNum));
        super.safeJsonPrint(response , result.toString());
    }

    @RequestMapping("/find")
    public void findItem(HttpServletResponse response , String uniquelyID) {
        TaskDO task = taskService.findTask(uniquelyID);
        JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.success(task));
        super.safeJsonPrint(response , result.toString());
    }

    @RequestMapping("/find/failed")
    public void findAllFailedWait(HttpServletResponse response) {
        List<TaskDO> allFailedItem = taskService.findAllFailedItem();
        JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.success(allFailedItem));
        super.safeJsonPrint(response , result.toString());
    }


}
