package com.example.tbdd_cki.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.attendance.app.R;
import com.example.tbdd_cki.domain.model.AttendanceSession;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {
    private List<AttendanceSession> sessions;
    private OnSessionClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnSessionClickListener {
        void onSessionClick(AttendanceSession session);
    }

    public SessionAdapter(List<AttendanceSession> sessions, OnSessionClickListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceSession session = sessions.get(position);
        holder.bind(session, listener);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void updateData(List<AttendanceSession> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClassName, tvSessionTime, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvSessionTime = itemView.findViewById(R.id.tvSessionTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        void bind(AttendanceSession session, OnSessionClickListener listener) {
            tvClassName.setText(session.getClassName());
            tvSessionTime.setText(dateFormat.format(session.getSessionDate()) + " - " + session.getSessionTime());
            tvStatus.setText(session.getStatus().name());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSessionClick(session);
                }
            });
        }
    }
}
