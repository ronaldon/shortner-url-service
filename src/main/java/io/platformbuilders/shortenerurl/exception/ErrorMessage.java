package io.platformbuilders.shortenerurl.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    private String message;

    private String link;

    private List<Error> errors;

}
