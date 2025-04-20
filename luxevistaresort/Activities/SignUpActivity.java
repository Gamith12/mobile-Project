package com.luxevistaresort.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, mobileNumberInput;
    private ImageView profileImageView, togglePasswordView;
    private Button signUpButton;
    private TextView signInLink;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath = "";
    private boolean isPasswordVisible = false;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DBHelper(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        usernameInput = findViewById(R.id.user_name);
        emailInput = findViewById(R.id.user_email);
        passwordInput = findViewById(R.id.user_password);
        mobileNumberInput = findViewById(R.id.mobile_number);
        signUpButton = findViewById(R.id.buttonCreateAccount);
        profileImageView = findViewById(R.id.imageViewProfilePic);
        togglePasswordView = findViewById(R.id.passViewButton);
        signInLink = findViewById(R.id.signupPageLogin);
    }

    private void setupListeners() {
        signUpButton.setOnClickListener(v -> handleSignUp());
        profileImageView.setOnClickListener(v -> openImagePicker());
        signInLink.setOnClickListener(v -> redirectToSignIn());
        togglePasswordView.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void handleSignUp() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String mobile = mobileNumberInput.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || mobile.isEmpty()) {
            showToast("Please fill all fields");
        } else if (!isValidPassword(password)) {
            showToast("Password must be at least 6 characters and include uppercase, lowercase, and a number");
        } else if (dbHelper.isUserExists(email)) {
            showToast("An account with this email already exists");
        } else {
            boolean inserted = dbHelper.insertUser(username, email, mobile, password, selectedImagePath);
            if (inserted) {
                showToast("Sign up successful!");
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            } else {
                showToast("Sign up failed!");
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordView.setImageResource(R.drawable.ic_eye_open);
        } else {
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordView.setImageResource(R.drawable.ic_eye_closed);
        }
        passwordInput.setSelection(passwordInput.getText().length()); // Keep cursor at end
    }

    private void redirectToSignIn() {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 &&
                password.matches(".*[A-Z].*") &&     // At least one uppercase
                password.matches(".*[a-z].*") &&     // At least one lowercase
                password.matches(".*\\d.*");         // At least one digit
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = selectedImageUri.toString();
            profileImageView.setImageURI(selectedImageUri);
        }
    }
}