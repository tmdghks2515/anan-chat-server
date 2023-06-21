package io.klovers.server.domains.chat.models.entities;

import io.klovers.server.common.models.entities.BaseTimeEntity;
import io.klovers.server.domains.chat.models.dtos.ChatDto;
import io.klovers.server.domains.user.models.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "user_chat_map",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "participant"))
    private List<User> participants = new ArrayList<>();

    public ChatDto toDto() {
        return ChatDto.builder()
                .id(id)
                .name(name)
                .participants(
                        participants
                                .stream()
                                .map(User::toDto)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
