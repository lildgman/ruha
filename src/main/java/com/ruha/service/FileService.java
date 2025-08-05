package com.ruha.service;

import com.ruha.exception.file.FileSaveException;
import com.ruha.exception.file.InvalidFileNameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    @Value("${file.upload.path:uploads/}")
    private String uploadBasePath;

    /**
     * 파일을 프로젝트 내 지정된 경로에 저장합니다.
     *
     * @param file 저장할 파일
     * @param subPath 하위 경로 (예: "todos/1")
     * @return 저장된 파일의 웹 접근 경로
     * @throws InvalidFileNameException 파일명이 유효하지 않은 경우
     * @throws FileSaveException 파일 저장 실패 시
     */
    public String saveFile(MultipartFile file, String subPath) {
        // 1. 원본 파일명에서 확장자 추출
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new InvalidFileNameException();
        }

        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }

        // 2. 고유한 파일명 생성
        String fileName = UUID.randomUUID().toString() + extension;

        // 3. 저장 경로 생성
        String fullPath = uploadBasePath + subPath + "/";
        File directory = new File(fullPath);

        // 4. 디렉토리가 없으면 생성
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new FileSaveException();
            }
            log.info("디렉토리가 생성되었습니다: {}", fullPath);
        }

        // 5. 파일 저장
        String filePath = fullPath + fileName;
        try {
            File destinationFile = new File(filePath);
            file.transferTo(destinationFile);
            log.info("파일이 저장되었습니다: {}", filePath);
        } catch (IOException e) {
            throw new FileSaveException(e);
        }

        // 6. 웹에서 접근 가능한 경로 반환
        return "/" + uploadBasePath + subPath + "/" + fileName;
    }

    /**
     * 파일을 삭제합니다.
     *
     * @param filePath 삭제할 파일의 경로
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(String filePath) {
        try {
            // 웹 경로를 실제 파일 경로로 변환
            String actualPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
            File file = new File(actualPath);
            
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("파일이 삭제되었습니다: {}", actualPath);
                } else {
                    log.warn("파일 삭제에 실패했습니다: {}", actualPath);
                }
                return deleted;
            } else {
                log.warn("삭제하려는 파일이 존재하지 않습니다: {}", actualPath);
                return false;
            }
        } catch (Exception e) {
            log.error("파일 삭제 중 오류가 발생했습니다: {}", filePath, e);
            return false;
        }
    }

    /**
     * 파일 확장자가 이미지인지 확인합니다.
     *
     * @param fileName 파일명
     * @return 이미지 여부
     */
    public boolean isImageFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        String extension = fileName.toLowerCase();
        return extension.endsWith(".jpg") || 
               extension.endsWith(".jpeg") || 
               extension.endsWith(".png") || 
               extension.endsWith(".gif") || 
               extension.endsWith(".bmp") || 
               extension.endsWith(".webp");
    }
}