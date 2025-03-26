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

public class YouTuBeUtil {

	public static String exec(String url,String outpath) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add("yt-dlp");
		command.add("--print-json");
		if (Global.youtubecookies != null && !Global.youtubecookies.equals("")) {
			command.add("--cookies " + Global.youtubecookies);
		}
		command.add(url);
//		command.add("-P "+outpath);
		command.add("-o");
		command.add(outpath + File.separator + "%(title)s"+File.separator+ "%(title)s.%(ext)s");
		command.add("--write-thumbnail");     
		command.add("%(title)s.%(ext)s");
		command.add("--restrict-filenames");
//		System.out.println(command.toString());
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

	public static void main(String[] args) throws InterruptedException {
		try {
			String exec = YouTuBeUtil.exec("https://www.youtube.com/watch?v=f7EDFdA10pg","xx");
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
					String url = jsonObject.getString("url");
					if (YouTuBeUtil.isAudioStream(jsonObject)) {
						a_link = url;
					}
					if (YouTuBeUtil.isVideoStream(jsonObject)) {
						v_linl = url;
					}
				}
			}
			System.out.println("a_link:" + a_link);

			System.out.println("---------------------------:");
			System.out.println("v_linl:" + v_linl);
			System.out.println("---------------------------:");
			System.out.println("title:" + title);
			System.out.println("---------------------------:");
			System.out.println("description:" + description);
			System.out.println("---------------------------:");
			System.out.println("display_id:" + display_id);
			System.out.println("---------------------------:");
			System.out.println("thumbnail:" + thumbnail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
