package com.luxevistaresort.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.Fragments.ProfileFragment;
import com.luxevistaresort.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingConfirmationActivity extends AppCompatActivity {

    private ImageView bookingRoomImage;
    private TextView hotelNameTextView, valueRoomType, valueNumRooms, valueRoomCost, valueTotal;
    private TextView valueGuestName, valueGuestEmail, valueGuestMobile, noOfNights, rating;
    private Button confirmButton;
    private ImageButton backBtn;
    private String userEmail;
    private int roomImageRes;

    private int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_confirmation);

        initViews();
        getUserDetails();
        loadDataFromIntent();
        setupBackButton();
        setupconfirmButton();

    }

    private void initViews() {
        // Get User Details
        valueGuestName = findViewById(R.id.value_guest_name);
        valueGuestEmail = findViewById(R.id.value_guest_email);
        valueGuestMobile = findViewById(R.id.value_guest_mobile);

        bookingRoomImage = findViewById(R.id.booking_room_image);
        hotelNameTextView = findViewById(R.id.hotelNameTextView);
        rating = findViewById(R.id.ratingTextView);

        valueRoomType = findViewById(R.id.value_room_type);
        valueNumRooms = findViewById(R.id.value_num_rooms);
        valueRoomCost = findViewById(R.id.value_room_cost);
        valueTotal = findViewById(R.id.value_total);
        noOfNights = findViewById(R.id.value_no_of_nights);

        confirmButton = findViewById(R.id.confirmButton);

    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        // Room data
        String roomTitle = getIntent().getStringExtra("room_title");
        String roomType = getIntent().getStringExtra("room_type");
        String roomPricePerNight = getIntent().getStringExtra("room_price");
        String ratings = getIntent().getStringExtra("room_rating");
        long numNights = getIntent().getLongExtra("num_nights", 1);
        int numRooms = getIntent().getIntExtra("num_rooms", 1);

        String imageName = getIntent().getStringExtra("room_image");
        if (imageName != null) {
            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            bookingRoomImage.setImageResource(resId);
        } else {
            bookingRoomImage.setImageResource(R.drawable.default_user); // fallback
        }


        // Populate views
        hotelNameTextView.setText(roomTitle);
        rating.setText(ratings);
        valueRoomType.setText(roomType != null ? roomType : "Standard Room");
        valueNumRooms.setText(String.valueOf(numRooms));
        noOfNights.setText(String.valueOf(numNights));

        // Price calculation
        double pricePerNight = Double.parseDouble(roomPricePerNight.replaceAll("[^\\d.]", ""));
        double totalRoomCost = pricePerNight * numNights;
        double totalAmount = totalRoomCost;


        valueRoomCost.setText(numNights + " Nights ($" + pricePerNight + " x " + numNights + " = $" + totalRoomCost + ")");
        valueTotal.setText("$" + totalAmount);

    }

    private void getUserDetails() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getUserByEmail(userEmail);

        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));

            valueGuestName.setText(username);
            valueGuestEmail.setText(email);
            valueGuestMobile.setText(number);

            cursor.close();
        } else {
            valueGuestName.setText("N/A");
            valueGuestEmail.setText("N/A");
            valueGuestMobile.setText("N/A");
        }
    }

    private void setupBackButton() {
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupconfirmButton() {
        confirmButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Booking Confirmed")
                    .setMessage("Your booking has been successfully confirmed!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        saveBookingToHistory(); // Save to DB
                        Intent intent = new Intent(BookingConfirmationActivity.this, MainActivity.class);
                        intent.putExtra("openProfile", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        });

    }

    private void saveBookingToHistory() {
        DBHelper dbHelper = new DBHelper(this);

        int roomId = getIntent().getIntExtra("room_id", -1);

        if (roomId == -1 || userId == 0) {
            Toast.makeText(this, "Error: Missing user or room info", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        int numRooms = Integer.parseInt(valueNumRooms.getText().toString());
        String totalCost = valueTotal.getText().toString();
        int nights = Integer.parseInt(noOfNights.getText().toString());
        String checkinDate = getIntent().getStringExtra("checkin_date");
        String checkoutDate = getIntent().getStringExtra("checkout_date");

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        dbHelper.insertBooking(userId, roomId, numRooms, nights, totalCost, checkinDate, checkoutDate, date);

        dbHelper.updateRoomAvailability(roomId, "0");
    }


}