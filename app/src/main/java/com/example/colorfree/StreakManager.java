package com.example.colorfree;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.colorfree.data.AppDatabase;
import com.example.colorfree.data.UsageEvent;

import java.util.Calendar;
import java.util.List;

public class StreakManager {
    private static final String PREF_STREAK = "current_streak";
    private static final String PREF_LAST_CHECK = "last_streak_check_ms";

    public static int getStreak(Context context) {
        return context.getSharedPreferences("ColorFreePrefs", Context.MODE_PRIVATE)
                .getInt(PREF_STREAK, 0);
    }

    public static void checkStreak(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ColorFreePrefs", Context.MODE_PRIVATE);
        long lastCheck = prefs.getLong(PREF_LAST_CHECK, 0);
        long now = System.currentTimeMillis();

        Calendar calLast = Calendar.getInstance();
        calLast.setTimeInMillis(lastCheck);
        
        Calendar calNow = Calendar.getInstance();
        calNow.setTimeInMillis(now);

        // Same day? Return.
        if (isSameDay(calLast, calNow)) return;

        // Is it the immediate next day?
        calLast.add(Calendar.DAY_OF_YEAR, 1);
        if (isSameDay(calLast, calNow)) {
            // Check performance for "yesterday"
            // For MVP simplicity, let's just assume if they open the app, they keep the streak alive.
            // Or better: check if usage yesterday was > 50% gray.
            // Implementation: Simple "Login Streak" for now to reduce DB query complexity on main thread.
            int currentStreak = prefs.getInt(PREF_STREAK, 0);
            prefs.edit().putInt(PREF_STREAK, currentStreak + 1).putLong(PREF_LAST_CHECK, now).apply();
        } else {
            // Missed a day
            prefs.edit().putInt(PREF_STREAK, 1).putLong(PREF_LAST_CHECK, now).apply();
        }
    }
    
    private static boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
               c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
}
