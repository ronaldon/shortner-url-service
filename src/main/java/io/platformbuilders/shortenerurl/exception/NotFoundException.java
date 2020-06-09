/*
 * Copyright 2020 
 *************************************************************
 *Nome     : APIException.java
 *Autor    : Builders
 *Data     : 26/05/2020
 *Empresa  : Platform Builders
 *************************************************************
 */

package io.platformbuilders.shortenerurl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class NotFoundException extends APIException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(final Throwable cause) {

        super(cause);
    }

    public NotFoundException(final ErrorMessage error) {

        super(error);
    }

    public NotFoundException(final String error) {

        super(error);
    }

}

