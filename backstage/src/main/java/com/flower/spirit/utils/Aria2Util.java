package com.flower.spirit.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Aria2Util {
	
	private static Logger logger = LoggerFactory.getLogger(Aria2Util.class);
	
	public static String sendMessage(String url,JSONObject post) {
		HttpPost httpPost = new HttpPost(url);
	    httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
	    httpPost.setEntity(new StringEntity(JSONObject.toJSONString(post), StandardCharsets.UTF_8));
	    CloseableHttpResponse response;
        try {
            response = HttpClients.createDefault().execute(httpPost);
        } catch (HttpHostConnectException e) {
        	logger.info("Aria2 无法连接"+"---------"+e.getMessage()+"----");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();

        String result = null;
        try {
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            logger.info("Aria2 statusCode = " + statusCode);
            logger.info("Aria2 result = " + result);
            if (statusCode == HttpStatus.SC_OK) {
                EntityUtils.consume(entity);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
	}
	
	public static JSONObject createBiliparameter(String downlink,String downpath,String downclass,String token) {
		JSONObject obj =  new JSONObject();
		
		obj.put("id", RandomStringUtils.randomNumeric(16));
		obj.put("jsonrpc", "2.0");
		obj.put("method", "aria2.addUri");
		JSONArray params = new JSONArray();
		if(token != null) {
			params.add("token:"+token);
		}
		JSONArray downLinkArray = new JSONArray();
		downLinkArray.add(downlink);
		params.add(downLinkArray);
		JSONObject confog =  new JSONObject();
		confog.put("dir", downpath);
		confog.put("out", downclass);
		confog.put("User-Agent", "Mozilla/5.0 BiliDroid/7.42.0 (bbcallen@gmail.com)");
		confog.put("referer", "https://www.bilibili.com");
		params.add(confog);
		obj.put("params", params);
		return obj;
	}
	public static JSONObject createDouparameter(String downlink,String downpath,String downclass,String token,String cookie) {
		JSONObject obj =  new JSONObject();
		
		obj.put("id", RandomStringUtils.randomNumeric(16));
		obj.put("jsonrpc", "2.0");
		obj.put("method", "aria2.addUri");
		JSONArray params = new JSONArray();
		if(token != null) {
			params.add("token:"+token);
		}
		JSONArray downLinkArray = new JSONArray();
		downLinkArray.add(downlink);
		params.add(downLinkArray);
		JSONObject confog =  new JSONObject();
		confog.put("dir", downpath);
		confog.put("out", downclass);
		confog.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		confog.put("cookie", cookie);
		params.add(confog);
		obj.put("params", params);
		return obj;
	}
	/**
	 * @param downlink
	 * @param downpath
	 * @param downclass
	 * @return
	 */
	public static JSONObject createparameter(String downlink,String downpath,String downclass,String token) {
		JSONObject obj =  new JSONObject();
		
		obj.put("id", RandomStringUtils.randomNumeric(16));
		obj.put("jsonrpc", "2.0");
		obj.put("method", "aria2.addUri");
		JSONArray params = new JSONArray();
		if(token != null) {
			params.add("token:"+token);
		}
		JSONArray downLinkArray = new JSONArray();
		downLinkArray.add(downlink);
		params.add(downLinkArray);
		JSONObject confog =  new JSONObject();
		confog.put("dir", downpath);
		confog.put("out", downclass);
		confog.put("referer", "*");
		params.add(confog);
		obj.put("params", params);
		return obj;
	}
	
	
	public static JSONObject createTaskStatus(String taskid,String token) {
		JSONObject obj =  new JSONObject();
		
		obj.put("id", RandomStringUtils.randomNumeric(16));
		obj.put("jsonrpc", "2.0");
		obj.put("method", "aria2.tellStatus");
		JSONArray params = new JSONArray();
		if(token != null) {
			params.add("token:"+token);
		}
		JSONArray fields = new JSONArray();
		fields.add("status");
		fields.add("totalLength");
		fields.add("completedLength");
		params.add(taskid);
		params.add(fields);
		JSONObject confog =  new JSONObject();
		confog.put("referer", "*");
		params.add(confog);
		obj.put("params", params);
		return obj;
	}

}
