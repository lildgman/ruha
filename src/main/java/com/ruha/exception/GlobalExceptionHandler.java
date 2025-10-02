package com.ruha.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     * 비즈니스 로직에서 발생하는 커스텀 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getErrorCode(), e);
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    /**
     * @Valid 검증 실패 시 발생
     * RequestBody로 넘어온 DTO 객체의 validation 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        return ErrorResponse.validationError(e.getBindingResult());
    }

    /**
     * @Validated 검증 실패 시 발생
     * PathVariable, RequestParam 등의 validation 실패
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        log.error("ConstraintViolationException: {}", e.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = e.getConstraintViolations().stream()
                .map(violation -> ErrorResponse.FieldError.builder()
                        .field(getFieldName(violation))
                        .value(violation.getInvalidValue() == null ? "" : violation.getInvalidValue().toString())
                        .reason(violation.getMessage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.name())
                        .code("CONSTRAINT_VIOLATION")
                        .message("입력값 제약 조건을 위반했습니다.")
                        .errors(fieldErrors)
                        .build()
                );
    }

    /**
     * JSON 파싱 실패 시 발생
     * 잘못된 JSON 형식이거나 타입 불일치
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.name())
                        .code("INVALID_INPUT_FORMAT")
                        .message("잘못된 형식의 입력입니다. JSON 형식을 확인해주세요.")
                        .build()
                );
    }

    /**
     * 예상치 못한 서버 에러
     * 개발 중 발견되지 않은 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .code("INTERNAL_SERVER_ERROR")
                        .message("서버 내부 오류가 발생했습니다. 관리자에게 문의해주세요.")
                        .build()
                );
    }

    /**
     * ConstraintViolation에서 필드명 추출
     */
    private String getFieldName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String[] splitPath = propertyPath.split("\\.");
        return splitPath[splitPath.length - 1];
    }
}
