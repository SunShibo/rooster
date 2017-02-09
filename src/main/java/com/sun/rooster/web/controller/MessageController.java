package com.sun.rooster.web.controller;

import com.sun.rooster.core.DefaultTaskScheduler;
import com.sun.rooster.entity.dto.ResultDTO;
import com.sun.rooster.entity.dto.ResultDTOBuilder;
import com.sun.rooster.entity.param.AddMessageParam;
import com.sun.rooster.service.TaskService;
import com.sun.rooster.task.HttpCallbackTask;
import com.sun.rooster.util.JsonUtils;
import com.sun.rooster.util.redisUtils.RedisLock;
import com.sun.rooster.util.redisUtils.RedissonHandler;
import com.sun.rooster.web.controller.base.BaseCotroller;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ScheduledFuture;

/**
 * 消息Controller
 */
@Controller
@RequestMapping("/message")
public class MessageController extends BaseCotroller {

    static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Resource(name = "defaultTaskScheduler")
    private DefaultTaskScheduler defaultTaskScheduler;

    @Resource(name = "taskService")
    private TaskService taskService;

    /**
     * 锁的前缀
     */
    final String LOCK_PREFIX = "rooster";

    private long lockTimeout = 1000 * 10; // 10 second

    private volatile RedisLock lock = new RedisLock();

    /**
     * 添加消息
     *
     * @param response
     */
    @RequestMapping("/add")
    public void add(HttpServletResponse response, AddMessageParam amp) {

        // 校验参数
        if (amp == null || StringUtils.isBlank(amp.getUniquelyID()) || StringUtils.isBlank(amp.getCallbackURL())
                || StringUtils.isBlank(amp.getCallbackMethod()) || StringUtils.isBlank(amp.getTriggerMode())
                || StringUtils.isBlank(amp.getTriggerTime())) {

            JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.failure("0010001"));
            super.safeJsonPrint(response, result.toString());
            return ;
        }

        // 生产key的规则
        String redisLockKey = amp.getUniquelyID();

        try {
            // 尝试获取锁
            if (!lock.tryLock(redisLockKey, lockTimeout)) {
                JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.failure("0000002"));
                super.safeJsonPrint(response, result.toString());
                return;
            }

            // 检查key是否唯一
            if (taskService.queryTask(amp.getUniquelyID()) != null) {
                JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.failure("0010003"));
                super.safeJsonPrint(response, result.toString());
            } else {
                // 添加任务
                defaultTaskScheduler.addTask(amp);
                log.debug("add message > id:{}, add message success. source data:{}", amp.getUniquelyID(), amp.toString());

                JSONObject callbackParam = new JSONObject();
                callbackParam.put("uniquelyID" , amp.getUniquelyID()) ;
                JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.success(callbackParam));
                super.safeJsonPrint(response, result.toString());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.failure("0010002", e));
            super.safeJsonPrint(response, result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.failure("0010002", e));
            super.safeJsonPrint(response, result.toString());
        }
        lock.releaseLock(redisLockKey);
    }

    /**
     * 取消任务
     * @param response
     * @param uniquelyID
     */
    @RequestMapping("/cancel")
    public void cancel(HttpServletResponse response, String uniquelyID) {
        if (StringUtils.isBlank(uniquelyID)) {
            JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.failure("0010001"));
            super.safeJsonPrint(response, result.toString());
            return ;
        }

        boolean cancel = defaultTaskScheduler.cancelTask(uniquelyID);

        if (cancel) {
            JSONObject callbackParam = new JSONObject();
            callbackParam.put("uniquelyID" , uniquelyID) ;
            JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.success(callbackParam));
            super.safeJsonPrint(response, result.toString());
            return ;
        } else {
            JSONObject result = JsonUtils.getJsonObject4JavaPOJO(ResultDTOBuilder.failure("0010001"));
            super.safeJsonPrint(response, result.toString());
            return ;
        }
    }

    public String getKey(String id) {
        return LOCK_PREFIX + id;
    }
}
