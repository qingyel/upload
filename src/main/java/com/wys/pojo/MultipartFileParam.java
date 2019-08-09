package com.wys.pojo;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MultipartFileParam {
    private String guid;
    /**
     * 任务ID
     */
    private String id;
    /**
     * 总分片数量
     */
    private Integer chunks;
    /**
     * 当前为第几块分片
     */
    @NotNull(message = "chunk may not be null")
    private Integer chunk;
    /**
     * 当前分片大小
     */
    private Long size = 0L;
    /**
     * 文件名
     */
    @NotBlank(message = "Name may not be blank")
    private String name;
    /**
     * 分片对象
     */
    private MultipartFile file;
    /**
     * MD5
     */
    private String md5value;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 最后更新时间
     */
    private String lastModifiedDate;

}
