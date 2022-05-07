package com.db.awmd.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @NotNull
    @Min(value = 0, message = "Amount should be grater then 0")
    private BigDecimal debitPaymt;
    @NotNull(message = "Sender Account number must not null")
    private String senderAccount;
    @NotNull(message = "Sender Account number must not null")
    private String receiverAccount;
    private BigDecimal creditPaymt;

    private String message;
    private String status;
    private String transactionReference;
}
