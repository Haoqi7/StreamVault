package com.flower.spirit.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 这个方法中有大量遗弃方法不再调用
 */
@SuppressWarnings("deprecation")
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * web 端
     * 
     * @param url
     * @param param
     * @return
     */
    public static String httpGet(String url, String param) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("请求出错: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, param);
        } catch (HttpException e) {
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }

    /**
     * 用于验证bili登录接口 并取出cookie
     * 
     * @param url
     * @param param
     * @return
     */
    public static String httpGetBypoll(String url, String param) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, param);
            String code = JSONObject.parseObject(response).getJSONObject("data").getString("code");
            if (code.equals("0")) {
                // 登录成功
                Header[] headers = getMethod.getResponseHeaders();
                String cookie = "";
                for (Header h : headers) {
                    if (h.getName().equals("Set-Cookie")) {
                        String str = h.getValue().split(";")[0];
                        cookie = cookie + ";" + str;
                    }
                }
                return cookie.substring(1, cookie.length());
            } else {
                return null;
            }

        } catch (HttpException e) {
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return null;
    }

    /**
     * json 字符串
     * 
     * @param url
     * @param param
     * @return
     */
    public static String getSerchPersion(String url, String param) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod getMethod = new GetMethod(url);
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter("user-agent",Global.configInfo.getSerchPersion.getValue());
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("请求出错: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, param);
        } catch (HttpException e) {
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }

    public static String httpGetBili(String url, String param, String cookie) {
        HttpClient httpClient = new HttpClient();

        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        GetMethod getMethod = new GetMethod(url);

        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        getMethod.getParams().setParameter("user-agent",Global.configInfo.getSerchPersion.getValue());
        getMethod.addRequestHeader("user-agent",Global.configInfo.getSerchPersion.getValue());
        if (null != cookie && !cookie.equals("")) {
            getMethod.addRequestHeader("cookie", cookie);
        }

        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("请求出错: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            response = new String(responseBody, param);
        } catch (HttpException e) {
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }
    
    public static String httpGetBili(String url, String cookie,String origin,String referer ) {
    	String ua = null!=Global.useragent && !"".equals(Global.useragent)?Global.useragent:"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)    // 连接超时
                .readTimeout(5, TimeUnit.SECONDS)       // 读取超时
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", ua);
        if (origin != null && !origin.isEmpty()) {
            requestBuilder.header("Origin", origin);
        }
        if (referer != null && !referer.isEmpty()) {
            requestBuilder.header("Referer", referer);
        }
        if (cookie != null && !cookie.isEmpty()) {
            requestBuilder.header("Cookie", cookie);
        }
        Request request = requestBuilder.build();
        String responseStr = "";
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("请求出错: " + response.code() + " - " + response.message());
            } else {
                ResponseBody body = response.body();
                if (body != null) {
                    byte[] bytes = body.bytes(); 
                    responseStr = new String(bytes, Charset.forName("UTF-8"));
                }
            }
        } catch (IOException e) {
            System.out.println("发生网络异常!");
            e.printStackTrace();
        }
        return responseStr;
    }
    
    
    
    

    /**
     * post请求
     * 
     * @param url
     * @param json
     * @return
     */
    public static JSONObject doPost(String url, JSONObject json) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        JSONObject response = null;
        try {
            StringEntity s = new StringEntity(json.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json;charset=UTF-8");// 发送json数据需要设置contentType
            post.setEntity(s);
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(res.getEntity());// 返回json格式：
                result = new String(result.getBytes("ISO-8859-1"), "utf-8");
                response = JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            client.close();
        }
        return response;
    }

    public static JSONObject doPostNew(String url, JSONObject json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-Type", "application/json");
        JSONObject response = null;
        try {
            post.setEntity(new StringEntity(json.toString(), "UTF-8"));
            HttpResponse res = httpClient.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(res.getEntity());
                result = new String(result.getBytes("ISO-8859-1"), "utf-8");
                response = JSONObject.parseObject(result);
            }
        } catch (Exception e) {

        } finally {
            try {
                httpClient.close();
            } catch (Exception e2) {
            }
        }
        return response;
    }

    public static String downBiliFromUrl(String urlStr, String fileName, String savePath) throws Exception {
        return downBiliFromUrl(urlStr, fileName, savePath, null);
    }

    public static String downBiliFromUrl(String urlStr, String fileName, String savePath, String cookie)
            throws Exception {
        if (urlStr == null || urlStr.isEmpty() || fileName == null || fileName.isEmpty() ||
                savePath == null || savePath.isEmpty()) {
            throw new IllegalArgumentException("urlStr, fileName, savePath 不能为空");
        }

        int maxRetries = 3;
        int retryCount = 0;
        long retryDelay = 5000;

        while (retryCount < maxRetries) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            File saveDir = new File(savePath);
            File file = new File(saveDir, fileName);
            long downloaded = 0;
            long lastReadTime = System.currentTimeMillis();
            boolean needRetry = false;

            try {
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                Request.Builder requestBuilder = new Request.Builder()
                        .url(urlStr)
                        .addHeader("User-Agent", Global.configInfo.BiliDroid.getValue())
                        .addHeader("referer", "https://www.bilibili.com");

                if (cookie != null && !cookie.isEmpty()) {
                    requestBuilder.addHeader("Cookie", cookie);
                }

                Request request = requestBuilder.build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        logger.info("下载失败: " + response.code());
                        logger.info("----------------打印调试参数-------------------");
                        logger.info(urlStr);
                        logger.info(fileName);
                        logger.info("----------------打印调试参数-------------------");
                        return "1";
                    }

                    long fileLength = response.body().contentLength();
                    if (file.exists() && fileLength > 0 && file.length() == fileLength) {
                        logger.info("文件已存在且大小相同,跳过下载: {}", fileName);
                        return "0";
                    }

                    try (BufferedInputStream bis = new BufferedInputStream(response.body().byteStream());
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {

                        byte[] buffer = new byte[32 * 1024];
                        int len;
                        long startTime = System.currentTimeMillis();
                        long lastProgressTime = startTime;

                        while ((len = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, len);
                            downloaded += len;
                            long currentTime = System.currentTimeMillis();
                            lastReadTime = currentTime;

                            if (fileLength > 0 && currentTime - lastProgressTime >= 1000) {
                                int progress = (int) (downloaded * 100 / fileLength);
                                if (progress % 10 == 0) {
                                    double speed = (downloaded / 1024.0) / ((currentTime - startTime) / 1000.0);
                                    if (LoggerFactory.getLogger(HttpUtil.class).isDebugEnabled()) {
                                        LoggerFactory.getLogger(HttpUtil.class).debug(
                                                "下载进度: {}%, 速度: {:.2f} KB/s, 文件: {}", progress, speed, fileName);
                                    }
                                }
                                lastProgressTime = currentTime;
                            }

                            if (currentTime - lastReadTime > 30000) {
                                logger.info("读取超时");
                                logger.info("----------------打印调试参数-------------------");
                                logger.info(urlStr);
                                logger.info(fileName);
                                logger.info("----------------打印调试参数-------------------");
                                return "1";
                            }
                        }
                        bos.flush();

                        if (fileLength > 0 && file.length() != fileLength) {
                            logger.info("文件下载不完整");
                            logger.info("----------------打印调试参数-------------------");
                            logger.info(urlStr);
                            logger.info(fileName);
                            logger.info("----------------打印调试参数-------------------");
                            return "1";
                        }

                        logger.info("文件下载完成: {}", fileName);
                        return "0";
                    }
                }
            } catch (SocketTimeoutException e) {
                logger.warn("下载超时(第 {} 次重试): {}", retryCount + 1, fileName);
                needRetry = true;
            } catch (IOException e) {
                logger.error("下载出错: {}", e.getMessage(), e);
                logger.info("----------------打印调试参数-------------------");
                logger.info(urlStr);
                logger.info(fileName);
                logger.info("----------------打印调试参数-------------------");
                return "1";
            } finally {
                if (needRetry && file.exists()) {
                    file.delete();
                }
            }

            retryCount++;
            if (retryCount < maxRetries) {
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        logger.info("----------------打印调试参数-------------------");
        logger.info(urlStr);
        logger.info(fileName);
        logger.info("----------------打印调试参数-------------------");
        logger.error("下载失败，已重试多次: " + fileName);
        return "1";
    }

    @SuppressWarnings("unused")
    private static class InputStreamWithProgress extends FilterInputStream {
        private long totalBytes;
        private long bytesRead;

        protected InputStreamWithProgress(InputStream in, long totalBytes) {
            super(in);
            this.totalBytes = totalBytes;
            this.bytesRead = 0;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int bytesRead = super.read(b, off, len);
            if (bytesRead != -1) {
                this.bytesRead += bytesRead;
            }
            return bytesRead;
        }

        public int getProgress() {
            if (totalBytes > 0) {
                return (int) ((bytesRead * 100) / totalBytes);
            } else {
                return 0;
            }
        }
    }

    public static void downDouFromUrl(String urlStr, String fileName, String savePath, String cookie) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestProperty("User-Agent", DouUtil.ua);
            if (cookie != null) {
                conn.setRequestProperty("cookie", cookie);
            }
            InputStream input = conn.getInputStream();
            byte[] getData = readInputStream(input);
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                FileUtils.createDirectory(savePath);
            }
            File file = new File(saveDir + File.separator + fileName);
            FileOutputStream output = new FileOutputStream(file);
            output.write(getData);
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
        } catch (Exception e) {

        }
    }



    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[10240];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void downloadFile(String urlStr, String fileName, String savePath, Map<String, String> headers)
            throws IOException {
        if (urlStr == null || urlStr.isEmpty() || fileName == null || fileName.isEmpty() ||
                savePath == null || savePath.isEmpty()) {
            throw new IllegalArgumentException("urlStr, fileName, savePath 不能为空");
        }
        int maxRetries = 3;
        int retryCount = 0;
        long retryDelay = 5000;
        while (retryCount < maxRetries) {
            HttpURLConnection conn = null;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            File saveDir = new File(savePath);
            File file = new File(saveDir, fileName);
            long downloaded = 0;
            long lastReadTime = System.currentTimeMillis(); // ✅ 初始化放到循环前
            boolean needRetry = false;
            try {
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }
                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(60000);
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(false);
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Keep-Alive", "300");
                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                int fileLength = conn.getContentLength(); // ✅ 可能为 -1
                if (file.exists() && fileLength > 0 && file.length() == fileLength) {
                    logger.info("文件已存在且大小相同,跳过下载: {}", fileName);
                    return;
                }
                if (fileLength <= 0) {
                    logger.warn("无法获取远程文件大小: {}", fileName);
                }
                bis = new BufferedInputStream(conn.getInputStream());
                bos = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[16 * 1024];
                int len;
                long startTime = System.currentTimeMillis();
                long lastProgressTime = startTime;
                while ((len = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                    downloaded += len;
                    long currentTime = System.currentTimeMillis();
                    lastReadTime = currentTime;
                    if (fileLength > 0 && currentTime - lastProgressTime >= 15000) {
                        int progress = (int) (downloaded * 100 / fileLength);
                        double speed = (downloaded / 1024.0) / ((currentTime - startTime) / 1000.0);
                        long remainingTime = (long) ((fileLength - downloaded) / (speed * 1024));
                        logger.info("下载进度: {}%, 速度: {:.2f} KB/s, 剩余时间: {} 秒, 文件: {}",
                                progress, speed, remainingTime, fileName);
                        lastProgressTime = currentTime;
                    }
                    if (currentTime - lastReadTime > 30000) {
                        throw new SocketTimeoutException("读取超时");
                    }
                }
                bos.flush();
                if (fileLength > 0 && file.length() != fileLength) {
                    throw new IOException("文件下载不完整");
                }
                logger.info("文件下载完成: {}", fileName);
                return;
            } catch (SocketTimeoutException e) {
                logger.warn("下载超时(第 {} 次重试): {}", retryCount + 1, fileName);
                needRetry = true;
            } catch (IOException e) {
                logger.error("下载出错: {}", e.getMessage(), e);
                throw e; // 非超时直接抛出
            } finally {
                try {
                    if (bos != null)
                        bos.close();
                    if (bis != null)
                        bis.close();
                    if (conn != null)
                        conn.disconnect();
                } catch (IOException e) {
                    logger.error("关闭资源时出错: {}", e.getMessage(), e);
                }
                // ✅ 放到 finally 中统一判断是否删除不完整文件
                if (needRetry && file.exists()) {
                    file.delete();
                }
            }
            retryCount++;
            if (retryCount < maxRetries) {
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        throw new IOException("下载失败，已重试多次: " + fileName);
    }

    public static String downloadFileWithOkHttp(String urlStr, String fileName, String savePath,
            Map<String, String> headers)
            throws IOException {
        if (urlStr == null || urlStr.isEmpty() || fileName == null || fileName.isEmpty() ||
                savePath == null || savePath.isEmpty()) {
            logger.info("----------------打印调试参数-------------------");
            logger.info(urlStr);
            logger.info(fileName);
            logger.info("----------------打印调试参数-------------------");
            throw new IllegalArgumentException("urlStr, fileName, savePath 不能为空");
        }
        int maxRetries = 3;
        int retryCount = 0;
        long retryDelay = 5000;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        while (retryCount < maxRetries) {
            File saveDir = new File(savePath);
            File file = new File(saveDir, fileName);
            long downloaded = 0;
            long lastReadTime = System.currentTimeMillis();
            boolean needRetry = false;

            try {
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                Request.Builder requestBuilder = new Request.Builder()
                        .url(urlStr)
                        .addHeader("Connection", "keep-alive");

                if (headers != null) {
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        requestBuilder.addHeader(entry.getKey(), entry.getValue());
                    }
                }

                Response response = client.newCall(requestBuilder.build()).execute();
                if (!response.isSuccessful()) {
                    // 此处被风控了 更换另一个链接下载 如果另一个链接 还是这样 则终止本次下载
                    logger.info("下载失败: " + response.code());
                    logger.info("----------------打印调试参数-------------------");
                    logger.info(urlStr);
                    logger.info(fileName);
                    logger.info("----------------打印调试参数-------------------");
                    return "1";
                }
                long fileLength = response.body().contentLength();
                if (file.exists() && fileLength > 0 && file.length() == fileLength) {
                    logger.info("文件已存在且大小相同,跳过下载: {}", fileName);
                    return "0";
                }

                try (BufferedInputStream bis = new BufferedInputStream(response.body().byteStream());
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {

                    byte[] buffer = new byte[16 * 1024];
                    int len;
                    long startTime = System.currentTimeMillis();
                    long lastProgressTime = startTime;
                    long lastBytesRead = 0;

                    while ((len = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                        downloaded += len;
                        long currentTime = System.currentTimeMillis();
                        lastReadTime = currentTime;

                        if (fileLength > 0 && currentTime - lastProgressTime >= 15000) {
                            int progress = (int) (downloaded * 100 / fileLength);

                            // 计算平均速度
                            double averageSpeed = (downloaded / 1024.0) /
                                    Math.max(1, (currentTime - startTime) / 1000.0);

                            // 计算实时速度
                            double instantSpeed = ((downloaded - lastBytesRead) / 1024.0) /
                                    Math.max(1, (currentTime - lastProgressTime) / 1000.0);

                            // 计算剩余时间
                            long remainingBytes = fileLength - downloaded;
                            long remainingTime = averageSpeed > 0 ? (long) (remainingBytes / (averageSpeed * 1024)) : 0;

                            logger.info("下载进度: {}%, 平均速度: {} KB/s, 实时速度: {} KB/s, 剩余时间: {} 秒, 文件: {}",
                                    progress,
                                    String.format("%.2f", averageSpeed),
                                    String.format("%.2f", instantSpeed),
                                    remainingTime,
                                    fileName);

                            lastProgressTime = currentTime;
                            lastBytesRead = downloaded;
                            /*
                             * if (instantSpeed < averageSpeed * 0.3 && averageSpeed > 0) { throw new
                             * SocketTimeoutException("下载速度严重下降"); }
                             */
                        }

                        if (currentTime - lastReadTime > 30000) {
                            logger.info("读取超时");
                            logger.info("----------------打印调试参数-------------------");
                            logger.info(urlStr);
                            logger.info(fileName);
                            logger.info("----------------打印调试参数-------------------");
                            return "1";
                        }
                    }

                    bos.flush();
                    if (fileLength > 0 && file.length() != fileLength) {
                        logger.info("文件下载不完整");
                        logger.info("----------------打印调试参数-------------------");
                        logger.info(urlStr);
                        logger.info(fileName);
                        logger.info("----------------打印调试参数-------------------");
                        return "1";
                    }

                    logger.info("文件下载完成: {}", fileName);
                    return "0";
                }

            } catch (SocketTimeoutException e) {
                logger.warn("下载超时(第 {} 次重试): {}", retryCount + 1, fileName);
                needRetry = true;
            } catch (IOException e) {
                logger.error("下载出错: {}", e.getMessage(), e);
                logger.info("----------------打印调试参数-------------------");
                logger.info(urlStr);
                logger.info(fileName);
                logger.info("----------------打印调试参数-------------------");
                return "1";
            } finally {
                if (needRetry && file.exists()) {
                    file.delete();
                }
            }

            retryCount++;
            if (retryCount < maxRetries) {
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }
        logger.info("----------------打印调试参数-------------------");
        logger.info(urlStr);
        logger.info(fileName);
        logger.info("----------------打印调试参数-------------------");
        logger.error("下载失败，已重试多次: " + fileName);
        return "1";
    }

}
