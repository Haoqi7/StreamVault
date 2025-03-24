package com.flower.spirit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TikTokUtil {
	
	private static boolean dev= false;
	
	public static String ua="Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";
	
	
	public static String getTikTokVideoId(String url) throws IOException {
		 String videoId = null;
		 Document document = null;
		 if(!url.contains("/video/") && !url.contains("/v/") ){
			 if(dev) {
				  document = Jsoup.connect(url).userAgent(ua).proxy("127.0.0.1",58089).get();
			 }else {
				  document = Jsoup.connect(url).userAgent(ua).get();
			 }
             String baseUri = document.baseUri();
             url = baseUri;
         }
		 Pattern pattern = Pattern.compile("/video/(\\d+)");
         Matcher matcher = pattern.matcher(url);
         if (matcher.find()) {
             videoId = matcher.group(1);
             return videoId;
         } 
         pattern = Pattern.compile("/v/(\\d+)");
         matcher = pattern.matcher(url);
         if (matcher.find()) {
             videoId = matcher.group(1);
             return videoId;
         } 
         return null;
	}
	public static Map<String, String> getVideoData(String id) throws IOException{
		HashMap<String, String> res = new HashMap<String,String>();
		String api = "https://api16-normal-c-useast1a.tiktokv.com/aweme/v1/feed/?aweme_id="+id;
		String httpget = httpget(api);
		JSONObject parseObject = JSONObject.parseObject(httpget);
		JSONObject aweme_detail = parseObject.getJSONArray("aweme_list").getJSONObject(0);
		String coveruri = "";
		String videoplay = "";
		JSONArray cover = aweme_detail.getJSONObject("video").getJSONObject("cover").getJSONArray("url_list");
		 if(cover.size() >=2) {
			 coveruri = cover.getString(cover.size()-1);
		 }else {
			 coveruri = cover.getString(0);
		 }
		JSONArray videolist = aweme_detail.getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list");
		 if(videolist.size() >=2) {
			 videoplay = videolist.getString(videolist.size()-1);
		 }else {
			 videoplay = videolist.getString(0);
		 }
		String desc = aweme_detail.getString("desc");
		System.out.println(httpget);
		res.put("awemeid", id);
		res.put("videoplay", videoplay);
		res.put("desc", desc);
		res.put("cover", coveruri);
		return res;
	}
	
	
	public static String httpget(String addr) throws IOException {
        String urlString = addr;    
        URL url = new URL(urlString);
        HttpURLConnection conn = null;
        if(dev) {
        	   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 58089));
        	   conn = (HttpURLConnection) url.openConnection(proxy);
        }else {
        	 conn = (HttpURLConnection) url.openConnection();
        }
        conn.setRequestMethod("GET");
        conn.setRequestProperty("user-agent", ua);
        conn.setRequestProperty("referer", "https://www.tiktok.com/");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
	}
	public static void main(String[] args) throws IOException {
		String tikTokVideoId = TikTokUtil.getTikTokVideoId("https://vt.tiktok.com/ZSN2uaA2M/");
		if(tikTokVideoId != null) {
			Map<String, String> videoData = TikTokUtil.getVideoData(tikTokVideoId);
			
		}
		
	}
}
