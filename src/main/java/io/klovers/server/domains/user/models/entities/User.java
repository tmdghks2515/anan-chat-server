package io.klovers.server.domains.user.models.entities;

import io.klovers.server.common.codes.Gender;
import io.klovers.server.common.codes.Role;
import io.klovers.server.common.codes.converters.GenderConverter;
import io.klovers.server.common.codes.converters.RoleConverter;
import io.klovers.server.domains.chat.models.entities.Chat;
import io.klovers.server.domains.user.models.dtos.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
/* 엔티티의 모든필드를 insert 하는 방식을 취하지 않겠다. (즉 set 하지 않은 필드를 null 로 insert 하지 않겠다) (default 적용가능) */
@DynamicInsert
@Builder
@Table(indexes =
    @Index(name = "idx__username", columnList = "username")
)
public class User {
    @Id
    @Column(unique = true)
    private String username;

    private String password;

    private String salt;

    @Column(unique = true)
    private String nickname;

    private int age;

    private String email;

    private String telNo;

    private String profileSrc;

    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Convert(converter = RoleConverter.class)
    private Role role;

    @ManyToMany(mappedBy = "participants")
    private List<Chat> chats;

    public UserDto toDto() {
        return UserDto.builder()
                .username(username)
                .nickname(nickname)
                .age(age)
                .email(email)
                .telNo(telNo)
                .profileSrc(profileSrc)
                .gender(gender.toCodeDto())
                .role(role.toCodeDto())
                .build();
    }

    public UserDto toDtoForAuth() {
        return UserDto.builder()
                .username(username)
                .role(role.toCodeDto())
                .password(password)
                .build();
    }
}
