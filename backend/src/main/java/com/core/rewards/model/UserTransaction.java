package com.core.rewards.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "points_ledger",
    indexes = {
        // Enforces a high-speed B-Tree lookup path over multi-month transaction queries
        @Index(name = "idx_customer_date", columnList = "customer_id, created_at")
    }
)
public class UserTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "tx_type", nullable = false)
    private String transactionType;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    public UserTransaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
