package com.ruha.exception.file;

import com.ruha.exception.CustomException;

public class InvalidFileNameException extends CustomException {
    public InvalidFileNameException() {
        super(FileErrorCode.INVALID_FILE_NAME);
    }
}