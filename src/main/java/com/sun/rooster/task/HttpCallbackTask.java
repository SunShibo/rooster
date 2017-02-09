package com.sun.rooster.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.rooster.core.DefaultTaskScheduler;
import com.sun.rooster.entity.param.AddMessageParam;
import com.sun.rooster.service.FailedService;
import com.sun.rooster.util.HttpRequest;
import com.sun.rooster.util.HttpRequestException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shibo on 17/1/23.
 */
@Component("httpCallbackTask") //定义为spring bean
@Scope("prototype")  // 定义为非单例
public class HttpCallbackTask implements Runnable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "failedService")
    private FailedService failedService;

    @Resource(name = "defaultTaskScheduler")
    private DefaultTaskScheduler defaultTaskScheduler;

    private volatile Integer requestFailedTimes = -1;
    /**
     * 添加消息时的信息
     */
    private AddMessageParam amp;

    /**
     * 通过http的方式回调消息
     */
    @Override
    public void run() {
        requestFailedTimes++;
        HttpRequest httpRequest = new HttpRequest();
        try {
            httpRequest.init();
        } catch (Exception e) {
            e.printStackTrace();
            failedService.createItem(amp.getUniquelyID() , ExceptionUtils.getFullStackTrace(e));
            logger.error("timed task > id:{}, httpRequest init failed", amp.getUniquelyID());
            return;
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("callbackData", amp.getCallbackData());
        param.put("uniquelyID", amp.getUniquelyID());
        param.put("failedTimes", requestFailedTimes);

        try {
            byte[] bytes = httpRequest.doPost(amp.getCallbackURL(), param);
            JSONObject result ;
            try {
                result = JSON.parseObject(new String(bytes));
            } catch (Exception e) {
                failedService.createItem(amp.getUniquelyID() , new String(bytes));
                System.out.println("返回结果失败 :" + new String(bytes));
                logger.debug("timed task > id:{}, callback data is incorrect:{} ", amp.getUniquelyID() , "响应报文 > " + new String(bytes));
                return;
            }
            if (result == null || result.get("success") == null || !result.get("success").equals("true")) {
                failedService.createItem(amp.getUniquelyID() , new String(bytes));
                System.out.println("返回结果失败 :" + new String(bytes));
                logger.debug("timed task > id:{}, callback data is incorrect:{} ", amp.getUniquelyID() , new String(bytes));
                return;
            }
            defaultTaskScheduler.afterCompletedTask(amp.getUniquelyID());
            logger.debug("timed task > id:{},success", amp.getUniquelyID());
            System.out.println("timed task > id:" + amp.getUniquelyID() + ",success");
        } catch (HttpRequestException e) {
            e.printStackTrace();
            failedService.createItem(amp.getUniquelyID() , ExceptionUtils.getFullStackTrace(e));
            System.out.println("返回结果失败 :" + ExceptionUtils.getFullStackTrace(e));
            logger.error("timed task > id:{}, httpRequest doPost failed. requestFailedTimes:{}", amp.getUniquelyID(), requestFailedTimes);
            return;
        }

    }

    public AddMessageParam getAmp() {
        return amp;
    }

    public void setAmp(AddMessageParam amp) {
        this.amp = amp;
    }

    public Integer getRequestFailedTimes() {
        return requestFailedTimes;
    }

    public void setRequestFailedTimes(Integer requestFailedTimes) {
        this.requestFailedTimes = requestFailedTimes;
    }

    public HttpCallbackTask(AddMessageParam amp) {
        this.amp = amp;
    }

    public HttpCallbackTask() {
    }
}
