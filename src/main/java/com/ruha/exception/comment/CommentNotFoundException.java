package com.ruha.exception.comment;

import com.ruha.exception.CustomException;

public class CommentNotFoundException extends CustomException {

    public CommentNotFoundException() {
        super(CommentErrorCode.COMMENT_NOT_FOUND);
    }
}
