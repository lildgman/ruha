package com.ruha.exception.comment;

import com.ruha.exception.todo.TodoErrorCode;
import lombok.Getter;

@Getter
public class CommentNotFoundException extends RuntimeException {

    private final CommentErrorCode code;

    public CommentNotFoundException() {
        super(CommentErrorCode.COMMENT_NOT_FOUND.getMessage());
        this.code = CommentErrorCode.COMMENT_NOT_FOUND;
    }

    public CommentNotFoundException(String message) {
        super(message);
        this.code = CommentErrorCode.COMMENT_NOT_FOUND;
    }

    public CommentNotFoundException(CommentErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
