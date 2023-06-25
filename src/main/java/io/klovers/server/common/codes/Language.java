package io.klovers.server.common.codes;

import lombok.Getter;

@Getter
public enum Language {
    en("영어"),
    ko("한국어"),
    ja("일본어"),
    ;

    private final String label;

    Language(String label) {
        this.label = label;
    }
}
