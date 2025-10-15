package com.ruha.dto.Todo.request;

import com.ruha.entity.Importance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateTodoRequest {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 50, message = "제목은 50자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "설명은 필수 입력 값입니다.")
    @Size(max = 500, message = "설명은 500자 이하로 입력해주세요.")
    private String description;

    @NotNull(message = "중요도는 필수 선택 값입니다.")
    private Importance importance;

    @NotNull(message = "공개 여부는 필수 선택 값입니다.")
    private Boolean isPublic;

    @NotNull(message = "카테고리 ID는 필수 입력 값입니다.")
    private Long categoryId;

}
