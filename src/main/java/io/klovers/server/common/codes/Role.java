package io.klovers.server.common.codes;

import io.klovers.server.common.models.dtos.CodeDto;
import lombok.Getter;

@Getter
public enum Role {
    MEMBER("회원"),
    ADMIN("관리자"),
    ADMIN_MASTER("마스터 관리자"),
    ANONYMOUS("방문자"),
    ;

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public CodeDto toCodeDto() {
        return CodeDto.builder()
                .label(label)
                .value(this.name())
                .build();
    }
}
