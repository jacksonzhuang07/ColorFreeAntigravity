package com.example.colorfree;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.util.Log;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class AppMonitorService extends AccessibilityService {
    private static final String TAG = "AppMonitorService";

    private String lastPackageName = "";
    private long lastMoveTime = 0;
    private boolean lastWasColor = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName() != null ? event.getPackageName().toString() : "";
            long now = System.currentTimeMillis();

            // Record previous session
            if (!lastPackageName.isEmpty() && lastMoveTime > 0) {
                com.example.colorfree.data.AppDatabase db = com.example.colorfree.data.AppDatabase.getDatabase(this);
                com.example.colorfree.data.UsageEvent usage = new com.example.colorfree.data.UsageEvent(
                    lastPackageName, lastMoveTime, now, lastWasColor
                );
                db.usageDao().insert(usage);
            }

            // Update state for new session
            lastPackageName = packageName;
            lastMoveTime = now;
            
            // Apply mode and update tracked state
            lastWasColor = checkAndApplyMode(packageName);
        }
    }

    // Returns true if Color, false if Grayscale
    private boolean checkAndApplyMode(String packageName) {
        SharedPreferences prefs = getSharedPreferences("ColorFreePrefs", MODE_PRIVATE);
        
        // 0. Check Focus Schedule (Pro Feature)
        boolean scheduleEnabled = prefs.getBoolean("schedule_enabled", false);
        if (scheduleEnabled) {
            java.util.Calendar now = java.util.Calendar.getInstance();
            int h = now.get(java.util.Calendar.HOUR_OF_DAY);
            int m = now.get(java.util.Calendar.MINUTE);
            int currentMins = h * 60 + m;
            
            int startMins = prefs.getInt("schedule_start_hour", 9) * 60 + prefs.getInt("schedule_start_min", 0);
            int endMins = prefs.getInt("schedule_end_hour", 17) * 60 + prefs.getInt("schedule_end_min", 0);
            
            // If outside schedule, disable grayscale (Return to Color)
            if (currentMins < startMins || currentMins > endMins) {
                GrayscaleHelper.disableGrayscale(this);
                return true;
            }
        }

        // 1. Check Global Unlock Timer
        long unlockExpiry = prefs.getLong("global_unlock_expiry", 0);
        if (System.currentTimeMillis() < unlockExpiry) {
            GrayscaleHelper.disableGrayscale(this);
            return true;
        }

        // 2. Check App Usage Limit (Pro Feature)
        int limitMins = prefs.getInt("limit_" + packageName, 0);
        if (limitMins > 0) {
            // Calculate usage for today
            // Note: In a real app, optimize this to not query DB every window switch.
            long usageToday = getTodayUsage(packageName);
            if (usageToday > limitMins * 60000L) {
                // Limit Exceeded -> Force Grayscale (Productivity penalty)
                GrayscaleHelper.enableGrayscale(this);
                return false;
            }
        }

        // 3. Check Whitelist
        Set<String> whitelist = prefs.getStringSet("whitelist", new HashSet<>());
        if (whitelist.contains(packageName)) {
            GrayscaleHelper.disableGrayscale(this);
            return true;
        } else {
            GrayscaleHelper.enableGrayscale(this);
            return false;
        }
    }

    private long getTodayUsage(String packageName) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        long startOfDay = cal.getTimeInMillis();
        
        com.example.colorfree.data.AppDatabase db = com.example.colorfree.data.AppDatabase.getDatabase(this);
        java.util.List<com.example.colorfree.data.UsageEvent> events = db.usageDao().getEventsSince(startOfDay);
        
        long total = 0;
        for (com.example.colorfree.data.UsageEvent e : events) {
            if (e.packageName.equals(packageName)) {
                total += e.getDuration();
            }
        }
        return total;
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service Interrupted");
    }
}
