package com.flower.spirit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * <p>
 * Title: Global
 * </p>
 * 
 * <p>
 * Description: 全局变量
 * </p>
 * 
 * @author QingFeng
 * 
 * @date 2020年8月14日
 * 
 */
@Component
public class Global {

	public static String user_session_key = "user_login_session";

	public static String ajax_success = "000001";

	public static String ajax_uri_error = "999998";

	public static String ajax_login_err = "999997";

	public static String ajax_login_success_message = "登录成功";

	public static String ajax_login_err_message = "您的账号或密码输入错误,请重新输入";

	public static String ajax_uri_error_message = "您的参数不完整,请检查提交参数";

	public static String ajax_add_user_err = "999996";

	public static String ajax_add_user_err_message = "添加用户失败,用户已存在";

	public static String ajax_add_user_success_message = "用户添加成功";

	public static String ajax_option_success = "操作成功";

	public static String ajax_nav_no_rule = "333333";

	public static String ajax_nav_no_rule_message = "未对外开放";

	public static String downtype = "a2";

	public static String a2_link = "http://localhost:6800/jsonrpc";

	public static String a2_token = "123456";

	public static String down_path = "/app/resources";

	public static String apptoken = "123456";

	public static String bilicookies = "";

	public static boolean bilimember = false;

	public static String bilibitstream = "64";

	public static boolean openprocesshistory = false;

	public static String tiktokCookie = "";

	// public static String analysiSserver="https://spirit.lifeer.xyz";

	public static String wallpaperid = "431960";

	public static String youtubecookies = "";

	public static String twittercookies = "";

	public static enum platform {
		bilibili,
		douyin,
		tiktok,
		youtube,
		instagram,
		twitter;
	};

	// 文件映射和保存路径
	public static String savefile;
	public static String uploadRealPath;

	@Value("${file.save}")
	public void setSavefile(String value) {
		Global.savefile = value;
	}

	@Value("${file.save.path}")
	public void setUploadRealPath(String value) {
		Global.uploadRealPath = value;
	}
}
