package io.klovers.server.domains.user.models.dtos;

import io.klovers.server.common.codes.Gender;
import lombok.Getter;

@Getter
public class ReqSignUpDto {
    private String username;
    private String nickname;
    private String email;
    private String password;
    private int age;
    private Gender gender;
}
