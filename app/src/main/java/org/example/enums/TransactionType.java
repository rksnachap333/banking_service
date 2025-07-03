package org.example.enums;

public enum TransactionType {
    DEPOSIT(101),
    WITHDRAWAL(102),
    TRANSFER(103);

    private final int code;

    TransactionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
