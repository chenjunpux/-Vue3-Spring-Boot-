package com.travel.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 对象存储配置
 * 
 * MinIO 是高性能的对象存储服务，兼容 S3 协议
 * 用于存储图片、视频、文档等文件
 */
@Configuration
public class MinIOConfig {

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${minio.bucket-name:travel-system}")
    private String bucketName;

    public static final String DEFAULT_BUCKET = "travel-system";
    
    // 业务类型 Bucket
    public static final String BUCKET_AVATAR = "avatar";
    public static final String BUCKET_SPOT = "spots";
    public static final String BUCKET_HOTEL = "hotels";
    public static final String BUCKET_ARTICLE = "articles";
    public static final String BUCKET_ORDER = "orders";

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getBucketName() {
        return bucketName;
    }
}
