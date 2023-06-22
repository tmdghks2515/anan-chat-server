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

    @Query(value = """
        SELECT c.id
        FROM chat c
        JOIN user_chat_map ucm ON c.id = ucm.chat_id
        JOIN user u ON ucm.participant = u.username
        WHERE u.username IN :participants
        GROUP BY c.id
        HAVING COUNT(DISTINCT u.username) = :participantsCount
           AND NOT EXISTS (
              SELECT 1
              FROM user_chat_map ucm2
              JOIN user u2 ON ucm2.participant = u2.username
              WHERE ucm2.chat_id = c.id
              AND u2.username NOT IN :participants
           );
    """, nativeQuery = true)
    List<Long> findIdsByParticipantsIn(@Param("participants") List<String> participants, @Param("ParticipantsCount") int participantsCount);
    List<Chat> findByParticipantsOrderByModTsDesc(User participant);
}
