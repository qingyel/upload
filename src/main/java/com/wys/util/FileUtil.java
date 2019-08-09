package com.wys.util;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @program: ssm
 * @description:
 * @author: syx
 * @create: 2019-07-30 16:02
 **/
public class FileUtil {
    /**
     * @param sliceFile 分片文件
     * @return
     * @Description: 分片文件追加
     */
    public static void randomWrite(byte[] sliceFile, String filePath) {
        try {
            /** 以读写的方式建立一个RandomAccessFile对象 **/
            //获取相对路径/home/gzxiaoi/apache-tomcat-8.0.45/webapps
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            // 将记录指针移动到文件最后
            raf.seek(raf.length());
            raf.write(sliceFile);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param request
     * @return
     * @Description: 取得tomcat中的webapps目录 如： /home/software/apache-tomcat-8.0.45/webapps
     */
    public static String getRealPath(HttpServletRequest request) {
        String realPath = request.getSession().getServletContext().getRealPath(File.separator);
        int aString = realPath.lastIndexOf(File.separator);
        realPath = realPath.substring(0, aString);
        return realPath;
    }

    /**
     * @param realPath 相对路径 ，如   /home/software/apache-tomcat-8.0.45/webapps
     * @param typeDir  文件类型 如： images/video/audio用于拼接文件保存路径，区分音视频
     * @return
     * @Description: 获取文件保存的路径，如果没有该目录，则创建
     */
    public static String getSavePath(String realPath, String typeDir, String guid) {
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat m = new SimpleDateFormat("MM");
        SimpleDateFormat d = new SimpleDateFormat("dd");
        Date date = new Date();
        String[] dirs = {realPath, "fileDate", typeDir, year.format(date), m.format(date), d.format(date), guid};
        String savePath = String.join(File.separator, dirs);
        File folder = new File(savePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return savePath;
    }

    public static String generateFilePath(HttpServletRequest request, String typeDir, String name, String guid) {
        String realPath = getRealPath(request);
        String savePath = getSavePath(realPath, typeDir, guid);
        String realName = name;
        String saveFile = savePath + File.separator + realName;
        return saveFile;
    }
}
