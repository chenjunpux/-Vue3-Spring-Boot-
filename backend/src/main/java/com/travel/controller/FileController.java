package com.travel.controller;

import com.travel.common.Result;
import com.travel.entity.File;
import com.travel.entity.User;
import com.travel.service.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizType", defaultValue = "common") String bizType,
            @RequestParam(value = "bizId", required = false) Long bizId) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            bizId = user.getId();
        }
        Map<String, Object> result = fileService.upload(file, bizType, bizId);
        return Result.ok(result);
    }

    /**
     * 多文件上传
     */
    @PostMapping("/upload/multiple")
    public Result<List<Map<String, Object>>> uploadMultiple(
            Authentication authentication,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "bizType", defaultValue = "common") String bizType,
            @RequestParam(value = "bizId", required = false) Long bizId) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            bizId = user.getId();
        }
        List<Map<String, Object>> results = fileService.uploadFiles(files, bizType, bizId);
        return Result.ok(results);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.ok();
    }

    /**
     * 根据业务类型查询文件
     */
    @GetMapping("/list")
    public Result<List<File>> listByBiz(
            @RequestParam String bizType,
            @RequestParam(required = false) Long bizId) {
        return Result.ok(null);
    }
}
