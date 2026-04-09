package com.example.financeapp.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financeapp.AddTransactionActivity;
import com.example.financeapp.R;
import com.example.financeapp.entity.TransactionEntity;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<TransactionEntity> transactions;

    public TransactionAdapter(List<TransactionEntity> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged(); // هذا السطر هو الذي يخبر القائمة بأن البيانات تغيرت لتظهر فوراً
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    // تم حذف السطر الذي كان يسبب الانهيار (التعريف الذاتي)

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // تأكد أن هذا الملف activity_item_transaction يحتوي على TextViews بنفس الـ IDs المستخدمة أسفل
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_transaction2, parent, false);
        return new ViewHolder(view);
    }
    // أضيفي هذه الميثود داخل كلاس TransactionAdapter
    public List<TransactionEntity> getAllTransactions() {
        return this.transactions;
    }
    public TransactionEntity getTransactionAt(int position) {
        return transactions.get(position);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionEntity transaction = transactions.get(position);

        // عرض الملاحظة والنوع
        holder.textType.setText(transaction.getType());
        holder.textNote.setText(transaction.getNote());

        // تنسيق وتلوين المبلغ
        if ("INCOME".equals(transaction.getType())) {
            holder.textAmount.setTextColor(Color.parseColor("#4CAF50")); // أخضر
            holder.textAmount.setText("+ $" + transaction.getAmount());
        } else {
            holder.textAmount.setTextColor(Color.parseColor("#F44336")); // أحمر
            holder.textAmount.setText("- $" + transaction.getAmount());
        }
//للضغط على الكارد للانتقال للتعديل
        holder.itemView.setOnClickListener(v -> {
            // 1. إنشاء الـ Intent لفتح شاشة الإضافة
            Intent intent = new Intent(v.getContext(), AddTransactionActivity.class);

            // 2. إرسال البيانات (تأكدي أن الـ "Key" يطابق ما هو موجود في AddTransactionActivity)
            intent.putExtra("id", transaction.getId()); // الـ ID هو الأهم للتعديل
            intent.putExtra("amount", transaction.getAmount());
            intent.putExtra("note", transaction.getNote());
            intent.putExtra("type", transaction.getType());
            intent.putExtra("catId", transaction.getCategoryId());
            intent.putExtra("date", transaction.getDate().getTime()); // إرسال التاريخ كـ Long

            // 3. تشغيل الشاشة
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textAmount;
        TextView textType;
        TextView textNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textAmount = itemView.findViewById(R.id.textAmount);
            textType = itemView.findViewById(R.id.textType);
            textNote = itemView.findViewById(R.id.textNote);
        }
    }
}