package com.wys.service;

import com.wys.pojo.MultipartFileParam;

public interface UploadService {
    void sliceUpload(MultipartFileParam fileParam, String savePath);

    void noSliceUpload(MultipartFileParam fileParam, String savePath);
}
