package com.quan.project.controller;

import com.quan.project.common.R;
import com.quan.project.exception.BusinessException;
import com.quan.project.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/file")
public class FileUploadController {
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    /**
     * 上传文件（图片或视频）
     */
    @PostMapping("/upload")
    public R<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = fileUploadUtil.uploadFile(file);
            return R.success(result);
        } catch (BusinessException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("文件上传失败");
        }
    }
} 