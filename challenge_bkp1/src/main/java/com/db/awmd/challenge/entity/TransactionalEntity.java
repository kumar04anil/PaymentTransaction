package com.db.awmd.challenge.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="TRANSACTION_DETAIL_TBL")
public class TransactionalEntity {

    @Id
    private String transactionId;

    @NotNull
    private String senderAccount;

    @NotNull
    private String receiverAccount;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal amountCredit;

    private BigDecimal amountDebit;

    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal totalAmountCredit;

    private String status;

}
