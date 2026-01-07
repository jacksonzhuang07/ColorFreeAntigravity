# 🧪 Manual Testing Guide - ColorFree V7

Since ColorFree interacts with system settings (Grayscale), hardware sensors (Shake/Rotation), and overlay permissions, it relies heavily on manual testing on a real device.

## 📱 Preparation
1.  **Install the APK**: Run `Run 'app'` in Android Studio to install the app on your phone.
2.  **Grant Permission**:
    *   Connect phone to PC.
    *   Run: `adb shell pm grant com.example.colorfree android.permission.WRITE_SECURE_SETTINGS`
    *   (Or use the new "Setup Tutorial" feature in the app to copy the command).

## 🧪 Test Cases

### 1. The Core Loop (Grayscale)
*   [ ] Open the app.
*   [ ] Tap **"Enable Accessibility Service"** (if not already enabled).
*   [ ] In Settings, enable **"ColorFree Service"**.
*   [ ] Go back to app. Is the screen Grayscale? ✅
*   [ ] Tap the **Checkbox** next to "Instagram" (or any app).
*   [ ] **[Challenge]**: A random challenge should appear (Math/Rotate/Shake/Scroll). Complete it.
*   [ ] Launch Instagram. Is it in **Color**? ✅
*   [ ] Go home. Is the launcher in **Grayscale**? ✅

### 2. Randomized Friction (V7)
Try to "Unlock Color Globally" or change a Checkbox 5 times to see different challenges.
#### A. 🧮 Math
*   [ ] "What is 12 + 7?" -> Enter `19`.
*   [ ] Success: Dialog closes, action proceeds.
#### B. 🔄 Rotate
*   [ ] "Rotate phone 5 times".
*   [ ] Flip phone face-down, then face-up.
*   [ ] Does the counter go up (1/5)? ✅
*   [ ] Repeat until 5/5. Success? ✅
#### C. 🫨 Shake
*   [ ] "Shake phone 30 times".
*   [ ] Shake vigorously. Counter should go up rapidly.
*   [ ] Stop shaking. Counter stops.
*   [ ] Reach 30/30. Success? ✅
#### D. 📜 Scroll
*   [ ] "Scroll to bottom".
*   [ ] Swipe up repeatedly. It should be a long page.
*   [ ] Find "UNLOCK COLOR" button at the very bottom.
*   [ ] Tap it. Success? ✅

### 3. Usage Limits (Pro)
*   [ ] Go to **Pro Features** -> **App Limits**.
*   [ ] Set valid limit for Instagram (e.g., 1 minute for testing).
*   [ ] Open Instagram. Use it for 60 seconds.
*   [ ] **Expected**: The screen forcibly turns **Grayscale** after 1 minute, even if Whitelisted. ✅

### 4. Focus Schedule (Pro)
*   [ ] Go to **Pro Features** -> **Focus Schedule**.
*   [ ] Set "Start Time" to NOW and "End Time" to 1 hour later.
*   [ ] Enable Schedule.
*   [ ] Go Home. Grayscale should be **Active**.
*   [ ] Change "End Time" to NOW (so schedule is over).
*   [ ] Go Home. Grayscale should be **Disabled** (Color). ✅

### 5. Gamification (Trees)
*   [ ] Look at the tree icon next to "ColorFree" title. 
*   [ ] Is it 🌱 (Sprout) or 🌲 (Tree)?
*   [ ] Note: Streak updates on new day login.

### 6. Data Export
*   [ ] Go to **Analytics**.
*   [ ] Tap **"Export CSV"**.
*   [ ] Select "Gmail" or "Drive".
*   [ ] Check the file. Does it contain rows like `com.instagram.android, 5000, true`? ✅

## 🐞 Troubleshooting
*   **"Permission Missing"**: Re-run the ADB command.
*   **"Service Disabled"**: Android sometimes kills services. Re-enable in Accessibility Settings.
*   **Ads not showing**: In debug builds, ads are test ads. If you are Premium/Dev Mode, ads never show.
