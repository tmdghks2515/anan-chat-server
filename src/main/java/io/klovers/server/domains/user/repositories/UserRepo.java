package io.klovers.server.domains.user.repositories;

import io.klovers.server.domains.user.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String>, UserRepoCustom {
    Optional<User> findByUsername(String username);
}
