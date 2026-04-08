package com.travel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.entity.File;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface FileService extends IService<File> {
    Map<String, Object> upload(MultipartFile file, String bizType, Long bizId);
    List<Map<String, Object>> uploadFiles(MultipartFile[] files, String bizType, Long bizId);
    void deleteFile(Long id);
}
