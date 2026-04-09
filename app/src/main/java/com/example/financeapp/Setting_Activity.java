package com.example.financeapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.financeapp.viewmodel.TransactionViewModel;

public class Setting_Activity extends AppCompatActivity {
    private TransactionViewModel viewModel;
    Spinner spinnerCurrency ;
    LinearLayout btnReset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnReset = findViewById(R.id.btnResetData);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        String[] currencies = {"$", "₪", "JD"};
// 3. إنشاء الـ Adapter لتنسيق شكل القائمة
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, currencies);

// 4. تحديد شكل القائمة عند الفتح
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// 5. ربط الـ Adapter بالـ Spinner
        spinnerCurrency.setAdapter(adapter);

        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete all records?")
                    .setPositiveButton("Yes, Delete", (dialog, which) -> {
                        viewModel.deleteAllTransactions(); // تأكدي أن هذه الدالة موجودة في الـ ViewModel
                        Toast.makeText(this, "All data cleared", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 1. تعريف الـ Switch من الـ XML
        androidx.appcompat.widget.SwitchCompat switchDarkMode = findViewById(R.id.switchDarkMode);

// 2. فحص الحالة الحالية للنظام (عشان يظهر المفتاح مفعل إذا كان الوضع الليلي شغال أصلاً)
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            switchDarkMode.setChecked(true);
        }

// 3. برمجة الحدث عند تغيير حالة المفتاح
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // تفعيل الوضع الداكن
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
            } else {
                // العودة للوضع الفاتح
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        });



    }
}