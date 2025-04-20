package com.luxevistaresort.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private DBHelper dbHelper;
    private String userEmail;

    private boolean isNewPasswordVisible = false;
    private boolean isOldPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private Button updateButton;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbHelper = new DBHelper(this);

        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        updateButton = findViewById(R.id.updateButton);
        progressBar = findViewById(R.id.progressBar);

        // Back button handler
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Get user email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

        updateButton.setOnClickListener(v -> updatePassword());

        ImageView toggleOldPassword = findViewById(R.id.toggleOldPassword);
        toggleOldPassword.setOnClickListener(v -> {
            isOldPasswordVisible = !isOldPasswordVisible;
            if (isOldPasswordVisible) {
                oldPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleOldPassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                oldPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleOldPassword.setImageResource(R.drawable.ic_eye_closed);
            }
            oldPasswordEditText.setSelection(oldPasswordEditText.getText().length()); // Keep cursor at end
        });

        ImageView toggleNewPassword = findViewById(R.id.toggleNewPassword);
        toggleNewPassword.setOnClickListener(v -> {
            isNewPasswordVisible = !isNewPasswordVisible;
            if (isNewPasswordVisible) {
                newPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleNewPassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                newPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleNewPassword.setImageResource(R.drawable.ic_eye_closed);
            }
            newPasswordEditText.setSelection(newPasswordEditText.getText().length());
        });

        ImageView ConfirmPasswordVisible = findViewById(R.id.toggleConfirmPassword);
        ConfirmPasswordVisible.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                confirmNewPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ConfirmPasswordVisible.setImageResource(R.drawable.ic_eye_open);
            } else {
                confirmNewPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ConfirmPasswordVisible.setImageResource(R.drawable.ic_eye_closed);
            }
            confirmNewPasswordEditText.setSelection(confirmNewPasswordEditText.getText().length()); // Keep cursor at end
        });
    }

    private void updatePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmNewPasswordEditText.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.matches(".*[A-Z].*") || !newPassword.matches(".*\\d.*") || !newPassword.matches(".*[@#$%^&+=!].*")) {
            Toast.makeText(this, "Password should contain at least one uppercase letter, number, and special character", Toast.LENGTH_LONG).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dbHelper.verifyUserPassword(userEmail, oldPassword)) {
            Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        updateButton.setEnabled(false);

        new Handler().postDelayed(() -> {
            boolean updated = dbHelper.updateUserPassword(userEmail, newPassword);

            progressBar.setVisibility(View.GONE);
            updateButton.setEnabled(true); // Re-enable button

            if (updated) {
                Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        }, 1500);

    }
}