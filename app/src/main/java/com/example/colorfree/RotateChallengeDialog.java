package com.example.colorfree;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RotateChallengeDialog {

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    private static final int ROTATIONS_REQUIRED = 5; // Reduced from 10 for better UX
    private static int rotationCount = 0;
    private static float lastZ = 0;
    private static boolean faceDown = false;

    public static void show(Context context, Callback callback) {
        rotationCount = 0;
        faceDown = false;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Physical Challenge");
        builder.setMessage("Rotate your phone face-down and back up 5 times.");
        builder.setCancelable(false);

        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(ROTATIONS_REQUIRED);
        progressBar.setProgress(0);
        
        TextView countText = new TextView(context);
        countText.setText("0 / " + ROTATIONS_REQUIRED);
        countText.setTextSize(24);
        countText.setGravity(android.view.Gravity.CENTER);
        countText.setPadding(0, 32, 0, 32);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);
        layout.addView(countText);
        layout.addView(progressBar);
        builder.setView(layout);

        builder.setNegativeButton("Give Up", (dialog, which) -> callback.onFailure());

        AlertDialog dialog = builder.create();
        dialog.show();

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float z = event.values[2];
                
                // Simple logic: If Z < -8 (Face Down) then Z > 8 (Face Up) -> +1
                if (z < -8 && !faceDown) {
                    faceDown = true;
                } else if (z > 8 && faceDown) {
                    faceDown = false;
                    rotationCount++;
                    progressBar.setProgress(rotationCount);
                    countText.setText(rotationCount + " / " + ROTATIONS_REQUIRED);

                    if (rotationCount >= ROTATIONS_REQUIRED) {
                        sensorManager.unregisterListener(this);
                        dialog.dismiss();
                        callback.onSuccess();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        
        dialog.setOnDismissListener(d -> sensorManager.unregisterListener(listener));
    }
}
