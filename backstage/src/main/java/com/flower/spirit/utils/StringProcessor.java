package com.flower.spirit.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 字符串处理工具类，用于实现抖音 ABogus 算法中的字符串处理方法
 */
public class StringProcessor {

    /**
     * 将字符串转换为 ASCII 码字符串
     */
    public static String toOrdStr(String s) {
        StringBuilder result = new StringBuilder();
        for (char c : s.toCharArray()) {
            result.append((char) c);
        }
        return result.toString();
    }

    /**
     * 将字符串转换为 ASCII 码列表
     */
    public static List<Integer> toOrdArray(String s) {
        List<Integer> result = new ArrayList<>();
        for (char c : s.toCharArray()) {
            result.add((int) c);
        }
        return result;
    }

    /**
     * 将 ASCII 码列表转换为字符串
     */
    public static String toCharStr(List<Integer> codes) {
        StringBuilder result = new StringBuilder();
        for (Integer code : codes) {
            result.append((char) code.intValue());
        }
        return result.toString();
    }

    /**
     * 将字符串转换为 ASCII 码列表
     */
    public static List<Integer> toCharArray(String s) {
        return toOrdArray(s);
    }

    /**
     * 实现 JavaScript 中的无符号右移运算
     */
    public static int jsShiftRight(int val, int n) {
        return (val & 0xFFFFFFFF) >>> n;
    }

    /**
     * 生成一组伪随机字节字符串
     */
    public static String generateRandomBytes(int length) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int rd = random.nextInt(256);
            result.append((char) rd);
        }

        return result.toString();
    }
}