package com.example.financeapp.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.financeapp.entity.TransactionEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {

    // Insert
    @Insert
    void insert(TransactionEntity transaction);


    // Update
    @Update
    void update(TransactionEntity transaction);

    // Delete
    @Delete
    void delete(TransactionEntity transaction);

    // Get All Transactions
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<TransactionEntity>> getAllTransactions();



    // Filter Between Two Dates

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<TransactionEntity> getTransactionsBetween(Date startDate, Date endDate);


    // Filter By Type (INCOME / EXPENSE)

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    List<TransactionEntity> getTransactionsByType(String type);



    // Filter By Category

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY date DESC")
    List<TransactionEntity> getTransactionsByCategory(int categoryId);


    // Sum Between Dates (Dashboard Monthly)

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate")
    Double getTotalIncomeBetween(Date startDate, Date endDate);


    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    Double getTotalExpenseBetween(Date startDate, Date endDate);

    // Sum of Income
    @Query("SELECT SUM(amount) FROM transactions WHERE type='INCOME'")
    LiveData<Double> getTotalIncome();

    // Sum of Expense
    @Query("SELECT SUM(amount) FROM transactions WHERE type='EXPENSE'")
    LiveData<Double> getTotalExpense();

    // Delete All (Settings Reset)

    @Query("DELETE FROM transactions")
    void deleteAllransaction();
    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    LiveData<List<TransactionEntity>> getTransactionsByPeriod(long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Income' AND date >= :startDate AND date <= :endDate")
    LiveData<Double> getTotalIncomeByPeriod(long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expense' AND date >= :startDate AND date <= :endDate")
    LiveData<Double> getTotalExpenseByPeriod(long startDate, long endDate);
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 5")
    LiveData<List<TransactionEntity>> getRecentTransactions();
//للبحث والفلترة
    @Query("SELECT * FROM transactions WHERE note LIKE :query OR type = :query")
    LiveData<List<TransactionEntity>> searchTransactions(String query);

    @Query("SELECT * FROM transactions WHERE categoryId = :catId")
    LiveData<List<TransactionEntity>> filterByCategory(int catId);

//المسؤولة عن فلترة البيانات حسب التاريخ
    //الكود بيحول التاريخ المخزن لـ "رقم الشهر" وبيقارنه بالشهر اللي بنختاره
    @Query("SELECT * FROM transactions WHERE strftime('%m', date / 1000, 'unixepoch') = :month")
    LiveData<List<TransactionEntity>> getTransactionsByMonth(String month);
}
