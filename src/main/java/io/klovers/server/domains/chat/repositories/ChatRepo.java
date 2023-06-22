package io.klovers.server.domains.chat.repositories;

import io.klovers.server.domains.chat.models.entities.Chat;
import io.klovers.server.domains.user.models.entities.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRepo extends JpaRepository<Chat, Long>, ChatRepoCustom {

    @Query("""
        SELECT DISTINCT c
        FROM Chat c
        JOIN FETCH c.participants p
        WHERE c.participants IN :participants 
    """)
    List<Chat> findByParticipantsIn(@Param("participants") List<User> participants);
    List<Chat> findByParticipantsOrderByModTsDesc(User participant);
}
