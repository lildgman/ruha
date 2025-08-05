package com.ruha.dto.Todo;

import com.ruha.entity.Importance;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateTodoResponse {

    private Long todoId;
    private String title;
    private String description;
    private Importance importance;
    private Boolean isPublic;
    private Boolean isCompleted;
    private Long categoryId;
    private String categoryName;
    private Long memberId;
    private String memberNickname;
    private List<TodoImageResponse> todoImages;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TodoImageResponse {
        private Long todoImageId;
        private String fileName;
        private String filePath;
        private LocalDateTime createdAt;
    }
}