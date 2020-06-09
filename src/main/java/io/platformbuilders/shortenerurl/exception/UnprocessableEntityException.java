package io.platformbuilders.shortenerurl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableEntityException extends APIException {

    private static final long serialVersionUID = 1L;

    public UnprocessableEntityException(Throwable cause) {

        super(cause);
    }

    public UnprocessableEntityException(ErrorMessage error) {

        super(error);
    }

    public UnprocessableEntityException(String error) {

        super(error);
    }

}
