package com.luxevistaresort.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.R;

public class ViewUsersActivity extends AppCompatActivity {

    private TextView usersTextView;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        usersTextView = findViewById(R.id.usersTextView);
        dbHelper = new DBHelper(this);

        displayAllUsers();
    }
    private void displayAllUsers() {
        Cursor cursor = dbHelper.getAllUsers();
        StringBuilder userData = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String roomType = cursor.getString(cursor.getColumnIndexOrThrow("roomType"));
                String numRooms = cursor.getString(cursor.getColumnIndexOrThrow("numRooms"));
                String nights = cursor.getString(cursor.getColumnIndexOrThrow("nights"));
                String totalCost = cursor.getString(cursor.getColumnIndexOrThrow("totalCost"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                userData.append("Email: ").append(email)
                        .append("\nRoom Type: ").append(roomType)
                        .append("\nNum Rooms: ").append(numRooms)
                        .append("\nNo Of Nights: ").append(nights)
                        .append("\nTotal Cost: ").append(totalCost)
                        .append("\nDate: ").append(date)
                        .append("\n\n");
            } while (cursor.moveToNext());
        } else {
            userData.append("No users found.");
        }

        cursor.close();
        usersTextView.setText(userData.toString());
    }
}