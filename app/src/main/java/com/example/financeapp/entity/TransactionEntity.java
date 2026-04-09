package com.example.financeapp.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;


@Entity(tableName = "transactions")
@TypeConverters({Conventer.class})
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private String type;
    private int categoryId;
    private Date date;
    private String note;
    @Ignore
    public TransactionEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public TransactionEntity(double amount, String type, int categoryId, Date date, String note) {
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.date = date;
        this.note = note;

    }
}
