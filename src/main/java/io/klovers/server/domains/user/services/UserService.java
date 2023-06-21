package io.klovers.server.domains.user.services;

import io.klovers.server.domains.user.models.dtos.ReqSignUpDto;
import io.klovers.server.domains.user.models.dtos.UserDto;

import java.util.List;

public interface UserService {
    void signUp(ReqSignUpDto data);

    List<UserDto> list(UserDto userDto);
}
