package com.ruha.controller;

import com.ruha.dto.follow.FollowListResponse;
import com.ruha.dto.follow.FollowResponse;
import com.ruha.dto.follow.FollowStatusResponse;
import com.ruha.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 팔로우 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /**
     * 특정 회원 팔로우
     *
     * @param memberId 팔로우할 회원 ID
     * @return 생성된 팔로우 정보
     */
    @PostMapping("/{memberId}")
    public ResponseEntity<FollowResponse> follow(@PathVariable Long memberId) {

        FollowResponse response = followService.follow(memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 특정 회원 언팔로우
     *
     * @param memberId 언팔로우할 회원 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> unfollow(@PathVariable Long memberId) {

        followService.unfollow(memberId);
        return ResponseEntity.noContent()
                .build();
    }

    /**
     * 특정 회원의 팔로잉 목록 조회
     * @param memberId 조회할 회원 ID
     * @return 해당 회원이 팔로우하는 회원 목록 (총 개수 포함)
     */
    @GetMapping("/{memberId}/followings")
    public ResponseEntity<FollowListResponse> getFollowings(@PathVariable Long memberId) {
        List<FollowResponse> follows = followService.getMemberFollowings(memberId);
        FollowListResponse response = FollowListResponse.of(follows);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 회원의 팔로워 목록 조회
     * @param memberId 조회할 회원 ID
     * @return 해당 회원을 팔로우하는 회원 목록 (총 개수 포함)
     */
    @GetMapping("/{memberId}/followers")
    public ResponseEntity<FollowListResponse> getFollowers(@PathVariable Long memberId) {
        List<FollowResponse> followers = followService.getMemberFollowers(memberId);
        FollowListResponse response = FollowListResponse.of(followers);
        return ResponseEntity.ok(response);
    }

    /**
     * 현재 로그인한 회원이 특정 회원을 팔로우하고 있는지 확인
     * @param memberId 확인할 회원 id
     * @return 팔로우 상태
     */
    @GetMapping("/{memberId}/status")
    public ResponseEntity<FollowStatusResponse> checkFollowStatus(@PathVariable Long memberId) {
        boolean isFollowing = followService.isFollowing(memberId);
        return ResponseEntity.ok(new FollowStatusResponse(isFollowing));
    }
}
