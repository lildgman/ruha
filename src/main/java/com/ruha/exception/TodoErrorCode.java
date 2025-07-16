package com.ruha.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TodoErrorCode {
    TODO_NOT_FOUND("TODO_NOT_FOUND","할 일이 존재하지 않습니다.");

    private final String code;
    private final String message;

}
