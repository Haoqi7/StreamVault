package com.flower.spirit.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {
	
	
    /**
     * 映射路径
     */
	public static String savefile;
    
    /**
     * 文件储存真实路径
     */
    public static String uploadRealPath;
	
    @Value("${file.save}")
    public void setSavefile(String savefile) {
    	FileUtil.savefile =savefile;
    }
    @Value("${file.save.path}")
    public void setUploadRealPath(String uploadRealPath) {
    	FileUtil.uploadRealPath =uploadRealPath;
    }

    
    /**
     * @param real
     * @param platform
     * @param odd
     * @param filename
     * @param favname
     * @return  /cos/bili/odd/2025   /cos/bili/collection/fav
     */
    public static String generateDir(boolean real,String platform,boolean odd,String filename,String favname,String ext) {
    	String datepath = DateUtils.getDate("yyyy")+System.getProperty("file.separator");
    	String resdir ="";
    	if(real) {
    		resdir = resdir+uploadRealPath+System.getProperty("file.separator");
    	}else {
    		resdir = resdir+savefile+System.getProperty("file.separator");
    	}
    	//拼接平台
    	resdir=resdir+platform+System.getProperty("file.separator");
    	
    	//拼接类型
    	if(odd) {
    		resdir=resdir+"odd"+System.getProperty("file.separator");
    	}else {
    		resdir=resdir+"collection"+System.getProperty("file.separator");
    	}
    	if(favname== null) {
    		resdir =resdir+datepath+System.getProperty("file.separator");
    	}else {
    		resdir =resdir+favname+System.getProperty("file.separator");
    	}
    	if(filename!= null) {
    		if(favname != null) {
    			resdir =resdir+"Season1";
    		}else {
    			resdir =resdir+filename;
    		}
    		
    		if(ext != null) {
    			resdir =resdir+System.getProperty("file.separator")+filename+"."+ext;
    		}else {
    			resdir =resdir+System.getProperty("file.separator");
    		}
    	}
    	return resdir;
    }
    
    
    public static String generateDir(String down,String platform,boolean odd,String filename,String favname,String ext) {
    	String datepath = DateUtils.getDate("yyyy")+System.getProperty("file.separator");
    	String resdir ="";
    	resdir = resdir+down+System.getProperty("file.separator");
    	//拼接平台
    	resdir=resdir+platform+System.getProperty("file.separator");
    	
    	//拼接类型
    	if(odd) {
    		resdir=resdir+"odd"+System.getProperty("file.separator");
    	}else {
    		resdir=resdir+"collection"+System.getProperty("file.separator");
    	}
    	if(favname== null) {
    		resdir =resdir+datepath+System.getProperty("file.separator");
    	}else {
    		resdir =resdir+favname+System.getProperty("file.separator");
    	}
    	if(filename!= null) {
    		resdir =resdir+filename;
    		if(ext != null) {
    			resdir =resdir+"."+ext;
    		}else {
    			resdir =resdir+System.getProperty("file.separator");
    		}
    	}
    	return resdir;
    }
    
    
	
	/**
	 * 创建目录文件 并返回路径   新版废弃  不调用
	 * @param directory
	 * @param ext
	 * @param filename
	 * @param platform
	 * @return
	 */
    @Deprecated
	public static String createDirFile(String directory,String ext,String filename,String platform) {
		String datepath = DateUtils.getDate("yyyy")+System.getProperty("file.separator");
		String path =directory
					+System.getProperty("file.separator")
					+platform
					+System.getProperty("file.separator")
					+datepath
					+System.getProperty("file.separator")+filename+System.getProperty("file.separator")
					+filename
					+ext;
		return path;
	}
	
    
    @Deprecated
	public  static String createTemporaryDirectory(String platform,String filename,String directory) {
		String datepath = DateUtils.getDate("yyyy")+System.getProperty("file.separator");
		String videofile = directory+System.getProperty("file.separator")
						   +platform
						   +System.getProperty("file.separator")
						   +datepath
						   +System.getProperty("file.separator")
						   +filename;
		return videofile;
	}
	

	/**
	 * 文件保存位置  新版废弃
	 * @param platform
	 * @param filename
	 * @return
	 */
	@Deprecated
	public  static String createTemporaryDirectory(String platform,String filename) {
		String datepath = DateUtils.getDate("yyyy")+System.getProperty("file.separator");
		String videofile = uploadRealPath
						   +platform
						   +System.getProperty("file.separator")
						   +datepath
						   +System.getProperty("file.separator")
						   +filename;
		return videofile;
	}
	
	@Deprecated
	public  static String createTemporaryDirectoryFav(String platform,String filename,String fav) {
		String datepath = DateUtils.getDate("yyyy")+System.getProperty("file.separator");
		String videofile = uploadRealPath
						   +platform
						   +System.getProperty("file.separator")
						   +datepath
						   +System.getProperty("file.separator")
						   +fav
						   +System.getProperty("file.separator")
						   +filename;
		return videofile;
	}
	
	/*
	 * 不再使用
	 * **/
	@Deprecated
	public  static String createTemporaryDirectoryFav(String platform,String filename,String directory,String fav) {
		String datepath = DateUtils.getDate("yyyy")+System.getProperty("file.separator");
		String videofile = directory+System.getProperty("file.separator")
						   +platform
						   +System.getProperty("file.separator")
						   +datepath
						   +System.getProperty("file.separator")
						   +fav
						   +System.getProperty("file.separator")
						   +filename;
		return videofile;
	}
	
	@Deprecated
	public static String createDirFileFav(String directory,String ext,String filename,String platform,String fav) {
		String datepath = DateUtils.getDate("yyyy")+System.getProperty("file.separator");
		String path =directory
					+System.getProperty("file.separator")
					+platform
					+System.getProperty("file.separator")
					+datepath
					+System.getProperty("file.separator")
					+fav
					+System.getProperty("file.separator")
					+filename
					+System.getProperty("file.separator")
					+filename
					+ext;
		return path;
	}
	
	/**
     * 复制文件夹
     * @param oldDir 原来的目录
     * @param newDir 复制到哪个目录
     */
    public static void copyDir(String oldDir, String newDir) {
            File srcDir = new File(oldDir);
            // 判断文件是否不存在或是否不是文件夹
            if (!srcDir.exists() || !srcDir.isDirectory()) {
                throw new IllegalArgumentException("参数错误");
            }
            File destDir = new File(newDir);
            if (!destDir.exists()) {
                // 不存在就创建目录
                if(destDir.mkdirs()){
                    // 列出目录中的文件
                    File[] files = srcDir.listFiles();
                    for (File f : files) {
                        // 是文件就调用复制文件方法 是目录就继续调用复制目录方法
                        if (f.isFile()) {
                        	File file = new File(newDir, f.getName());
                        	if(!file.exists()) {
                        		copyFile(f, file);
                        	}
                        	
                        } else if (f.isDirectory()) {
                            copyDir(oldDir + File.separator + f.getName(),
                                    newDir + File.separator + f.getName());
                        }
                    }
                }
            }else {
                File[] files = srcDir.listFiles();
                for (File f : files) {
                    // 是文件就调用复制文件方法 是目录就继续调用复制目录方法
                    if (f.isFile()) {
                    	File file = new File(newDir, f.getName());
                     	if(!file.exists()) {
                    		copyFile(f, file);
                    	}
                    } else if (f.isDirectory()) {
                        copyDir(oldDir + File.separator + f.getName(),
                                newDir + File.separator + f.getName());
                    }
                }
            }
        }
	/**
     * 复制文件
     * @param oldDir 原来的文件
     * @param newDir 复制到的文件
     */
    public static void copyFile(File oldDir, File newDir) {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        byte[] b = new byte[1024];
        try {
            // 将要复制文件输入到缓冲输入流
            bufferedInputStream = new BufferedInputStream(new FileInputStream(oldDir));
            // 将复制的文件定义为缓冲输出流
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newDir));
            // 定义字节数
            int len;
            while ((len = bufferedInputStream.read(b)) > -1) {
                // 写入文件
                bufferedOutputStream.write(b, 0, len);
            }
            //刷新此缓冲输出流
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedInputStream != null) {
                try {
                    // 关闭流
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
