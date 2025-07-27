package com.ruha.exception.todo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import com.ruha.exception.ErrorCode;


@Getter
@RequiredArgsConstructor
public enum TodoErrorCode implements ErrorCode {
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND,"할 일이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
