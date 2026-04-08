package com.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.entity.File;
import com.travel.mapper.FileMapper;
import com.travel.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    @Override
    @Transactional
    public Map<String, Object> upload(MultipartFile file, String bizType, Long bizId) {
        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String ext = getFileExt(file.getOriginalFilename());
            String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;

            String relativePath = bizType + "/" + datePath + "/" + fileName;
            String fullDir = uploadDir + "/" + bizType + "/" + datePath;
            Files.createDirectories(Paths.get(fullDir));

            Path targetPath = Paths.get(fullDir, fileName);
            file.transferTo(targetPath.toFile());

            File fileEntity = new File();
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setFilePath(relativePath);
            fileEntity.setFileUrl(baseUrl + "/" + relativePath);
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFileType(file.getContentType());
            fileEntity.setFileExt(ext);
            fileEntity.setStorageType("local");
            fileEntity.setBizType(bizType);
            fileEntity.setBizId(bizId);
            fileEntity.setCreatedAt(LocalDateTime.now());
            baseMapper.insert(fileEntity);

            Map<String, Object> result = new HashMap<>();
            result.put("id", fileEntity.getId());
            result.put("fileName", fileEntity.getFileName());
            result.put("fileUrl", fileEntity.getFileUrl());
            result.put("fileSize", fileEntity.getFileSize());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Map<String, Object>> uploadFiles(MultipartFile[] files, String bizType, Long bizId) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                results.add(upload(file, bizType, bizId));
            }
        }
        return results;
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        File fileEntity = baseMapper.selectById(id);
        if (fileEntity != null) {
            try {
                Path path = Paths.get(uploadDir, fileEntity.getFilePath());
                Files.deleteIfExists(path);
            } catch (Exception ignored) {
            }
            baseMapper.deleteById(id);
        }
    }

    private String getFileExt(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
