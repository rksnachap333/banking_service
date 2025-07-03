package org.example.enums;

public enum AccountType
{
    SAVING(101),
    CURRENT(102),
    MINOR(103);

    private final int code;

    AccountType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
