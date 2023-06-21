package io.klovers.server.domains.chat.repositories;

import io.klovers.server.domains.chat.models.entities.Chat;
import io.klovers.server.domains.user.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepo extends JpaRepository<Chat, Long> {
//    Optional<Chat> findByParticipants(List<User> participants);
}
