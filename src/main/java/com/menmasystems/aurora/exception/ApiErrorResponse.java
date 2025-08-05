/*
 * Copyright (c) 2025 Fotios Makris, Menma Systems, Menma Software. All rights reserved.
 */
package com.menmasystems.aurora.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ApiErrorResponse {

    public record FieldErrorDetail(String field, String message) {}

    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldErrorDetail> errors;

    public ApiErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiErrorResponse(int code, String message, List<FieldErrorDetail> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public ApiErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ApiErrorResponse(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldErrorDetail> errors) {
        this.errors = errors;
    }
}
