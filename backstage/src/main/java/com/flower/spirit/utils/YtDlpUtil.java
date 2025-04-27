package com.flower.spirit.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YtDlpUtil {

	private static final Logger logger = LoggerFactory.getLogger(YtDlpUtil.class);

	public static String exec(String url, String outpath, String p,Boolean createnfo) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add("yt-dlp");
		command.add("--print-json");
		String apppath = Global.apppath;
		File cookieDir = new File(apppath + "/cookies");
		if (null!=p && p.equals("youtube")) {
			File youtubeFile = new File(cookieDir, "youtube.txt");
			if (youtubeFile.exists()) {
				command.add("--cookies");
				command.add(youtubeFile.getAbsolutePath());
			}
		}

		if (null!=p && p.equals("twitter")) {
			File twitterFile = new File(cookieDir, "twitter.txt");
			if (twitterFile.exists()) {
				command.add("--cookies");
				command.add(twitterFile.getAbsolutePath());
			}
		}

		if (Global.proxyinfo != null) {
			command.add("--proxy");
			command.add(Global.proxyinfo);
		}
		command.add(url);
		command.add("-o");
		if (Global.getGeneratenfo && createnfo) {
			command.add(outpath + File.separator + "%(title)s" + File.separator + "%(id)s.%(ext)s");
		} else {
			command.add(outpath + File.separator + "%(title)s" + File.separator + "%(title)s.%(ext)s");
		}

		command.add("--write-thumbnail");
		command.add("--convert-thumbnails");
		command.add("webp");
		// command.add("%(title)s.%(ext)s");
//		command.add("--restrict-filenames");
		if (null != Global.useragent) {
			command.add("--user-agent");
			command.add(Global.useragent);
		}
		// System.out.println(command.toString());
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		int exitCode = process.waitFor();
		System.out.println("Command executed with exit code: " + exitCode);
		String completeString = stringBuilder.toString();
		if(exitCode!=0) {
			 System.out.println(completeString);
		}
		
		return completeString;
	}

	public static boolean isVideoStream(JSONObject format) {
		String vcodec = format.getString("vcodec");
		return vcodec != null && !"none".equals(vcodec);
	}

	public static boolean isAudioStream(JSONObject format) {
		String vcodec = format.getString("vcodec");
		String audioExt = format.getString("audio_ext");
		return "none".equals(vcodec) &&
				audioExt != null &&
				!"none".equals(audioExt);
	}

	public static String exec(String url) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add("yt-dlp");
		command.add("--print-json");
		command.add("--skip-download");
		command.add(url);
		// System.setProperty("http.proxyHost", "192.168.12.13");
		// System.setProperty("http.proxyPort", "58089");
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			;
		}
		int exitCode = process.waitFor();
		System.out.println("Command executed with exit code: " + exitCode);
		String completeString = stringBuilder.toString();
		return completeString;
	}

	public static String getPlatform(String url) {
		try {
			// 使用yt-dlp的--dump-json参数获取视频信息
			String command = "yt-dlp --dump-json " + url;
//			System.out.println(command);
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String jsonStr = reader.readLine();
//			System.out.println(jsonStr);
			if (jsonStr != null) {
				JSONObject json = JSONObject.parseObject(jsonStr);
				// 从json中获取extractor字段,这就是平台信息
				String platform = json.getString("extractor");
//				System.out.println(jsonStr);
				if (platform != null) {
					return platform;
				}
			}
		} catch (Exception e) {
			logger.error("获取平台信息失败: " + e.getMessage());
		}
		return null;
	}
	public static void main(String[] args) throws IOException, InterruptedException {
		String a = YtDlpUtil.getPlatform("https://x.com/FRIEREN_PR/status/1914514504383078713");
		System.out.println(a);
	}
}
