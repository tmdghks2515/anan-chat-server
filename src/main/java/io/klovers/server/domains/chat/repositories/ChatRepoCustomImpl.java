package io.klovers.server.domains.chat.repositories;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepoCustomImpl implements ChatRepoCustom {
    private final JPAQueryFactory queryFactory;
}
