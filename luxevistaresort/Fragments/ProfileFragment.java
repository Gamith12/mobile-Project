package com.luxevistaresort.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luxevistaresort.Activities.ChangePasswordActivity;
import com.luxevistaresort.Activities.EditProfileActivity;
import com.luxevistaresort.Activities.SignInActivity;
import com.luxevistaresort.Adapters.BookingHistoryAdapter;
import com.luxevistaresort.Adapters.ServiceHistoryAdapter;
import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.Models.Booking;
import com.luxevistaresort.Models.ServiceReservation;
import com.luxevistaresort.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView usernameView, emailView, numberView;
    private DBHelper dbHelper;

    private ImageView profileImageView;

    private ImageButton menuButton;

    private RecyclerView bookingHistoryRecyclerView;

    private BookingHistoryAdapter bookingHistoryAdapter;

    private ArrayList<Booking> bookingHistory;
    private ItemTouchHelper itemTouchHelper;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Reload user data when fragment resumes
        loadBookingHistory();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameView = view.findViewById(R.id.userName);
        emailView = view.findViewById(R.id.userEmail);
        numberView = view.findViewById(R.id.userNumber);
        profileImageView = view.findViewById(R.id.profileImageView);
        menuButton = view.findViewById(R.id.moreOptionsButton);
        bookingHistoryRecyclerView = view.findViewById(R.id.bookingHistoryRecyclerView);
        FloatingActionButton editFab = view.findViewById(R.id.editFab);

        dbHelper = new DBHelper(requireContext());
        bookingHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        editFab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });

        menuButton.setOnClickListener(v -> showProfilePopupMenu(v));

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.bookingHistoryToggleGroup);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonRooms) {
                    // Show room booking history
                    loadBookingHistory();
                } else if (checkedId == R.id.buttonServices) {
                    // Show service reservation history
                    showServiceBookings(view);
                }
            }
        });

    }

    private void loadUserData() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String email = prefs.getString("user_email", null);

        if (email != null) {
            Cursor cursor = dbHelper.getUserByEmail(email);
            if (cursor != null && cursor.moveToFirst()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                usernameView.setText(username);
                emailView.setText(email);
                numberView.setText(number);

                if (imagePath != null && !imagePath.isEmpty()) {
                    Glide.with(this)
                            .load(Uri.parse(imagePath))
                            .placeholder(R.drawable.default_user)
                            .into(profileImageView);
                } else {
                    profileImageView.setImageResource(R.drawable.default_user);
                }
                cursor.close();
            }
        }
    }

    private void showProfilePopupMenu(View anchorView) {
        // Inflate the custom popup layout
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_profile_menu, null);

        // Create the PopupWindow
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // Allows dismissal by tapping outside
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Set background for proper dismissal behavior
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        // Find menu items inside the popup layout
        TextView changePasswordOption = popupView.findViewById(R.id.popup_change_password);
        TextView logoutOption = popupView.findViewById(R.id.logoutButton);

        changePasswordOption.setOnClickListener(item -> {
            Toast.makeText(getContext(), "Change Password clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
            startActivity(intent);
            popupWindow.dismiss();
        });

        logoutOption.setOnClickListener(item -> {
            popupWindow.dismiss();

            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_logout_dialog, null);

            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setView(dialogView)
                    .create();

            Button btnYes = dialogView.findViewById(R.id.btnYes);
            Button btnCancel = dialogView.findViewById(R.id.btnCancel);

            btnYes.setOnClickListener(v -> {
                SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            btnCancel.setOnClickListener(v -> dialog.dismiss());

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }

            dialog.show();
        });
        // Show the popup window anchored to the menu button
        popupWindow.showAsDropDown(anchorView, 0, 0);
    }

    private void loadBookingHistory() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", null);

        if (userEmail != null) {
            int userId = dbHelper.getUserIdByEmail(userEmail);

            if (userId != -1) {
                List<Booking> bookingList = dbHelper.getBookingsByUser(userId);
                bookingHistory = new ArrayList<>(bookingList);

                bookingHistoryAdapter = new BookingHistoryAdapter(bookingHistory);
                bookingHistoryRecyclerView.setAdapter(bookingHistoryAdapter);

                itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Booking booking = bookingHistory.get(position);

                        if (booking.getStatus() == 1) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Cancel Booking")
                                    .setMessage("Are you sure you want to cancel this booking?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        dbHelper.updateRoomAvailability(booking.getRoomId(), "1");
                                        dbHelper.updateBookingStatus(booking.getId(), 0);
                                        bookingHistory.remove(position);
                                        bookingHistoryAdapter.notifyItemRemoved(position);
                                        Toast.makeText(getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        bookingHistoryAdapter.notifyItemChanged(position);
                                        dialog.dismiss();
                                    })
                                    .setCancelable(false)
                                    .show();
                        } else {
                            bookingHistoryAdapter.notifyItemChanged(position);
                            Toast.makeText(getContext(), "Only current bookings can be cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // 🎨 Customize swipe background and icon
                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                            int actionState, boolean isCurrentlyActive) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                        View itemView = viewHolder.itemView;
                        Paint paint = new Paint();
                        paint.setColor(Color.parseColor("#FF3743"));

                        if (dX < 0) { // Swiping to left
                            // Draw red background
                            c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                    (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                            // Draw delete icon
                            Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete_forever); // Replace with your icon
                            if (icon != null) {
                                int iconSize = 88;
                                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                                int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
                                int iconBottom = iconTop + iconSize;
                                int iconRight = itemView.getRight() - (itemView.getHeight() - iconSize) / 2;
                                int iconLeft = iconRight - iconSize;

                                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                                icon.draw(c);
                            }
                        }
                    }
                });
                itemTouchHelper.attachToRecyclerView(bookingHistoryRecyclerView);

            }
        }
    }



    private void showServiceBookings(View view) {
        // Get user ID
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", null);

        int userId = -1;
        if (userEmail != null) {
            DBHelper dbHelper = new DBHelper(getContext());
            Cursor cursor = dbHelper.getUserByEmail(userEmail);
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                cursor.close();
            }
        }

        // Now show bookings if userId is valid
        if (userId != -1) {
            RecyclerView recyclerView = view.findViewById(R.id.bookingHistoryRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            DBHelper dbHelper = new DBHelper(getContext());
            List<ServiceReservation> serviceList = dbHelper.getServiceReservationsByUserId(userId);

            ServiceHistoryAdapter adapter = new ServiceHistoryAdapter(serviceList);

            if (itemTouchHelper != null) {
                itemTouchHelper.attachToRecyclerView(null);
            }

            recyclerView.setAdapter(adapter);
        }
    }



}