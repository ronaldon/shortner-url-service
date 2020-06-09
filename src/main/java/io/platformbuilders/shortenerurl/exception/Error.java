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

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Error implements Serializable {

    private static final long serialVersionUID = 1L;

    private String field;

    private String message;
}
