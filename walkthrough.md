# ColorFree App - Build & Usage Guide

We have successfully generated the Android project for **ColorFree**. This app requires a specific manual setup step to function because of Android's security restrictions on programmatically changing display settings.

## 1. Open in Android Studio
1.  Open **Android Studio**.
2.  Select **Open an existing project**.
3.  Navigate to `C:\Users\jray\OneDrive - Omnitrans Inc\Desktop\ColorFree` and click OK.
4.  Wait for Gradle Sync to complete.

## 2. Build and Install
1.  Connect your Android device via USB (ensure **USB Debugging** is enabled in Developer Options).
2.  Click the green **Run** button (Play icon) in Android Studio.
3.  Select your connected device.
4.  The app should install and open.

## 3. Grant Permissions (CRITICAL)
When you first open the app, you will see a red warning: *"Missing Permission: WRITE_SECURE_SETTINGS"*.

1.  Keep your phone connected to the PC.
2.  Open a terminal or command prompt on your PC.
3.  Run the following command:
    ```powershell
    adb shell pm grant com.example.colorfree android.permission.WRITE_SECURE_SETTINGS
    ```
    *(Note: If `adb` is not in your path, you may need to use the terminal inside Android Studio or navigate to your SDK platform-tools folder).*

## 4. Enable Service
1.  After granting the ADB permission, the app should show a button: **"Open Accessibility Settings"**.
2.  Click it (or manually go to Settings > Accessibility).
3.  Find **ColorFree Monitor** in the list of installed services.
4.  Tap it and toggle it **ON**.

## 5. Usage
*   **Default State**: Your phone will now switch to **Grayscale** automatically.
*   **Whitelist**: In the ColorFree app, check the box next to any app you want to see in **Color** (e.g., Photos, Instagram).
*   **Testing**: Open a checked app -> Screen turns color. Go home or open an unchecked app -> Screen turns grayscale.

## 6. Productivity Features (V2)
*   **Global Unlock**:
    *   To see the whole phone in color for a while, tap **"Temporarily Unlock Color"**.
    *   Select a duration (e.g., 15 minutes).
    *   **The Cost**: You must solve 1 math problem for every 5 minutes requested (e.g., 3 problems for 15 mins).
*   **Settings Friction**:
    *   To add or remove an app from the Color Whitelist, you must solve **1 math problem** per change. This prevents impulsive switching.

## 7. Monetization & Design (V3)
*   **Minimalist UI**: Users enjoy a cleaner, high-contrast black & white aesthetic.
*   **Ad-Supported Productivity**:
    *   By default, after solving a math challenge (for unlocking color or changing settings), an **Interstitial Ad** will be shown.
    *   *Note: Ads are disabled in Debug builds by default.*
*   **Subscription**:
    *   Users can subscribe for **$0.99/week** to remove all ads.
*   **Secret Dev Mode (For You)**:
    *   To disable ads on your personal device without paying:
    1.  Tap the **"ColorFree"** title text at the top of the screen **7 times** quickly.
    2.  A dialog will ask: **"what is your name?"**
    3.  Type the answer: **Jianhao Zhuang**
    4.  Ads will stay off permanently on this device.
    *   *Note: You can change this Question/Answer in `MainActivity.java`.*

    *   *Note: You can change this Question/Answer in `MainActivity.java`.*

## 8. Analytics (V4)
*   **Track Your Habits**:
    *   The app now records how much time you spend in each app.
    *   Tap **"View Analytics"** on the home screen.
*   **Visual Breakdown**:
    *   See a sorted bar graph of your top used apps.
    *   **Stacked Bars**: Each bar is divided into **Gray** (Productive) and **Color** (Distracted/Whitelisted) time.
    *   **Global Stats**: See your overall Grayscale vs. Color percentage at the top.

## 9. Pro Features (V5)
*   **Access**: Tap **"Pro Features"** (Requires Subscription or Dev Mode).
*   **Streaks**:
    *   Tracks how many consecutive days you've used the app. Keep the streak alive!
*   **Focus Schedule**:
    *   Set a "Work Schedule" (e.g., 9:00 to 17:00).
    *   **Outside** this schedule, Grayscale is **Disabled** automatically (Phone is in Color).
    *   **During** this schedule, Grayscale is **Enforced** (Whitlist applies).
*   **App Limits**:
    *   Set a daily limit (in minutes) for specific apps (e.g., `com.instagram.android`).
    *   If you exceed the limit, the app is **Forced to Grayscale** for the rest of the day, even if it's on your Whitelist.

## 10. Ultimate Features (V6)
*   **Gamification**: The 🌳 icon next to the title grows as your streak increases. Don't let it die!
*   **Data Export**: Go to **Analytics** -> **Export CSV** to save your usage history.
*   **Setup Tutorial**: Stuck? Tap **"Setup Tutorial"** on the home screen for a guide on the ADB command.
*   **Hardcore Mode**: Advanced users using Device Admin will find it much harder to uninstall the app.

## 11. Randomized Friction (V7)
To prevent you from getting "good" at the challenges, the app now randomizes the unlock requirement. You might get:
*   **Math Problems**: Solve equations.
*   **Rotate Challenge**: Physically flip your phone face-down and up 5 times.
*   **Shake Challenge**: Shake your phone vigorously 30 times.
*   **Scroll Challenge**: Doomscroll through a 50-foot empty page to find the unlock button.

## Troubleshooting
*   **"Binder Transaction Failed"**: If the list of apps is huge, the loading might be slow.
*   **Permission doesn't stick**: Determine if your specific device manufacturer (e.g., Xiaomi/MIUI) has extra "Security" settings required for background services to modify settings.
