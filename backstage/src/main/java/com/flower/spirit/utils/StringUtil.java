package com.flower.spirit.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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

			// 替换所有非中文、数字、字母为点号
			String result = obj.replaceAll("[^A-Za-z0-9\\u4e00-\\u9fa5]+", ".");

			// 合并多个点号为一个
			result = result.replaceAll("\\.+", ".");
			


			// 去除首尾点号
			result = result.replaceAll("^\\.|\\.$", "");

			// 去除孤立的点号（即两个点号之间无字符的情况）
			result = result.replaceAll("(?<=\\.)\\.(?=\\.)", "");

			// 再次去除首尾点号，防止中间清理后新产生的
			result = result.replaceAll("^\\.|\\.$", "");

			result = result.replace(".", "");
			
			// 如果结果为空，使用备用ID
			if (result.isEmpty()) {
				return aid;
			}

			// 文件名不能以数字开头
			if (result.matches("^[0-9].*")) {
				result = "ep" + result;
			}

			return result;
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
    public static boolean isNotBlank(final CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

	public static void main(String[] args) {
		System.out.println(StringUtil.getFileName("反恐精英1.6 #动画制作 #游戏 #反恐精英 #童年回忆", "123"));
	}
}
