package io.klovers.server.domains.user.services;

import io.klovers.server.domains.user.models.dtos.ReqSignUpDto;

public interface UserService {
    void signUp(ReqSignUpDto data);
}
