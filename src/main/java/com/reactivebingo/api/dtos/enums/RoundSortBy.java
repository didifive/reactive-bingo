package com.reactivebingo.api.dtos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoundSortBy {

    NAME("name"), PRIZE("prize"), DATE("created_at");

    private final String field;

}
