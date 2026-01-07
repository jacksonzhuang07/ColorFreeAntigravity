package com.example.colorfree;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        TextView codeText = findViewById(R.id.code_text);
        Button btnCopy = findViewById(R.id.btn_copy_command);
        WebView webView = findViewById(R.id.webview_tutorial);

        final String command = "adb shell pm grant com.example.colorfree android.permission.WRITE_SECURE_SETTINGS";

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("ADB Command", command);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Command Copied!", Toast.LENGTH_SHORT).show();
        });

        // Load a generic ADB tutorial video (e.g. from Google or a Placeholder)
        // For now, loading a helpful guide page
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://developer.android.com/studio/command-line/adb");
    }
}
