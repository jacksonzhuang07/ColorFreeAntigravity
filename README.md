# ColorFree - Digital Wellbeing & Focus App

**ColorFree** is an Android application designed to reduce screen addiction and improve focus by enforcing a system-wide **Grayscale** mode. It reintroduces color only for user-selected "productive" apps or when explicitly unlocked via cognitive friction (Math Challenges).

## 🚀 Features

### Core Functionality
*   **Default Grayscale**: The entire phone display is black & white by default.
*   **App Whitelist**: Selected apps (e.g., Maps, Photos) automatically switch the screen to **Color** when opened.
*   ** Instant toggle**: Uses an **Accessibility Service** to detect the foreground app and switch color modes instantly.

### Productivity & Friction (V2)
*   **Global Unlock**: Temporarily unlock color for the whole device (5, 15, 30, 60 mins).
    *   *Cost*: Users must solve **1 Math Problem** for every 5 minutes requested.
*   **Intentional Settings**: Changing the app whitelist requires solving a math problem to prevent impulsive toggling.

### Monetization & Design (V3)
*   **Minimalist Aesthetic**: High-contrast, text-first black & white UI.
*   **Ad-Supported**: Interstitial ads appear after solving math challenges (before the reward).
*   **Subscription**: "Remove Ads" ($0.99/week) via Google Play Billing.
*   **Secret Dev Mode**: Tap the title "ColorFree" **7 times** -> Answer "Jianhao Zhuang" to "what is your name?" -> Permanently disable ads locally.

### Analytics (V4)
*   **Usage Tracking**: Records time spent in every app.
*   **Focus Ratio**: Tracks Time in Grayscale vs. Time in Color.
*   **Visualization**: Stacked Bar Graphs showing usage breakdown per app.
*   **Local Storage**: All data is stored privately on-device using **Room Database**.

---

## 🛠 Technical Architecture

### Key Components
1.  **`MainActivity`**: UI for whitelist, permissions, and dashboards.
2.  **`AppMonitorService`**: An extended `AccessibilityService`.
    *   Listens for `TYPE_WINDOW_STATE_CHANGED`.
    *   Checks foreground package against Whitelist & Global Timer.
    *   Logs usage sessions to the Database.
3.  **`GrayscaleHelper`**:
    *   Manages `Settings.Secure.ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED`.
    *   **Requires** `WRITE_SECURE_SETTINGS` permission.
4.  **`MathChallengeDialog`**: standardized friction mechanism.
5.  **`BillingManager` / `AdManager`**: Wrappers for Google Play services.

### Data Storage
*   **SharedPreferences**: Stores Whitelist (`Set<String>`), Global Timer (`long`), and Dev Mode status.
*   **Room Database**: Stores `UsageEvent` (package, start, end, isColor).

### Key Dependencies
*   `com.google.android.gms:play-services-ads`
*   `com.android.billingclient:billing`
*   `androidx.room:room-runtime`
*   `com.github.PhilJay:MPAndroidChart`

---

## 💻 Setup & Installation

### Prerequisites
*   Android Studio Hedgehog or newer.
*   Android Device/Emulator (API 24+).
*   **ADB (Android Debug Bridge)** installed on your PC.

### Step 1: Clone & Build
1.  Open the project in Android Studio.
2.  Sync Gradle.
3.  Run the app on your device.

### Step 2: Grant Permissions (CRITICAL)
The app **will crash or fail** to toggle grayscale without this step. Android does not allow apps to change display settings without special permission.

1.  Connect phone to PC with USB Debugging enabled.
2.  Open terminal/command prompt.
3.  Run:
    ```bash
    adb shell pm grant com.example.colorfree android.permission.WRITE_SECURE_SETTINGS
    ```

### Step 3: Enable Service
1.  Open the app.
2.  Tap "Enable Accessibility Service".
3.  Find **"ColorFree Monitor"** and toggle it **ON**.

---

## 📂 Project Structure

```text
com.example.colorfree
├── data/
│   ├── AppDatabase.java       # Room DB entry point
│   ├── UsageDao.java          # SQL queries
│   └── UsageEvent.java        # Entity model
├── AdManager.java             # AdMob logic
├── AppListAdapter.java        # RecyclerView with Checkbox logic
├── AppMonitorService.java     # The "Brain" (Background Service)
├── BillingManager.java        # Subscriptions
├── GrayscaleHelper.java       # Secure Settings Writer
├── MainActivity.java          # Primary UI Controller
├── MathChallengeDialog.java   # Friction Logic
└── MathHelper.java            # Problem Generator
```

## 🔒 Security Note
*   **Dev Mode**: The secret answer is hardcoded in `MainActivity.java`. Search for "Jianhao Zhuang" to change it.
*   **Ads**: Test Ads are used by default. Replace `AD_UNIT_ID` in `AdManager.java` before release.
