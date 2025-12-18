package com.example.tbdd_cki.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tbdd_cki.R;
import com.example.tbdd_cki.domain.model.ClassModel;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<ClassModel> classes;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(ClassModel classModel);
    }

    public ClassAdapter(List<ClassModel> classes, OnClassClickListener listener) {
        this.classes = classes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassModel classModel = classes.get(position);
        holder.bind(classModel, listener);
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void updateClasses(List<ClassModel> newClasses) {
        this.classes = newClasses;
        notifyDataSetChanged();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView tvClassName;
        private TextView tvClassCode;
        private TextView tvStudentCount;
        private TextView tvStatus;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvClassCode = itemView.findViewById(R.id.tvClassCode);
            tvStudentCount = itemView.findViewById(R.id.tvStudentCount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(ClassModel classModel, OnClassClickListener listener) {
            tvClassName.setText(classModel.getName());
            tvClassCode.setText("Mã lớp: " + classModel.getCode());
            tvStudentCount.setText(classModel.getStudentCount() + " sinh viên");
            tvStatus.setText(classModel.isActive() ? "Đang hoạt động" : "Đã kết thúc");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClassClick(classModel);
                }
            });
        }
    }
}