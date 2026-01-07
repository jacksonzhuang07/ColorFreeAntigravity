package com.example.colorfree;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.colorfree.data.AppDatabase;
import com.example.colorfree.data.UsageEvent;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AnalyticsActivity extends AppCompatActivity {

    private HorizontalBarChart chart;
    private TextView statsText;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        chart = findViewById(R.id.chart);
        statsText = findViewById(R.id.total_stats);
        db = AppDatabase.getDatabase(this);
        
        findViewById(R.id.btn_export).setOnClickListener(v -> exportData());

        loadData();
    }
    
    public void exportData() {
        new Thread(() -> {
            try {
                java.io.File file = new java.io.File(getExternalFilesDir(null), "colorfree_usage.csv");
                java.io.FileWriter writer = new java.io.FileWriter(file);
                writer.append("Package,Duration(ms),IsColor\n");
                
                List<UsageEvent> events = db.usageDao().getAllEvents();
                for (UsageEvent e : events) {
                    writer.append(e.packageName)
                          .append(",")
                          .append(String.valueOf(e.getDuration()))
                          .append(",")
                          .append(String.valueOf(e.isColorMode))
                          .append("\n");
                }
                writer.flush();
                writer.close();
                
                // Share
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/csv");
                intent.putExtra(android.content.Intent.EXTRA_STREAM, 
                    androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".provider", file));
                intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(android.content.Intent.createChooser(intent, "Export Usage Data"));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadData() {
        new Thread(() -> {
            List<UsageEvent> events = db.usageDao().getAllEvents();
            
            // Process Data
            Map<String, AppUsage> usageMap = new HashMap<>();
            long totalColor = 0;
            long totalGray = 0;

            PackageManager pm = getPackageManager();

            for (UsageEvent event : events) {
                long duration = event.getDuration();
                if (duration <= 0) continue;

                if (event.isColorMode) totalColor += duration;
                else totalGray += duration;

                usageMap.putIfAbsent(event.packageName, new AppUsage(event.packageName));
                AppUsage usage = usageMap.get(event.packageName);
                usage.add(duration, event.isColorMode);
                
                // Cache label
                if (usage.label == null) {
                    try {
                        usage.label = pm.getApplicationLabel(
                            pm.getApplicationInfo(event.packageName, 0)
                        ).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        usage.label = event.packageName;
                    }
                }
            }

            // Prepare Chart Data
            // Sort by total usage descending
            List<AppUsage> sortedApps = usageMap.values().stream()
                .sorted((a, b) -> Long.compare(b.totalDuration(), a.totalDuration()))
                .limit(10) // Top 10
                .collect(Collectors.toList());

            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            for (int i = 0; i < sortedApps.size(); i++) {
                AppUsage app = sortedApps.get(i);
                // MPAndroidChart uses float. Convert ms to minutes.
                float colorMins = app.colorTime / 60000f;
                float grayMins = app.grayTime / 60000f;
                
                // Reverse index because Horizontal chart draws bottom-up
                // But let's stick to standard 0..N and handle display 
                entries.add(new BarEntry(i, new float[]{grayMins, colorMins}));
                labels.add(app.label);
            }

            final long finalTotalColor = totalColor;
            final long finalTotalGray = totalGray;

            runOnUiThread(() -> {
                updateUI(entries, labels, finalTotalColor, finalTotalGray);
            });
        }).start();
    }

    private void updateUI(List<BarEntry> entries, List<String> labels, long totalColorMs, long totalGrayMs) {
        // Summary
        long totalMs = totalColorMs + totalGrayMs;
        long totalMins = TimeUnit.MILLISECONDS.toMinutes(totalMs);
        if (totalMins == 0) totalMins = 1; // avoid /0
        
        long colorPerc = (totalColorMs * 100) / totalMs;
        long grayPerc = 100 - colorPerc;

        statsText.setText(String.format("Total Tracked: %d mins\nColor: %d%% | Grayscale: %d%%", 
            totalMins, colorPerc, grayPerc));

        // Chart
        BarDataSet set = new BarDataSet(entries, "Usage (Minutes)");
        set.setColors(Color.GRAY, Color.MAGENTA); // Stack colors: Gray, Color
        set.setStackLabels(new String[]{"Grayscale", "Color"});
        
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        
        BarData data = new BarData(dataSets);
        data.setValueTextColor(Color.BLACK);
        
        chart.setData(data);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);
        chart.invalidate();
    }

    private static class AppUsage {
        String packageName;
        String label;
        long colorTime = 0;
        long grayTime = 0;

        AppUsage(String pkg) { this.packageName = pkg; }
        
        void add(long duration, boolean isColor) {
            if (isColor) colorTime += duration;
            else grayTime += duration;
        }

        long totalDuration() { return colorTime + grayTime; }
    }
}
