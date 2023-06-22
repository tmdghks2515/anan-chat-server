package io.klovers.server.common.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListResDto<T> {
    List<T> list;
    boolean hasNext;
    int totalCount;
}
