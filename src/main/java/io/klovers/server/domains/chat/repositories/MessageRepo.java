package io.klovers.server.domains.chat.repositories;

import io.klovers.server.domains.chat.models.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, Long> {
}
