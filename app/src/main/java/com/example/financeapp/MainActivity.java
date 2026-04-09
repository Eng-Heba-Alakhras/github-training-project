package com.example.financeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button btnAddTransaction, btnViewTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ربط الأزرار
        btnAddTransaction = findViewById(R.id.btnAddTransaction);
        btnViewTransactions = findViewById(R.id.btnViewTransactions);
        // داخل onCreate
        Button btnViewDashboard = findViewById(R.id.btnViewDashboard);

        btnViewDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        // التنقل لشاشة إضافة معاملة
        btnAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            startActivity(intent);
        });

        // التنقل لشاشة عرض القائمة
        btnViewTransactions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionListActivity.class);
            startActivity(intent);
        });

        // التعامل مع الحواف (Window Insets) لضمان عدم تداخل التصميم مع شريط الحالة
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ملاحظة: قمنا بحذف كود viewModel.insert من هنا تماماً
    }
}