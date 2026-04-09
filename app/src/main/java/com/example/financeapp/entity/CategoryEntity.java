package com.example.financeapp.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class CategoryEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String icon;
    private String color;
@Ignore
    public CategoryEntity(String color, String icon, String name) {
        this.color = color;
        this.icon = icon;
        this.name = name;
    }
    // Constructor يستقبل الاسم فقط (للإضافة الجديدة)
    public CategoryEntity(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
