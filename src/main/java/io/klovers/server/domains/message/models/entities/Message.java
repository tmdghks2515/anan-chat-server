package io.klovers.server.domains.message.models.entities;

import io.klovers.server.common.models.entities.BaseTimeEntity;
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
    /* 16진수 generator (bulk insert 시킬때 bulk 쿼리가 나가는지 확인 필요. bulk 쿼리가 나가지 않는다면 성능저하 됨으로 bulk 쿼리 나가도록 설정 필요) */
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "hexStringGenerator")
    @GenericGenerator(name = "hexStringGenerator")
    private String id;

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
}
