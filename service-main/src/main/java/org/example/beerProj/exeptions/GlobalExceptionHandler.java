package org.example.beerProj.exeptions;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity<Object> handleException(Throwable ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return processing(ex, request, status, null, null);
    }


    @SuppressWarnings("unused")
    private ResponseEntity<Object> processing(
            Throwable ex,
            WebRequest request,
            HttpStatusCode status,
            HttpHeaders headers,
            Object body
    ) {
        var path = request.getDescription(false);
        if (request instanceof ServletWebRequest r) {
            path = r.getRequest().getRequestURI();
        }
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(ex.getClass().getSimpleName())
                .path(path)
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            @NonNull HttpRequestMethodNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            @NonNull HttpMediaTypeNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            @NonNull HttpMediaTypeNotAcceptableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            @NonNull MissingPathVariableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            @NonNull MissingServletRequestPartException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            @NonNull ServletRequestBindingException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            @NonNull HandlerMethodValidationException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            @NonNull NoHandlerFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            @NonNull NoResourceFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
            @NonNull AsyncRequestTimeoutException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(
            @NonNull ErrorResponseException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            @NonNull MaxUploadSizeExceededException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(
            @NonNull ConversionNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            @NonNull TypeMismatchException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            @NonNull HttpMessageNotWritableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleMethodValidationException(
            @NonNull MethodValidationException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatus status,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, status, headers, null);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestNotUsableException(
            @NonNull AsyncRequestNotUsableException ex,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, null);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            @NonNull Exception ex,
            @NonNull Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request
    ) {
        return processing(ex, request, statusCode, headers, body);
    }
}