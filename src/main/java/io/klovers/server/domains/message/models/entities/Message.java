package io.klovers.server.domains.message.models.entities;

import io.klovers.server.common.models.entities.BaseTimeEntity;
import io.klovers.server.domains.message.models.dtos.MessageDto;
import io.klovers.server.domains.user.models.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class Message extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne /* ToOne - default fetch type: Eager */
    @JoinColumn(name = "sender_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User recipient;

    private String content;

    @Column(columnDefinition = "boolean default false")
    private boolean deleted;

    public MessageDto toDto() {
        return MessageDto.builder()
                .id(id)
                .content(content)
                .sender(sender.toDto())
                .recipient(recipient.toDto())
                .regTs(getRegTs())
                .modTs(getModTs())
                .build();
    }
}
