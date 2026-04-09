package com.example.financeapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.financeapp.daos.CategoryDao;
import com.example.financeapp.daos.TransactionDao;
import com.example.financeapp.entity.CategoryEntity;
import com.example.financeapp.entity.Conventer;
import com.example.financeapp.entity.TransactionEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// تعريف الجداول والإصدار
@Database(entities = {TransactionEntity.class, CategoryEntity.class}, version =5, exportSchema = false)
@TypeConverters({Conventer.class}) // لضمان عمل التاريخ بشكل صحيح
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    // تعريف الـ DAOs
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();

    // خادم لتنفيذ العمليات في الخلفية (Background Thread)
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, "finance_database"
                    )
                    .fallbackToDestructiveMigration() // لمسح البيانات القديمة عند تغيير الإصدار
                    .addCallback(roomCallback) // هذا السطر هو المسؤول عن إضافة الفئات
                    .build();
        }
        return instance;
    }

    // الـ Callback الذي ينفذ عند إنشاء قاعدة البيانات لأول مرة
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // تنفيذ الإضافة في الخلفية
            databaseWriteExecutor.execute(() -> {
                CategoryDao dao = instance.categoryDao();

                // فحص إذا كان الجدول فارغاً قبل الإضافة (عشان ما يكرر الأسماء كل مرة يفتح)
                if (dao.getAnyCategory() == null || dao.getAnyCategory().isEmpty()) {
                    dao.insert(new CategoryEntity("Food"));
                    dao.insert(new CategoryEntity("Salary"));
                    dao.insert(new CategoryEntity("Transport"));
                    dao.insert(new CategoryEntity("Shopping"));
                    dao.insert(new CategoryEntity("Health"));
                    Log.d("AppDatabase", "Default categories inserted!");
                }
            });
        }
    };
}
