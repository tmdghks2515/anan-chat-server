package io.klovers.server.domains.user.repositories;

import io.klovers.server.domains.user.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long>, UserRepoCustom {
    Optional<User> findByUsername(String username);
    List<User> findByUsernameNot(String username);
    Optional<User> findByNickname(String nickname);
}
