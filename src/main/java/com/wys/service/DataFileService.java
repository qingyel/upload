package com.wys.service;


import com.wys.pojo.DataFile;
import com.wys.pojo.MultipartFileParam;

public interface DataFileService {
    void saveDataFile(MultipartFileParam fileParam, String savePath);

    void updateDataFile(DataFile dataFile);

    DataFile getDataFile(String id);

    DataFile getDataFileByMd5(String id);
}
