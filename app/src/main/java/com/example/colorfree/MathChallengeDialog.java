package com.example.colorfree;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class MathChallengeDialog {

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    public static void show(Context context, int numberOfProblems, Callback callback) {
        showNextProblem(context, numberOfProblems, 0, callback);
    }

    private static void showNextProblem(Context context, int total, int current, Callback callback) {
        if (current >= total) {
            callback.onSuccess();
            return;
        }

        MathHelper.Problem problem = MathHelper.generateProblem();

        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(context)
            .setTitle("Solve to Unlock (" + (current + 1) + "/" + total + ")")
            .setMessage(problem.question)
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Submit", (dialog, which) -> {
                try {
                    int answer = Integer.parseInt(input.getText().toString().trim());
                    if (answer == problem.answer) {
                        showNextProblem(context, total, current + 1, callback);
                    } else {
                        Toast.makeText(context, "Wrong answer!", Toast.LENGTH_SHORT).show();
                        callback.onFailure();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Invalid input!", Toast.LENGTH_SHORT).show();
                    callback.onFailure();
                }
            })
            .setNegativeButton("Cancel", (dialog, which) -> callback.onFailure())
            .show();
    }
}
