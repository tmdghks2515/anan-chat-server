package io.klovers.server.domains.chat.repositories;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.klovers.server.domains.chat.models.entities.Chat;
import io.klovers.server.domains.chat.models.entities.QChat;
import io.klovers.server.domains.user.models.entities.QUser;
import io.klovers.server.domains.user.models.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static io.klovers.server.domains.chat.models.entities.QChat.chat;
import static io.klovers.server.domains.user.models.entities.QUser.user;

@Repository
@RequiredArgsConstructor
public class ChatRepoCustomImpl implements ChatRepoCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<Chat> findByParticipants(List<User> participants) {
        return queryFactory
                .selectFrom(chat)
                .join(chat.participants, user).fetchJoin()
                .where()
                .fetch();
    }
}
