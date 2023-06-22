package io.klovers.server.domains.chat.repositories;

import io.klovers.server.domains.chat.models.entities.Chat;
import io.klovers.server.domains.user.models.entities.User;

import java.util.List;

public interface ChatRepoCustom {
    List<Chat> findByParticipants(List<User> participants);
}
