package com.recipebox.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponseDTO {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;

    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(int status, String message, Map<String, String> validationErrors) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.validationErrors = validationErrors;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
