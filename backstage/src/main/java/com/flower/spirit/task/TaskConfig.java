package com.flower.spirit.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.service.CollectDataService;
import com.flower.spirit.service.ConfigService;

@Component
@EnableScheduling
public class TaskConfig implements SchedulingConfigurer {
	
	private Logger logger = LoggerFactory.getLogger(TaskConfig.class);
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private CollectDataService collectDataService;

	public static String cron;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		
		cron = "0 0 0/3 * * ?";
        Runnable task = () -> {
        	collectDataService.findMonitor();
        };
        Trigger trigger = (triggerContext) -> {
         //在这检查数据库
        	ConfigEntity data = configService.getData();
      
        	if(null !=data.getTaskcron() && !data.getTaskcron().equals("")) {
        	  	boolean validExpression = CronExpression.isValidExpression(data.getTaskcron());
        		if(validExpression) {
        			cron = data.getTaskcron();
        		}else {
        			logger.info("未设置有效定时器规则,本次不执行,将执行默认定时器");
        		}
        	  	
        	}else {
        		logger.info("未设置定时器,将执行默认定时器");
        	}    
            CronTrigger cronTrigger = new CronTrigger(cron);
            Date nextExec = cronTrigger.nextExecutionTime(triggerContext);
            return nextExec;
        };
        scheduledTaskRegistrar.addTriggerTask(task,trigger);
		
	}

}
