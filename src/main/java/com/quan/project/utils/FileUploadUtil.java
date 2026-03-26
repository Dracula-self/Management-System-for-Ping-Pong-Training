package com.quan.project.utils;

import com.quan.project.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Component
public class FileUploadUtil {
    
    private static final Logger log = LoggerFactory.getLogger(FileUploadUtil.class);
    
    @Value("${file.upload.root-path}")
    private String rootPath;
    
    @Value("${file.upload.allowed-types.images}")
    private String allowedImageTypes;
    
    @Value("${file.upload.allowed-types.videos}")
    private String allowedVideoTypes;
    
    @Value("${file.upload.max-size.image}")
    private long maxImageSize;
    
    @Value("${file.upload.max-size.video}")
    private long maxVideoSize;
    
    /**
     * 上传文件
     * @param file 上传的文件
     * @return 返回文件信息
     */
    public Map<String, Object> uploadFile(MultipartFile file) {
        // 参数验证
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new BusinessException("文件名不能为空");
        }
        
        // 获取文件扩展名
        String fileType = getFileType(originalFileName);
        if (fileType.isEmpty()) {
            throw new BusinessException("无法获取文件类型");
        }
        
        // 检查文件类型
        if (!isAllowedFileType(fileType)) {
            throw new BusinessException("不支持的文件类型: " + fileType);
        }
        
        // 检查文件大小
        if (!isAllowedFileSize(file, fileType)) {
            String maxSize = isImageFile(fileType) ? "10MB" : "100MB";
            throw new BusinessException("文件大小超出限制，最大允许 " + maxSize);
        }
        
        try {
            // 生成文件名和路径
            String fileName = generateFileName(originalFileName);
            String filePath = generateFilePath(fileType);
            String fileUrl = generateFileUrl(filePath, fileName);
            
            // 创建存储目录
            createDirectories(filePath);
            
            // 保存文件
            saveFile(file, filePath, fileName);
            
            log.info("文件上传成功，文件名: {}, 大小: {}", originalFileName, formatFileSize(file.getSize()));
            
            // 返回文件信息
            Map<String, Object> result = new HashMap<>();
            result.put("originalName", originalFileName);
            result.put("fileName", fileName);
            result.put("fileType", fileType);
            result.put("fileSize", file.getSize());
            result.put("fileSizeFormatted", formatFileSize(file.getSize()));
            result.put("filePath", filePath + "/" + fileName);
            result.put("fileUrl", fileUrl);
            result.put("uploadTime", LocalDateTime.now());
            
            return result;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查文件类型是否允许
     */
    private boolean isAllowedFileType(String fileType) {
        return isImageFile(fileType) || isVideoFile(fileType);
    }
    
    /**
     * 检查文件大小是否符合限制
     */
    private boolean isAllowedFileSize(MultipartFile file, String fileType) {
        long fileSize = file.getSize();
        
        if (isImageFile(fileType)) {
            return fileSize <= maxImageSize;
        } else if (isVideoFile(fileType)) {
            return fileSize <= maxVideoSize;
        }
        
        return false;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFileName) {
        String fileType = getFileType(originalFileName);
        return UUID.randomUUID().toString().replace("-", "") + "." + fileType;
    }
    
    /**
     * 生成文件存储路径
     */
    private String generateFilePath(String fileType) {
        // 按日期和文件类型分类存储
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String typeFolder = isImageFile(fileType) ? "images" : "videos";
        return typeFolder + "/" + dateFolder;
    }
    
    /**
     * 创建目录
     */
    private void createDirectories(String filePath) throws IOException {
        Path path = Paths.get(rootPath, filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("创建目录: {}", path.toString());
        }
    }
    
    /**
     * 保存文件
     */
    private void saveFile(MultipartFile file, String filePath, String fileName) throws IOException {
        Path targetPath = Paths.get(rootPath, filePath, fileName);
        file.transferTo(targetPath.toFile());
        log.info("文件保存成功: {}", targetPath.toString());
    }
    
    /**
     * 生成文件访问URL
     */
    private String generateFileUrl(String filePath, String fileName) {
        return "/uploads/" + filePath + "/" + fileName;
    }
    
    /**
     * 格式化文件大小
     */
    private String formatFileSize(long fileSize) {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String fileType) {
        return Arrays.asList(allowedImageTypes.split(",")).contains(fileType.toLowerCase());
    }
    
    /**
     * 判断是否为视频文件
     */
    private boolean isVideoFile(String fileType) {
        return Arrays.asList(allowedVideoTypes.split(",")).contains(fileType.toLowerCase());
    }
} 