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

import lombok.Getter;

@Getter
public abstract class APIException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final ErrorMessage error;

    public APIException(final Throwable cause) {

        super(cause);
        this.error = ErrorMessage.builder().message(cause.getMessage()).build();
    }

    public APIException(final ErrorMessage error) {

        this.error = error;
    }

    public APIException(final String error) {

        this.error = ErrorMessage.builder().message(error).build();
    }

}
