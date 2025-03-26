package com.flower.spirit.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Map;
import java.util.Date;
import java.util.List;
import com.alibaba.fastjson.JSONObject;

public class EmbyMetadataGenerator {

    // 生成 movie.nfo（单个视频）
    public static void generateMetadata(String title, String year, String overview, String genre, String director,
            String actor, String outputPath, String upface, String upmid, String pic) {
        try {
            // 处理时间
            String dateadded = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String premiered = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

            String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<movie>\n" +
                    "    <title>" + title + "</title>\n" +
                    "    <originaltitle>" + title + "</originaltitle>\n" +
                    "    <sorttitle>" + title + "</sorttitle>\n" +
                    "    <epbookmark/>\n" +
                    "    <year>" + year + "</year>\n" +
                    "    <ratings/>\n" +
                    "    <userrating>0</userrating>\n" +
                    "    <top250>0</top250>\n" +
                    "    <set/>\n" +
                    "    <plot>" + overview + "</plot>\n" +
                    "    <outline>" + overview + "</outline>\n" +
                    "    <tagline>" + title + "</tagline>\n" +
                    "    <runtime>1</runtime>\n" +
                    "    <mpaa/>\n" +
                    "    <certification/>\n" +
                    "    <id>" + upmid + "</id>\n" +
                    "    <tmdbid/>\n" +
                    "    <country>无</country>\n" +
                    "    <status/>\n" +
                    "    <code/>\n" +
                    "    <premiered>" + premiered + "</premiered>\n" +
                    "    <watched>false</watched>\n" +
                    "    <playcount>0</playcount>\n" +
                    "    <studio>" + actor + "</studio>\n" +
                    "    <actor>\n" +
                    "        <name>" + actor + "</name>\n" +
                    "        <role>创作主</role>\n" +
                    (upface != null && !upface.trim().isEmpty()
                            ? "        <thumb>" + upface.replace('\\', '/') + "</thumb>\n"
                            : "")
                    +
                    "    </actor>\n" +
                    "    <trailer/>\n" +
                    "    <languages>中文</languages>\n" +
                    "    <dateadded>" + dateadded + "</dateadded>\n" +
                    "</movie>";
            // 测试发现 jellyfin 默认为 movie.nfo 后续有空改为 系统可选配置
            writeToFile(outputPath + "/" + "movie" + ".nfo", xmlContent);
            // writeToFile(outputPath + "/" + title + ".nfo", xmlContent);
        } catch (Exception e) {
            System.err.println("生成nfo文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 生成 tvshow.nfo（系列视频）
    public static void generateSeriesNfo(String title, String overview, String genre, String rating,
            String outputPath, List<Map<String, String>> upInfoList, String cover, String ctime) {
        try {
            // 转换时间戳为年份和日期
            String year = "2025";
            String dateadded = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String premiered = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            try {
                long timestamp = Long.parseLong(ctime);
                Date date = new Date(timestamp * 1000L);
                year = new SimpleDateFormat("yyyy").format(date);
                dateadded = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                premiered = new SimpleDateFormat("yyyy-MM-dd").format(date);
            } catch (Exception e) {
                // 使用默认值
            }

            StringBuilder nfoContent = new StringBuilder();
            nfoContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
            nfoContent.append("<!--created by spirit for KODI-->\n");
            nfoContent.append("<tvshow>\n");
            nfoContent.append("    <title>bilibili收藏夹-" + title + "</title>\n");
            nfoContent.append("    <originaltitle>" + title + "</originaltitle>\n");
            nfoContent.append("    <showtitle>bilibili收藏夹-" + title + "</showtitle>\n");
            nfoContent.append("    <sorttitle>" + title + "</sorttitle>\n");
            nfoContent.append("    <year>" + year + "</year>\n");
            nfoContent.append("    <top250>0</top250>\n");
            nfoContent.append("    <ratings/>\n");
            nfoContent.append("    <userrating>0</userrating>\n");
            nfoContent.append(
                    "    <outline>" + (overview.isEmpty() ? "bilibili收藏夹-" + title : overview) + "</outline>\n");
            nfoContent.append("    <plot>" + (overview.isEmpty() ? "bilibili收藏夹-" + title : overview) + "</plot>\n");
            nfoContent.append("    <tagline/>\n");
            nfoContent.append("    <runtime>0</runtime>\n");
            nfoContent.append("    <mpaa/>\n");
            nfoContent.append("    <certification/>\n");
            nfoContent.append("    <episodeguide>{}</episodeguide>\n");
            nfoContent.append("    <id/>\n");
            nfoContent.append("    <imdbid/>\n");
            nfoContent.append("    <tmdbid/>\n");
            nfoContent.append("    <premiered>" + premiered + "</premiered>\n");
            nfoContent.append("    <status>Unknown</status>\n");
            nfoContent.append("    <watched>false</watched>\n");
            nfoContent.append("    <playcount/>\n");
            nfoContent.append("    <studio>哔哩哔哩</studio>\n");
            nfoContent.append("    <country>中国</country>\n");
            if(cover!= null) {
                nfoContent.append("    <thumb aspect=\"poster\">" + cover.replace('\\', '/') + "</thumb>\n");
                nfoContent.append("    <thumb aspect=\"banner\">" + cover.replace('\\', '/') + "</thumb>\n");
                nfoContent.append("    <thumb aspect=\"landscape\">" + cover.replace('\\', '/') + "</thumb>\n");
            }
    

            // 只有在upInfoList不为空时才添加UP主信息
            if (upInfoList != null && !upInfoList.isEmpty()) {
                for (Map<String, String> upInfo : upInfoList) {
                    nfoContent.append("    <actor>\n");
                    nfoContent.append("        <name>" + upInfo.get("upname") + "</name>\n");
                    nfoContent.append("        <role>UP主</role>\n");
                    nfoContent.append("        <thumb>" + upInfo.get("upface").replace('\\', '/') + "</thumb>\n");
                    nfoContent.append("        <profile>" + upInfo.get("upmid") + "</profile>\n");
                    nfoContent.append("    </actor>\n");
                }
            }

            nfoContent.append("    <trailer/>\n");
            nfoContent.append("    <dateadded>" + dateadded + "</dateadded>\n");
            nfoContent.append("</tvshow>\n");

            writeToFile(outputPath + "/tvshow.nfo", nfoContent.toString());
        } catch (Exception e) {
            System.err.println("生成tvshow.nfo文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 生成单集 SxxExx.nfo
    public static void generateEpisodeNfo(String title, String overview, String upname, String upface, String upmid,
            String outputPath, String pic, String ctime, int episodeNumber) {
        try {
            // 处理时间
            String dateadded = "2025-03-24 10:13:00";
            String aired = "2025-03-24";
            try {
                long timestamp = Long.parseLong(ctime);
                Date date = new Date(timestamp * 1000L);
                dateadded = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                aired = new SimpleDateFormat("yyyy-MM-dd").format(date);
            } catch (Exception e) {
                // 使用默认值
            }

            StringBuilder nfoContent = new StringBuilder();
            nfoContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
            nfoContent.append("<!--created by spirit for KODI-->\n");
            nfoContent.append("<episodedetails>\n");
            nfoContent.append("    <title>" + title + "</title>\n");
            nfoContent.append("    <originaltitle/>\n");
            nfoContent.append("    <showtitle>bilibili收藏夹</showtitle>\n");
            nfoContent.append("    <season>1</season>\n");
            nfoContent.append("    <episode>" + episodeNumber + "</episode>\n");
            nfoContent.append("    <id/>\n");
            nfoContent.append("    <ratings/>\n");
            nfoContent.append("    <userrating>0</userrating>\n");
            nfoContent.append("    <plot>" + overview + "</plot>\n");
            nfoContent.append("    <runtime>0</runtime>\n");
            nfoContent.append("    <mpaa/>\n");
            nfoContent.append("    <premiered>" + aired + "</premiered>\n");
            nfoContent.append("    <aired>" + aired + "</aired>\n");
            nfoContent.append("    <watched>false</watched>\n");
            nfoContent.append("    <playcount>0</playcount>\n");
            nfoContent.append("    <thumb>" + pic.replace('\\', '/') + "</thumb>\n");
            nfoContent.append("    <actor>\n");
            nfoContent.append("        <name>" + upname + "</name>\n");
            nfoContent.append("        <role>UP主</role>\n");
            nfoContent.append("        <thumb>" + upface.replace('\\', '/') + "</thumb>\n");
            nfoContent.append("        <profile>" + upmid + "</profile>\n");
            nfoContent.append("    </actor>\n");
            nfoContent.append("    <dateadded>" + dateadded + "</dateadded>\n");
            nfoContent.append("    <epbookmark/>\n");
            nfoContent.append("    <code/>\n");
            nfoContent.append("</episodedetails>\n");

            writeToFile(outputPath + "/" + title + ".nfo", nfoContent.toString());
        } catch (Exception e) {
            System.err.println("生成episode.nfo文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 写入文件方法
    private static void writeToFile(String filePath, String content) {
        try (FileWriter writer = new FileWriter(new File(filePath))) {
            writer.write(content);
            System.out.println("文件生成成功: " + filePath);
        } catch (IOException e) {
            System.err.println("写入文件失败: " + e.getMessage());
        }
    }

    public static void createDouNfo(String upname,String upmid, String ctime, String cid, String title,
            String desc, String pic,String out) {
        String year = "";
        try {
            long timestamp = Long.parseLong(ctime);
            java.util.Date date = new java.util.Date(timestamp * 1000L);
            java.text.SimpleDateFormat yearFormat = new java.text.SimpleDateFormat("yyyy");
            year = yearFormat.format(date);
        } catch (Exception e) {
            year = "2025"; // 默认年份
        }
        String overview = desc;
        if (overview == null || overview.equals("-") || overview.trim().isEmpty()) {
            overview = "由创作主【" + upname + "】上传的视频内容";
        }
        overview += "创作主：" + upname + "\rUID：" + upmid + "\r视频ID：" + cid;
        String genre = "抖音";
        String director = upname;
        // 设置actor为UP主
        String actor = "抖音"+upname;
        // 获取输出路径（使用视频所在目录）
        generateMetadata(
        		title, // 标题
                year, // 年份
                overview, // 概述/描述
                genre, // 类型
                director, // 导演（UP主）
                actor, // 演员（UP主）
                out, // 输出路径
                null, // UP主头像
                upmid, // UP主ID
                pic // 封面图片
        );
        System.out.println("元数据生成成功: " + title);
    }
    
    
    public static void createBillNfo(String upname, String upface, String upmid, String ctime, String cid, String title,
            String desc, String pic,String out) {
        try {
            // 处理标题（去除引号）
            String cleanTitle = title.replace("\"", "").trim();

            // 从时间戳获取年份
            String year = "";
            try {
                long timestamp = Long.parseLong(ctime);
                java.util.Date date = new java.util.Date(timestamp * 1000L);
                java.text.SimpleDateFormat yearFormat = new java.text.SimpleDateFormat("yyyy");
                year = yearFormat.format(date);
            } catch (Exception e) {
                year = "2025"; // 默认年份
            }
            // 处理描述
            String overview = desc;
            if (overview == null || overview.equals("-") || overview.trim().isEmpty()) {
                overview = "由UP主【" + upname + "】上传的视频内容";
            }
            overview += ",UP主：" + upname + ",UID：" + upmid + ",视频ID：" + cid;
            // 设置类型（genre）
            String genre = "哔哩哔哩";
            // 设置导演为UP主
            String director = upname;
            // 设置actor为UP主
            String actor = upname;
            // 获取输出路径（使用视频所在目录）
            // 调用generateMetadata方法（使用正确的参数）
            generateMetadata(
                    cleanTitle, // 标题
                    year, // 年份
                    overview, // 概述/描述
                    genre, // 类型
                    director, // 导演（UP主）
                    actor, // 演员（UP主）
                    out, // 输出路径
                    upface, // UP主头像
                    upmid, // UP主ID
                    pic // 封面图片
            );
            System.out.println("元数据生成成功: " + cleanTitle);
        } catch (Exception e) {
            System.err.println("生成元数据时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 添加UP主信息到tvshow.nfo
    private static void addUpInfoToTvshowNfo(String outputPath, String upname, String upface, String upmid) {
        try {
            File nfoFile = new File(outputPath + "/tvshow.nfo");
            if (!nfoFile.exists()) {
                System.err.println("tvshow.nfo文件不存在");
                return;
            }

            // 读取文件内容
            StringBuilder content = new StringBuilder();
            try (Scanner scanner = new Scanner(nfoFile, "UTF-8")) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // 如果找到</tvshow>标签，在其前面插入UP主信息
                    if (line.trim().equals("</tvshow>")) {
                        // 检查是否已存在该UP主
                        if (!content.toString().contains("<profile>" + upmid + "</profile>")) {
                            content.append("    <actor>\n");
                            content.append("        <name>" + upname + "</name>\n");
                            content.append("        <role>发布者</role>\n");
                            if(upface!= null) {
                            	 content.append("        <thumb>" + upface.replace('\\', '/') + "</thumb>\n");
                            }
                            content.append("        <profile>" + upmid + "</profile>\n");
                            content.append("    </actor>\n");
                        }
                    }
                    content.append(line).append("\n");
                }
            }

            // 写回文件
            try (FileWriter writer = new FileWriter(nfoFile)) {
                writer.write(content.toString());
            }

        } catch (Exception e) {
            System.err.println("更新tvshow.nfo文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createFavoriteNfo(String jsonData, String output) {
        try {
            JSONObject object = JSONObject.parseObject(jsonData);
            JSONObject data = object.getJSONObject("data");

            String title = data.getString("title");
            String overview = data.getString("intro");
            String genre = "哔哩哔哩收藏夹";
            String rating = "0.0";
            String outputPath = output;
            String cover = data.getString("cover"); // 已经是本地路径
            String ctime = data.getString("ctime");

            // 确保输出目录存在
            new File(outputPath).mkdirs();

            // 生成初始tvshow.nfo，不包含UP主信息
            generateSeriesNfo(title, overview, genre, rating, outputPath, null, cover, ctime);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void createFavoriteDouNfo(String title, String output) {
        try {
            String overview = title;
            String genre = "来自抖音";
            String rating = "0.0";
            String outputPath = output;
            String ctime = DateUtils.getDate("yyyy");
            // 确保输出目录存在
            new File(outputPath).mkdirs();
            // 生成初始tvshow.nfo，不包含UP主信息
            generateSeriesNfo(title, overview, genre, rating, outputPath, null, null, ctime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public static void createFavoriteEpisodeNfo(Map<String, String> videoInfo, String outputPath, int episodeNumber,
            String tvshow) {
        try {
            String episodeTitle = videoInfo.get("title");
            String episodeOverview = videoInfo.get("desc");
            if (episodeOverview == null || episodeOverview.equals("-") || episodeOverview.trim().isEmpty()) {
                episodeOverview = "由UP主【" + videoInfo.get("upname") + "】上传的视频";
            }

            // 处理时间
            String airedDate = "";
            String dateadded = "";
            try {
                long timestamp = Long.parseLong(videoInfo.get("ctime"));
                Date date = new java.util.Date(timestamp * 1000L);
                SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                airedDate = dateFormat.format(date);
                dateadded = dateTimeFormat.format(date);
            } catch (Exception e) {
                airedDate = "2025-01-01";
                dateadded = "2025-01-01 00:00:00";
            }

            String nfoContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<episodedetails>\n" +
                    "    <title>" + episodeTitle + "</title>\n" +
                    "    <season>1</season>\n" +
                    "    <episode>" + episodeNumber + "</episode>\n" +
                    "    <showtitle>第" + episodeNumber + "集</showtitle>\n" +
                    "    <aired>" + airedDate + "</aired>\n" +
                    "    <plot>" + episodeOverview + "</plot>\n" +
                    "    <runtime></runtime>\n" +
                    "    <thumb>" + videoInfo.get("piclocal").replace('\\', '/') + "</thumb>\n" +
                    "    <uniqueid type=\"bilibili\" default=\"true\">" + videoInfo.get("bvid") + "</uniqueid>\n" +
                    "    <director>" + videoInfo.get("upname") + "</director>\n" +
                    "    <actor>\n" +
                    "        <name>" + videoInfo.get("upname") + "</name>\n" +
                    "        <role>UP主</role>\n" +
                    "        <thumb>" + videoInfo.get("upface").replace('\\', '/') + "</thumb>\n" +
                    "        <profile>" + videoInfo.get("upmid") + "</profile>\n" +
                    "    </actor>\n" +
                    "    <id>" + videoInfo.get("cid") + "</id>\n" +
                    "    <source>bilibili</source>\n" +
                    "    <dateadded>" + dateadded + "</dateadded>\n" +
                    "</episodedetails>\n";

            // 获取视频文件所在的目录
            // String videoDir = new File(videoInfo.get("videolocal")).getParent();
            // 在视频文件所在目录创建episode.nfo
            writeToFile(outputPath + "/" + episodeTitle + ".nfo", nfoContent);

            // 添加UP主信息到tvshow.nfo
            addUpInfoToTvshowNfo(tvshow, videoInfo.get("upname"), videoInfo.get("upface"), videoInfo.get("upmid"));

        } catch (Exception e) {
            System.err.println("生成单集nfo文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public static void createFavoriteEpisodeDouNfo(Map<String, String> videoInfo, String outputPath, int episodeNumber,
            String tvshow) {
        try {
            String episodeTitle = videoInfo.get("title");
            String episodeOverview = videoInfo.get("desc");
            if (episodeOverview == null || episodeOverview.equals("-") || episodeOverview.trim().isEmpty()) {
                episodeOverview = "由创作主【" + videoInfo.get("upname") + "】上传的视频";
            }

            // 处理时间
            String airedDate = "";
            String dateadded = "";
            try {
                long timestamp = Long.parseLong(videoInfo.get("ctime"));
                Date date = new java.util.Date(timestamp * 1000L);
                SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                airedDate = dateFormat.format(date);
                dateadded = dateTimeFormat.format(date);
            } catch (Exception e) {
                airedDate = "2025-01-01";
                dateadded = "2025-01-01 00:00:00";
            }

            String nfoContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<episodedetails>\n" +
                    "    <title>" + episodeTitle + "</title>\n" +
                    "    <season>1</season>\n" +
                    "    <episode>" + episodeNumber + "</episode>\n" +
                    "    <showtitle>第" + episodeNumber + "集</showtitle>\n" +
                    "    <aired>" + airedDate + "</aired>\n" +
                    "    <plot>" + episodeOverview + "</plot>\n" +
                    "    <runtime></runtime>\n" +
                    "    <thumb>" + videoInfo.get("piclocal").replace('\\', '/') + "</thumb>\n" +
                    "    <uniqueid type=\"抖音\" default=\"true\">" + videoInfo.get("bvid") + "</uniqueid>\n" +
                    "    <director>" + videoInfo.get("upname") + "</director>\n" +
                    "    <actor>\n" +
                    "        <name>" + videoInfo.get("upname") + "</name>\n" +
                    "        <role>创作主</role>\n" +
                    "        <profile>" + videoInfo.get("upmid") + "</profile>\n" +
                    "    </actor>\n" +
                    "    <id>" + videoInfo.get("cid") + "</id>\n" +
                    "    <source>bilibili</source>\n" +
                    "    <dateadded>" + dateadded + "</dateadded>\n" +
                    "</episodedetails>\n";

            // 获取视频文件所在的目录
            // String videoDir = new File(videoInfo.get("videolocal")).getParent();
            // 在视频文件所在目录创建episode.nfo
            writeToFile(outputPath + "/" + episodeTitle + ".nfo", nfoContent);

            // 添加UP主信息到tvshow.nfo
            addUpInfoToTvshowNfo(tvshow, videoInfo.get("upname"),null, videoInfo.get("upmid"));

        } catch (Exception e) {
            System.err.println("生成单集nfo文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
