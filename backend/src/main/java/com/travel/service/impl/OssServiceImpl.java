package com.travel.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.travel.common.Result;
import com.travel.config.MinIOConfig;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 对象存储服务
 * 
 * 支持功能：
 * 1. 文件上传
 * 2. 文件下载
 * 3. 文件删除
 * 4. 生成预签名URL（用于前端直传）
 * 5. 批量删除
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssServiceImpl {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:travel-system}")
    private String defaultBucket;

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    /**
     * 上传文件
     *
     * @param file     文件
     * @param bucket   存储桶名称
     * @param fileType 文件类型（avatar/spot/hotel/article/order）
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String bucket, String fileType) {
        try {
            // 1. 创建存储桶（如果不存在）
            createBucketIfNotExists(bucket);

            // 2. 生成文件路径: type/2026/04/07/xxx.jpg
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = fileType + "/" + 
                    LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + 
                    IdUtil.fastSimpleUUID() + ext;

            // 3. 上传文件
            String contentType = file.getContentType();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(contentType != null ? contentType : "application/octet-stream")
                            .build()
            );

            // 4. 返回访问URL
            String url = endpoint + "/" + bucket + "/" + fileName;
            log.info("文件上传成功: bucket={}, fileName={}, url={}", bucket, fileName, url);
            return url;

        } catch (Exception e) {
            log.error("文件上传失败: bucket={}, fileType={}", bucket, fileType, e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到默认桶
     */
    public String uploadFile(MultipartFile file, String fileType) {
        return uploadFile(file, defaultBucket, fileType);
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file) {
        return uploadFile(file, defaultBucket, "avatar");
    }

    /**
     * 上传游记图片
     */
    public String uploadArticleImage(MultipartFile file) {
        return uploadFile(file, defaultBucket, "article");
    }

    /**
     * 上传景点图片
     */
    public String uploadSpotImage(MultipartFile file) {
        return uploadFile(file, defaultBucket, "spot");
    }

    /**
     * 上传酒店图片
     */
    public String uploadHotelImage(MultipartFile file) {
        return uploadFile(file, defaultBucket, "hotel");
    }

    /**
     * 生成预签名上传URL（用于前端直传，不暴露secret-key）
     *
     * @param bucket    存储桶名称
     * @param fileName  文件名
     * @param expiry    过期时间（秒）
     * @return 预签名上传URL
     */
    public String getPresignedUploadUrl(String bucket, String fileName, int expiry) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(fileName)
                            .expiry(expiry, TimeUnit.SECONDS)
                            .build()
            );
            log.info("生成预签名上传URL: bucket={}, fileName={}, url={}", bucket, fileName, url);
            return url;
        } catch (Exception e) {
            log.error("生成预签名上传URL失败: bucket={}, fileName={}", bucket, fileName, e);
            throw new RuntimeException("生成预签名URL失败: " + e.getMessage());
        }
    }

    /**
     * 生成预签名下载URL
     */
    public String getPresignedDownloadUrl(String bucket, String fileName, int expiry) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(fileName)
                            .expiry(expiry, TimeUnit.SECONDS)
                            .build()
            );
            log.info("生成预签名下载URL: bucket={}, fileName={}, url={}", bucket, fileName, url);
            return url;
        } catch (Exception e) {
            log.error("生成预签名下载URL失败: bucket={}, fileName={}", bucket, fileName, e);
            throw new RuntimeException("生成预签名URL失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param bucket 存储桶名称
     * @param fileName 文件名
     * @return 文件输入流
     */
    public InputStream downloadFile(String bucket, String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("文件下载失败: bucket={}, fileName={}", bucket, fileName, e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String bucket, String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
            log.info("文件删除成功: bucket={}, fileName={}", bucket, fileName);
        } catch (Exception e) {
            log.error("文件删除失败: bucket={}, fileName={}", bucket, fileName, e);
            throw new RuntimeException("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String bucket, String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 创建存储桶（如果不存在）
     */
    private void createBucketIfNotExists(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build()
            );
            log.info("创建存储桶成功: bucket={}", bucket);
        }
    }

    /**
     * 获取文件信息
     */
    public StatObjectResponse getFileInfo(String bucket, String fileName) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件信息失败: bucket={}, fileName={}", bucket, fileName, e);
            throw new RuntimeException("获取文件信息失败: " + e.getMessage());
        }
    }
}
