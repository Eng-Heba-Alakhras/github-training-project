package com.example.financeapp.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.financeapp.entity.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {

    // Insert Category
    @Insert
    void insert(CategoryEntity category);


    // Update Category
    @Update
    void update(CategoryEntity category);
    //تتاكد اذا كان الجدول فارغا ام لا
    @Query("SELECT * FROM categories LIMIT 1")
    List<CategoryEntity> getAnyCategory();


    // Delete Category
    @Delete
    void delete(CategoryEntity category);


    // Get All Categories
    @Query("SELECT * FROM categories")
    LiveData<List<CategoryEntity>> getAllCategories();


    // Get Category By Id
    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    CategoryEntity getCategoryById(int id);


    // Delete All Categories
    @Query("DELETE FROM categories")
    void deleteAll();
}
