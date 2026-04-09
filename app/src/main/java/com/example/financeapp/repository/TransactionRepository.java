package com.example.financeapp.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Query;

import com.example.financeapp.AppDatabase;
import com.example.financeapp.daos.CategoryDao;
import com.example.financeapp.daos.TransactionDao;
import com.example.financeapp.entity.CategoryEntity;
import com.example.financeapp.entity.TransactionEntity;

import java.util.Date;
import java.util.List;

public class TransactionRepository {
    private TransactionDao transactionDao;
    private CategoryDao categoryDao;
    private LiveData<List<TransactionEntity>> allTransactions;

    public TransactionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        transactionDao = db.transactionDao();

        allTransactions = transactionDao.getAllTransactions();
    }

    // Insert
    public void insert(TransactionEntity transaction) {
        new Thread(() -> transactionDao.insert(transaction)).start();
    }

    // Update
    public void update(TransactionEntity transaction) {
        new Thread(() -> transactionDao.update(transaction)).start();
    }
    public void insertCategory(CategoryEntity category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.insert(category);
        });
    }

    // Delete
    public void delete(TransactionEntity transaction) {
        new Thread(() -> transactionDao.delete(transaction)).start();
    }
    // Get All Transactions
    public LiveData<List<TransactionEntity>> getAllTransactions() {

        return allTransactions;
    }




    // Get Transactions Between Dates
    public List<TransactionEntity> getTransactionsBetween(Date startDate, Date endDate) {
        return transactionDao.getTransactionsBetween(startDate, endDate);
    }

    public LiveData<Double> getTotalIncome() {
        return transactionDao.getTotalIncome();
    }

    public LiveData<Double> getTotalExpense() {
        return transactionDao.getTotalExpense();
    }
    // إضافة دالة حذف الكل في الريبوزيتوري
    public void deleteAllTransactions() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            transactionDao.deleteAllransaction();
        });
    }
    // مثال لما يجب أن يكون داخل الـ Repository
    // جلب العمليات لفترة معينة
    public LiveData<List<TransactionEntity>> getTransactionsByPeriod(long start, long end) {
        return transactionDao.getTransactionsByPeriod(start, end);
    }

    // جلب إجمالي الدخل لفترة معينة
    public LiveData<Double> getTotalIncomeByPeriod(long start, long end) {
        return transactionDao.getTotalIncomeByPeriod(start, end);
    }

    // جلب إجمالي المصروف لفترة معينة
    public LiveData<Double> getTotalExpenseByPeriod(long start, long end) {
        return transactionDao.getTotalExpenseByPeriod(start, end);
    }

    // جلب آخر 5 عمليات مثلاً للداشبورد
    public LiveData<List<TransactionEntity>> getRecentTransactions() {
        return transactionDao.getRecentTransactions();
    }

   public LiveData<List<TransactionEntity>> getTransactionsByMonth(String month){
      return transactionDao.getTransactionsByMonth(month)  ;
   }

}
