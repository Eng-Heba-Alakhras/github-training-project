package com.example.financeapp.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.financeapp.AppDatabase;
import com.example.financeapp.daos.CategoryDao;
import com.example.financeapp.entity.CategoryEntity;

import java.util.List;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<CategoryEntity>> allCategories;

    public CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
        // تهيئة المتغير بجلب الـ LiveData من الـ DAO
        allCategories = categoryDao.getAllCategories();
    }

    // التصحيح الأهم: يجب أن تعيد الدالة LiveData وليس List عادية
    public LiveData<List<CategoryEntity>> getAllCategories() {
        return allCategories;
    }

    // Insert Category
    public void insert(CategoryEntity category) {
        new Thread(() -> categoryDao.insert(category)).start();
    }

    // Update Category
    public void update(CategoryEntity category) {
        new Thread(() -> categoryDao.update(category)).start();
    }

    // Delete Category
    public void delete(CategoryEntity category) {
        new Thread(() -> categoryDao.delete(category)).start();
    }
}