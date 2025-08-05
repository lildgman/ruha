package com.ruha.exception.file;

import com.ruha.exception.CustomException;

public class InvalidFileTypeException extends CustomException {
    public InvalidFileTypeException() {
        super(FileErrorCode.INVALID_FILE_TYPE);
    }
}