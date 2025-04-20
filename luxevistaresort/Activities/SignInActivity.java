package com.luxevistaresort.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.R;

public class SignInActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private ImageView viewPasswordButton;
    private boolean isPasswordVisible = false;

    private DBHelper dbHelper;
    private ProgressBar progressBar;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        initViews();
        dbHelper = new DBHelper(this);

        setupSignUpRedirect();
        setupLoginButton();
        setupPasswordToggle();
    }

    private void initViews() {
        emailInput = findViewById(R.id.user_email);
        passwordInput = findViewById(R.id.user_password);
        viewPasswordButton = findViewById(R.id.passViewButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSignUpRedirect() {
        TextView signUp = findViewById(R.id.loginPageSignup);
        signUp.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

    private void setupLoginButton() {
        loginButton = findViewById(R.id.buttonSignIn);
        loginButton.setOnClickListener(v -> handleLogin());
    }

    private void setupPasswordToggle() {
        viewPasswordButton.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                viewPasswordButton.setImageResource(R.drawable.ic_eye_open);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                viewPasswordButton.setImageResource(R.drawable.ic_eye_closed);
            }
            passwordInput.setSelection(passwordInput.getText().length()); // Move cursor to end
        });
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        new Handler().postDelayed(() -> {

            if (dbHelper.checkUser(email, password)) {
                saveUserSession(email);
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
            }
        }, 1500);
    }

    private void saveUserSession(String email) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_email", email);
        editor.apply();
    }
}