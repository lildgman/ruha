package com.ruha.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberRequest {

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 4, max = 15, message = "닉네임은 4자 이상 15자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 12, message = "이름은 2자 이상 12자 이하로 입력해주세요.")
    private String name;

}
