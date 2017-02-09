package com.sun.rooster.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.rooster.core.DefaultTaskScheduler;
import com.sun.rooster.entity.param.AddMessageParam;
import com.sun.rooster.service.TaskService;
import com.sun.rooster.task.HttpCallbackTask;
import com.sun.rooster.task.Task2;
import com.sun.rooster.util.DateUtils;
import com.sun.rooster.util.redisUtils.RedissonHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by Shibo on 17/1/23.
 */
@Service("springTest")
public class SpringTest {

    @Autowired
    private TaskScheduler scheduler;

    @Resource(name = "defaultTaskScheduler")
    private DefaultTaskScheduler defaultTaskScheduler;


    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/conf/spring/application-context.xml");
        SpringTest springTest = (SpringTest)context.getBean("springTest");
        springTest.testRedis();
    }

    public void test(){
        AddMessageParam amp = new AddMessageParam();
        amp.setCallbackData("test data");
        amp.setCallbackMethod("http");
        amp.setCallbackURL("http://sunshibo.com:8080/demo-html/successD.html");
        amp.setTriggerMode("single");

        String time = DateUtils.formatDate(DateUtils.LONG_DATE_PATTERN , new Date(System.currentTimeMillis() - 6000));
        amp.setTriggerTime(time);
        amp.setUniquelyID(System.currentTimeMillis() + "");

        try {
            defaultTaskScheduler.addTask(amp);
//            DefaultTaskScheduler.getInstance().isCancel(amp.getUniquelyID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testRedis(){
        RedissonHandler.getInstance().set("user", "sunshibo", 100l);

        String  u2 = RedissonHandler.getInstance().get("user");

        System.out.println(u2);
    }

    public void addNewTask(){

        Date date = new Date(System.currentTimeMillis() - 5000);


        ScheduledFuture<?> schedule = scheduler.scheduleWithFixedDelay(new Task2(), date , 5000 );
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("wake up");
        schedule.cancel(false);
//        scheduler.
    }

    public void test2(){
        JSONObject json = JSON.parseObject("s") ;
        System.out.println(json.toString());
    }
}
