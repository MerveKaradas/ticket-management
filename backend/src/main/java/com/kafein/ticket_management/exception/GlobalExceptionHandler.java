package com.kafein.ticket_management.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private ErrorResponse buildError(HttpStatus status, String message, HttpServletRequest request) {
                return ErrorResponse.builder()
                                .status(status.value())
                                .error(status.getReasonPhrase())
                                .message(message)
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build();

        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
                Map<String, Object> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> {
                        errors.put(error.getField(), error.getDefaultMessage());
                });
                Map<String, Object> body = Map.of(
                                "status", 400,
                                "error", "Bad Request",
                                "messages", errors,
                                "timestamp", LocalDateTime.now());
                return ResponseEntity.badRequest().body(body);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request));
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(buildError(HttpStatus.BAD_REQUEST, "Geçersiz veri formatı: " + ex.getMessage(),
                                                request));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request));
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(buildError(HttpStatus.CONFLICT, ex.getMessage(), request));
        }

        @ExceptionHandler(DisabledException.class)
        public ResponseEntity<ErrorResponse> handleDisabledAccount(DisabledException ex, HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(buildError(HttpStatus.FORBIDDEN, "Hesabınız pasife alınmıştır.", request));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGeneral(Exception ex,
                        HttpServletRequest request) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu",
                                                request));
        }

}
