package com.example.financeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.financeapp.entity.TransactionEntity;
import com.example.financeapp.viewmodel.TransactionViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView textMostSpent, textTotalReport;
    private TransactionViewModel viewModel;
    private com.github.mikephil.charting.charts.BarChart barChart;
    private String currentType = "Expense"; // الافتراضي حسب المطلوب Overview/Expense
    private com.github.mikephil.charting.charts.LineChart lineChart;
    private boolean isLineChartVisible = false; // متغير لمتابعة الحالة

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        // 1. الربط أولاً
        pieChart = findViewById(R.id.pieChartReports);
        textMostSpent = findViewById(R.id.textMostSpentCategory);
        textTotalReport = findViewById(R.id.textTotalReport);
        Spinner spinnerMonth = findViewById(R.id.spinnerMonth);
        Button btnExpense = findViewById(R.id.btnShowExpense);
        Button btnIncome = findViewById(R.id.btnShowIncome);
        barChart = findViewById(R.id.barChartReports);
        lineChart = findViewById(R.id.lineChartReports);
        Button btnSwitch = findViewById(R.id.btnSwitchChart);

        btnSwitch.setOnClickListener(v -> {
            if (!isLineChartVisible) {
                barChart.setVisibility(View.GONE);
                lineChart.setVisibility(View.VISIBLE);
                btnSwitch.setText("Switch to Bar Chart");
            } else {
                barChart.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.GONE);
                btnSwitch.setText("Switch to Line Chart");
            }
            isLineChartVisible = !isLineChartVisible;
        });


        // 2. تهيئة الـ ViewModel (قبل السبنر!)
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        btnExpense.setOnClickListener(v -> {
            currentType = "Expense";
            // إعادة تحميل البيانات بناءً على الشهر المختار حالياً والنوع الجديد
            loadMonthlyData(spinnerMonth.getSelectedItem().toString());
        });

        btnIncome.setOnClickListener(v -> {
            currentType = "Income";
            loadMonthlyData(spinnerMonth.getSelectedItem().toString());
        });


        // 3. إعداد سبنر الأشهر
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        spinnerMonth.setAdapter(adapter);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadMonthlyData(months[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // جلب بيانات الشهر الحالي كبداية
        loadMonthlyData("01");
    }

    private void loadMonthlyData(String month) {
        viewModel.getTransactionsByMonth(month).observe(this, transactions -> {
            if (transactions != null) {
                processAndDisplayData(transactions);
            }
        });
    }

    private void processAndDisplayData(List<TransactionEntity> transactions) {
        Map<Integer, Float> categorySumMap = new HashMap<>();
        float total = 0;
        float maxAmount = 0;
        String topCategory = "None";

        List<PieEntry> entries = new ArrayList<>();

        for (TransactionEntity t : transactions) {
            // فلترة حسب النوع (نبدأ بالـ Expense كما في الصورة)
            if (t.getType() != null && t.getType().equalsIgnoreCase(currentType)) {
                int catId = t.getCategoryId();
                float amount = (float) t.getAmount();

                categorySumMap.put(catId, categorySumMap.getOrDefault(catId, 0f) + amount);
                total += amount;
            }
        }

        for (Map.Entry<Integer, Float> entry : categorySumMap.entrySet()) {
            String name = getCategoryNameById(entry.getKey());
            entries.add(new PieEntry(entry.getValue(), name));

            if (entry.getValue() > maxAmount) {
                maxAmount = entry.getValue();
                topCategory = name;
            }
        }

        // تحديث النصوص
        textTotalReport.setText("Total " + currentType + ": $" + String.format("%.2f", total));
        textMostSpent.setText("Top " + currentType + ": " + topCategory);

        // تحديث الرسمة
        updateChartUI(entries);
    }

    private void updateChartUI(List<PieEntry> entries) {
        // --- أولاً: تحديث الدائرة (Pie Chart) ---
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(14f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText(currentType);
        pieChart.invalidate();

        // --- ثانياً: تحديث الأعمدة (Bar Chart) ---
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            // تحويل بيانات الدائرة إلى أعمدة
            barEntries.add(new BarEntry(i, entries.get(i).getValue()));
            labels.add(entries.get(i).getLabel());
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Amount by Category");
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS); // ألوان مختلفة للأعمدة
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // تنسيق شكل المحاور (X and Y)
        // حماية من الكراش: وضعنا شرط if (index >= 0 && index < labels.size()) لضمان أنه إذا حاول التطبيق رسم عمود ليس له اسم، لا يتوقف التطبيق (No Crash).
        barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // نتأكد أن الرقم (index) موجود داخل نطاق القائمة عشان ما يصير كراش
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                } else {
                    return "";
                }
            }
        });
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);

        barChart.animateY(1000);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setLabelCount(labels.size()); // يظهر كل الأسماء
        barChart.getXAxis().setGranularity(1f); // يمنع تكرار الاسم تحت أكثر من عمود
        barChart.getAxisRight().setEnabled(false); // إخفاء الأرقام على الجهة اليمنى لشكل أرتب
        barChart.invalidate();
        // --- ثالثاً: تحديث الخط البياني (Line Chart) ---
        List<Entry> lineEntries = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            lineEntries.add(new Entry(i, entries.get(i).getValue()));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Trends");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.RED);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setDrawFilled(true); // تلوين ما تحت الخط ليعطي شكلاً جميلاً
        lineDataSet.setFillAlpha(50);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

// تنسيق المحور الأفقي بنفس أسماء الفئات
        lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < labels.size()) ? labels.get(index) : "";
            }
        });
        lineChart.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        lineChart.animateX(1000);
        // الحصول على المحور السيني (X)
        com.github.mikephil.charting.components.XAxis xAxis = lineChart.getXAxis();

// 1. إجبار الرسمة على إظهار جميع الأسماء بعدد الفئات الموجودة
        xAxis.setLabelCount(labels.size(), true);

// 2. منع التكرار (عشان ما يكرر Food مرتين لو كانت المسافة واسعة)
        xAxis.setGranularity(1f);

// 3. التأكد من أن الأسماء تبدأ من الصفر تماماً مع أول نقطة
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(labels.size() - 1f);

// 4. (اختياري) تدوير الأسماء لو كانت طويلة عشان ما تتداخل
// xAxis.setLabelRotationAngle(-45);
        lineChart.invalidate();
    }

    private String getCategoryNameById(int id) {
        switch (id) {
            case 1: return "Food";
            case 2: return "Salary";
            case 3: return "Transport";
            case 4: return "Shopping";
            case 5: return "Health";
            default: return "Other";
        }
    }
}