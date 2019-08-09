package com.wys.controller;

import com.wys.pojo.DataFile;
import com.wys.pojo.MultipartFileParam;
import com.wys.pojo.ParamValidateVo;
import com.wys.pojo.RestResult;
import com.wys.service.DataFileService;
import com.wys.service.UploadService;
import com.wys.util.FileUtil;
import com.wys.util.ImageUtil;
import com.wys.util.JsonResult;
import com.wys.utlis.IsAllUploaded;
import com.wys.utlis.SaveFile;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wangyushuai@fang.com on 2018/7/12.
 * WebUploader 实例之前我是用.net写的，由于有些小朋友看不懂C#，在这里补发一个java的demo
 */
@Controller
@RequestMapping("/")
public class UploadController {
    private static Logger logger = LoggerFactory.getLogger(UploadController.class);
    private final static String UPLOAD_IMAGES = "_upload/images/";
    @Autowired
    DataFileService dataFileService;
    @Autowired
    UploadService uploadService;

    @RequestMapping("")
    public String index() {
        return "/upload";
    }

    @RequestMapping(value = "/validate")
    public ParamValidateVo testParam(@RequestBody @Validated ParamValidateVo vo) throws Exception {
        throw new Exception("hhhh");
    }

    @RequestMapping("fileupload")
    public String fileUpload() {
        return "/file";
    }

    @ResponseBody
    @RequestMapping("/add")
    public String add(@RequestParam("file") MultipartFile image, HttpServletRequest request, HttpSession session) throws IOException {
        File imageFolder = new File(session.getServletContext().getRealPath(UPLOAD_IMAGES));
        String fileName = UUID.randomUUID().toString().replace("-", "");//生成唯一标识，避免文件名重复
        File file = new File(imageFolder, fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (image != null) {
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);//工具类，转成统一jpg 格式，不转也可以,此处可省略
            ImageIO.write(img, "jpg", file);
        }
        JsonResult<String> result = new JsonResult<>();
        result.setCode(100);
        result.setMessage("上传成功");
        return new JSONObject(result).toString();
    }

    /**
     * @param guid             临时文件名
     * @param md5value         客户端生成md5值
     * @param chunks           分块数
     * @param chunk            分块序号
     * @param id               文件id便于区分
     * @param name             上传文件名
     * @param type             文件类型
     * @param lastModifiedDate 上次修改时间
     * @param size             文件大小
     * @param file             文件本身
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/BigFileUp")
    public String fileUpload(String guid,
                             String md5value,
                             String chunks,
                             String chunk,
                             String id,
                             String name,
                             String type,
                             String lastModifiedDate,
                             int size,
                             MultipartFile file) {
        String fileName;
        try {
            int index;
            String uploadFolderPath = "D:\\upload\\";

            String mergePath = uploadFolderPath + guid + File.separator;
            String ext = name.substring(name.lastIndexOf("."));

            //判断文件是否分块
            if (chunks != null && chunk != null) {
                index = Integer.parseInt(chunk);
                fileName = index + ext;
                // 将文件分块保存到临时文件夹里，便于之后的合并文件
                SaveFile.saveFile(mergePath, fileName, file);
                // 验证所有分块是否上传成功，成功的话进行合并
                IsAllUploaded.Uploaded(md5value, guid, chunk, chunks, uploadFolderPath, fileName, ext);
            } else {
                fileName = guid + ext;
                //上传文件没有分块的话就直接保存
                SaveFile.saveFile(uploadFolderPath, fileName, file);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return "{\"error\":true}";
        }

        return "{jsonrpc = \"2.0\",id = id,filePath = \"/Upload/\" + fileFullName}";
    }


    @RequestMapping("/uploadSlice")
    @ResponseBody
    public String uploadSlice(@Validated MultipartFileParam fileParam, HttpServletRequest request) throws Exception {
        byte[] bytes = fileParam.getName().getBytes("ISO-8859-1");
        fileParam.setName(new String(bytes, "UTF-8"));

        byte[] bytes2 = fileParam.getLastModifiedDate().getBytes("ISO-8859-1");
        fileParam.setLastModifiedDate(new String(bytes2, "UTF-8"));

        if (StringUtils.isBlank(fileParam.getType())) {
            logger.info("文件类型不能为空");
            return new RestResult(RestResult.RESULT_ERROR, "文件类型不能为空").toString();
        }

        String typeDir = fileParam.getType().split("\\/")[0];
        String savePath = FileUtil.generateFilePath(request, typeDir, fileParam.getName(), fileParam.getGuid());

        //没有分片
        if (fileParam.getChunks() == null || fileParam.getChunk() == null) {
            uploadService.noSliceUpload(fileParam, savePath);
            dataFileService.saveDataFile(fileParam, savePath);
            return new RestResult().toString();
        }
        //说明是有分片
        uploadService.sliceUpload(fileParam, savePath);
        if (fileParam.getChunk().equals(0)) {
            DataFile checkFile = dataFileService.getDataFileByMd5(fileParam.getMd5value());
            if (checkFile != null) {
                //TODO MD5校验
                logger.info("已存在相同的文件");
            }
            dataFileService.saveDataFile(fileParam, savePath);
        } else {
            // 说明已经成功上传一个文件
            if (fileParam.getChunk().equals(fileParam.getChunks() - 1)) {
                DataFile dataFile = dataFileService.getDataFile(fileParam.getGuid());
                dataFile.setStatus(1);
                dataFileService.updateDataFile(dataFile);
            }
            return new RestResult().toString();
        }
        return new RestResult<>(RestResult.RESULT_ERROR, "未知错误").toString();
    }

}
