package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration {

    @Bean  // 交给IOC 容器管理
    @ConditionalOnMissingBean  // 当没有这种Bean的时候才去创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
            log.info("开始创建阿里云文件上传工具类对象，{}",aliOssProperties);
            return  new AliOssUtil(aliOssProperties.getEndpoint(),
                    aliOssProperties.getAccessKeyId(),
                    aliOssProperties.getAccessKeySecret(),
                    aliOssProperties.getBucketName());
    }

}
