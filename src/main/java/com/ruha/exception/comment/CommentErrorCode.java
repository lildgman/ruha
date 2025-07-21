package com.ruha.exception.comment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"댓글이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;

}
