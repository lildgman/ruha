package com.ruha.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMemberRequest {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 12, message = "이름은 2자 이상 12자 이하로 입력해주세요.")
    private String name;

    public UpdateMemberRequest(String name) {
        this.name = name;
    }
}
