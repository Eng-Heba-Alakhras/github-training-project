package com.example.financeapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.financeapp.entity.CategoryEntity;
import com.example.financeapp.entity.TransactionEntity;
import com.example.financeapp.repository.CategoryRepository;
import com.example.financeapp.repository.TransactionRepository;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private TransactionRepository repository;
    private CategoryRepository categoryRepository;
    private LiveData<List<TransactionEntity>> allTransactions;
    private LiveData<List<CategoryEntity>> allCategories;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        // تهيئة الريبوزيتوري
        repository = new TransactionRepository(application);
        categoryRepository = new CategoryRepository(application);

        // جلب البيانات الأساسية من الريبوزيتوري
        allTransactions = repository.getAllTransactions();
        allCategories = categoryRepository.getAllCategories();
    }

    public LiveData<List<CategoryEntity>> getallcategories() {
        return allCategories;
    }

    public LiveData<List<TransactionEntity>> getallTransactions() {
        return allTransactions;
    }

    public LiveData<Double> getTotalIncome() {
        return repository.getTotalIncome();
    }

    public LiveData<Double> getTotalExpense() {
        return repository.getTotalExpense();
    }

    // --- التصحيح هنا: يجب أن تنادي الـ repository وليس الـ transactionDao مباشرة ---

    public LiveData<List<TransactionEntity>> getTransactionsByPeriod(long start, long end) {
        return repository.getTransactionsByPeriod(start, end);
    }

    public LiveData<Double> getTotalIncomeByPeriod(long start, long end) {
        return repository.getTotalIncomeByPeriod(start, end);
    }

    public LiveData<Double> getTotalExpenseByPeriod(long start, long end) {
        return repository.getTotalExpenseByPeriod(start, end);
    }

    public LiveData<List<TransactionEntity>> getRecentTransactions() {
        return repository.getRecentTransactions();
    }

    // باقي الدوال (Insert, Update, Delete)
    public void insert(TransactionEntity transaction) { repository.insert(transaction); }
    public void update(TransactionEntity transaction) { repository.update(transaction); }
    public void delete(TransactionEntity transaction) { repository.delete(transaction); }
    public void insertCategory(CategoryEntity category) { categoryRepository.insert(category); }
    public void deleteAllTransactions() { repository.deleteAllTransactions(); }
    public LiveData<List<TransactionEntity>> getTransactionsByMonth(String month){
        return repository.getTransactionsByMonth(month)  ;
    }
}
