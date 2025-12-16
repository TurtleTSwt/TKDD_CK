package com.example.tbdd_cki.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.attendance.app.R;
import com.example.tbdd_cki.domain.model.AttendanceRecord;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder> {
    private List<AttendanceRecord> records;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public AttendanceHistoryAdapter(List<AttendanceRecord> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attendance_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceRecord record = records.get(position);
        holder.bind(record);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateData(List<AttendanceRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionInfo, tvAttendanceTime, tvStatus, tvMethod;

        ViewHolder(View itemView) {
            super(itemView);
            tvSessionInfo = itemView.findViewById(R.id.tvSessionInfo);
            tvAttendanceTime = itemView.findViewById(R.id.tvAttendanceTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvMethod = itemView.findViewById(R.id.tvMethod);
        }

        void bind(AttendanceRecord record) {
            tvSessionInfo.setText("Session ID: " + record.getSessionId());
            tvAttendanceTime.setText(dateFormat.format(record.getAttendanceTime()));
            tvStatus.setText(record.getStatus().name());
            tvMethod.setText("Method: " + record.getMethod().name());

            // Color status
            int color = record.getStatus().name().equals("PRESENT") ?
                    android.graphics.Color.GREEN : android.graphics.Color.RED;
            tvStatus.setTextColor(color);
        }
    }
}