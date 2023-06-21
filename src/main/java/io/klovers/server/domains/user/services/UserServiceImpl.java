package io.klovers.server.domains.user.services;

import io.klovers.server.common.codes.Role;
import io.klovers.server.common.exceptions.ApiException;
import io.klovers.server.common.utils.AuthUtils;
import io.klovers.server.domains.user.models.dtos.ReqSignUpDto;
import io.klovers.server.domains.user.models.dtos.UserDto;
import io.klovers.server.domains.user.models.entities.User;
import io.klovers.server.domains.user.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthUtils authUtils;
    private final UserRepo userRepo;

    @Override
    public void signUp(ReqSignUpDto data) {
        User userByUsername = userRepo.findByUsername(data.getUsername()).orElse(null);
        if (userByUsername != null) {
            throw new ApiException("이미 사용중인 아이디 입니다, 다른 아이디를 입력해 주세요.");
        }
        User userByNickname = userRepo.findByNickname(data.getNickname()).orElse(null);
        if (userByNickname != null) {
            throw new ApiException("이미 사용중인 닉네임 입니다, 다른 닉네임을 입력해 주세요.");
        }
        /* User userByEmail = userRepository.findByEmail(data.getEmail()).orElse(null);
        if (userByEmail != null) {
            throw new ApiException("이미 사용중인 이메일 입니다, 다른 이메일을 입력해 주세요.");
        } */

        String salt = authUtils.genSalt();
        String encryptedPwd = authUtils.hashPwd(data.getPassword(), salt);
        User user = User.builder()
                .username(data.getUsername())
                .nickname(data.getNickname())
                .email(data.getEmail())
                .password(encryptedPwd)
                .salt(salt)
                .age(data.getAge())
                .gender(data.getGender())
                .role(Role.MEMBER)
                .build();
        userRepo.save(user);
    }

    @Override
    public List<UserDto> list(UserDto userDto) {
        return userRepo
                .findByUsernameNot(userDto.getUsername())
                .stream()
                .map(User::toDto)
                .collect(Collectors.toList());
    }
}
