package com.example.financeapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class BudgetEntity {
@PrimaryKey(autoGenerate = true)
    private int id;

    private int month;
    private int year;
    private int categoryId;
    private double limitAmount;

    public BudgetEntity(double limitAmount, int categoryId, int year, int month) {
        this.limitAmount = limitAmount;
        this.categoryId = categoryId;
        this.year = year;
        this.month = month;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}


