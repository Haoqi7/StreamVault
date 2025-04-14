package com.flower.spirit.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类：用于解析多个粘连的 JSON 对象字符串（如 yt-dlp 输出）
 */
public class JsonChunkParser {

    /**
     * 解析为 JSONObject 列表
     * @param input 多个 JSON 对象拼接的字符串
     * @return List<JSONObject>
     */
    public static List<JSONObject> parseJsonObjects(String input) {
        List<JSONObject> result = new ArrayList<>();
        for (String jsonStr : splitJsonObjects(input)) {
            try {
                result.add(JSON.parseObject(jsonStr));
            } catch (Exception e) {
                System.err.println("JSON 解析失败: " + jsonStr);
            }
        }
        return result;
    }

    /**
     * 解析为任意类型对象的列表
     * @param input 多个 JSON 对象拼接的字符串
     * @param clazz 目标类型
     * @return List<T>
     */
    public static <T> List<T> parseJsonObjects(String input, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (String jsonStr : splitJsonObjects(input)) {
            try {
                result.add(JSON.parseObject(jsonStr, clazz));
            } catch (Exception e) {
                System.err.println("JSON 解析失败: " + jsonStr);
            }
        }
        return result;
    }

    /**
     * 将拼接的 JSON 字符串切分成一个个合法 JSON 对象
     * @param input 原始拼接字符串
     * @return List<String>
     */
    private static List<String> splitJsonObjects(String input) {
        List<String> chunks = new ArrayList<>();
        int braceCount = 0;
        int startIndex = -1;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == '{') {
                if (braceCount == 0) {
                    startIndex = i;
                }
                braceCount++;
            } else if (ch == '}') {
                braceCount--;
                if (braceCount == 0 && startIndex != -1) {
                    String jsonStr = input.substring(startIndex, i + 1);
                    chunks.add(jsonStr);
                    startIndex = -1;
                }
            }
        }

        return chunks;
    }
}
