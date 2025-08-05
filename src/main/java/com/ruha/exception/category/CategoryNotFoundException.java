package com.ruha.exception.category;

import com.ruha.exception.CustomException;

public class CategoryNotFoundException extends CustomException {
    public CategoryNotFoundException() {
        super(CategoryErrorCode.CATEGORY_NOT_FOUND);
    }
}