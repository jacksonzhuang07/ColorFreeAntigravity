package com.example.colorfree;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class GrayscaleHelper {
    private static final String TAG = "GrayscaleHelper";
    private static final String DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled";
    private static final String DISPLAY_DALTONIZER = "accessibility_display_daltonizer";
    private static final String MONOCHROMACY = "0"; // check documentation, usually 0 is Monochromacy

    public static void enableGrayscale(Context context) {
        if (!hasPermission(context)) return;
        try {
            ContentResolver cr = context.getContentResolver();
            Settings.Secure.putString(cr, DISPLAY_DALTONIZER_ENABLED, "1");
            Settings.Secure.putString(cr, DISPLAY_DALTONIZER, MONOCHROMACY);
            Log.d(TAG, "Grayscale ENABLED");
        } catch (Exception e) {
            Log.e(TAG, "Error enabling grayscale", e);
        }
    }

    public static void disableGrayscale(Context context) {
        if (!hasPermission(context)) return;
        try {
            ContentResolver cr = context.getContentResolver();
            Settings.Secure.putString(cr, DISPLAY_DALTONIZER_ENABLED, "0");
            Log.d(TAG, "Grayscale DISABLED");
        } catch (Exception e) {
            Log.e(TAG, "Error disabling grayscale", e);
        }
    }

    public static boolean hasPermission(Context context) {
        // We can check if we can write to secure settings
        // Actually, checking WRITE_SECURE_SETTINGS via checkCallingOrSelfPermission doesn't always work for normal apps 
        // trying to access SetupWizard permissions.
        // Best check is to try catching the SecurityException or use Settings.System.canWrite (but that's for SYSTEM settings).
        // For SECURE settings, we just have to rely on the fact that if it fails, it throws/crashes or logs. 
        // A safer check is usually just assuming false if the logic explicitly fails.
        // But let's check correct permission granting via pm check
        return context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }
}
