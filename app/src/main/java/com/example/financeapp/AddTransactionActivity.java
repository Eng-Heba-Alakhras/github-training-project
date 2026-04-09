package com.example.financeapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.financeapp.entity.CategoryEntity;
import com.example.financeapp.entity.TransactionEntity;
import com.example.financeapp.viewmodel.TransactionViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText editAmount, editNote, editDate;
    private Spinner spinnerType, spinnerCategory;
    private Button btnSave;
    private TransactionViewModel viewModel;
    private List<CategoryEntity> categoryList = new ArrayList<>();
    private Date transactionDate = new Date(); // هذا الذي سيتم حفظه في الداتابيز
    private int editId = -1; // القيمة -1 تعني أننا في وضع "إضافة جديدة"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // 1. ربط العناصر
        editAmount = findViewById(R.id.editAmount);
        editNote = findViewById(R.id.editNote);
        editDate = findViewById(R.id.editDate);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);

        // 2. إعداد التاريخ
        setupDatePicker();

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // 3. إعداد أنواع العمليات
        String[] types = {"INCOME", "EXPENSE"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        spinnerType.setAdapter(typeAdapter);

        // 4. مراقبة الفئات وتحديث السبنر
        viewModel.getallcategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                this.categoryList = categories; // تخزين القائمة الأصلية
                List<String> names = new ArrayList<>();
                for (CategoryEntity cat : categories) {
                    names.add(cat.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            }
        });

        //للتاكد اننا نستقبل التاريخ القديم ونخزنه في نفس المتغير
        long dateMillis = getIntent().getLongExtra("date", -1);
        if (dateMillis != -1) {
            transactionDate = new Date(dateMillis);
        }

        // إضافة خاصية التعديل - شيكي إذا فيه بيانات مبعوتة من الأدابتر
        editId = getIntent().getIntExtra("id", -1);

        if (editId != -1) {
            // يعني إحنا في وضع "تعديل"
            btnSave.setText("Update");

            // تعبئة الحقول بالبيانات اللي وصلت
            editAmount.setText(String.valueOf(getIntent().getDoubleExtra("amount", 0)));
            editNote.setText(getIntent().getStringExtra("note"));
            // 3. تحديد نوع العملية (دخل أو صرف) تلقائياً في السبنر
            String type = getIntent().getStringExtra("type");
            if (type != null) {
                // بيبحث في السبنر عن كلمة INCOME أو EXPENSE ويختارها
                int typePos = ((ArrayAdapter)spinnerType.getAdapter()).getPosition(type.toUpperCase());
                spinnerType.setSelection(typePos);
        }}

        // برمجة زر الحفظ/التعديل
        btnSave.setOnClickListener(v -> {
            // 1. تجهيز البيانات
            double amount = 0;
            try {
                amount = Double.parseDouble(editAmount.getText().toString());
            } catch (Exception e) {
                amount = 0;
            }

            String note = editNote.getText().toString();
            String type = spinnerType.getSelectedItem().toString();

            // جلب الـ ID الحقيقي للفئة المختارة من القائمة
            int catId = 1;
            if (categoryList != null && !categoryList.isEmpty()) {
                catId = categoryList.get(spinnerCategory.getSelectedItemPosition()).getId();
            }

            java.util.Date date = new java.util.Date(); // التاريخ الحالي

            // 2. إنشاء الكائن (حسب الترتيب اللي في الـ Entity)
            TransactionEntity transaction = new TransactionEntity(amount, type, catId, date, note);
            // 3. التنفيذ (إضافة أو تعديل)
            if (editId == -1) {
                // إذا جديد
                viewModel.insert(transaction);
            } else {
                // إذا تعديل (نعطيه الـ ID القديم)
                transaction.setId(editId);
                viewModel.update(transaction);
            }
            finish(); // إغلاق الشاشة والعودة
        }); // نهاية btnSave.setOnClickListener
    } // نهاية onCreate

    private void setupDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // إذا كانت عملية تعديل، النص سيظهر التاريخ القديم، وإذا إضافة سيظهر اليوم
        editDate.setText(sdf.format(transactionDate));

        editDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.setTime(transactionDate); // اجعل التقويم يفتح على التاريخ المختار حالياً

            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_YEAR, dayOfMonth); // تأكيد اليوم من السنة

                // تحديث المتغير الأساسي
                transactionDate = c.getTime();

                // تحديث النص للعرض فقط
                editDate.setText(sdf.format(transactionDate));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    // ميثود إضافية (اختيارية)  للحفظ
    private void saveTransaction() {
        String amountStr = editAmount.getText().toString().trim();
        if (amountStr.isEmpty()) return;

        double amount = Double.parseDouble(amountStr);
        String type = spinnerType.getSelectedItem().toString().trim();
        String note = editNote.getText().toString().trim();

        if (categoryList != null && !categoryList.isEmpty()) {
            int categoryId = categoryList.get(spinnerCategory.getSelectedItemPosition()).getId();

            // التعديل هنا: نستخدم transactionDate بدلاً من new Date()
            TransactionEntity transaction = new TransactionEntity(amount, type, categoryId, transactionDate, note);

            if (editId != -1) {
                transaction.setId(editId);
                viewModel.update(transaction);
            } else {
                viewModel.insert(transaction);
            }
            finish();
        }
    }
} // نهاية الكلاس