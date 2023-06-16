package io.klovers.server.domains.message.repositories;

import io.klovers.server.domains.message.models.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepo extends JpaRepository<Message, String> {
}
