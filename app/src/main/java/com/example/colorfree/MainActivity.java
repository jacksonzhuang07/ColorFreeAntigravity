package com.example.colorfree;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BillingManager.SubscriptionStatusListener {

    private RecyclerView recyclerView;
    private AppListAdapter adapter;
    private TextView permissionStatus;
    private Button btnAccessibility;
    private Button btnRemoveAds;

    private BillingManager billingManager;
    private AdManager adManager;
    private boolean isPremium = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Managers
        billingManager = new BillingManager(this, this);
        adManager = new AdManager(this);

        recyclerView = findViewById(R.id.recycler_view);
        permissionStatus = findViewById(R.id.permission_status);
        btnAccessibility = findViewById(R.id.btn_accessibility_settings);
        Button btnUnlock = findViewById(R.id.btn_unlock_global);
        TextView titleText = findViewById(R.id.title_text); // Requires ID in XML
        btnRemoveAds = findViewById(R.id.btn_remove_ads);
        Button btnAnalytics = findViewById(R.id.btn_analytics);

        setupDevMode(titleText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadApps();

        btnAccessibility.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });

        btnUnlock.setOnClickListener(v -> showUnlockDialog());
        btnRemoveAds.setOnClickListener(v -> billingManager.launchPurchaseFlow(this));
        btnAnalytics.setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));
        
        Button btnPro = findViewById(R.id.btn_pro_features);
        btnPro.setOnClickListener(v -> startActivity(new Intent(this, ProFeaturesActivity.class)));
        
        Button btnTutorial = findViewById(R.id.btn_tutorial);
        btnTutorial.setOnClickListener(v -> startActivity(new Intent(this, TutorialActivity.class)));

        // Update Streak daily
        StreakManager.checkStreak(this);
        
        // Update Tree Gamification
        updateTreeStatus();
    }

    private void updateTreeStatus() {
        TextView treeIcon = findViewById(R.id.tree_icon);
        // Simple logic: If Streak > 2, Tree is big. If broken, dead.
        int streak = StreakManager.getStreak(this);
        if (streak > 5) treeIcon.setText("🌳");
        else if (streak > 2) treeIcon.setText("🌲");
        else if (streak > 0) treeIcon.setText("🌱");
        else treeIcon.setText("🍂");
    }

    private void showUnlockDialog() {
        final String[] options = { "5 Minutes", "15 Minutes", "30 Minutes", "60 Minutes" };
        final int[] durations = { 5, 15, 30, 60 };

        new android.app.AlertDialog.Builder(this)
                .setTitle("Unlock Color Globally")
                .setItems(options, (dialog, which) -> {
                    int minutes = durations[which];

                    ChallengeRouter.showRandomChallenge(this, new ChallengeRouter.ChallengeCallback() {
                        @Override
                        public void onSuccess() {
                            // Show Ad after Challenge Success, before Unlock
                            adManager.showAd(MainActivity.this, () -> {
                                performGlobalUnlock(minutes);
                            });
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(MainActivity.this, "Challenge Failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show();
    }

    private void performGlobalUnlock(int minutes) {
        long expiry = System.currentTimeMillis() + (minutes * 60 * 1000);
        getSharedPreferences("ColorFreePrefs", MODE_PRIVATE)
                .edit()
                .putLong("global_unlock_expiry", expiry)
                .apply();

        Toast.makeText(MainActivity.this,
                "Unlocked for " + minutes + " minutes!",
                Toast.LENGTH_LONG).show();

        GrayscaleHelper.disableGrayscale(MainActivity.this);
    }

    private int devTapCount = 0;
    private long lastTapTime = 0;

    private void setupDevMode(TextView titleView) {
        // Check if already in Dev Mode
        boolean isDev = getSharedPreferences("ColorFreePrefs", MODE_PRIVATE).getBoolean("dev_mode", false);
        if (isDev) {
            onSubscriptionStatusChanged(true); // Treat as premium
        }

        titleView.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if (now - lastTapTime > 1000) {
                devTapCount = 0;
            }
            lastTapTime = now;
            devTapCount++;

            if (devTapCount == 7) {
                // Reset count immediately
                devTapCount = 0;

                // Show Security Question Dialog
                final android.widget.EditText input = new android.widget.EditText(MainActivity.this);
                input.setHint("Answer...");

                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Security Question")
                        .setMessage("what is your name?")
                        .setView(input)
                        .setCancelable(true)
                        .setPositiveButton("Unlock", (dialog, which) -> {
                            String answer = input.getText().toString().trim();
                            if ("Jianhao Zhuang".equalsIgnoreCase(answer)) {
                                toggleDevMode();
                            } else {
                                Toast.makeText(MainActivity.this, "Incorrect. Access Denied.", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .show();
            }
        });
    }

    private void toggleDevMode() {
        boolean newState = !getSharedPreferences("ColorFreePrefs", MODE_PRIVATE).getBoolean("dev_mode", false);
        getSharedPreferences("ColorFreePrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("dev_mode", newState)
                .apply();

        String msg = newState ? "Dev Mode: Ads Removed!" : "Dev Mode Disabled.";
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

        // Refresh state
        if (newState) {
            onSubscriptionStatusChanged(true);
        } else {
            onSubscriptionStatusChanged(false);
            billingManager.launchPurchaseFlow(this);
        }
    }

    @Override
    public void onSubscriptionStatusChanged(boolean isSubscribed) {
        this.isPremium = isSubscribed;
        adManager.setPremium(isSubscribed);
        runOnUiThread(() -> {
            if (isSubscribed) {
                btnRemoveAds.setVisibility(View.GONE);
            } else {
                btnRemoveAds.setVisibility(View.VISIBLE);
            }
            if (adapter != null) {
                adapter.setAdManager(adManager);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions() {
        boolean secureSettingsGranted = GrayscaleHelper.hasPermission(this);
        boolean accessibilityEnabled = isAccessibilityServiceEnabled();

        if (!secureSettingsGranted) {
            permissionStatus.setText("Core Permission Missing.\nRun ADB command.");
            permissionStatus.setVisibility(View.VISIBLE);
            btnAccessibility.setVisibility(View.GONE);
        } else if (!accessibilityEnabled) {
            permissionStatus.setText("Accessibility Service Disabled.");
            permissionStatus.setVisibility(View.VISIBLE);
            btnAccessibility.setVisibility(View.VISIBLE);
        } else {
            permissionStatus.setVisibility(View.GONE);
            btnAccessibility.setVisibility(View.GONE);
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        String prefString = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return prefString != null && prefString.contains(getPackageName() + "/" + AppMonitorService.class.getName());
    }

    private void loadApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> userApps = new ArrayList<>();

        for (ApplicationInfo app : apps) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                userApps.add(app);
            }
        }

        adapter = new AppListAdapter(this, userApps);
        adapter.setAdManager(adManager); // Initial set
        recyclerView.setAdapter(adapter);
    }
}
