package com.ruha.service;

import com.ruha.exception.file.InvalidFileNameException;
import com.ruha.exception.file.InvalidFileTypeException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileService fileService;

    @TempDir
    Path tmpDir;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(fileService, "uploadBasePath", tmpDir.toString() + "/");
    }

    @Nested
    @DisplayName("이미지 파일 저장 테스트")
    class SaveFileTest {

        @Test
        @DisplayName("jpg 이미지 파일 저장 성공")
        void jpg_이미지_파일_저장_성공() throws IOException {

            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    "test content".getBytes()
            );

            String subPath = "todos/1";

            // when
            String savedPath = fileService.saveFile(file, subPath);
            // then
            assertThat(savedPath)
                    .startsWith("/" + tmpDir.toString() + "/" + subPath + "/")
                    .endsWith(".jpg");

            String actualPath = savedPath.substring(1);
            File savedFile = new File(actualPath);
            assertThat(savedFile).exists();
            assertThat(Files.readString(savedFile.toPath())).isEqualTo("test content");
        }

        @Test
        @DisplayName("png 이미지 파일 저장 성공")
        void png_이미지_파일_저장_성공() {

            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.png",
                    "image/png",
                    "test content".getBytes()
            );
            String subPath = "todos/1";

            // when
            String savedPath = fileService.saveFile(file, subPath);

            // then
            assertThat(savedPath)
                    .startsWith("/" + tmpDir.toString() + "/" + subPath + "/")
                    .endsWith(".png");
        }

        @Test
        @DisplayName("파일명이 null일 경우 예외 발생")
        void 파일명_null일_경우_예외_발생() {

            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    null,
                    "text/plain",
                    "test content".getBytes()
            );

            String subPath = "todos/1";

            assertThrows(InvalidFileNameException.class,
                    () -> fileService.saveFile(file, subPath));
        }

        @Test
        @DisplayName("파일명이 빈 문자열일 경우 예외 발생")
        void 파일명이_빈_문자열인_경우_예외_발생() {

            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "",
                    "text/plain",
                    "test content".getBytes()
            );

            String subPath = "todos/1";

            assertThrows(InvalidFileNameException.class,
                    () -> fileService.saveFile(file, subPath));

        }

        @Test
        @DisplayName("이미지 파일이 아닌 경우예외 발생")
        void 이미지_파일이_아닌_경우_예외_발생() {

            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.txt",
                    "text/plain",
                    "test content".getBytes()
            );

            String subPath = "todos/1";

            assertThrows(InvalidFileTypeException.class,
                    () -> fileService.saveFile(file, subPath));

        }
    }

    @Nested
    @DisplayName("파일 삭제 테스트")
    class DeleteFileTest {

        @Test
        @DisplayName("파일 삭제 성공")
        void 파일_삭제_성공() {

            // given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    "test content".getBytes()
            );
            String subPath = "todos/1";
            String savedPath = fileService.saveFile(file, subPath);

            String actualPath = savedPath.substring(1);
            File savedFile = new File(actualPath);
            assertThat(savedFile).exists();

            // when
            boolean result = fileService.deleteFile(savedPath);

            // then
            assertThat(result).isTrue();
            assertThat(savedFile.exists()).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 파일 삭제 시도 시 false")
        void 존재하지_않는_파일_삭제_시도_시_false() {

            // given
            String str = "/non/exist/file.jpg";

            // when
            boolean result = fileService.deleteFile(str);

            // then
            assertThat(result).isFalse();

        }
    }
}