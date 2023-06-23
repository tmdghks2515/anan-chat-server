package io.klovers.server.domains.chat.models.dtos.req;

import io.klovers.server.common.codes.Language;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class ReqGetMessagesDto {
    private Long chatId;
    private String participantUsername;
    private Language targetLang;
    private Pageable pageable;
}
