package com.crittermap.backcountrynavigator.xe.data.model;

public enum BCMembershipType {
    BRONZE("Bronze"),
    SILVER("Silver"),
    GOLD("Gold");

    private final String name;

    BCMembershipType(String name) {
        this.name = name;
    }

    public static BCMembershipType getFromName(String name) {
        for (BCMembershipType type : BCMembershipType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
