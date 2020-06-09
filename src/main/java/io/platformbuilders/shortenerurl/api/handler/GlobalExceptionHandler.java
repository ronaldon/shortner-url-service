package io.platformbuilders.shortenerurl.api.handler;

import java.net.SocketTimeoutException;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.platformbuilders.shortenerurl.exception.APIException;
import io.platformbuilders.shortenerurl.exception.Error;
import io.platformbuilders.shortenerurl.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String EXCEPTION_LOG_MSG = "e=%s,m=%s";

    private static final String BAD_REQUEST_MSG = "Invalid request";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    private void logE(final Exception e) {

        final String message = String.format(EXCEPTION_LOG_MSG, e.getClass().getSimpleName(), e.getMessage());
        log.error(message, e);
    }

    private void logE(final Exception e, final String message) {

        log.error(message, e);
    }

    @ExceptionHandler(APIException.class)
    protected ResponseEntity<ErrorMessage> processAPIException(final APIException ex) {

        final ResponseStatus status = ex.getClass().getDeclaredAnnotation(ResponseStatus.class);
        logE(ex);
        return new ResponseEntity<>(ex.getError(), Objects.nonNull(status) ? status.code() : HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorMessage> processException(final Exception ex) {

        logE(ex);
        return new ResponseEntity<>(ErrorMessage.builder().message(ex.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    protected ResponseEntity<ErrorMessage> processHttpStatusCodeException(final HttpStatusCodeException ex) {

        logE(ex, ex.getResponseBodyAsString());

        return new ResponseEntity<>(prepareErrorItem(ex), ex.getStatusCode());
    }

    @ExceptionHandler(SocketTimeoutException.class)
    @ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
    protected void processSocketTimeoutException(final SocketTimeoutException ex) {

        logE(ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    protected void processHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException ex) {

        logE(ex);
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected void processHttpRequestMethodNotSupportedException(final TypeMismatchException ex) {

        logE(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected void processHttpMessageNotReadableException(final HttpMessageNotReadableException ex) {

        logE(ex);
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public void invalidFormatException(final InvalidFormatException ex) {

        logE(ex);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    protected void processHttpMediaTypeNotAcceptableException(final HttpMediaTypeNotAcceptableException ex) {

        logE(ex);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    protected void processHttpMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException ex) {

        logE(ex);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorMessage> processBindException(final BindException ex) {

        logE(ex);
        return badRequest(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {

        logE(ex);
        final ErrorMessage.ErrorMessageBuilder builder = ErrorMessage.builder().message(BAD_REQUEST_MSG);

        builder.errors(ex.getBindingResult().getFieldErrors().stream()
                         .map(error -> Error.builder().field(error.getField()).message(getMessage(error)).build())
                         .collect(Collectors.toList()));
        return new ResponseEntity<>(builder.build(), HttpStatus.BAD_REQUEST);
    }

    private String getMessage(final ObjectError objectError) {

        final String code = objectError.getDefaultMessage();
        final Object[] args = objectError.getArguments();
        return messageSource.getMessage(code, args, code, Locale.getDefault());
    }

    private ResponseEntity<ErrorMessage> badRequest(final BindException ex) {

        final ErrorMessage.ErrorMessageBuilder builder = ErrorMessage.builder().message(ex.getMessage());

        builder.errors(ex.getAllErrors().stream()
                         .map(error -> Error.builder().message(getMessage(error)).build())
                         .collect(Collectors.toList()));

        return new ResponseEntity<>(builder.build(), HttpStatus.BAD_REQUEST);
    }

    private ErrorMessage prepareErrorItem(final HttpStatusCodeException ex) {

        try {
            final ErrorMessage errorMessage = objectMapper.readValue(ex.getResponseBodyAsString(), ErrorMessage.class);
            if (Objects.isNull(errorMessage)) {
                return ErrorMessage.builder().message(ex.getResponseBodyAsString()).build();
            } else {
                return errorMessage;
            }
        } catch (Exception e) {
            return ErrorMessage.builder().message(ex.getResponseBodyAsString()).build();
        }
    }

}
