package com.service.himalaya.enamclass;

public enum Roll_id_ENUM {


    ADMIN("1"),
    MANAGER("2"),
    WORKER("3");

    private String intValue;

    Roll_id_ENUM(String value) {
        intValue = value;
    }

    @Override
    public String toString() {
        return intValue;
    }

}
