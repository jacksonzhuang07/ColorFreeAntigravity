package com.example.colorfree;

import android.content.Context;
import java.util.Random;

public class ChallengeRouter {

    public interface ChallengeCallback {
        void onSuccess();
        void onFailure();
    }

    public static void showRandomChallenge(Context context, ChallengeCallback callback) {
        int random = new Random().nextInt(4); // 0, 1, 2, 3

        switch (random) {
            case 0:
                // Math Challenge (Standard)
                // Default to 3 problems for general friction
                MathChallengeDialog.show(context, 3, new MathChallengeDialog.Callback() {
                    @Override public void onSuccess() { callback.onSuccess(); }
                    @Override public void onFailure() { callback.onFailure(); }
                });
                break;
            case 1:
                // Rotate Challenge
                RotateChallengeDialog.show(context, new RotateChallengeDialog.Callback() {
                    @Override public void onSuccess() { callback.onSuccess(); }
                    @Override public void onFailure() { callback.onFailure(); }
                });
                break;
            case 2:
                // Shake Challenge
                ShakeChallengeDialog.show(context, new ShakeChallengeDialog.Callback() {
                    @Override public void onSuccess() { callback.onSuccess(); }
                    @Override public void onFailure() { callback.onFailure(); }
                });
                break;
            case 3:
                // Scroll Challenge
                ScrollChallengeDialog.show(context, new ScrollChallengeDialog.Callback() {
                    @Override public void onSuccess() { callback.onSuccess(); }
                    @Override public void onFailure() { callback.onFailure(); }
                });
                break;
        }
    }
}
