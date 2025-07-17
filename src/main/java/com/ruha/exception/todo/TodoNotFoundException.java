package com.ruha.exception.todo;

import lombok.Getter;

@Getter
public class TodoNotFoundException extends RuntimeException {

    private final TodoErrorCode todoErrorCode;

    public TodoNotFoundException() {
        super(TodoErrorCode.TODO_NOT_FOUND.getMessage());
        this.todoErrorCode = TodoErrorCode.TODO_NOT_FOUND;
    }

    public TodoNotFoundException(String message) {
        super(message);
        this.todoErrorCode = TodoErrorCode.TODO_NOT_FOUND;
    }

    public TodoNotFoundException(TodoErrorCode todoErrorCode) {
        super(todoErrorCode.getMessage());
        this.todoErrorCode = todoErrorCode;
    }
}
