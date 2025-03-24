package com.flower.spirit.utils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XbogusUtil {
    private static final Integer[] Array = {
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, 10, 11, 12, 13, 14, 15
    };
    private static final String character = "Dkdpgh4ZKsQB80/Mfvw36XI1R25-WUAlEi7NLboqYTOPuzmFjJnryx9HVGcaStCe=";

    public static Integer[] md5StrToArray(String md5Str) {
        if (md5Str instanceof String && md5Str.length() > 32) {
            Integer[] array = new Integer[md5Str.length()];
            for (int i = 0; i < md5Str.length(); i++) {
                array[i] = (int) md5Str.charAt(i);
            }
            return array;
        } else {
            List<Integer> array = new ArrayList<>();
            int idx = 0;
            while (idx < md5Str.length()) {
                array.add((Array[(int) md5Str.charAt(idx)] << 4) | Array[(int) md5Str.charAt(idx + 1)]);
                idx += 2;
            }
            return array.toArray(new Integer[0]);
        }
    }

    public static String md5Encrypt(String urlPath) throws NoSuchAlgorithmException {
        Integer[] hashedUrlPath = md5StrToArray(md5(md5StrToArray(md5(urlPath))));
        StringBuilder sb = new StringBuilder();
        for (Integer value : hashedUrlPath) {
            sb.append(Character.toString((char) value.intValue()));
        }
        return sb.toString();
    }

    public static String md5(Object input) throws NoSuchAlgorithmException {
        Integer[] array;
        if (input instanceof String) {
            array = md5StrToArray((String) input);
        } else if (input instanceof Integer[]) {
            array = (Integer[]) input;
        } else {
            throw new IllegalArgumentException("Invalid input type. Expected String or Integer[].");
        }

        MessageDigest md5Hash = MessageDigest.getInstance("MD5");
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bytes[i] = array[i].byteValue();
        }
        byte[] md5Bytes = md5Hash.digest(bytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : md5Bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String encodingConversion(Integer a, Integer b, Integer c, Integer e, Integer d, Integer t, Integer f, Integer r, Integer n, Integer o, Integer i,Integer z, Integer x,
    		Integer u, Integer s, Integer l, Integer v, Integer h, Integer p) {
        List<Integer> y = new ArrayList<>();
        y.add(a);
        y.add(i);
        y.addAll(Arrays.asList(b, z, c, x, e, u, d, s, t, l, f, v, r, h, n, p, o));
        byte[] byteArray = new byte[y.size()];
        for (int idx = 0; idx < y.size(); idx++) {
            byteArray[idx] = y.get(idx).byteValue();
        }
        return new String(byteArray, StandardCharsets.ISO_8859_1);
    }

    public static String encodingConversion2(int a, int b, String c) {
        return Character.toString((char) a) + Character.toString((char) b) + c;
    }

    public static byte[] rc4Encrypt(byte[] key, byte[] data) {
        int[] S = new int[256];
        int j = 0;
        byte[] encryptedData = new byte[data.length];

        // 初始化 S 盒
        for (int i = 0; i < 256; i++) {
            S[i] = i;
        }

        j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + S[i] + key[i % key.length]) % 256;
            int temp = S[i];
            S[i] = S[(j>0?j:0)];
            S[(j>0?j:0)] = temp;
        }

        // 生成密文
        int i = 0;
        j = 0;
        for (int k = 0; k < data.length; k++) {
            i = (i + 1) % 256;
            j = (j + S[i]) % 256;
            int temp = S[i];
            S[i] = S[j];
            S[j] = temp;
            int t = (S[i] + S[j]) % 256;
            int encryptedByte = data[k] ^ S[t];
            encryptedData[k] = (byte) encryptedByte;
        }

        return encryptedData;
    }


    public static String calculation(int a1, int a2, int a3) {
        int x1 = (a1 & 255) << 16;
        int x2 = (a2 & 255) << 8;
        int x3 = x1 | x2 | a3;
        return Character.toString(character.charAt((x3 & 16515072) >> 18))
                + character.charAt((x3 & 258048) >> 12)
                + character.charAt((x3 & 4032) >> 6)
                + character.charAt(x3 & 63);
    }

    public static String getXBogus(String urlPath) throws NoSuchAlgorithmException {
        Integer[] array1 = md5StrToArray("d88201c9344707acde7261b158656c0e");
        Integer[] array2 = md5StrToArray(md5(md5StrToArray("d41d8cd98f00b204e9800998ecf8427e")));
        Integer[] urlPathArray = md5Encrypt(urlPath).chars().boxed().toArray(Integer[]::new);

        int timer = (int) (System.currentTimeMillis() / 1000);
        int ct = 536919696;
        List<Integer> array3 = new ArrayList<>();
        List<Integer> array4 = new ArrayList<>();
        StringBuilder xb = new StringBuilder();

        List<Object> newArray = new ArrayList<>(Arrays.asList(
                64, 0.00390625, 1, 8,
                urlPathArray[14], urlPathArray[15], array2[14], array2[15], array1[14], array1[15],
                (timer >> 24) & 255, (timer >> 16) & 255, (timer >> 8) & 255, timer & 255,
                (ct >> 24) & 255, (ct >> 16) & 255, (ct >> 8) & 255, ct & 255
        ));

        String newArrayStr =String.valueOf(newArray.get(0));
        int xorResult = (int) Math.floor(Double.valueOf(newArrayStr));
        for (int i = 1; i < newArray.size(); i++) {
//            double b = (double) newArray.get(i);
        	double b = Double.valueOf(String.valueOf(newArray.get(i)));
            if (b % 1 == 0) {
                xorResult ^= (int) b;
            } else {
                xorResult ^= (int) Math.floor(b);
            }
        }

        newArray.add(xorResult);

        for (int i = 0; i < newArray.size(); i += 2) {
            array3.add((int) Math.floor(Double.valueOf(String.valueOf(newArray.get(i)))));
            if (i + 1 < newArray.size()) {
                array4.add((int) Math.floor(Double.valueOf(String.valueOf(newArray.get(i+1)))));
            }
        }

        List<Integer> mergeArray = new ArrayList<>(array3);
        mergeArray.addAll(array4);
        Integer[] array5 = mergeArray.toArray(new Integer[0]);
        String garbledCode = encodingConversion2(
                2, 255,
                new String(rc4Encrypt("ÿ".getBytes(StandardCharsets.ISO_8859_1),
                        encodingConversion(array5[0],
                        				   array5[1],
                        				   array5[2],
                        				   array5[3],
                        				   array5[4],
                        				   array5[5],
                        				   array5[6],
                        				   array5[7],
                        				   array5[8],
                        				   array5[9],
                        				   array5[10],
                        				   array5[11],
                        				   array5[12],
                        				   array5[13],
                        				   array5[14],
                        				   array5[15],
                        				   array5[16],
                        				   array5[17],
                        				   array5[18]
                        				).getBytes(StandardCharsets.ISO_8859_1)),
                        StandardCharsets.ISO_8859_1)
        );

        for (int i = 0; i < garbledCode.length(); i += 3) {
            xb.append(calculation((int) garbledCode.charAt(i), (int) garbledCode.charAt(i + 1), (int) garbledCode.charAt(i + 2)));
        }
//        String params = urlPath + "&X-Bogus=" + xb.toString();
        return xb.toString();
    }

    @SuppressWarnings("static-access")
	public static void main(String[] args) throws NoSuchAlgorithmException {
        String urlPath = "aweme_id=7221047525594139944&aid=6383&cookie_enabled=true&platform=PC&downlink=10";
        XbogusUtil xb = new XbogusUtil();
        String xbogus = xb.getXBogus(urlPath);
        System.out.println(xbogus);
    }
}
