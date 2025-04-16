package com.flower.spirit.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;

public class WbiUtil {
    private static final int[] mixinKeyEncTab = new int[] {
            46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
            33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
            61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
            36, 20, 34, 44, 52
    };

    private static final char[] hexDigits = "0123456789abcdef".toCharArray();

    // 缓存的wbi keys
    private static String cachedImgKey = null;
    private static String cachedSubKey = null;
    private static LocalDate cacheDate = null;

    /**
     * 获取当前有效的WBI签名密钥
     * 
     * @return String[] 返回img_key和sub_key
     */
    public static String[] getWbiKeys() {
        LocalDate today = LocalDate.now();

        // 检查缓存是否存在且在当天有效
        if (cachedImgKey != null && cachedSubKey != null &&
                cacheDate != null && cacheDate.equals(today)) {
            return new String[] { cachedImgKey, cachedSubKey };
        }

        // 缓存不存在或已过期，重新获取
        try {
            String navResponse = HttpUtil.httpGetBili("https://api.bilibili.com/x/web-interface/nav", "UTF-8",
                    Global.bilicookies);
            JSONObject json = JSONObject.parseObject(navResponse);
            if (json.getInteger("code") == 0) {
                JSONObject data = json.getJSONObject("data");
                JSONObject wbiImg = data.getJSONObject("wbi_img");
                String imgUrl = wbiImg.getString("img_url");
                String subUrl = wbiImg.getString("sub_url");

                // 提取文件名中的字符串作为key
                cachedImgKey = imgUrl.substring(imgUrl.lastIndexOf("/") + 1).split("\\.")[0];
                cachedSubKey = subUrl.substring(subUrl.lastIndexOf("/") + 1).split("\\.")[0];
                cacheDate = today;

                return new String[] { cachedImgKey, cachedSubKey };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取缓存的img_key
     */
    public static String getImgKey() {
        String[] keys = getWbiKeys();
        return keys != null ? keys[0] : null;
    }

    /**
     * 获取缓存的sub_key
     */
    public static String getSubKey() {
        String[] keys = getWbiKeys();
        return keys != null ? keys[1] : null;
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            char[] result = new char[messageDigest.length * 2];
            for (int i = 0; i < messageDigest.length; i++) {
                result[i * 2] = hexDigits[(messageDigest[i] >> 4) & 0xF];
                result[i * 2 + 1] = hexDigits[messageDigest[i] & 0xF];
            }
            return new String(result);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String getMixinKey(String imgKey, String subKey) {
        String s = imgKey + subKey;
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < 32; i++)
            key.append(s.charAt(mixinKeyEncTab[i]));
        return key.toString();
    }

    public static String encodeURIComponent(Object o) {
        try {
            return URLEncoder.encode(o.toString(), "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成带签名的URL参数
     * 
     * @param params 请求参数（已按key排序）
     * @return 带签名的URL参数字符串
     */
    public static String buildWbiUrl(TreeMap<String, Object> params) {
        // 获取当前的wbi keys
        String[] wbiKeys = getWbiKeys();
        if (wbiKeys == null) {
            return null;
        }

        // 添加当前时间戳参数
        params.put("wts", System.currentTimeMillis() / 1000);

        // 将参数转换为URL参数格式
        String param = params.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), encodeURIComponent(entry.getValue())))
                .collect(Collectors.joining("&"));

        // 计算签名
        String mixinKey = getMixinKey(wbiKeys[0], wbiKeys[1]);
        String wbiSign = md5(param + mixinKey);

        // 组合最终参数
        return param + "&w_rid=" + wbiSign;
    }

    public static void main(String[] args) {
        String imgKey = "653657f524a547ac981ded72ea172057";
        String subKey = "6e4909c702f846728e64f6007736a338";
        String mixinKey = getMixinKey(imgKey, subKey);
        System.out.println(mixinKey); // 72136226c6a73669787ee4fd02a74c27

        // 用TreeMap自动排序
        TreeMap<String, Object> map = new TreeMap<>();
        map.put("foo", "one one four");
        map.put("bar", "五一四");
        map.put("baz", 1919810);

        String finalParam = buildWbiUrl(map);
        System.out.println(finalParam);
    }
}
