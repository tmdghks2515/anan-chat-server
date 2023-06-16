package io.klovers.server.common.codes;

import io.klovers.server.common.models.dtos.CodeDto;
import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    FEMALE("여성"),
    MALE_HOMO("남성(동성애자)"),
    FEMALE_HOMO("여성(동성애자)"),
    TG_MTF("트렌스젠더(MTF)"),
    TG_FTM("트렌스젠더(FTM)"),
    ;

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    public CodeDto toCodeDto() {
        return CodeDto.builder()
                .label(label)
                .value(this.name())
                .build();
    }
}
