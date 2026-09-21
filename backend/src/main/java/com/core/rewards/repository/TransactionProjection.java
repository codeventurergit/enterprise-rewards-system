package com.core.rewards.repository;

import java.time.LocalDate;

public interface TransactionProjection {
    double getAmount();
    String getTxType();
    LocalDate getCreatedAt();
}
