package com.lista.compras.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UnitEnum {
    UND("und"),
    G("g"),
    KG("kg"),
    L("l"),
    ML("ml");

    private final String value;

    UnitEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UnitEnum fromValue(String value) {
        for (UnitEnum unit : UnitEnum.values()) {
            if (unit.value.equalsIgnoreCase(value)) {
                return unit;
            }
        }
        return UND;
    }
}
