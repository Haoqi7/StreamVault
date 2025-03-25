package com.flower.spirit.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.FfmpegQueueDao;
import com.flower.spirit.dao.FfmpegQueueDataDao;
import com.flower.spirit.entity.FfmpegQueueDataEntity;
import com.flower.spirit.entity.FfmpegQueueEntity;
import com.flower.spirit.utils.Aria2Util;
import com.flower.spirit.utils.CommandUtil;
import com.flower.spirit.utils.FileUtils;


@Service
public class FfmpegQueueService {
	
	private static Logger logger = LoggerFactory.getLogger(FfmpegQueueService.class);
	
	
	@Autowired
	private  FfmpegQueueDataDao ffmpegQueueDataDao;
	
	
	@Autowired
	private  FfmpegQueueDao ffmpegQueueDao;
	
	/*
	 * 
	 * 错误json 
	 * {"id":"9550912718846233","jsonrpc":"2.0","error":{"code":1,"message":"GID 1259397694693021 is not found"}}
	 * 
	 * 完成
	 * {"id":"2089886591013176","jsonrpc":"2.0","result":{"completedLength":"4755777","status":"complete","totalLength":"4755777"}}
	 * 
	 * 进行
	 * {"id":"7601640503497689","jsonrpc":"2.0","result":{"completedLength":"688128","status":"active","totalLength":"4755777"}}
	 * 
	 * 
	 * */
	
	public void taskCheckStatus() {
		List<FfmpegQueueDataEntity> list =  ffmpegQueueDataDao.findByStatus("0");
		for(FfmpegQueueDataEntity entity:list) {
			JSONObject createTaskStatus = Aria2Util.createTaskStatus(entity.getTaskid(), Global.a2_token);
			String sendMessage = Aria2Util.sendMessage(Global.a2_link, createTaskStatus);
			JSONObject parseObject = JSONObject.parseObject(sendMessage);
			JSONObject result = parseObject.getJSONObject("result");
			if(result != null) {
				String status = result.getString("status");
				if(status.equals("complete")) {
					Optional<FfmpegQueueEntity> findById = ffmpegQueueDao.findById(entity.getQueueid());
					if(findById.isPresent()) {
						FfmpegQueueEntity ffmpegQueueEntity = findById.get();
						if(entity.getFiletype().equals("v")) {
							ffmpegQueueEntity.setVideostatus("1");
						}
						if(entity.getFiletype().equals("a")) {
							ffmpegQueueEntity.setAudiostatus("1");
						}
						ffmpegQueueDao.save(ffmpegQueueEntity);
					}
					entity.setStatus("1");
					ffmpegQueueDataDao.save(entity);
				}
				
			}
//			System.out.println(sendMessage);
		}
	}

	/**
	 * 合并任务
	 */
	public void taskMergeTasks() {
		List<FfmpegQueueEntity> list =  ffmpegQueueDao.findByVideostatusAndAudiostatusAndStatus("1","1","0");
		if(list.size()>0) {
			logger.info("存在下载完成的任务 开始进入合并");
			for(FfmpegQueueEntity entity:list) {
				String audio ="";
				String video ="";
				List<FfmpegQueueDataEntity> data =  ffmpegQueueDataDao.findByQueueid(entity.getId());
				for(FfmpegQueueDataEntity l:data) {
					if(l.getFiletype().equals("v")) {
						video = l.getFilepath();
						ffmpegQueueDataDao.delete(l);
					}
					if(l.getFiletype().equals("a")) {
						audio = l.getFilepath();
						ffmpegQueueDataDao.delete(l);
					}
					
				}
				//执行合并任务
				//判断是否存在 存在就删掉
				FileUtils.deleteFile(entity.getFilepath());
				System.out.println("ffmpeg -i "+video+" -i "+audio+" -c:v copy -c:a copy -f mp4 "+entity.getFilepath());
				CommandUtil.command("ffmpeg -i "+video+" -i "+audio+" -c:v copy -c:a copy -f mp4 "+entity.getFilepath());
				//删除任务
				FileUtils.deleteFile(video);
				FileUtils.deleteFile(audio);
				//合并结束修改装改
				entity.setStatus("1");
				ffmpegQueueDao.save(entity);
			}
		}
	}
	
	
	public void clearTask() {
		ffmpegQueueDao.deleteAll();
		ffmpegQueueDataDao.deleteAll();
	}
	

}
