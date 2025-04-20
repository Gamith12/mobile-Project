package com.luxevistaresort.Activities;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editName, editEmail, editNumber;
    private Button updateButton;
    private DBHelper dbHelper;
    private String originalEmail;

    TextView deleteAccountButton;

    private ImageView profileImage;
    private String selectedImagePath = "";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DBHelper(this);

        editName = findViewById(R.id.editTextFullName);
        editEmail = findViewById(R.id.editTextEmail);
        editNumber = findViewById(R.id.editTextMobileNumber);
        updateButton = findViewById(R.id.buttonUpdate);
        profileImage = findViewById(R.id.imageViewProfilePic);
        deleteAccountButton = findViewById(R.id.delete_account);
        progressBar = findViewById(R.id.progressBar);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        originalEmail = prefs.getString("user_email", null);

        if (originalEmail != null) {
            Cursor cursor = dbHelper.getUserByEmail(originalEmail);
            if (cursor != null && cursor.moveToFirst()) {
                editName.setText(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                editEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
                editNumber.setText(cursor.getString(cursor.getColumnIndexOrThrow("number")));
                selectedImagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                    Glide.with(this)
                            .load(Uri.parse(selectedImagePath))
                            .placeholder(R.drawable.default_user)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.default_user);
                }
                cursor.close();
            }
        }

        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        // Update Button
        updateButton.setOnClickListener(v -> {
            String newName = editName.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();
            String newNumber = editNumber.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty() || newNumber.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            updateButton.setEnabled(false);

            new Handler().postDelayed(() -> {

            boolean success = dbHelper.updateUser(originalEmail, newName, newEmail, newNumber, selectedImagePath);

            if (success) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_email", newEmail);
                editor.apply();

                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show();
            }

            }, 1500);
        });


        profileImage.setOnClickListener(v -> {
            Intent imageIntent  = new Intent(Intent.ACTION_PICK);
            imageIntent.setType("image/*");
            startActivityForResult(imageIntent, PICK_IMAGE_REQUEST);
        });

        deleteAccountButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Yes", (dialog, which) -> deleteAccount())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            selectedImagePath = imageUri.toString();

            Glide.with(this).load(imageUri).into(profileImage);
        }
    }

    private void deleteAccount() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", null);

        progressBar.setVisibility(View.VISIBLE);
        updateButton.setEnabled(false);

        new Handler().postDelayed(() -> {

        if (userEmail != null) {
            dbHelper = new DBHelper(this);
            dbHelper.deleteUserByEmail(userEmail); // delete user and related records

            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Account deleted.", Toast.LENGTH_SHORT).show();

            // Navigate to login activity
            Intent intent = new Intent(this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        }, 1500);
    }

}