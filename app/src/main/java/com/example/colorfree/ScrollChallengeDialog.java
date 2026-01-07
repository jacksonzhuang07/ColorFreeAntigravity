package com.example.colorfree;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ScrollChallengeDialog {

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    public static void show(Context context, Callback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Scroll Challenge");
        builder.setCancelable(false);

        ScrollView scrollView = new ScrollView(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        // Very tall content
        TextView topText = new TextView(context);
        topText.setText("Scroll all the way to the bottom to find the button.\n\nKeep going...\n\n");
        topText.setTextSize(18);
        topText.setGravity(Gravity.CENTER);
        topText.setPadding(32, 32, 32, 32);
        layout.addView(topText);

        // Filler space to force scrolling
        android.view.View filler = new android.view.View(context);
        // 5000 pixels height approx
        filler.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 15000)); 
        layout.addView(filler);

        Button unlockButton = new Button(context);
        unlockButton.setText("UNLOCK COLOR");
        unlockButton.setTextSize(20);
        unlockButton.setPadding(32, 32, 32, 32);
        
        layout.addView(unlockButton);
        scrollView.addView(layout);
        builder.setView(scrollView);

        builder.setNegativeButton("Give Up", (dialog, which) -> callback.onFailure());

        AlertDialog dialog = builder.create();
        
        unlockButton.setOnClickListener(v -> {
            dialog.dismiss();
            callback.onSuccess();
        });

        dialog.show();
    }
}
