# 🚀 ColorFree: Launch & Monetization Guide

You have the code. Now you need to turn it into a business. This guide explains how to replace the "Test IDs" with real money-making IDs and publish to the Google Play Store.

## 💰 1. Monetization Setup

### A. AdMob (Ads)
Currently, `AdManager.java` uses a Google Test ID (`ca-app-pub-3940256099942544...`). You need your own.

1.  **Create Account**: Go to [admob.google.com](https://admob.google.com/) and sign up.
2.  **Add App**: Click **Apps > Add App**. Select "Android".
3.  **Create Ad Unit**:
    *   Select **Interstitial Video**.
    *   Name it "ColorFree Global Unlock".
    *   **Copy the Ad Unit ID** (It looks like `ca-app-pub-XXXXXXXX/YYYYYYYY`).
4.  **Update Code**:
    *   Open `app/src/main/java/com/example/colorfree/AdManager.java`.
    *   Find line 18: `private static final String AD_UNIT_ID = "...";`
    *   Replace the string with your **Real Ad Unit ID**.

### B. Google Play Billing (Subscriptions)
Currently, `BillingManager.java` looks for a product called `remove_ads_weekly`.

1.  **Create Developer Account**: Go to [play.google.com/console](https://play.google.com/console). Cost is $25 (one-time).
2.  **Create App**: Click "Create App", name it "ColorFree".
3.  **Monetization Setup**:
    *   Go to **Monetize > Products > Subscriptions**.
    *   Click **Create Subscription**.
    *   **Product ID**: Enter `remove_ads_weekly` (Must match `BillingManager.java` line 24 exactly!).
    *   **Base Plan**: Set price (e.g., $0.99) and billing period (Weekly).
    *   **Activate** the subscription.

---

## ☁️ 2. Server & Leaderboards
**Question**: *"Do I need server stuff for the leaderboard?"*

**Answer**: 
*   **For MVP (Current)**: No. We implemented a **Local Gamification** system ("Tree Status"). Your tree grows based on your local streak. This requires **zero server costs** and **zero maintenance**.
*   **For Global Leaderboards (Future)**: If you want a "World Top 100" list later, you *will* need a backend (like Firebase). But for launch, the current local system is significantly cheaper and easier. Launch with what you have!

---

## 🚀 3. Publishing to Play Store

### A. Generate Signed Bundle
1.  Open Project in **Android Studio**.
2.  Go to **Build > Generate Signed Bundle / APK**.
3.  Select **Android App Bundle**.
4.  **Key Store**: Create a new one. **SAVE THIS FILE AND PASSWORD**. If you lose it, you can never update your app again.
5.  Click **Finish**. It will generate a `.aab` file (release).

### B. Upload to Console
1.  In Play Console, go to **Testing > Closed testing** (Recommended for first run).
2.  **Create Release**: Upload your `.aab` file.
3.  **Store Listing**: Upload your icon, screenshots, and write a description using keywords (ADHD, Focus, Grayscale).
4.  **Privacy Policy**: You need a URL. Use a free generator (e.g., Flycricket) and host it on GitHub Pages.
5.  **Review**: Submit for review. Google usually takes 3-7 days.

---

## 📋 Pre-Flight Checklist
*   [ ] Changed AdMob ID in `AdManager.java`?
*   [ ] Created Subscription `remove_ads_weekly` in Play Console?
*   [ ] Created `privacy_policy.html` (e.g. on GitHub)?
*   [ ] Generated Signed `.aab`?

**Good luck! You are building a digital wellbeing empire.** 🧘
