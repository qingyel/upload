package com.wys.pojo;

import lombok.Data;

/**
 * @program: ssm
 * @description:
 * @author: syx
 * @create: 2019-08-05 11:12
 **/
@Data
public class DataFile {
    /**
     * 主键
     */
   private String id;
    /**
     * 文件名称
     */
   private String name;
    /**
     * 文件保存路径
     */
   private String url;
    /**
     * 文件状态，分片是否拼接完成 1:完成 0:未完成
     */
   private Integer status;

    /**
     * 加密后密文
     */
   private String md5;

}
