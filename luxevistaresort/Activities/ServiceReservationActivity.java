package com.luxevistaresort.Activities;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.EventDay;
import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.Models.ServiceReservation;
import com.luxevistaresort.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ServiceReservationActivity extends AppCompatActivity {

    private Spinner serviceTypeSpinner, timeSlotSpinner;
    private TextView selectedDateText, guestNameInput;
    private CalendarView calendarView;
    private Button reserveButton;
    private List<Calendar> reservedDates = new ArrayList<>();
    private String selectedDate;
    private Integer userId;
    private EventDay selectedEvent = null;
    private List<EventDay> allEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_reservation);

        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());

        initViews();


        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "Guest");

        if (userEmail != null) {
            DBHelper dbHelper = new DBHelper(this);
            Cursor cursor = dbHelper.getUserByEmail(userEmail);

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                guestNameInput.setText(name);

                cursor.close();
            }
        }

        // Populate spinners
        ArrayAdapter<CharSequence> serviceAdapter = ArrayAdapter.createFromResource(this,
                R.array.service_types, android.R.layout.simple_spinner_item);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceTypeSpinner.setAdapter(serviceAdapter);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this,
                R.array.time_slots, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(timeAdapter);

        markReservedDates();

        // Calendar selection logic
        calendarView.setOnCalendarDayClickListener(eventDay -> {
            Calendar clickedDay = eventDay.getCalendar();

            for (Calendar reserved : reservedDates) {
                if (sameDay(reserved, clickedDay)) {
                    Toast.makeText(this, "This date is already reserved!", Toast.LENGTH_SHORT).show();
                    selectedDate = null;
                    selectedDateText.setText("Selected Date: None");
                    return;
                }
            }

            try {
                calendarView.setDate(clickedDay);
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
                return;
            }

            // Remove previous selection if exists
            if (selectedEvent != null) {
                allEvents.remove(selectedEvent);
            }

            // Add new selected event
            selectedEvent = new EventDay((Calendar) clickedDay.clone(), R.drawable.ic_selected_date);
            allEvents.add(selectedEvent);
            calendarView.setEvents(allEvents);

            int day = clickedDay.get(Calendar.DAY_OF_MONTH);
            int month = clickedDay.get(Calendar.MONTH) + 1;
            int year = clickedDay.get(Calendar.YEAR);
            selectedDate = day + "/" + month + "/" + year;
            selectedDateText.setText("Selected Date: " + selectedDate);

            calendarView.setMinimumDate(Calendar.getInstance());
        });


        // Reserve logic
        reserveButton.setOnClickListener(v -> {
            String service = serviceTypeSpinner.getSelectedItem().toString();
            String time = timeSlotSpinner.getSelectedItem().toString();
            String guestName = guestNameInput.getText().toString();

            if (selectedDate == null) {
                Toast.makeText(this, "Please select date", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper db = new DBHelper(this);

            if (db.isServiceAvailable(service, selectedDate, time)) {
                db.addServiceReservation(new ServiceReservation(userId,service, selectedDate, time, guestName), userId);
                Toast.makeText(this, "Reservation Confirmed!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Slot Unavailable. Choose another time.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initViews() {
        serviceTypeSpinner = findViewById(R.id.spinner_service_type);
        timeSlotSpinner = findViewById(R.id.spinner_time_slot);
        selectedDateText = findViewById(R.id.text_selected_date);
        reserveButton = findViewById(R.id.btn_reserve);
        guestNameInput = findViewById(R.id.input_guest_name);
        calendarView = findViewById(R.id.calendarView);
    }

    private boolean sameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private void markReservedDates() {
        DBHelper db = new DBHelper(this);
        List<ServiceReservation> reservations = db.getAllServiceReservations();

        for (ServiceReservation res : reservations) {
            Calendar cal = Calendar.getInstance();
            String[] parts = res.getDate().split("/");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int year = Integer.parseInt(parts[2]);
                cal.set(year, month, day);
                reservedDates.add((Calendar) cal.clone());
                allEvents.add(new EventDay((Calendar) cal.clone(), R.drawable.ic_reserved));
            }
        }

        calendarView.setEvents(allEvents);
    }


}