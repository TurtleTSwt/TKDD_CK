package com.example.tbdd_cki.presentation.ui.gps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.tbdd_cki.R;
import com.google.android.gms.location.*;

public class GPSTestActivity extends AppCompatActivity {
    private TextView tvCurrentLocation, tvStatus;
    private Button btnGetLocation, btnCopyLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat, currentLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        tvStatus = findViewById(R.id.tvStatus);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnCopyLocation = findViewById(R.id.btnCopyLocation);

        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
        btnCopyLocation.setOnClickListener(v -> copyToClipboard());

        // Auto get location on start
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        tvStatus.setText("🔍 Getting your location...");
        btnGetLocation.setEnabled(false);

        // Create location request for high accuracy
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdates(1)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        btnGetLocation.setEnabled(true);

                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            Location location = locationResult.getLastLocation();
                            currentLat = location.getLatitude();
                            currentLon = location.getLongitude();

                            String locationText = String.format(
                                    "📍 Your Current Location:\n\n" +
                                            "Latitude: %.6f\n" +
                                            "Longitude: %.6f\n\n" +
                                            "Accuracy: %.2f meters",
                                    currentLat, currentLon, location.getAccuracy()
                            );

                            tvCurrentLocation.setText(locationText);
                            tvStatus.setText("✅ Location obtained successfully!");
                            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                            Toast.makeText(GPSTestActivity.this,
                                    "Location updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            tvStatus.setText("❌ Failed to get location");
                            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            Toast.makeText(GPSTestActivity.this,
                                    "Please enable GPS and try again", Toast.LENGTH_LONG).show();
                        }
                    }
                }, null);
    }

    private void copyToClipboard() {
        if (currentLat == 0 && currentLon == 0) {
            Toast.makeText(this, "Get location first!", Toast.LENGTH_SHORT).show();
            return;
        }

        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(
                "Location",
                currentLat + "," + currentLon
        );
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Location copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
}
