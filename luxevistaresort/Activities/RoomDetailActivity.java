package com.luxevistaresort.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.Models.Room;
import com.luxevistaresort.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RoomDetailActivity extends AppCompatActivity {

    private ImageView roomImage;
    private TextView roomTitle, roomPrice, roomDescription, roomRating;
    private TextView checkinDateText, checkoutDateText, adultsTextView, childrenTextView, roomsTextView;
    private Button btnBookNow, buttonDone, buttonCancel;
    private MaterialButtonToggleGroup hotelInfoToggleGroup;
    private ScrollView scrollContent;
    private GridLayout staticPhotoGrid;
    private NumberPicker numberPickerAdults, numberPickerChildren, numberPickerRooms;

    private int checkinYear, checkinMonth, checkinDay, checkinHour, checkinMinute;
    private int checkoutYear, checkoutMonth, checkoutDay, checkoutHour, checkoutMinute, roomId;
    private DBHelper dbHelper;
    private Room currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.single_room_detail_view);

        initViews();
        setupInsets();
        populateRoomDataFromIntent();
        setupBackButton();
        setupToggleGroup();
        setupDatePickers();
        setupGuestsDialogTrigger();
        setupBookNowButton();
    }

    private void initViews(){
        roomImage = findViewById(R.id.single_page_image);
        roomTitle = findViewById(R.id.title_name);
        roomPrice = findViewById(R.id.btn_price);
        roomDescription = findViewById(R.id.single_description);
        roomRating = findViewById(R.id.rating);

        btnBookNow = findViewById(R.id.bookNowButton);

        // --- Find Views for Tab Switching ---
        hotelInfoToggleGroup = findViewById(R.id.hotelInfoToggleGroup);
        scrollContent = findViewById(R.id.scrollContent);
        staticPhotoGrid = findViewById(R.id.staticPhotoGrid);

        checkinDateText = findViewById(R.id.checkinDateText);
        checkoutDateText = findViewById(R.id.checkoutDateText);

        // Initialize picker the views
        adultsTextView = findViewById(R.id.adultsTextView);
        childrenTextView = findViewById(R.id.childrenTextView);
        roomsTextView = findViewById(R.id.roomsTextView);

    }

    private void setupInsets(){
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void populateRoomDataFromIntent(){
        // --- Load Initial Data from Intent ---
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        roomId = intent.getIntExtra("room_id", -1);

        if (roomId != -1) {
            loadRoomDetails(roomId);
        } else {
            Toast.makeText(this, "Room not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadRoomDetails(int id) {
        currentRoom  = dbHelper.getRoomById(id);

        if (currentRoom  != null) {
            roomTitle.setText(currentRoom .getRoomTitle());
            roomPrice.setText("$" + currentRoom .getPrice() + " / night");
            roomDescription.setText(currentRoom .getShortDescription());

            int imageResId = getResources().getIdentifier(currentRoom .getImageUrl().replace(".png", ""), "drawable", this.getPackageName());
            if (imageResId != 0) {
                roomImage.setImageResource(imageResId);
            } else {
                roomImage.setImageResource(R.drawable.default_user); // fallback
            }
            roomRating.setText(currentRoom .getRating());
        }
    }

    private void setupBackButton(){
        ImageButton backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupToggleGroup(){
        // Check if views were found (important!)
        if (hotelInfoToggleGroup == null || scrollContent == null || staticPhotoGrid == null) {
            android.util.Log.e("RoomDetailActivity", "Error finding essential views for tab switching!");
            return;
        }

        hotelInfoToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            staticPhotoGrid.setVisibility(checkedId == R.id.photoButtonToggle ? View.VISIBLE : View.GONE);
            scrollContent.setVisibility(checkedId == R.id.reviewButtonToggle ? View.VISIBLE : View.GONE);
        });

        int initialCheckedId = hotelInfoToggleGroup.getCheckedButtonId();
        staticPhotoGrid.setVisibility(initialCheckedId == R.id.photoButtonToggle ? View.VISIBLE : View.GONE);
        scrollContent.setVisibility(initialCheckedId == R.id.reviewButtonToggle ? View.VISIBLE : View.GONE);
    }

    private void setupDatePickers() {
        findViewById(R.id.checkinDateLayout).setOnClickListener(v -> showDateTimePickerDialog(true));
        findViewById(R.id.checkoutDateLayout).setOnClickListener(v -> showDateTimePickerDialog(false));
    }

    // Function to show both Date and Time Picker Dialog
    private void showDateTimePickerDialog(final boolean isCheckin) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            String selectedDate = formatDate(year, month, day);
            showTimePickerDialog(isCheckin, selectedDate, year, month, day);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Function to show TimePicker Dialog
    private void showTimePickerDialog(final boolean isCheckin, final String selectedDate, final int year, final int month, final int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = formatTime(hourOfDay, minute1);
            String formattedDateTime = selectedDate + ", " + selectedTime;

            if (isCheckin) {
                checkinDateText.setText("Check-in: " + formattedDateTime);
                checkinYear = year;
                checkinMonth = month;
                checkinDay = dayOfMonth;
                checkinHour = hourOfDay;
                checkinMinute = minute1;
            } else {
                checkoutDateText.setText("Check-out: " + formattedDateTime);
                checkoutYear = year;
                checkoutMonth = month;
                checkoutDay = dayOfMonth;
                checkoutHour = hourOfDay;
                checkoutMinute = minute1;

                validateDates();
            }

        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void validateDates() {
        Calendar checkinCalendar = Calendar.getInstance();
        checkinCalendar.set(checkinYear, checkinMonth, checkinDay, checkinHour, checkinMinute);

        Calendar checkoutCalendar = Calendar.getInstance();
        checkoutCalendar.set(checkoutYear, checkoutMonth, checkoutDay, checkoutHour, checkoutMinute);

        if (checkoutCalendar.before(checkinCalendar)) {
            Toast.makeText(this, "Check-out date and time must be after check-in date and time!", Toast.LENGTH_SHORT).show();
            // Clear check-out date and time text if invalid
            checkoutDateText.setText("Check-out: Invalid");
        }
    }

    private void setupGuestsDialogTrigger() {
        findViewById(R.id.guestsLayout).setOnClickListener(v -> showGuestsDialog());
    }
    private void showGuestsDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_guests, null);

        numberPickerAdults = dialogView.findViewById(R.id.numberPickerAdults);
        numberPickerChildren = dialogView.findViewById(R.id.numberPickerChildren);
        numberPickerRooms = dialogView.findViewById(R.id.numberPickerRooms);

        numberPickerAdults.setMinValue(0);
        numberPickerAdults.setMaxValue(10);
        numberPickerChildren.setMinValue(0);
        numberPickerChildren.setMaxValue(10);
        numberPickerRooms.setMinValue(1);
        numberPickerRooms.setMaxValue(5);

        buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonDone = dialogView.findViewById(R.id.buttonDone);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        buttonDone.setOnClickListener(v -> {
            // Get the selected values from the number pickers
            int adults = numberPickerAdults.getValue();
            int children = numberPickerChildren.getValue();
            int rooms = numberPickerRooms.getValue();

            // Update the TextViews with the selected values
            adultsTextView.setText(adults + " Adults");
            childrenTextView.setText(children + " Children");
            roomsTextView.setText(rooms + " Room" + (rooms > 1 ? "s" : "")); // Handle plural for rooms

            // Dismiss the dialog after selecting
            dialog.dismiss();
        });

        dialog.show();
    }
    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String formatTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private int extractRoomCount() {
        String text = roomsTextView.getText().toString();
        if (text.isEmpty()) return 1;
        String[] parts = text.split(" ");
        try {
            return Integer.parseInt(parts[0]);
        } catch (Exception e) {
            return 1;
        }
    }

    private void setupBookNowButton() {
        btnBookNow.setOnClickListener(v -> {
            Calendar checkinCalendar = Calendar.getInstance();
            checkinCalendar.set(checkinYear, checkinMonth, checkinDay, checkinHour, checkinMinute);

            Calendar checkoutCalendar = Calendar.getInstance();
            checkoutCalendar.set(checkoutYear, checkoutMonth, checkoutDay, checkoutHour, checkoutMinute);

            if (checkoutCalendar.before(checkinCalendar)) {
                Toast.makeText(this, "Invalid check-out date!", Toast.LENGTH_SHORT).show();
                return;
            }

            long diffInMillis = checkoutCalendar.getTimeInMillis() - checkinCalendar.getTimeInMillis();
            long numberOfNights = diffInMillis / (1000 * 60 * 60 * 24);
            if (numberOfNights <= 0) {
                Toast.makeText(this, "Stay must be at least 1 night!", Toast.LENGTH_SHORT).show();
                return;
            }

            int numRooms = extractRoomCount();
            String title = roomTitle.getText().toString();
            String price = roomPrice.getText().toString();
            String rating = roomRating.getText().toString();
            String imageName = currentRoom .getImageUrl().replace(".png", "");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            String checkinFormatted = sdf.format(checkinCalendar.getTime());
            String checkoutFormatted = sdf.format(checkoutCalendar.getTime());

            Intent intent = new Intent(RoomDetailActivity.this, BookingConfirmationActivity.class);
            intent.putExtra("room_id", roomId);
            intent.putExtra("room_title", title);
            intent.putExtra("room_price", price);
            intent.putExtra("room_image", imageName);
            intent.putExtra("checkin_date", checkinFormatted);
            intent.putExtra("checkout_date", checkoutFormatted);
            intent.putExtra("num_nights", numberOfNights);
            intent.putExtra("num_rooms", numRooms);
            intent.putExtra("room_rating", rating);
            startActivity(intent);
        });
    }

}