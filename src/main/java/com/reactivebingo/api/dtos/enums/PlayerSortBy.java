package com.reactivebingo.api.dtos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PlayerSortBy {

    NAME("name"), EMAIL("email");

    private final String field;

}
