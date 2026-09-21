package com.core.rewards.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    private Long id;

    @Column(name = "total_rewards_balance", nullable = false)
    private double totalRewardsBalance;

    @Version
    private int version; // Intercepts concurrent multi-node updates at the database level

    public UserAccount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalRewardsBalance() {
        return totalRewardsBalance;
    }

    public void setTotalRewardsBalance(double totalRewardsBalance) {
        this.totalRewardsBalance = totalRewardsBalance;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
