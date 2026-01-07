package com.example.colorfree;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdManager {
    private static final String TAG = "AdManager";
    // Test Ad Unit ID for Interstitial
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"; 
    
    private InterstitialAd mInterstitialAd;
    private final Context context;
    private boolean isPremium = false;

    public AdManager(Context context) {
        this.context = context;
        MobileAds.initialize(context, initializationStatus -> {});
        loadAd();
    }

    public void setPremium(boolean premium) {
        this.isPremium = premium;
    }

    public void loadAd() {
        if (isPremium) return;
        
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, AD_UNIT_ID, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    Log.i(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.d(TAG, loadAdError.toString());
                    mInterstitialAd = null;
                }
            });
    }

    public void showAd(Activity activity, Runnable onComplete) {
        // "Using strict math" -> if debug and not strictly testing ads, maybe skip?
        // User asked: "only dont show ads for my use case". 
        // We will assume "my use case" = Debug Build or Premium.
        // But for "release in play store", we need it.
        // Let's rely on isPremium primarily. 
        // And if BuildConfig.DEBUG is true, we might want to skip for convenience, 
        // OR show it because user wants to verify implementation.
        // Re-reading: "for my use case only dont show ads". 
        // I'll make it so if Debug && !Premium, we skip ads effectively.
        
        if (isPremium) {
            onComplete.run();
            return;
        }

        if (BuildConfig.DEBUG) {
            // Skip ads for debug builds as requested for "my use case"
            Log.d(TAG, "Skipping ad for Debug build");
            onComplete.run();
            return;
        }

        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(TAG, "The ad was dismissed.");
                    mInterstitialAd = null;
                    loadAd(); // Preload next one
                    onComplete.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.d(TAG, "The ad failed to show.");
                    mInterstitialAd = null;
                    onComplete.run();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d(TAG, "The ad was shown.");
                }
            });
            mInterstitialAd.show(activity);
        } else {
            Log.d(TAG, "The ad wasn't ready yet.");
            loadAd(); // Try loading again
            onComplete.run();
        }
    }
}
