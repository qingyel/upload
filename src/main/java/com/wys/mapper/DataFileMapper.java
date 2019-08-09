package com.wys.mapper;


import com.wys.pojo.DataFile;

public interface DataFileMapper {
    void add(DataFile dataFile);

    void update(DataFile dataFile);

    DataFile get(String id);

    DataFile getByMd5(String md5);
}
