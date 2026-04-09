package com.example.financeapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.adapter.TransactionAdapter;
import com.example.financeapp.viewmodel.TransactionViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    // 1. تعريف العناصر (UI)
    private TextView textIncome, textExpense, textBalance;
    private ImageButton btnSettings;
     private  Button btnViewReports;
    private androidx.cardview.widget.CardView cardAddTransaction, cardShowHistory;
    private Spinner spinnerPeriod;
    private BarChart miniBarChart;
    private RecyclerView recyclerRecent;

    // 2. تعريف المتغيرات البرمجية
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;//
    private double lastIncome = 0.0;
    private double lastExpense = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 3. ربط العناصر (findViewById) - خطوة حاسمة لمنع الكراش
        initViews();

        // 4. تهيئة الـ ViewModel والـ Adapter
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        setupRecyclerView();
        setupSpinner();

        // 5. مراقبة البيانات (Observers)
        observeData();

        // 6. إعداد المستمعات (Click Listeners)
        setupClickListeners();

        // جلب بيانات الشهر الحالي مبدئياً
        fetchMonthlyData();
    }

    private void initViews() {
        textIncome = findViewById(R.id.textIncome);
        textExpense = findViewById(R.id.textExpense);
        textBalance = findViewById(R.id.textBalance);
        btnSettings = findViewById(R.id.btnOpenSettings);
        btnViewReports=findViewById(R.id.btnViewReports);
        cardAddTransaction = findViewById(R.id.cardAddTransaction);
        cardShowHistory = findViewById(R.id.cardShowHistory);
        spinnerPeriod = findViewById(R.id.spinnerPeriod);
        miniBarChart = findViewById(R.id.miniBarChart);
        recyclerRecent = findViewById(R.id.recyclerRecent);
    }

    private void setupRecyclerView() {
        recyclerRecent.setLayoutManager(new LinearLayoutManager(this));
        // نمرر قائمة فارغة في البداية لحل مشكلة الكونستركتور
        adapter = new TransactionAdapter(new ArrayList<>());
        recyclerRecent.setAdapter(adapter);
    }

    private void observeData() {
        // مراقبة الدخل
        viewModel.getTotalIncome().observe(this, income -> {
            lastIncome = (income != null) ? income : 0.0;
            updateUI();
        });

        // مراقبة المصروف
        viewModel.getTotalExpense().observe(this, expense -> {
            lastExpense = (expense != null) ? expense : 0.0;
            updateUI();
        });

        // مراقبة القائمة (آخر العمليات)
        viewModel.getRecentTransactions().observe(this, list -> {
            if (list != null && adapter != null) {
                adapter.setTransactions(list); // تأكدي من وجود ميثود setTransactions في الأدابتر
            }
        });
    }

    private void updateUI() {
        // حماية ضد الـ Null إذا لم تكن النصوص جاهزة
        if (textIncome == null || textExpense == null || textBalance == null) return;

        double balance = lastIncome - lastExpense;

        textIncome.setText("Income: $" + String.format("%.2f", lastIncome));
        textExpense.setText("Expense: $" + String.format("%.2f", lastExpense));
        textBalance.setText("Balance: $" + String.format("%.2f", balance));

        // تلوين النص حسب الحالة
        if (balance >= 0) {
            textBalance.setTextColor(Color.parseColor("#00B894")); // أخضر
        } else {
            textBalance.setTextColor(Color.RED); // أحمر
        }

        updateBarChart((float) lastIncome, (float) lastExpense);
    }

    private void updateBarChart(float income, float expense) {
        if (miniBarChart == null) return;

        try {
            ArrayList<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, income));
            entries.add(new BarEntry(1f, expense));

            BarDataSet dataSet = new BarDataSet(entries, "Summary");
            dataSet.setColors(new int[]{Color.GREEN, Color.RED});
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(10f);

            BarData data = new BarData(dataSet);
            miniBarChart.setData(data);
            miniBarChart.getDescription().setEnabled(false);
            miniBarChart.getLegend().setEnabled(false);
            miniBarChart.animateY(800);
            miniBarChart.invalidate();
        } catch (Exception e) {
            Log.e("DASHBOARD_CHART", "Error: " + e.getMessage());
        }
    }

    private void setupSpinner() {
        String[] periods = {"This Month", "This Year", "All Time"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periods);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (spinnerPeriod != null) {
            spinnerPeriod.setAdapter(spinnerAdapter);

            // --- إضافة ميثود التشغيل (Listener) من هنا ---
            spinnerPeriod.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                    String selected = periods[position];

                    if (selected.equals("This Month")) {
                        fetchMonthlyData(); // جلب بيانات الشهر الحالي
                    } else if (selected.equals("This Year")) {
                        fetchYearlyData();  // جلب بيانات السنة (تحتاجين لإضافة الميثود)
                    } else if (selected.equals("All Time")) {
                        fetchAllTimeData(); // جلب كل البيانات
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    // لا نفعل شيئاً هنا
                }
            });
        }
    }

    private void setupClickListeners() {
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> startActivity(new Intent(this, Setting_Activity.class)));
        }

        if (cardAddTransaction != null) {
            cardAddTransaction.setOnClickListener(v -> startActivity(new Intent(this, AddTransactionActivity.class)));
        }

        if (cardShowHistory != null) {
            cardShowHistory.setOnClickListener(v -> startActivity(new Intent(this, TransactionListActivity.class)));
        }

        btnViewReports.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportsActivity.class);
            startActivity(intent);
        });
    }

    private void fetchMonthlyData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        long start = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        long end = calendar.getTimeInMillis();

        viewModel.getTransactionsByPeriod(start, end).observe(this, list -> {
            // يمكن تحديث القائمة هنا إذا أردتِ تصفية حسب الشهر
        });
    }
    // 1. جلب بيانات السنة الحالية
    private void fetchYearlyData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1); // أول يوم في السنة
        long start = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); // آخر يوم
        long end = calendar.getTimeInMillis();

        updateObserversByPeriod(start, end);
    }

    // 2. جلب كل البيانات (بدون فلترة زمنية)
    private void fetchAllTimeData() {
        // نعود لمراقبة الدوال الأساسية التي تعطي المجموع الكلي
        viewModel.getTotalIncome().observe(this, income -> {
            lastIncome = (income != null) ? income : 0.0;
            updateUI();
        });
        viewModel.getTotalExpense().observe(this, expense -> {
            lastExpense = (expense != null) ? expense : 0.0;
            updateUI();
        });
    }

    // ميثود مساعدة لتحديث الأرقام بناءً على الفترة المختارة
    private void updateObserversByPeriod(long start, long end) {
        viewModel.getTotalIncomeByPeriod(start, end).observe(this, income -> {
            lastIncome = (income != null) ? income : 0.0;
            updateUI();
        });
        viewModel.getTotalExpenseByPeriod(start, end).observe(this, expense -> {
            lastExpense = (expense != null) ? expense : 0.0;
            updateUI();
        });
    }
}