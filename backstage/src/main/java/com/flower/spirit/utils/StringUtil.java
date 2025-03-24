package com.flower.spirit.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * <p>
 * Title: StringUtil
 * </p>
 * 
 * <p>
 * Description:字符串工具类
 * </p>
 * 
 * @author QingFeng
 * 
 * @date 2020年8月14日
 * 
 */
public class StringUtil {

	/**
	 * 
	 * <p>
	 * Title: isString
	 * </p>
	 * 
	 * <p>
	 * Description:判断是否是有效的字符串
	 * </p>
	 * 
	 * @param str
	 * @return
	 * 
	 */
	public static boolean isString(String str) {
		if (null == str || str.trim().equals("")) {
			return false;
		}
		return true;
	}

	/**
	 * 处理特殊字符串并返回安全的文件名
	 * 
	 * @param obj 原始文件名
	 * @param aid 备用ID
	 * @return 处理后的文件名
	 */
	public static String getFileName(String obj, String aid) {
		try {
			if (obj == null || obj.trim().isEmpty()) {
				return aid;
			}

			// 限制长度
			if (obj.length() > 64) {
				obj = obj.substring(0, 64);
			}

			// 先替换连续的特殊字符为单个下划线
			String result = obj.replaceAll("[^A-Za-z0-9\\u4e00-\\u9fa5]+", "_");

			// 去除首尾下划线
			result = result.replaceAll("^_+|_+$", "");

			// 确保非空结果
			return result.isEmpty() ? aid : result;
		} catch (Exception e) {
			return aid;
		}
	}

	public static String getRemoteAddr(HttpServletRequest request) {
		String remoteAddr = request.getHeader("X-Real-IP");
		if (isString(remoteAddr)) {
			remoteAddr = request.getHeader("X-Forwarded-For");
		} else if (isString(remoteAddr)) {
			remoteAddr = request.getHeader("Proxy-Client-IP");
		} else if (isString(remoteAddr)) {
			remoteAddr = request.getHeader("WL-Proxy-Client-IP");
		}
		return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}

	public static void main(String[] args) {
		System.out.println(StringUtil.getFileName("反恐精英1.6 #动画制作 #游戏 #反恐精英 #童年回忆", "123"));
	}
}
