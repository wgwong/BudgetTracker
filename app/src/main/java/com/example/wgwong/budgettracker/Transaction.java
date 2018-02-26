package com.example.wgwong.budgettracker;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wgwong on 2/26/2018.
 */

public class Transaction {
    private Date timestamp;
    private BigDecimal cost;
    private String category;

    public Transaction(Date timestamp, BigDecimal cost, String category) {
        this.timestamp = timestamp;
        this.cost = cost;
        this.category = category;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Transaction (" + timestamp.toString() + ") - Cost: " + cost.toString() + " - Category: " + category;
    }
}
