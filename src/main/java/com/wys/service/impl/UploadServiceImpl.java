package com.wys.service.impl;

import com.wys.pojo.DataFile;
import com.wys.pojo.MultipartFileParam;
import com.wys.service.UploadService;
import com.wys.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @program: demo_ssm-master
 * @description:
 * @author: syx
 * @create: 2019-08-08 18:00
 **/
@Service
public class UploadServiceImpl implements UploadService {
    Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Override
    public void sliceUpload(MultipartFileParam fileParam, String savePath) {
        Integer shard = fileParam.getChunk();
        Integer shards = fileParam.getChunks();
        if (shard <= shards) {
            // 文件类型，用于创建不同的目录，如(video/audio)
            logger.info(fileParam.getFile().getSize() + "----" + fileParam.getId() + "-----" + fileParam.getChunks() + "----" + fileParam.getChunk());
            try {
                FileUtil.randomWrite(fileParam.getFile().getBytes(), savePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void noSliceUpload(MultipartFileParam fileParam, String savePath) {
        logger.info("开始写入数据");
        try {
            FileUtil.randomWrite(fileParam.getFile().getBytes(), savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
