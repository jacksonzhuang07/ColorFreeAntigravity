package com.example.colorfree;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;

public class ShakeChallengeDialog {

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    private static final int SHAKES_REQUIRED = 30; // High friction
    private static int shakeCount = 0;
    private static long lastShakeTime = 0;
    private static float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD = 800;

    public static void show(Context context, Callback callback) {
        shakeCount = 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Shake Challenge");
        builder.setMessage("Shake your phone vigorously " + SHAKES_REQUIRED + " times!");
        builder.setCancelable(false);

        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(SHAKES_REQUIRED);
        progressBar.setProgress(0);
        
        TextView countText = new TextView(context);
        countText.setText("0 / " + SHAKES_REQUIRED);
        countText.setTextSize(24);
        countText.setGravity(Gravity.CENTER);
        
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
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
                long now = System.currentTimeMillis();
                
                if ((now - lastShakeTime) > 100) {
                    long diffTime = (now - lastShakeTime);
                    lastShakeTime = now;

                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        shakeCount++;
                        progressBar.setProgress(shakeCount);
                        countText.setText(shakeCount + " / " + SHAKES_REQUIRED);
                        
                        if (shakeCount >= SHAKES_REQUIRED) {
                            sensorManager.unregisterListener(this);
                            dialog.dismiss();
                            callback.onSuccess();
                        }
                    }

                    lastX = x;
                    lastY = y;
                    lastZ = z;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        dialog.setOnDismissListener(d -> sensorManager.unregisterListener(listener));
    }
}
