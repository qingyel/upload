package com.wys.utlis;


import com.wys.pojo.UploadInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class IsAllUploaded {

    private final static List<UploadInfo> uploadInfoList = new ArrayList<>();

    /**
     * @param md5
     * @param chunks
     * @return
     */
    public static boolean isAllUploaded(final String md5,
                                        final String chunks) {
        int size = uploadInfoList.stream()
                .filter(item -> item.getMd5().equals(md5))
                .distinct()
                .collect(Collectors.toList())
                .size();
        boolean bool = (size == Integer.parseInt(chunks));
        if (bool) {
            synchronized (uploadInfoList) {
                uploadInfoList.removeIf(item -> Objects.equals(item.getMd5(), md5));
            }
        }
        return bool;
    }

    /**
     * @param md5         MD5
     * @param guid        随机生成的文件名
     * @param chunk       文件分块序号
     * @param chunks      文件分块数
     * @param fileName    文件名
     * @param ext         文件后缀名
     */
    public static void Uploaded(final String md5,
                                final String guid,
                                final String chunk,
                                final String chunks,
                                final String uploadFolderPath,
                                final String fileName,
                                final String ext)
            throws Exception {
        synchronized (uploadInfoList) {
            uploadInfoList.add(new UploadInfo(md5, chunks, chunk, uploadFolderPath, fileName, ext));
        }
        boolean allUploaded = isAllUploaded(md5, chunks);
        int chunksNumber = Integer.parseInt(chunks);

        if (allUploaded) {
            mergeFile(chunksNumber, ext, guid, uploadFolderPath);
//            fileService.save(new com.zhangzhihao.FileUpload.Java.Model.File(guid + ext, md5, new Date()));
        }
    }
    /**
     * @param chunksNumber
     * @param ext
     * @param guid
     * @param uploadFolderPath
     * @throws Exception
     */
    public static void mergeFile(final int chunksNumber,
                               final String ext,
                                  final String guid,
                                 final String uploadFolderPath)
            throws Exception {
        /*合并输入流*/
        String mergePath = uploadFolderPath + guid + "/";
        SequenceInputStream s ;
        InputStream s1 = new FileInputStream(mergePath + 0 + ext);
        InputStream s2 = new FileInputStream(mergePath + 1 + ext);
        s = new SequenceInputStream(s1, s2);
        for (int i = 2; i < chunksNumber; i++) {
            InputStream s3 = new FileInputStream(mergePath + i + ext);
            s = new SequenceInputStream(s, s3);
        }

        //通过输出流向文件写入数据
        saveStreamToFile(s, uploadFolderPath + guid + ext);

        //删除保存分块文件的文件夹
        deleteFolder(mergePath);
    }

    public static void saveStreamToFile( final InputStream inputStream,
                                         final String filePath)
            throws Exception {
        /*创建输出流，写入数据，合并分块*/
        OutputStream outputStream = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    public static boolean deleteFolder( final String folderPath) {
        File dir = new File(folderPath);
        File[] files = dir.listFiles();
        if(files!=null){
            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dir.delete();
    }
}



