package com.example.financeapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.adapter.TransactionAdapter;
import com.example.financeapp.entity.CategoryEntity;
import com.example.financeapp.entity.TransactionEntity;
import com.example.financeapp.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private EditText editSearch;
    private Spinner spinnerFilter;
    private TextView tvNoTransactions;
    private List<TransactionEntity> allTransactions = new ArrayList<>();
    private List<CategoryEntity> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        // 1. ربط العناصر
        recyclerView = findViewById(R.id.recyclerTransactions);
        editSearch = findViewById(R.id.editSearch);
        spinnerFilter = findViewById(R.id.spinnerFilterCategory);
        ImageButton btnSort = findViewById(R.id.btnSortList); // هذا هو الزر الوحيد للترتيب
        tvNoTransactions=findViewById(R.id.tvNoTransactions);

        com.google.android.material.chip.ChipGroup chipGroup = findViewById(R.id.chipGroupDate);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // checkedIds عبارة عن قائمة (List) بالـ IDs التي تم اختيارها
            if (checkedIds.contains(R.id.chipToday)) {
                filterByDate("today");
            } else if (checkedIds.contains(R.id.chipThisMonth)) {
                filterByDate("month");
            } else {
                // في حال اختيار All أو عدم اختيار شيء
                adapter.setTransactions(allTransactions);
            }
        });

        // 2. برمجة زر الترتيب (Popup Menu)
        if (btnSort != null) {
            btnSort.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(TransactionListActivity.this, v);
                popup.getMenu().add("Sort by Amount");
                popup.getMenu().add("Sort by Date");

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Sort by Amount")) {
                        sortList("Amount");
                    } else {
                        sortList("Date");
                    }
                    return true;
                });
                popup.show();
            });
        }

        // 3. تهيئة الـ Adapter والـ RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 4. تهيئة الـ ViewModel ومراقبة البيانات
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        viewModel.getallTransactions().observe(this, transactions -> {
            if (transactions != null) {
                this.allTransactions = transactions;
                adapter.setTransactions(transactions);
            }
        });
        viewModel.getallTransactions().observe(this, transactions -> {
            if (transactions == null || transactions.isEmpty()) {
                // لو القائمة فارغة: أظهري الرسالة وأخفي الريسايكلرفيو
                tvNoTransactions.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                // لو فيها بيانات: أخفي الرسالة وأظهري الريسايكلرفيو
                tvNoTransactions.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                allTransactions = transactions;
                adapter.setTransactions(transactions);
            }
        });

        // 5. السبنر والبحث (نفس كودك السابق شغال تمام)
        setupCategorySpinner();
        setupSearch();

        // 6. الحذف بالسحب
        setupSwipeToDelete();
    }
   // هذه الميثودات هي اللي بتعطي للأزرار (Chips) عشان تعرف تميز إذا العملية صارت اليوم أو خلال الشهر الحالي
   private boolean isToday(Date date) {
       if (date == null) return false;
       Calendar cal1 = Calendar.getInstance(); // اليوم
       Calendar cal2 = Calendar.getInstance();
       cal2.setTime(date); // تاريخ العملية

       return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
   }

    private boolean isThisMonth(Date date) {
        if (date == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }
//وهذه الميثود التي تستخدم الميثودات السابقة لفلترة القائمة:
    private void filterByDate(String period) {
        List<TransactionEntity> filtered = new ArrayList<>();

        for (TransactionEntity t : allTransactions) {
            if (period.equals("today") && isToday(t.getDate())) {
                filtered.add(t);
            } else if (period.equals("month") && isThisMonth(t.getDate())) {
                filtered.add(t);
            }
        }
        adapter.setTransactions(filtered);
    }

    private void sortList(String criteria) {
        // 1. نجلب القائمة الحالية من الأدابتر
        List<TransactionEntity> currentList = adapter.getAllTransactions();
        if (currentList == null || currentList.isEmpty()) return;

        // 2. ننشئ نسخة جديدة لنرتبها (عشان ما نعدل على الأصلية مباشرة)
        ArrayList<TransactionEntity> sortedList = new ArrayList<>(currentList);

        // 3. نقوم بالترتيب حسب النوع
        if (criteria.equals("Amount")) {
            Collections.sort(sortedList, (t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount()));
        } else if (criteria.equals("Date")) {
            Collections.sort(sortedList, (t1, t2) -> {
                if (t1.getDate() == null || t2.getDate() == null) return 0;
                return t2.getDate().compareTo(t1.getDate());
            });
        }

        // 4. هنا المكان الصحيح للإضافة (تحديث الأدابتر)
        adapter.setTransactions(sortedList);


        // 5. اختياري: العودة بأول عنصر في القائمة للأعلى
        recyclerView.scrollToPosition(0);
    }

    // --- ميثودات مساعدة لتنظيم الكود ---

    private void setupCategorySpinner() {
        viewModel.getallcategories().observe(this, categories -> {
            if (categories != null) {
                this.categoryList = categories;
                List<String> categoryNames = new ArrayList<>();
                categoryNames.add("All Categories");
                for (CategoryEntity cat : categories) {
                    categoryNames.add(cat.getName());
                }
                ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFilter.setAdapter(catAdapter);
            }
        });

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) adapter.setTransactions(allTransactions);
                else filterByCategory(categoryList.get(position - 1).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTransactions(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(RecyclerView r, RecyclerView.ViewHolder v, RecyclerView.ViewHolder t) { return false; }
            @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TransactionEntity transactionToDelete = adapter.getTransactionAt(position);
                viewModel.delete(transactionToDelete);
                Toast.makeText(TransactionListActivity.this, "Transaction Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void filterTransactions(String query) {
        List<TransactionEntity> filteredList = new ArrayList<>();
        for (TransactionEntity item : allTransactions) {
            if (item.getNote().toLowerCase().contains(query.toLowerCase()) ||
                    item.getType().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.setTransactions(filteredList);
    }

    private void filterByCategory(int catId) {
        List<TransactionEntity> filteredList = new ArrayList<>();
        for (TransactionEntity item : allTransactions) {
            if (item.getCategoryId() == catId) {
                filteredList.add(item);
            }
        }
        adapter.setTransactions(filteredList);
    }
}