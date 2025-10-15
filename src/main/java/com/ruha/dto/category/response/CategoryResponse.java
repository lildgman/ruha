package com.ruha.dto.category.response;

import com.ruha.entity.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryResponse {

    private final Long categoryId;
    private final String name;
    private final LocalDateTime createdAt;

    public static CategoryResponse from(Category category) {

        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
