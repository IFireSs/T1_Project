package com.account_processing.dto;

import com.account_processing.entity.Account;
import com.account_processing.enums.PaymentSystem;

public class CardDto {
    private String accountId;
    private String cardId;
    private PaymentSystem paymentSystem;
}
