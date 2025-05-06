package com.flower.spirit.task;

import java.util.Date;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.flower.spirit.config.Global;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.service.CollectDataService;
import com.flower.spirit.service.ConfigService;
import com.flower.spirit.utils.DateUtils;

@Component
public class TaskConfig implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TaskConfig.class);

    @Autowired
    private ConfigService configService;

    @Autowired
    private CollectDataService collectDataService;

    private String cachedCron = "0 0 0/3 * * ?";
    private long lastUpdateTime = 0;
    private final long refreshInterval = 60 * 1000;

    // 单线程线程池，最多排队10个任务，排满就丢最老的
    private final ExecutorService executor = new ThreadPoolExecutor(
            1,
            1,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(10),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Runnable task = () -> {
            executor.submit(() -> {
                try {
                    logger.info("开始执行任务...");
                    collectDataService.findMonitor();
                    logger.info("任务执行完成。");
                } catch (Exception e) {
                    logger.error("任务执行出错：", e);
                }
            });
        };

        Trigger trigger = (triggerContext) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > refreshInterval) {
                try {
                    ConfigEntity config = configService.getData();
                    if (config != null && config.getTaskcron() != null && CronExpression.isValidExpression(config.getTaskcron())) {
                        if (!config.getTaskcron().equals(cachedCron)) {
                            logger.info("检测到新的cron表达式：{}", config.getTaskcron());
                            cachedCron = config.getTaskcron();
                        }
                    } else {
                        logger.warn("未检测到有效cron表达式，继续使用上一次的定时器规则：{}", cachedCron);
                    }
                    lastUpdateTime = currentTime;
                } catch (Exception e) {
                    logger.error("读取配置出错，继续使用上一次的定时器规则：{}", cachedCron, e);
                }
            }
            Date nextExecutionTime = new CronTrigger(cachedCron).nextExecutionTime(triggerContext);
            String nextDateStr = DateUtils.formatDate(nextExecutionTime, "yyyy-MM-dd HH:mm:ss");
            Global.tasknexttime = nextDateStr;
            logger.info("下一次定时任务执行时间为：{}", nextDateStr);
            return nextExecutionTime;
        };

        taskRegistrar.addTriggerTask(task, trigger);
    }
}

