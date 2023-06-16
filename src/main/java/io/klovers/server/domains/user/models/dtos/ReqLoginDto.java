package io.klovers.server.domains.user.models.dtos;

import lombok.Getter;

@Getter
public class ReqLoginDto {
    private String username;
    private String password;
}
