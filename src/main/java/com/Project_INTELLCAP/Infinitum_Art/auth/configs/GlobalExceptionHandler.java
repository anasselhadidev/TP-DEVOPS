package com.Project_INTELLCAP.Infinitum_Art.auth.configs;

import com.Project_INTELLCAP.Infinitum_Art.auth.DTO.ErrorResponse;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    // Helper method to get the most specific error message
    private String getErrorMessage(Exception ex) {
        return ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : getDefaultMessage(ex);
    }

    // Helper method to get default messages based on exception type
    private String getDefaultMessage(Exception ex) {
        if (ex instanceof DataIntegrityViolationException) return "Database constraint violation";
        if (ex instanceof DataAccessException) return "Database operation failed";
        if (ex instanceof AuthenticationException) return "Authentication failed";
        // Add more default messages as needed
        return "An error occurred";
    }

    // 1. Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        errors.toString(),
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    // 2. Security Exceptions
    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthExceptions(
            Exception ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Authentication Failed",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler({
            DisabledException.class,
            LockedException.class
    })
    public ResponseEntity<ErrorResponse> handleAccountStatusExceptions(
            Exception ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "Account Issue",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.FORBIDDEN.value(),
                        "Access Denied",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    // 3. Database Exceptions
    @ExceptionHandler({
            EntityNotFoundException.class,
            NoHandlerFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(
            Exception ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "Resource Not Found",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        String message = ex.getRootCause() != null
                ? ex.getRootCause().getMessage()
                : getErrorMessage(ex);

        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "Data Conflict",
                        message,
                        request.getRequestURI()
                ),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Constraint Violation",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
            DataAccessException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Database Error",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    // 4. Request Errors
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        "Method Not Allowed",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        "Unsupported Media Type",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        String message = ex.getMessage() != null
                ? ex.getMessage()
                : "Required parameter '" + ex.getParameterName() + "' is missing";

        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Missing Parameter",
                        message,
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingPart(
            MissingServletRequestPartException ex,
            HttpServletRequest request) {
        String message = ex.getMessage() != null
                ? ex.getMessage()
                : "Required request part '" + ex.getRequestPartName() + "' is missing";

        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Missing Request Part",
                        message,
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Malformed Request",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    // 5. File Upload Errors
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.PAYLOAD_TOO_LARGE.value(),
                        "File Too Large",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    // 6. Custom Business Exceptions
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleCustomStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        ex.getStatusCode().value(),
                        ex.getReason() != null ? ex.getReason() : "Custom Error",
                        ex.getReason() != null ? ex.getReason() : getErrorMessage(ex),
                        request.getRequestURI()
                ),
                ex.getStatusCode()
        );
    }

    // 7. Additional Common Exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid Argument",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "Invalid State",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(
            NullPointerException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Null Pointer Error",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponse> handleNumberFormat(
            NumberFormatException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid Number Format",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    // JSON Processing Exceptions (if using Jackson)
    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessing(
            com.fasterxml.jackson.core.JsonProcessingException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "JSON Processing Error",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    // Timeout Exceptions
    @ExceptionHandler(java.util.concurrent.TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeout(
            java.util.concurrent.TimeoutException ex,
            HttpServletRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.REQUEST_TIMEOUT.value(),
                        "Request Timeout",
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                HttpStatus.REQUEST_TIMEOUT
        );
    }

    @ExceptionHandler({
            MailAuthenticationException.class,
            MailSendException.class,
            MessagingException.class,
            MailException.class
    })
    public ResponseEntity<ErrorResponse> handleEmailExceptions(
            Exception ex,
            HttpServletRequest request) {

        String errorType = "Email Service Error";
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

        if (ex instanceof MailAuthenticationException) {
            errorType = "Email Authentication Failure";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(
                new ErrorResponse(
                        status.value(),
                        errorType,
                        getErrorMessage(ex),
                        request.getRequestURI()
                ),
                status
        );
    }



    // 8. Catch-All Handler (MUST BE LAST)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        logger.error("Unexpected exception occurred at {}: {}",
                request.getRequestURI(), ex.getMessage(), ex);

        String message = "dev".equals(activeProfile) || "test".equals(activeProfile)
                ? getErrorMessage(ex)
                : "An unexpected error occurred";

        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        message,
                        request.getRequestURI()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}