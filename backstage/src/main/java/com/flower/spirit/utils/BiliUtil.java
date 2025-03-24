package com.flower.spirit.utils;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.FfmpegQueueDao;
import com.flower.spirit.dao.FfmpegQueueDataDao;
import com.flower.spirit.entity.FfmpegQueueDataEntity;
import com.flower.spirit.entity.FfmpegQueueEntity;

@Component
public class BiliUtil {

	private static Logger logger = LoggerFactory.getLogger(BiliUtil.class);

	@Autowired
	private FfmpegQueueDataDao ffmpegQueueDataDao;

	@Autowired
	private FfmpegQueueDao ffmpegQueueDao;

	private static BiliUtil biliUtil;

	@PostConstruct
	public void init() {
		biliUtil = this;
		biliUtil.ffmpegQueueDataDao = this.ffmpegQueueDataDao;
		biliUtil.ffmpegQueueDao = this.ffmpegQueueDao;
	}

	/**
	 * 
	 * 用于收藏夹下载
	 * 
	 * 2023/09/11 移除参数url 无用参数
	 * 2023/09/11 移除参数filepath 优化路径生成
	 * 
	 * @throws Exception
	 * 
	 */
	public static Map<String, String> findVideoStreamingNoData(Map<String, String> videoDataInfo, String token,
			String quality,String namepath) throws Exception {
		String api = buildInterfaceAddress(videoDataInfo.get("aid"), videoDataInfo.get("cid"), token, quality);
		String httpGetBili = HttpUtil.httpGetBili(api, "UTF-8", token);
		JSONObject parseObject = JSONObject.parseObject(httpGetBili);
		String filename = StringUtil.getFileName(videoDataInfo.get("title"), videoDataInfo.get("cid"));
		if ((Integer.valueOf(Global.bilibitstream) >= 80 && quality.equals("1"))
				|| parseObject.getJSONObject("data").containsKey("dash")) {
			Map<String, String> processing = processing(parseObject, videoDataInfo, filename,namepath);
			return processing;
		}
		String video = parseObject.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");
		if (Global.downtype.equals("http")) {
			HttpUtil.downBiliFromUrl(video, filename + ".mp4",FileUtil.generateDir(true, Global.platform.bilibili.name(), false, filename, namepath, null));
		}
		if (Global.downtype.equals("a2")) {
		
			Aria2Util.sendMessage(Global.a2_link,
					Aria2Util.createBiliparameter(
							video, 
							FileUtil.generateDir(Global.down_path, Global.platform.bilibili.name(), false, filename, namepath, null),
							filename + ".mp4",
							Global.a2_token));
		}
		videoDataInfo.put("video",FileUtil.generateDir(true, Global.platform.bilibili.name(), false, filename, namepath, "mp4"));
		videoDataInfo.put("videoname", filename + ".mp4");
		System.out.println(videoDataInfo);
		return videoDataInfo;
	}

	/**
	 * 解析视频信息并下载视频源文件
	 * 
	 * @param url   视频URL
	 * @param token 用户token
	 * @return 视频信息列表
	 * @throws Exception 处理过程中的异常
	 */
	public static List<Map<String, String>> findVideoStreaming(String url, String token) throws Exception {
		List<Map<String, String>> result = new ArrayList<>();

		// 获取视频基本信息
		List<Map<String, String>> videoInfoList = getVideoDataInfo(url);
		if (videoInfoList == null || videoInfoList.isEmpty()) {
			logger.warn("未找到视频信息: {}", url);
			return result;
		}

		logger.info("获取到{}个视频数据", videoInfoList.size());

		for (Map<String, String> videoInfo : videoInfoList) {
			try {
				// 获取必要参数
				String aid = videoInfo.get("aid");
				String cid = videoInfo.get("cid");
				String title = videoInfo.get("title");
				String quality = videoInfo.get("quality");

				if (aid == null || cid == null || title == null) {
					logger.warn("视频数据不完整: aid={}, cid={}, title={}", aid, cid, title);
					continue;
				}

				// 生成文件名
				String filename = StringUtil.getFileName(title, cid);

				// 构建API请求地址
				String apiUrl = buildInterfaceAddress(aid, cid, token, quality);

				// 请求视频源信息
				String response = HttpUtil.httpGetBili(apiUrl, "UTF-8", token);
				JSONObject videoData = JSONObject.parseObject(response);

				// 根据视频流类型选择处理方法
				Map<String, String> processedVideo;

				// 判断是否为DASH格式视频
				boolean isDashFormat = (Integer.valueOf(Global.bilibitstream) >= 80 && quality.equals("1")) ||
						videoData.getJSONObject("data").containsKey("dash");

				if (isDashFormat) {
					// 处理DASH格式视频
					processedVideo = processDashVideo(videoData, videoInfo, filename,null);
				} else {
					// 处理普通格式视频
					processedVideo = processNormalVideo(videoData, videoInfo, filename);
				}

				result.add(processedVideo);

				// 如果是DASH格式，通常只有一个视频
				if (isDashFormat) {
					return result;
				}
			} catch (Exception e) {
				logger.error("处理视频{}时出错: {}", videoInfo.get("title"), e.getMessage());
				throw e;
			}
		}

		return result;
	}

	/**
	 * 处理DASH格式视频
	 * 
	 * @param videoData 视频JSON数据
	 * @param videoInfo 视频基本信息
	 * @param filename  文件名
	 * @return 处理后的视频信息
	 * @throws Exception 处理过程中的异常
	 */
	private static Map<String, String> processDashVideo(JSONObject videoData, Map<String, String> videoInfo,
			String filename,String fav) throws Exception {
		return processing(videoData, videoInfo, filename,fav);
	}

	/**
	 * 处理普通格式视频
	 * 
	 * @param videoData 视频JSON数据
	 * @param videoInfo 视频基本信息
	 * @param filename  文件名
	 * @return 处理后的视频信息
	 * @throws Exception 处理过程中的异常
	 */
	private static Map<String, String> processNormalVideo(JSONObject videoData, Map<String, String> videoInfo,
			String filename) throws Exception {
		// 获取视频直链
		String videoUrl = videoData.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");

		// 根据下载类型选择下载方式
		if (Global.downtype.equals("http")) {
			// 使用HTTP直接下载
			String targetDir = FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, null, null);
			HttpUtil.downBiliFromUrl(videoUrl, filename + ".mp4", targetDir);
		} else if (Global.downtype.equals("a2")) {
			// 使用Aria2下载
			String targetDir = FileUtil.generateDir(Global.down_path, Global.platform.bilibili.name(), true, filename, null, null);
			Aria2Util.sendMessage(
					Global.a2_link,
					Aria2Util.createBiliparameter(videoUrl, targetDir, filename + ".mp4", Global.a2_token));
		}

		// 更新视频信息
		Map<String, String> result = new HashMap<>(videoInfo);
		result.put("video",FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, null, "mp4"));
		result.put("videoname", filename + ".mp4");

		return result;
	}

	/**
	 * 处理DASH格式视频，下载和合并音视频
	 * 
	 * @param videoData JSON视频数据
	 * @param videoInfo 视频信息
	 * @param filename  文件名
	 * @return 更新后的视频信息
	 * @throws Exception 处理过程中可能的异常
	 */
	private static Map<String, String> processing(JSONObject videoData, Map<String, String> videoInfo, String filename,String fav)
			throws Exception {
		// 获取音视频流URL
		JSONObject dashData = videoData.getJSONObject("data").getJSONObject("dash");
		String videoUrl = dashData.getJSONArray("video").getJSONObject(0).getString("base_url");
		String audioUrl = dashData.getJSONArray("audio").getJSONObject(0).getString("base_url");

		// 根据下载类型处理
		if (Global.downtype.equals("http")) {
			processHttpDownload(videoUrl, audioUrl, filename,fav);
		} else if (Global.downtype.equals("a2")) {
			processAria2Download(videoUrl, audioUrl, videoInfo, filename,fav);
		}

		// 更新视频信息
		Map<String, String> result = new HashMap<>(videoInfo);
		
		result.put("video",FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, null, "mp4"));
		result.put("videoname", filename + ".mp4");

		return result;
	}

	/**
	 * 使用HTTP方式下载并处理DASH格式视频
	 */
	private static void processHttpDownload(String videoUrl, String audioUrl, String filename,String fav) throws Exception {
		// 创建临时目录
		String tempDir = FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, null, null);
		String outputPath =  FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, null, "mp4");
		
		
		if(fav != null) {
			 tempDir = FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, fav, null);
			 outputPath =  FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, fav, "mp4");
		}
		

		// 确保目录存在
		FileUtils.createDirectory(tempDir);

		// 下载音视频文件
		String videoFile = tempDir + File.separator + filename + "-video.m4s";
		String audioFile = tempDir + File.separator + filename + "-audio.m4s";

		HttpUtil.downBiliFromUrl(videoUrl, filename + "-video.m4s", tempDir);
		HttpUtil.downBiliFromUrl(audioUrl, filename + "-audio.m4s", tempDir);

		// 合并音视频文件
		String ffmpegCmd = String.format("ffmpeg -i %s -i %s -c:v copy -c:a copy -f mp4 %s",
				videoFile, audioFile, outputPath);
		System.out.println(ffmpegCmd);
		CommandUtil.command(ffmpegCmd);

		// 清理临时文件
		System.gc();
		FileUtils.deleteFile(videoFile);
		FileUtils.deleteFile(audioFile);
	}

	/**
	 * 使用Aria2方式下载DASH格式视频
	 */
	private static void processAria2Download(String videoUrl, String audioUrl, Map<String, String> videoInfo,
			String filename,String fav) throws Exception {
		// 创建下载目录
		String downloadDir = FileUtil.generateDir(Global.down_path, Global.platform.bilibili.name(), true, filename, null, null);
		if(fav != null) {
			downloadDir = FileUtil.generateDir(Global.down_path, Global.platform.bilibili.name(), true, filename, null, fav);
		}
		// 发送下载任务
		String videores = Aria2Util.sendMessage(Global.a2_link,
				Aria2Util.createBiliparameter(videoUrl, downloadDir, filename + "-video.m4s", Global.a2_token));

		String audiores = Aria2Util.sendMessage(Global.a2_link,
				Aria2Util.createBiliparameter(audioUrl, downloadDir, filename + "-audio.m4s", Global.a2_token));

		// 创建合并任务
		createFfmpegMergeTask(videoInfo, filename, videores, audiores);
	}

	/**
	 * 创建FFmpeg合并任务
	 */
	private static void createFfmpegMergeTask(Map<String, String> videoInfo, String filename, String videores,
			String audiores) {
		logger.info("高清视频使用DASH格式，提交到FFmpeg队列等待下载完成后合并");

		// 临时目录和输出路径
		String tempDir =  FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, null, null);
		String outputPath=  FileUtil.generateDir(true, Global.platform.bilibili.name(), true, filename, null, "mp4");
		// 保存队列信息
		FfmpegQueueEntity ffmpegQueue = new FfmpegQueueEntity();
		ffmpegQueue.setVideoid(videoInfo.get("cid"));
		ffmpegQueue.setVideoname(videoInfo.get("title"));
		ffmpegQueue.setPendingfolder(tempDir);
		ffmpegQueue.setAudiostatus("0");
		ffmpegQueue.setVideostatus("0");
		ffmpegQueue.setFilepath(outputPath);
		ffmpegQueue.setStatus("0");
		ffmpegQueue.setCreatetime(DateUtils.getDateTime());
		biliUtil.ffmpegQueueDao.save(ffmpegQueue);

		// 保存视频下载任务
		saveQueueData(ffmpegQueue.getId(), JSONObject.parseObject(videores).getString("result"),
				"v", tempDir + "/" + filename + "-video.m4s");

		// 保存音频下载任务
		saveQueueData(ffmpegQueue.getId(), JSONObject.parseObject(audiores).getString("result"),
				"a", tempDir + "/" + filename + "-audio.m4s");
	}

	/**
	 * 保存队列数据
	 */
	private static void saveQueueData(Integer queueId, String taskId, String fileType, String filePath) {
		FfmpegQueueDataEntity queueData = new FfmpegQueueDataEntity();
		queueData.setQueueid(queueId);
		queueData.setTaskid(taskId);
		queueData.setFiletype(fileType);
		queueData.setStatus("0");
		queueData.setFilepath(filePath);
		queueData.setCreatetime(DateUtils.getDateTime());
		biliUtil.ffmpegQueueDataDao.save(queueData);
	}

	/**
	 * 方法需要代码优化 有时间再说
	 * 
	 * @param url
	 * @return
	 */
	public static List<Map<String, String>> getVideoDataInfo(String url) {
		List<Map<String, String>> res = new ArrayList<Map<String, String>>();
		String parseEntry = BiliUtil.parseEntry(url);
		String api = "";
		if (parseEntry.contains("BV")) {
			api = "https://api.bilibili.com/x/web-interface/view?bvid=" + parseEntry.substring(2, parseEntry.length());
		}
		if (parseEntry.contains("av")) {
			api = "https://api.bilibili.com/x/web-interface/view?aid=" + parseEntry.substring(2, parseEntry.length());
		}
		String serchPersion = HttpUtil.getSerchPersion(api, "UTF-8");
		JSONObject videoData = JSONObject.parseObject(serchPersion);
		System.out.println(videoData);
		if (videoData.getString("code").equals("0")) {
			// 优化多集问题 从page 里取

			String bvid = videoData.getJSONObject("data").getString("bvid");
			String aid = videoData.getJSONObject("data").getString("aid");
			String desc = videoData.getJSONObject("data").getString("desc");
			JSONObject dimension = videoData.getJSONObject("data").getJSONObject("dimension");
			Integer width = dimension.getInteger("width");
			Integer height = dimension.getInteger("height");

			JSONArray jsonArray = videoData.getJSONObject("data").getJSONArray("pages");

			for (int i = 0; i < jsonArray.size(); i++) {
				Map<String, String> data = new HashMap<String, String>();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String cid = jsonObject.getString("cid");
				String title = jsonObject.getString("part");
				String pic = jsonObject.getString("first_frame");
				data.put("aid", aid);
				data.put("bvid", bvid);
				data.put("desc", desc);
				if (width >= 1920 || height >= 1080) {
					data.put("quality", "1");
				} else {
					data.put("quality", "0");
				}
				if (null == pic) {
					pic = videoData.getJSONObject("data").getString("pic");
				}
				data.put("cid", cid);
				data.put("title", title);
				data.put("pic", pic);
				data.put("owner", videoData.getJSONObject("data").getString("owner"));
				data.put("ctime", videoData.getJSONObject("data").getString("ctime"));
				res.add(data);
			}
			return res;
		} else {
			return null;
		}
	}

	public static String parseEntry(String url) {
		if (url.contains("/video/av") || url.contains("/video/BV")) {
			return BiliUtil.findUrlAidOrBid(url);
		} else {
			Document document = null;
			try {
				document = Jsoup.connect(url).userAgent(
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36")
						.get();
				String baseUri = document.baseUri();
				return BiliUtil.findUrlAidOrBid(baseUri);
			} catch (IOException e1) {

			}
		}
		return "";
	}

	public static String findUrlAidOrBid(String url) {
		String replace = "";
		if (url.contains("http")) {
			replace = url.replaceAll("http://", "").replaceAll("https://", "").replace("www.bilibili.com/video/", "");
			int indexOf = replace.indexOf("/");
			String id = replace.substring(0, indexOf);
			return id;
		} else {
			replace = url.replaceAll("/video/", "");
			return replace;
		}
	}

	public static String buildInterfaceAddress(String aid, String cid, String token, String quality) {
		String bilibitstream = Global.bilibitstream;
		if (quality.equals("0")) {
			logger.info("视频没有2k以上进行画质降级");
			bilibitstream = "80"; // 画质降级
		}
		String api = "https://api.bilibili.com/x/player/playurl?avid=" + aid + "&cid=" + cid;
		if (null != token && !token.equals("")) {
			if (!bilibitstream.equals("64")) {
				// vip
				if (Integer.valueOf(bilibitstream) >= 120) {
					api = api + "&qn=0";
				} else {
					api = api + "&qn=" + bilibitstream;
				}

			} else {
				api = api + "&qn=80";
			}

		} else {
			api = api + "&qn=64";
		}
		api = api + "&fnver=0"; // 固定 0
		switch (bilibitstream) {
			case "80":
				api = api + "&fourk=1&fnval=" + Integer.toString(16 | 128); // 4k 传128
				break;
			case "112":
				api = api + "&fourk=1&fnval=" + Integer.toString(16 | 128); // 4k 传128
				break;
			case "116":
				api = api + "&fourk=1&fnval=" + Integer.toString(16 | 128); // 4k 传128
				break;
			case "120":
				api = api + "&fourk=1&fnval=" + Integer.toString(16 | 128); // 4k 传128
				break;
			case "125":
				api = api + "&fourk=1&fnval=" + Integer.toString(16 | 64); // hdr 传64
				break;
			case "126":
				api = api + "&fourk=1&fnval=" + Integer.toString(16 | 512); // 杜比视界 传128
				break;
			case "127":
				api = api + "&fourk=1&fnval=" + Integer.toString(16 | 1024); // 8k 传128
				break;
			default:
				api = api + "&fourk=0&fnval=1";
				break;
		}
		return api;
	}

	public static void main(String[] args) throws Exception {
		// video/BV1mz4y1q7Pb
		/// video/BV1qM4y1w716

		String a2path = FileUtil.createTemporaryDirectory(Global.platform.bilibili.name(), Global.down_path);
		System.out.println(a2path);
	}
}
