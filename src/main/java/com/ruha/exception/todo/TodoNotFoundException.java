package com.ruha.exception.todo;

import com.ruha.exception.CustomException;

public class TodoNotFoundException extends CustomException {

    public TodoNotFoundException() {
        super(TodoErrorCode.TODO_NOT_FOUND);
    }
}
