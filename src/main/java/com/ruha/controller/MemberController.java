package com.ruha.controller;

import com.ruha.dto.member.*;
import com.ruha.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 관련 API를 처리하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 새로운 회원을 가입시킵니다.
     *
     * @param request 회원 가입에 필요한 정보 (닉네임, 비밀번호, 이름)
     * @return 생성된 회원의 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<MemberResponse> signUp(@Valid @RequestBody CreateMemberRequest request) {

        MemberResponse response = memberService.createMember(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 회원 로그인을 처리합니다.
     *
     * @param request 로그인 정보 (닉네임, 비밀번호)
     * @return JWT 액세스 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {

        TokenResponse response = memberService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인한 회원의 정보를 조회합니다.
     *
     * @return 로그인한 회원의 정보
     */
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getCurrentMemberInfo() {
        MemberResponse response = memberService.getCurrentMemberInfo();
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인한 회원의 이름을 변경합니다.
     *
     * @param request 변경할 이름 정보
     * @return 업데이트된 회원 정보
     */
    @PatchMapping("/me/name")
    public ResponseEntity<MemberResponse> updateName(@Valid @RequestBody UpdateMemberRequest request) {

        MemberResponse response = memberService.updateName(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인한 회원의 비밀번호를 변경합니다.
     *
     * @param request 현재 비밀번호 및 새 비밀번호
     * @return 업데이트된 회원 정보
     */
    @PatchMapping("/me/password")
    public ResponseEntity<MemberResponse> updatePassword(@Valid @RequestBody PasswordChangeRequest request) {

        MemberResponse response = memberService.updatePassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인한 회원을 탈퇴시킵니다.
     *
     * @param request 비밀번호 확인 정보
     * @return 204 No Content
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@Valid @RequestBody DeleteMemberRequest request) {
        memberService.deleteMember(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDetailResponse> getMemberInfo(@PathVariable Long memberId) {
        MemberDetailResponse response = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(response);
    }
}
