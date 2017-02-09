package com.sun.rooster.core;

import com.sun.rooster.entity.bo.TaskBO;
import com.sun.rooster.entity.param.AddMessageParam;
import com.sun.rooster.service.TaskService;
import com.sun.rooster.task.HttpCallbackTask;
import com.sun.rooster.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Component;

import javax.activation.UnsupportedDataTypeException;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

/**
 * DefaultTaskScheduler
 * Created by Shibo on 17/1/10.
 */
@Component("defaultTaskScheduler")
public class DefaultTaskScheduler  implements ApplicationContextAware {


    static final Logger log = LoggerFactory.getLogger(DefaultTaskScheduler.class);

    @Resource(name = "taskService")
    private TaskService taskService;

    private ApplicationContext applicationContext;

    private volatile static ConcurrentHashMap<String, ScheduledFuture> scheduledFutureMap =
            new ConcurrentHashMap<String, ScheduledFuture>();

    private final static int POOL_SIZE = 1024;

    private final ConcurrentTaskScheduler SCHEDULER = new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(POOL_SIZE));

    private final long DELAY = 8000;

    private DefaultTaskScheduler() {
    }

    /**
     * 添加新任务
     */
    public void addTask(AddMessageParam amp) throws UnsupportedDataTypeException {
        if (amp.getCallbackMethod().equals("http")) {

            HttpCallbackTask task = (HttpCallbackTask)this.applicationContext.getBean("httpCallbackTask");
            task.setAmp(amp);

            if (amp.getTriggerMode().equals("single")) {
                Date time = DateUtils.parseDate(amp.getTriggerTime(), DateUtils.LONG_DATE_PATTERN);
                ScheduledFuture<?> schedule = SCHEDULER.scheduleWithFixedDelay(task, time, DELAY);
                scheduledFutureMap.put(amp.getUniquelyID(), schedule);
                taskService.createItem(amp);
            } else {
                throw new UnsupportedDataTypeException("不支持数据类型:triggerMode" + amp.getTriggerMode());
            }
        }
    }

    /**
     * 取消任务
     *
     * @param uniquelyID
     * @return
     */
    public boolean cancelTask(String uniquelyID) {
        ScheduledFuture scheduledFuture = scheduledFutureMap.get(uniquelyID);
        boolean cancel = scheduledFuture.isCancelled();
        if (!cancel) {
            cancel = scheduledFuture.cancel(false);
            scheduledFutureMap.remove(uniquelyID);
            taskService.cancel(uniquelyID);
        }
        return cancel;
    }



    public void isCancel(String uniquelyID) {
        ScheduledFuture scheduledFuture = scheduledFutureMap.get(uniquelyID);
        System.out.println(scheduledFuture.isCancelled());
    }

    /**
     * 重新加载任务
     */
    public void reloadTaskFromDB() {
        List<TaskBO> noCompleteTask = taskService.findNoCompleteTask();
        for (TaskBO taskBO : noCompleteTask) {
            HttpCallbackTask task = (HttpCallbackTask)this.applicationContext.getBean("httpCallbackTask");
            task.setAmp(taskBO);
            task.setRequestFailedTimes(taskBO.getFailedTimes());
            if (taskBO.getTriggerMode().equals("single")) {
                Date time = DateUtils.parseDate(taskBO.getTriggerTime(), DateUtils.LONG_DATE_PATTERN);
                ScheduledFuture<?> schedule = SCHEDULER.scheduleWithFixedDelay(task, time, DELAY);
                scheduledFutureMap.put(taskBO.getUniquelyID(), schedule);
                log.debug("reload task from DB > id:{}, add task success", taskBO.getUniquelyID());
                System.out.println("reload task from DB > id:{" + taskBO.getUniquelyID() + "}, add task success");
            } else {
//                throw new UnsupportedDataTypeException("不支持数据类型:triggerMode" + taskBO.getTriggerMode());
            }
        }
    }

    /**
     * 任务执行完成
     *
     * @param uniquelyID
     * @return
     */
    public boolean afterCompletedTask(String uniquelyID) {

        ScheduledFuture scheduledFuture = scheduledFutureMap.get(uniquelyID);
        boolean cancel = scheduledFuture.isCancelled();
        if (!cancel) {
            cancel = scheduledFuture.cancel(false);
            scheduledFutureMap.remove(uniquelyID);
            taskService.complete(uniquelyID);
        }
        return cancel;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }
}
