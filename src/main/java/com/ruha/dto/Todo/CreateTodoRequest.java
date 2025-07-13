package com.ruha.dto.Todo;

import com.ruha.entity.Category;
import com.ruha.entity.Importance;
import com.ruha.entity.Member;
import com.ruha.entity.Todo;
import lombok.Data;

@Data
public class CreateTodoRequest {

    private String title;
    private String description;
    private Importance importance;
    private Boolean isPublic;
    private Long categoryId;

    public Todo toEntityNoImage(Member member, Category category) {

        return Todo.builder()
                .title(this.title)
                .description(this.description)
                .importance(this.importance)
                .isPublic(this.isPublic)
                .member(member)
                .category(category)
                .build();
    }
}
