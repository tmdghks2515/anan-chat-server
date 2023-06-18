package io.klovers.server.domains.user.models.dtos;

import io.klovers.server.common.models.dtos.CodeDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String nickname;
    private int age;
    private String email;
    private String telNo;
    private String profileSrc;
    private CodeDto gender;
    private CodeDto role;
    private String password;
}
