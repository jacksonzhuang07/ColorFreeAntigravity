package com.example.colorfree;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ProFeaturesActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private int startHour = 9, startMin = 0;
    private int endHour = 17, endMin = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_features);

        prefs = getSharedPreferences("ColorFreePrefs", MODE_PRIVATE);
        
        // For MVP, checking dev_mode or assuming passed in intent.
        // In real app, re-verify BillingManager status.
        boolean isPro = prefs.getBoolean("dev_mode", false); 
        // Note: Real implementation would check BillingManager here too.

        setupUI(isPro);
    }

    private void setupUI(boolean isPro) {
        TextView streakText = findViewById(R.id.streak_text);
        streakText.setText("Current Streak: " + StreakManager.getStreak(this) + " Days");

        Switch switchSchedule = findViewById(R.id.switch_schedule);
        Button btnStart = findViewById(R.id.btn_start_time);
        Button btnEnd = findViewById(R.id.btn_end_time);
        
        EditText inputPkg = findViewById(R.id.input_limit_pkg);
        EditText inputMins = findViewById(R.id.input_limit_mins);
        Button btnSaveLimit = findViewById(R.id.btn_save_limit);

        // Load saved
        switchSchedule.setChecked(prefs.getBoolean("schedule_enabled", false));
        startHour = prefs.getInt("schedule_start_hour", 9);
        startMin = prefs.getInt("schedule_start_min", 0);
        endHour = prefs.getInt("schedule_end_hour", 17);
        endMin = prefs.getInt("schedule_end_min", 0);
        
        updateTimeButton(btnStart, startHour, startMin);
        updateTimeButton(btnEnd, endHour, endMin);

        if (!isPro) {
            switchSchedule.setEnabled(false);
            btnStart.setEnabled(false);
            btnEnd.setEnabled(false);
            btnSaveLimit.setEnabled(false);
            inputPkg.setEnabled(false);
            inputMins.setEnabled(false);
            Toast.makeText(this, "Upgrade to Pro to enable these features!", Toast.LENGTH_LONG).show();
            return;
        }

        switchSchedule.setOnCheckedChangeListener((v, checked) -> 
            prefs.edit().putBoolean("schedule_enabled", checked).apply());

        btnStart.setOnClickListener(v -> showTimePicker(true, btnStart));
        btnEnd.setOnClickListener(v -> showTimePicker(false, btnEnd));

        btnSaveLimit.setOnClickListener(v -> {
            String pkg = inputPkg.getText().toString().trim();
            String minsStr = inputMins.getText().toString().trim();
            if (!pkg.isEmpty() && !minsStr.isEmpty()) {
                int mins = Integer.parseInt(minsStr);
                prefs.edit().putInt("limit_" + pkg, mins).apply();
                Toast.makeText(this, "Limit set for " + pkg, Toast.LENGTH_SHORT).show();
                inputPkg.setText("");
                inputMins.setText("");
            }
        });
    }

    private void showTimePicker(boolean isStart, Button btn) {
        int h = isStart ? startHour : endHour;
        int m = isStart ? startMin : endMin;

        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            if (isStart) {
                startHour = hourOfDay;
                startMin = minute;
                prefs.edit().putInt("schedule_start_hour", startHour)
                        .putInt("schedule_start_min", startMin).apply();
            } else {
                endHour = hourOfDay;
                endMin = minute;
                prefs.edit().putInt("schedule_end_hour", endHour)
                        .putInt("schedule_end_min", endMin).apply();
            }
            updateTimeButton(btn, hourOfDay, minute);
        }, h, m, true).show();
    }

    private void updateTimeButton(Button btn, int h, int m) {
        btn.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));
    }
}
