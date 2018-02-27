package com.example.wgwong.budgettracker;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Transaction implements Serializable{
    private int id;
    private Date timestamp;
    private BigDecimal cost;
    private String category;

    public Transaction(Date timestamp, BigDecimal cost, String category) {
        id = Utilities.generateRandomId();
        this.timestamp = timestamp;
        this.cost = cost;
        this.category = category;
    }

    public int getId() { return id; }

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
        return "Transaction [" + String.valueOf(id) + "] (" + timestamp.toString() + ") - Cost: " + cost.toString() + " - Category: " + category;
    }
}
