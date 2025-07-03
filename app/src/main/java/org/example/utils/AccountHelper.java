package org.example.utils;

import org.example.enums.AccountType;

public class AccountHelper
{
    public static final String BANK_ID = "1001";

    public static String generateAccountNumber(AccountType accountType, Long id)
    {
        return BANK_ID + String.valueOf(accountType.getCode()) + String.format("%05d", id);
    }
}
