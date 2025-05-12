package com.flower.spirit.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.flower.spirit.config.Global;

public class EncoderUtil {

	public static void checkAndSetEncoder() {
	    try {
	        // 检查是否支持 h264_qsv, hevc_qsv, vp9_qsv
	        String encoder = getSupportedEncoder();

	        // 将选中的编码器赋值给 Global.encoder
	        Global.encoder = encoder;

	        System.out.println("检测到编码器: " + encoder);
	    } catch (Exception e) {
	        // 出现异常时选择 libx264
	        Global.encoder = "libx264";
	        System.err.println("检测编码器失败，默认使用 libx264: " + e.getMessage());
	    }
	}

	private static String getSupportedEncoder() {
	    // 检查 h264_qsv, hevc_qsv, vp9_qsv 的支持情况
	    if (isEncoderSupported("h264_qsv")) {
	        return "h264_qsv";
	    } else if (isEncoderSupported("hevc_qsv")) {
	        return "hevc_qsv";
	    } else if (isEncoderSupported("vp9_qsv")) {
	        return "vp9_qsv";
	    } else {
	        // 如果没有硬件加速支持，返回默认的软件编码器
	        return "libx264";
	    }
	}

	private static boolean isEncoderSupported(String encoderName) {
	    try {
	        // 执行 ffmpeg -encoders 命令来判断是否支持指定的编码器
	        Process process = Runtime.getRuntime().exec("ffmpeg -encoders");
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line;
	        boolean isSupported = false;

	        while ((line = reader.readLine()) != null) {
	            if (line.contains(encoderName)) {
	                isSupported = true;
	                break;
	            }
	        }

	        reader.close();
	        process.waitFor();

	        return isSupported;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

    public static void main(String[] args) {
		System.out.println(Global.encoder);
		checkAndSetEncoder();
		System.out.println(Global.encoder);
	}
}
