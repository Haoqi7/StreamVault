package com.flower.spirit.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.FfmpegQueueDao;
import com.flower.spirit.dao.FfmpegQueueDataDao;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.FfmpegQueueDataEntity;
import com.flower.spirit.entity.FfmpegQueueEntity;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.Aria2Util;
import com.flower.spirit.utils.BiliUtil;
import com.flower.spirit.utils.CommandUtil;
import com.flower.spirit.utils.DateUtils;
import com.flower.spirit.utils.DouUtil;
import com.flower.spirit.utils.EmbyMetadataGenerator;
import com.flower.spirit.utils.FileUtil;
import com.flower.spirit.utils.FileUtils;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.Steamcmd;
import com.flower.spirit.utils.StringUtil;
import com.flower.spirit.utils.TikTokUtil;
import com.flower.spirit.utils.URLUtil;
import com.flower.spirit.utils.YouTuBeUtil;
import com.flower.spirit.utils.YtDlpUtil;

/**
 * @author flower
 *         废弃 重写
 */
@Service
public class AnalysisService {

	@Autowired
	private VideoDataDao videoDataDao;

	private Logger logger = LoggerFactory.getLogger(AnalysisService.class);

	@Autowired
	private ProcessHistoryService processHistoryService;

	private ExecutorService steamcmd = Executors.newFixedThreadPool(1);

	private ExecutorService douyin = Executors.newFixedThreadPool(1);

	private ExecutorService bilibili = Executors.newFixedThreadPool(1);

	private ExecutorService ytdlp = Executors.newFixedThreadPool(1);

	@Autowired
	private FfmpegQueueDao ffmpegQueueDao;

	@Autowired
	private FfmpegQueueDataDao ffmpegQueueDataDao;

	/**
	 * 解析资源
	 * 
	 * @param token
	 * @param video
	 * @throws Exception
	 */
	public void processingVideos(String token, String video) throws Exception {
		if (null == token || !token.equals(Global.apptoken)) {
			logger.error("无效的token");
			return;
		}

		logger.info("解析开始~原地址:" + video);
		String platform = this.getPlatform(video);
		String url = this.getUrl(video);
		Map<String, Runnable> platformHandlers = new HashMap<>();
		platformHandlers.put("哔哩", () -> executeTask(bilibili, () -> this.bilivideo(platform, url)));
//		platformHandlers.put("抖音", () -> executeTask(douyin, () -> this.dyvideo(platform, url)));
//		platformHandlers.put("steam", () -> executeTask(steamcmd, () -> this.steamwork(video)));
//		platformHandlers.put("tiktok", () -> executeTask(douyin, () -> this.tiktok(platform, url)));
//		platformHandlers.put("YouTube", () -> executeTask(ytdlp, () -> this.YouTube(platform, url)));
//		platformHandlers.put("instagram", () -> executeTask(ytdlp, () -> this.instagram(platform, url)));
//		platformHandlers.put("twitter", () -> executeTask(ytdlp, () -> this.twitter(platform, url)));
		// 获取并执行对应平台的处理逻辑
		Runnable handler = platformHandlers.get(platform);
		if (handler != null) {
			handler.run();
		} else {
			logger.warn("不支持的平台类型: " + platform);
		}
	}

	/**
	 * 在指定线程池中执行任务并处理异常
	 * 
	 * @param executor 线程池
	 * @param task     要执行的任务
	 */
	private void executeTask(ExecutorService executor, ExceptionRunnable task) {
		executor.execute(() -> {
			try {
				task.run();
			} catch (Exception e) {
				logger.error("任务执行失败: " + e.getMessage(), e);
			}
		});
	}

	/**
	 * 可抛出异常的Runnable接口
	 */
	@FunctionalInterface
	private interface ExceptionRunnable {
		void run() throws Exception;
	}

	private void twitter(String platform, String url) {
		ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, url, platform);
		try {
			String exec = YtDlpUtil.exec(url);
			int lastIndexOf = exec.lastIndexOf("}}");
			exec = exec.substring(0, lastIndexOf + 2);
			JSONObject parseObject = JSONObject.parseObject(exec);
			String thumbnail = parseObject.getString("thumbnail");
			String videourl = parseObject.getString("url");
			String id = parseObject.getString("id");
			String description = parseObject.getString("description");
			String title = parseObject.getString("title");
			String filename = StringUtil.getFileName(title, id);
			String videounrealaddr = FileUtil.createDirFile(FileUtil.savefile, ".mp4", filename,
					Global.platform.twitter.name());
			String coverunaddr = FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename,
					Global.platform.twitter.name());
			String filepath = FileUtil.createDirFile("/app/resources", ".mp4", filename,
					Global.platform.twitter.name());
			if (Global.downtype.equals("http")) {
				// http 需要创建临时目录
				String newpath = FileUtil.createTemporaryDirectory(Global.platform.twitter.name(), filename,
						"/app/resources");
				FileUtils.createDirectory(newpath);
				HttpUtil.downLoadFromUrl(videourl, filename + ".mp4", newpath);
			}
			if (Global.downtype.equals("a2")) {
				// a2 不需要 目录有a2托管 此处路径应该可以优化
				String a2path = FileUtil.createTemporaryDirectory(Global.platform.twitter.name(), filename,
						Global.down_path);
				String videores = Aria2Util.sendMessage(Global.a2_link,
						Aria2Util.createBiliparameter(videourl, a2path, filename + ".mp4", Global.a2_token));
			}
			HttpUtil.downLoadFromUrl(thumbnail, filename + ".jpg",
					FileUtil.createTemporaryDirectory(Global.platform.twitter.name(), filename, Global.uploadRealPath) + "/");
			// 建档
			VideoDataEntity videoDataEntity = new VideoDataEntity(id, title, description, platform, coverunaddr,
					filepath, videounrealaddr, url);
			videoDataDao.save(videoDataEntity);
			processHistoryService.saveProcess(saveProcess.getId(), url, platform);

		} catch (Exception e) {

		}
		processHistoryService.saveProcess(saveProcess.getId(), url, platform);

	}

	private void instagram(String platform, String url) {
		ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, url, platform);
		try {
			String exec = YtDlpUtil.exec(url);
			JSONObject parseObject = JSONObject.parseObject(exec);
			String thumbnail = parseObject.getString("thumbnail");
			String videourl = parseObject.getString("url");
			String id = parseObject.getString("id");
			String description = parseObject.getString("description");
			String title = parseObject.getString("title");
			String filename = StringUtil.getFileName(title, id);
			String videounrealaddr = FileUtil.createDirFile(FileUtil.savefile, ".mp4", filename,
					Global.platform.instagram.name());
			String coverunaddr = FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename,
					Global.platform.instagram.name());
			String filepath = FileUtil.createDirFile("/app/resources", ".mp4", filename,
					Global.platform.instagram.name());
			if (Global.downtype.equals("http")) {
				// http 需要创建临时目录
				String newpath = FileUtil.createTemporaryDirectory(Global.platform.instagram.name(), filename,
						"/app/resources");
				FileUtils.createDirectory(newpath);
				HttpUtil.downLoadFromUrl(videourl, filename + ".mp4", newpath);
			}
			if (Global.downtype.equals("a2")) {
				// a2 不需要 目录有a2托管 此处路径应该可以优化
				String a2path = FileUtil.createTemporaryDirectory(Global.platform.instagram.name(), filename,
						Global.down_path);
				String videores = Aria2Util.sendMessage(Global.a2_link,
						Aria2Util.createBiliparameter(videourl, a2path, filename + ".mp4", Global.a2_token));
			}
			HttpUtil.downLoadFromUrl(thumbnail, filename + ".jpg",
					FileUtil.createTemporaryDirectory(Global.platform.instagram.name(), filename, Global.uploadRealPath)
							+ "/");
			// 建档
			VideoDataEntity videoDataEntity = new VideoDataEntity(id, title, description, platform, coverunaddr,
					filepath, videounrealaddr, url);
			videoDataDao.save(videoDataEntity);
			processHistoryService.saveProcess(saveProcess.getId(), url, platform);

		} catch (Exception e) {

		}
		processHistoryService.saveProcess(saveProcess.getId(), url, platform);
	}

	private void YouTube(String platform, String youtube) throws Exception {
		ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, youtube, platform);
		try {
			String exec = YouTuBeUtil.exec("https://www.youtube.com/watch?v=f7EDFdA10pg");
			JSONObject parseObject = JSONObject.parseObject(exec);
			String a_link = "";
			String v_linl = "";
			String title = parseObject.getString("title");
			String description = parseObject.getString("description");
			String display_id = parseObject.getString("display_id");
			String thumbnail = parseObject.getString("thumbnail");
			if (parseObject.containsKey("requested_formats")) {
				JSONArray jsonArray = parseObject.getJSONArray("requested_formats");
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String ext = jsonObject.getString("video_ext");
					String url = jsonObject.getString("url");
					if (ext.equals("none")) {
						a_link = url;
					}
					if (ext.equals("webm")) {
						v_linl = url;
					}
				}
			}
			if (a_link.equals("") || v_linl.equals("")) {
				logger.error(youtube + "解析异常");
				return;
			}
			String filename = StringUtil.getFileName(title, display_id);
			String videounrealaddr = FileUtil.createDirFile(FileUtil.savefile, ".mp4", filename,
					Global.platform.youtube.name());
			String coverunaddr = FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename,
					Global.platform.youtube.name());
			String filepath = FileUtil.createDirFile("/app/resources", ".mp4", filename,
					Global.platform.youtube.name());
			if (Global.downtype.equals("http")) {
				// http 需要创建临时目录

				String newpath = FileUtil.createTemporaryDirectory(Global.platform.youtube.name(), filename,
						"/app/resources");
				FileUtils.createDirectory(newpath);
				HttpUtil.downLoadFromUrl(v_linl, filename + "-video.webm", newpath);
				HttpUtil.downLoadFromUrl(a_link, filename + "-audio.weba", newpath);
				// 此处可以直接合并 由于http 不是异步
				CommandUtil.command("ffmpeg -i " + newpath + File.separator + filename + "-video.webm -i " + newpath
						+ File.separator + filename + "-audio.weba -c:v copy -c:a copy -f mp4 " + filepath);
				// 删除
				FileUtils.deleteFile(newpath + File.separator + filename + "-video.m4s");
				FileUtils.deleteFile(newpath + File.separator + filename + "-audio.m4s");
			}
			if (Global.downtype.equals("a2")) {
				// a2 不需要 目录有a2托管 此处路径应该可以优化
				String a2path = FileUtil.createTemporaryDirectory(Global.platform.youtube.name(), filename,
						Global.down_path);
				String videores = Aria2Util.sendMessage(Global.a2_link,
						Aria2Util.createBiliparameter(v_linl, a2path, filename + "-video.webm", Global.a2_token));
				String audiores = Aria2Util.sendMessage(Global.a2_link,
						Aria2Util.createBiliparameter(a_link, a2path, filename + "-audio.weba", Global.a2_token));
				// 保存到FfmpegQueueEntity队列
				logger.info("youtube仅支持dash 提交到ffmpeg队列等待下载完成合并-前置任务结束");
				FfmpegQueueEntity ffmpegQueueEntity = new FfmpegQueueEntity();
				ffmpegQueueEntity.setVideoid(display_id);
				ffmpegQueueEntity.setVideoname(title);
				ffmpegQueueEntity
						.setPendingfolder(FileUtil.createTemporaryDirectory(Global.platform.youtube.name(), filename));
				ffmpegQueueEntity.setAudiostatus("0");
				ffmpegQueueEntity.setVideostatus("0");
				ffmpegQueueEntity.setFilepath(FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename,
						Global.platform.youtube.name()));
				ffmpegQueueEntity.setStatus("0");
				ffmpegQueueEntity.setCreatetime(DateUtils.getDateTime());
				ffmpegQueueDao.save(ffmpegQueueEntity);
				// 数据
				FfmpegQueueDataEntity videoData = new FfmpegQueueDataEntity();
				videoData.setQueueid(ffmpegQueueEntity.getId());
				videoData.setTaskid(JSONObject.parseObject(videores).getString("result"));
				videoData.setFiletype("v");
				videoData.setStatus("0");
				videoData.setFilepath(FileUtil.createTemporaryDirectory(Global.platform.youtube.name(), filename) + "/"
						+ filename + "-video.webm");
				videoData.setCreatetime(DateUtils.getDateTime());
				ffmpegQueueDataDao.save(videoData);
				FfmpegQueueDataEntity audioData = new FfmpegQueueDataEntity();
				audioData.setQueueid(ffmpegQueueEntity.getId());
				audioData.setTaskid(JSONObject.parseObject(audiores).getString("result"));
				audioData.setFiletype("a");
				audioData.setStatus("0");
				audioData.setFilepath(FileUtil.createTemporaryDirectory(Global.platform.youtube.name(), filename) + "/"
						+ filename + "-audio.weba");
				audioData.setCreatetime(DateUtils.getDateTime());
				ffmpegQueueDataDao.save(audioData);
			}
			HttpUtil.downLoadFromUrl(thumbnail, filename + ".jpg",
					FileUtil.createTemporaryDirectory(Global.platform.youtube.name(), filename, Global.uploadRealPath) + "/");
			// 建档
			VideoDataEntity videoDataEntity = new VideoDataEntity(display_id, title, description, platform, coverunaddr,
					filepath, videounrealaddr, youtube);
			videoDataDao.save(videoDataEntity);
			processHistoryService.saveProcess(saveProcess.getId(), youtube, platform);

		} catch (Exception e) {
			throw e;
			// logger.error(youtube+"解析异常");
		}

	}

	private void tiktok(String platform, String url) throws IOException {
		ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, url, platform);
		String tikTokVideoId = TikTokUtil.getTikTokVideoId(url);
		if (tikTokVideoId != null) {
			Map<String, String> videoData = TikTokUtil.getVideoData(tikTokVideoId);
			String awemeid = videoData.get("awemeid");
			String videoplay = videoData.get("videoplay");
			String desc = videoData.get("desc");
			String cover = videoData.get("cover");

			String filename = StringUtil.getFileName(desc, awemeid);
			String videofile = FileUtil.createDirFile(Global.down_path, ".mp4", filename,
					Global.platform.tiktok.name());
			String videounrealaddr = FileUtil.createDirFile(FileUtil.savefile, ".mp4", filename,
					Global.platform.tiktok.name());
			String coverunaddr = FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename,
					Global.platform.tiktok.name());

			if (Global.downtype.equals("a2")) {
				Aria2Util.sendMessage(Global.a2_link,
						Aria2Util.createparameter(videoplay, FileUtil
								.createTemporaryDirectory(Global.platform.tiktok.name(), filename, Global.down_path),
								filename + ".mp4", Global.a2_token));
			}
			if (Global.downtype.equals("http")) {
				// 内置下载器
				HttpUtil.downDouFromUrl(videoplay, filename + ".mp4", FileUtil.createTemporaryDirectory(
						Global.platform.tiktok.name(), filename, FileUtil.uploadRealPath), null);
			}
			videofile = FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename,
					Global.platform.tiktok.name());
			HttpUtil.downLoadFromUrl(cover, filename + ".jpg",
					FileUtil.createTemporaryDirectory(Global.platform.tiktok.name(), filename, Global.uploadRealPath) + "/");
			VideoDataEntity videoDataEntity = new VideoDataEntity(awemeid, desc, desc, platform, coverunaddr, videofile,
					videounrealaddr, url);
			videoDataDao.save(videoDataEntity);
			logger.info("下载流程结束");
		}
		processHistoryService.saveProcess(saveProcess.getId(), url, platform);

	}

	/**
	 * 不支持 从本处登录帐号并验证 二次令牌 请先使用 docker exec 进行登录帐号 在配置帐号密码进行下载
	 * 支持从steam 工坊下载
	 * 
	 * @param video
	 * @throws IOException
	 */
	private void steamwork(String video) throws IOException {
		File file = new File("/app/db/account.txt");
		if (file.exists()) {
			String account = "";
			String password = "";
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String readStr;
				while ((readStr = reader.readLine()) != null) {
					if (readStr.contains("account")) {
						account = readStr.replaceAll("account:", "");
					}
					if (readStr.contains("password")) {
						password = readStr.replaceAll("password:", "");
					}
				}
				reader.close();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			String wallpaperId = getWallpaperId(video);
			VideoDataEntity findByVideoid = findByVideoid(video, "wallpaper");
			if (findByVideoid != null) {
				logger.info("重复ID 不下载");
				return;
			}
			String steamcmd = CommandUtil.steamcmd(account, password, wallpaperId);
			if (!steamcmd.equals("")) {
				// 下载完成 cp 文件
				logger.info("ok");
				String localapp = Global.uploadRealPath + "wallpaper/" + DateUtils.getDate("yyyy") + "/"
						+ DateUtils.getDate("MM");
				FileUtils.createDirectory(localapp);
				CommandUtil.command("mv " + steamcmd + " " + localapp);
				// 复制完成 建档
				String json = localapp + "/" + wallpaperId + "/project.json";
				try {
					String jsonContent = new String(Files.readAllBytes(Paths.get(json)));
					JSONObject jsonObject = JSON.parseObject(jsonContent);
					String filename = jsonObject.getString("file");
					String previewname = jsonObject.getString("preview");
					String title = jsonObject.getString("title");
					String cosaddr = Global.savefile + "wallpaper/" + DateUtils.getDate("yyyy") + "/" + DateUtils.getDate("MM")
							+ "/" + wallpaperId;
					VideoDataEntity videoDataEntity = new VideoDataEntity(wallpaperId, title, title, "wallpaper",
							cosaddr + "/" + previewname, localapp + "/" + wallpaperId + "/" + filename,
							cosaddr + "/" + filename, video);
					videoDataDao.save(videoDataEntity);
					logger.info("下载流程结束");
				} catch (IOException e) {
					logger.info("建档异常-");
				}
			}
		} else {
			// 文件不存在 认为使用screen 模式
			String wallpaperId = getWallpaperId(video);
			VideoDataEntity findByVideoid = findByVideoid(video, "wallpaper");
			if (findByVideoid != null) {
				logger.info("重复ID 不下载");
				return;
			}
			try {
				String execAndListening = Steamcmd.execAndListening(wallpaperId);
				if (execAndListening != null) {
					logger.info("ok");
					String localapp = Global.uploadRealPath + "wallpaper/" + DateUtils.getDate("yyyy") + "/"
							+ DateUtils.getDate("MM");
					FileUtils.createDirectory(localapp);
					CommandUtil.command("mv " + execAndListening + " " + localapp);
					// 复制完成 建档
					String json = localapp + "/" + wallpaperId + "/project.json";
					try {
						String jsonContent = new String(Files.readAllBytes(Paths.get(json)));
						JSONObject jsonObject = JSON.parseObject(jsonContent);
						String filename = jsonObject.getString("file");
						String previewname = jsonObject.getString("preview");
						String title = jsonObject.getString("title");
						String cosaddr = Global.savefile + "wallpaper/" + DateUtils.getDate("yyyy") + "/"
								+ DateUtils.getDate("MM") + "/" + wallpaperId;
						VideoDataEntity videoDataEntity = new VideoDataEntity(wallpaperId, title, title, "wallpaper",
								cosaddr + "/" + previewname, localapp + "/" + wallpaperId + "/" + filename,
								cosaddr + "/" + filename, video);
						videoDataDao.save(videoDataEntity);
						logger.info("下载流程结束");
					} catch (IOException e) {
						logger.info("建档异常-");
					}
				}
			} catch (Exception e) {
				logger.info("系统异常");
			}
		}

	}

	/**
	 * 哔哩解析
	 * 
	 * @param platform
	 * @param video
	 * @throws Exception
	 */
	public void bilivideo(String platform, String video) throws Exception {
		logger.info("开始解析哔哩哔哩视频: {}", video);
		ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, video, platform);
		try {
			
			// 获取视频源信息
			List<Map<String, String>> videoStreams = BiliUtil.findVideoStreaming(video, Global.bilicookies);
			System.out.println(videoStreams);
			if (videoStreams == null || videoStreams.isEmpty()) {
				logger.warn("未找到视频流信息: {}", video);
				processHistoryService.saveProcess(saveProcess.getId(), video, platform);
				return;
			}
			logger.info("找到{}个视频流", videoStreams.size());
			for (Map<String, String> videoInfo : videoStreams) {
				String cid = videoInfo.get("cid");
				String title = videoInfo.get("title");
				String desc = videoInfo.get("desc");
				String pic = videoInfo.get("pic");
				String videoPath = videoInfo.get("video");
				
				if (cid == null || cid.isEmpty() || title == null || title.isEmpty()) {
					logger.warn("视频信息不完整: cid={}, title={}", cid, title);
					continue;
				}
				// 生成文件名和路径
				String filename = StringUtil.getFileName(title, cid);
				String dir = FileUtil.generateDir(true,  Global.platform.bilibili.name(), true, filename, null, null);
				String dbdir = FileUtil.generateDir(false, Global.platform.bilibili.name(), true, filename, null, null);
				String coverunaddr =	FileUtil.generateDir(false, Global.platform.bilibili.name(), true, filename, null, "jpg");
				String videounaddr =	FileUtil.generateDir(false, Global.platform.bilibili.name(), true, filename, null, "mp4");
				// 下载封面
				try {
					
					HttpUtil.downBiliFromUrl(pic, filename + ".jpg",dir);
					logger.debug("封面下载完成: {}", filename);
				} catch (Exception e) {
					logger.warn("封面下载失败: {}, 原因: {}", filename, e.getMessage());
				}
				//单视频  生成nfo文件
				//此处还要下载up 头像  获取owner
				JSONObject owner =  JSONObject.parseObject(videoInfo.get("owner"));
				String upface = owner.getString("face");
				String upname = owner.getString("name");
				String upmid = owner.getString("mid");
				String ctime = videoInfo.get("ctime");
				//下载up 头像  up头像不参与数据 只参与nfo
				HttpUtil.downBiliFromUrl(upface, "upcover.jpg",dir);
				String uplocal =  dir+"upcover.jpg";
				String piclocal = dir+filename+".jpg";
				EmbyMetadataGenerator.createBillNfo(upname,uplocal,upmid,ctime,cid,title,desc,piclocal);
				// 建档
				VideoDataEntity videoDataEntity = new VideoDataEntity(cid, title, desc, platform, coverunaddr,
						videoPath, videounaddr, video);
				videoDataDao.save(videoDataEntity);
				logger.info("视频 {} 处理完成", title);
			}
			logger.info("哔哩哔哩视频解析下载流程结束");
		} catch (Exception e) {
			logger.error("哔哩哔哩视频解析失败: " + e.getMessage(), e);
			throw e;
		} finally {
			// 确保处理历史记录被更新
			processHistoryService.saveProcess(saveProcess.getId(), video, platform);
		}
	}

	/**
	 * 抖音解析
	 * 
	 * @param platform
	 * @param video
	 * @throws Exception
	 */
	public void dyvideo(String platform, String video) throws Exception {
		ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, video, platform);
		Map<String, String> downVideo = DouUtil.downVideo(video);

		this.putRecord(downVideo.get("awemeid"), downVideo.get("desc"), downVideo.get("videoplay"),
				downVideo.get("cover"), platform, video, downVideo.get("type"), downVideo.get("cookie"));
		System.gc();

		processHistoryService.saveProcess(saveProcess.getId(), video, platform);

	}

	/**
	 * 推送建档
	 * 
	 * @param awemeId
	 * @param desc
	 * @param playApi
	 * @param cover
	 * @param platform
	 */
	public void putRecord(String awemeId, String desc, String playApi, String cover, String platform,
			String originaladdress, String type, String cookie) {
		String filename = StringUtil.getFileName(desc, awemeId);
		String videofile = FileUtil.createDirFile(Global.down_path, ".mp4", filename, Global.platform.douyin.name());
		String videounrealaddr = FileUtil.createDirFile(FileUtil.savefile, ".mp4", filename,
				Global.platform.douyin.name());
		String coverunaddr = FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename, Global.platform.douyin.name());
		if (type.equals("client")) {
			logger.info("已使用htmlunit进行解析,下载器类型为:" + Global.downtype);
			if (Global.downtype.equals("a2")) {
				Aria2Util.sendMessage(Global.a2_link,
						Aria2Util.createparameter("https:" + playApi, FileUtil
								.createTemporaryDirectory(Global.platform.douyin.name(), filename, Global.down_path),
								filename + ".mp4", Global.a2_token));
			}
			if (Global.downtype.equals("http")) {
				// 内置下载器
				HttpUtil.downLoadFromUrl("https:" + playApi, filename + ".mp4", FileUtil
						.createTemporaryDirectory(Global.platform.douyin.name(), filename, FileUtil.uploadRealPath));
			}
			videofile = FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename,
					Global.platform.douyin.name());
			// 下载封面图当容器映射目录
			HttpUtil.downLoadFromUrl("https:" + cover, filename + ".jpg",
					FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, Global.uploadRealPath) + "/");
		}
		if (type.equals("api")) {
			logger.info("已使用api进行解析,下载器类型为:" + Global.downtype);
			if (Global.downtype.equals("a2")) {
				Aria2Util.sendMessage(Global.a2_link, Aria2Util.createDouparameter(playApi,
						FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, Global.down_path),
						filename + ".mp4", Global.a2_token, cookie));
			}
			if (Global.downtype.equals("http")) {
				// 内置下载器
				HttpUtil.downDouFromUrl(playApi, filename + ".mp4", FileUtil.createTemporaryDirectory(
						Global.platform.douyin.name(), filename, FileUtil.uploadRealPath), cookie);
			}
			videofile = FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename,
					Global.platform.douyin.name());
			HttpUtil.downLoadFromUrl(cover, filename + ".jpg",
					FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, Global.uploadRealPath) + "/");
		}
		// 推送完成后建立历史资料 此处注意 a2 地址需要与spring boot 一致否则 无法打开视频
		VideoDataEntity videoDataEntity = new VideoDataEntity(awemeId, desc, desc, platform, coverunaddr, videofile,
				videounrealaddr, originaladdress);
		videoDataDao.save(videoDataEntity);
		logger.info("下载流程结束");
	}

	/**
	 * 判断是否为json
	 * 
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unused")
	public static boolean isJSONString(String str) {
		boolean result = false;
		try {
			JSONObject obj = JSONObject.parseObject(str);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/**
	 * 获得文本中的https地址
	 * 
	 * @param videourl
	 * @return
	 */
	public String findAddr(String videourl) {
		return this.getUrl(videourl);
	}

	/**
	 * 正则获取url
	 * 
	 * @param input
	 * @return
	 */
	public String getUrl(String input) {
		String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			return matcher.group();
		}
		return "";
	}

	public String getPlatform(String input) {
		if (input.contains("抖音")) {
			return "抖音";
		}
		if (input.contains("哔哩")) {
			return "哔哩";
		}
		if (input.contains("steamcommunity.com")) {
			return "steam";
		}
		return URLUtil.urlAnalysis(input);
	}

	private String getWallpaperId(String url) {
		// 创建正则表达式模式，匹配id参数的值
		Pattern pattern = Pattern.compile("\\?id=(\\d+)");
		Matcher matcher = pattern.matcher(url);

		if (matcher.find()) {
			// 提取id的值
			String idValue = matcher.group(1);
			return idValue;
		} else {
			System.out.println("ID not found in the URL.");
		}
		return null;
	}

	public VideoDataEntity findByVideoid(String id, String platform) {
		List<VideoDataEntity> findByVideoid = videoDataDao.findByVideoidAndVideoplatform(id, platform);
		if (findByVideoid.size() > 1) {
			return findByVideoid.get(0);
		}
		return null;
	}

	public static void main(String[] args) {
		String x = "1234}}aaa11";
		int lastIndexOf = x.lastIndexOf("}}");
		System.out.println(x.substring(0, lastIndexOf + 2));
	}

}
