package com.example.colorfree;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {
    
    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Hardcore Mode Enabled: Uninstall blocked.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "Warning: Disabling this allow you to uninstall ColorFree and lose your focus streak.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "Hardcore Mode Disabled.", Toast.LENGTH_SHORT).show();
    }
}
