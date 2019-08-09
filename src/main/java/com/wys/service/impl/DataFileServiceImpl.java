package com.wys.service.impl;


import com.wys.mapper.DataFileMapper;
import com.wys.pojo.DataFile;
import com.wys.pojo.MultipartFileParam;
import com.wys.service.DataFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: ssm
 * @description:
 * @author: syx
 * @create: 2019-08-05 12:09
 **/
@Service
public class DataFileServiceImpl implements DataFileService {
    @Autowired
    DataFileMapper dataFileMapper;

    @Override
    public void saveDataFile(MultipartFileParam fileParam, String savePath) {
        DataFile dataFile = new DataFile();
        dataFile.setName(fileParam.getName());
        dataFile.setId(fileParam.getGuid());
        dataFile.setUrl(savePath);
        dataFile.setMd5(fileParam.getMd5value());
        if(fileParam.getChunks() == null){
            dataFile.setStatus(1);
        }else {
            dataFile.setStatus(0);
        }
        dataFileMapper.add(dataFile);
    }

    @Override
    public void updateDataFile(DataFile dataFile) {
        dataFileMapper.update(dataFile);
    }

    @Override
    public DataFile getDataFile(String id) {
        return dataFileMapper.get(id);
    }

    @Override
    public DataFile getDataFileByMd5(String md5) {
        return dataFileMapper.getByMd5(md5);
    }
}
