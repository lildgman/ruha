package com.ruha.exception.file;

import com.ruha.exception.CustomException;

public class FileSaveException extends CustomException {
    public FileSaveException() {
        super(FileErrorCode.FILE_SAVE_FAILED);
    }
    
    public FileSaveException(Throwable cause) {
        super(FileErrorCode.FILE_SAVE_FAILED);
        initCause(cause);
    }
}