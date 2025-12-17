package com.example.tbdd_cki.presentation.ui.viewQR;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tbdd_cki.R;
import com.example.tbdd_cki.data.repository.AttendanceRepositoryImpl;
import com.example.tbdd_cki.domain.model.AttendanceSession;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewQRCodeActivity extends AppCompatActivity {
    private ImageView ivQRCode;
    private TextView tvSessionInfo, tvTimer, tvStatus, tvQRCodeText;
    private Button btnCloseSession, btnRefresh, btnBack;
    private AttendanceRepository attendanceRepository;
    private CompositeDisposable disposables = new CompositeDisposable();
    private String sessionId;
    private CountDownTimer countDownTimer;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr_code);

        sessionId = getIntent().getStringExtra("SESSION_ID");

        if (sessionId == null) {
            Toast.makeText(this, "Error: No session ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        attendanceRepository = new AttendanceRepositoryImpl(this);

        initViews();
        loadSession();
    }

    private void initViews() {
        ivQRCode = findViewById(R.id.ivQRCode);
        tvSessionInfo = findViewById(R.id.tvSessionInfo);
        tvTimer = findViewById(R.id.tvTimer);
        tvStatus = findViewById(R.id.tvStatus);
        tvQRCodeText = findViewById(R.id.tvQRCodeText);
        btnCloseSession = findViewById(R.id.btnCloseSession);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnBack = findViewById(R.id.btnBack);

        btnCloseSession.setOnClickListener(v -> closeSession());
        btnRefresh.setOnClickListener(v -> loadSession());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadSession() {
        tvStatus.setText("Loading session...");

        disposables.add(
                attendanceRepository.getSessionById(sessionId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                AttendanceSession session = result.getData();
                                displaySession(session);
                            } else {
                                Toast.makeText(this, "Error: " + result.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, error -> {
                            Toast.makeText(this, "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void displaySession(AttendanceSession session) {
        // Display session info
        String sessionInfo = String.format(
                "📚 Class: %s\n" +
                        "📅 Date: %s\n" +
                        "⏰ Time: %s\n" +
                        "📊 Status: %s",
                session.getClassName(),
                dateFormat.format(session.getSessionDate()),
                session.getSessionTime(),
                session.getStatus().name()
        );
        tvSessionInfo.setText(sessionInfo);

        // Generate and display QR Code
        if (session.getQrCode() != null && !session.getQrCode().isEmpty()) {
            try {
                Bitmap bitmap = generateQRCode(session.getQrCode());
                ivQRCode.setImageBitmap(bitmap);
                ivQRCode.setVisibility(ImageView.VISIBLE);
                tvQRCodeText.setText("QR Code: " + session.getQrCode());
            } catch (WriterException e) {
                Toast.makeText(this, "Error generating QR", Toast.LENGTH_SHORT).show();
                ivQRCode.setVisibility(ImageView.GONE);
            }
        } else {
            ivQRCode.setVisibility(ImageView.GONE);
            tvQRCodeText.setText("No QR Code available");
        }

        // Start countdown timer
        if (session.getQrExpiredAt() != null) {
            startTimer(session.getQrExpiredAt().getTime());
        }

        // Update attendance status
        updateStatus(session);
    }

    private void startTimer(long expiredTime) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long timeLeft = expiredTime - System.currentTimeMillis();

        if (timeLeft <= 0) {
            tvTimer.setText("⏱️ QR Code EXPIRED!");
            tvTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("⏱️ Time left: %02d:%02d", minutes, seconds));
                tvTimer.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("⏱️ QR Code EXPIRED!");
                tvTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                Toast.makeText(ViewQRCodeActivity.this,
                        "QR Code has expired. Please create a new session.",
                        Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void updateStatus(AttendanceSession session) {
        String status = String.format(
                "📊 Attendance Summary:\n\n" +
                        "✅ Present: %d\n" +
                        "❌ Absent: %d\n" +
                        "👥 Total Students: %d",
                session.getPresentCount(),
                session.getAbsentCount(),
                session.getTotalStudents()
        );
        tvStatus.setText(status);
    }

    private void closeSession() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Close Session")
                .setMessage("Are you sure you want to close this session? Students will no longer be able to check in.")
                .setPositiveButton("Yes, Close", (dialog, which) -> {
                    performCloseSession();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performCloseSession() {
        disposables.add(
                attendanceRepository.closeSession(sessionId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                Toast.makeText(this, "Session closed successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Error: " + result.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, error -> {
                            Toast.makeText(this, "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private Bitmap generateQRCode(String text) throws WriterException {
        int size = 512;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                size,
                size
        );

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        disposables.clear();
    }
}