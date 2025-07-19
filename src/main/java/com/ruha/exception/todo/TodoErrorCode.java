package com.ruha.exception.todo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TodoErrorCode {
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND,"할 일이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;

}
