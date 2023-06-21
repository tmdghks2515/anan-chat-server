package io.klovers.server.domains.chat.repositories;

import io.klovers.server.domains.chat.models.entities.Chat;
import io.klovers.server.domains.user.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRepo extends JpaRepository<Chat, Long> {
    List<Chat> findByParticipantsIn(List<User> participants);
}
