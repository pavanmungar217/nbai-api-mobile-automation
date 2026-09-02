package com.nbai.automation.api.booker.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthRequest {

    private final String username;
    private final String password;
}
