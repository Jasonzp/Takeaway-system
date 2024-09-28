package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;


    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传：{}", file.getOriginalFilename());
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 截取原始文件名的后缀
        String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
       // 构造新文件的名称
        String objectName = UUID.randomUUID().toString() + extention;



        String filePath = aliOssUtil.upload(file.getBytes(), objectName);
        return Result.success(filePath);
    }


}
