package com.flower.spirit.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flower.spirit.service.FfmpegQueueService;

@Configuration
@Component
public class TaskService {
	
	@Autowired
	private FfmpegQueueService ffmpegQueueService;
	
	
	@Scheduled(fixedDelay = 1000*5)
	public void taskCheckStatus() {
		ffmpegQueueService.taskCheckStatus();
	}
	
	@Scheduled(fixedDelay = 1000*5)
	public void taskMergeTasks() {
		ffmpegQueueService.taskMergeTasks();
	}

}
