// presentation/ui/CreateSessionActivity.java
package com.example.tbdd_cki.presentation.ui.teacher;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.attendance.app.R;
import com.example.tbdd_cki.data.repository.AttendanceRepositoryImpl;
import com.example.tbdd_cki.data.repository.UserRepositoryImpl;
import com.example.tbdd_cki.domain.repository.AttendanceRepository;
import com.example.tbdd_cki.domain.repository.UserRepository;
import com.example.tbdd_cki.domain.usecase.CreateSessionUseCase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class CreateSessionActivity extends AppCompatActivity {
    private Spinner spinnerClass;
    private EditText etSessionTime, etQRExpiration;
    private Button btnCreateSession;
    private TextView tvQRCode;
    private ImageView ivQRCode;
    private UserRepository userRepository;
    private AttendanceRepository attendanceRepository;
    private CreateSessionUseCase createSessionUseCase;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);

        userRepository = new UserRepositoryImpl(this);
        attendanceRepository = new AttendanceRepositoryImpl(this);
        createSessionUseCase = new CreateSessionUseCase(attendanceRepository);

        initViews();
        setupSpinner();
    }

    private void initViews() {
        spinnerClass = findViewById(R.id.spinnerClass);
        etSessionTime = findViewById(R.id.etSessionTime);
        etQRExpiration = findViewById(R.id.etQRExpiration);
        btnCreateSession = findViewById(R.id.btnCreateSession);
        tvQRCode = findViewById(R.id.tvQRCode);
        ivQRCode = findViewById(R.id.ivQRCode);

        btnCreateSession.setOnClickListener(v -> createSession());
    }

    private void setupSpinner() {
        // In a real app, load classes from database
        String[] classes = {"Class 1", "Class 2", "Class 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);
    }

    private void createSession() {
        String sessionTime = etSessionTime.getText().toString().trim();
        String qrExpirationStr = etQRExpiration.getText().toString().trim();

        if (sessionTime.isEmpty() || qrExpirationStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int qrExpiration = Integer.parseInt(qrExpirationStr);
        long sessionDate = System.currentTimeMillis();

        // For demo, using class ID as "1"
        String classId = "1";

        disposables.add(
                createSessionUseCase.execute(classId, sessionDate, sessionTime, qrExpiration)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result.isSuccess()) {
                                Toast.makeText(this, "Session created successfully!",
                                        Toast.LENGTH_SHORT).show();

                                // Generate and display QR code
                                String qrCode = result.getData().getQrCode();
                                displayQRCode(qrCode);
                            } else {
                                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, error -> {
                            Toast.makeText(this, "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void displayQRCode(String qrCode) {
        try {
            Bitmap bitmap = generateQRCode(qrCode);
            ivQRCode.setImageBitmap(bitmap);
            ivQRCode.setVisibility(ImageView.VISIBLE);
            tvQRCode.setText("Scan this QR code to check in");
        } catch (WriterException e) {
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap generateQRCode(String text) throws WriterException {
        int width = 500;
        int height = 500;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height
        );

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ?
                        0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}