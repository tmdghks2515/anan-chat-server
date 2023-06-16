package io.klovers.server.domains.user.repositories;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.klovers.server.domains.user.models.dtos.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static io.klovers.server.domains.user.models.entities.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepoCustomImpl implements UserRepoCustom {
    private final JPAQueryFactory queryFactory;
}
