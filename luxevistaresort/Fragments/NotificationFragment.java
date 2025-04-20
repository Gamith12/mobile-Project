package com.luxevistaresort.Fragments; // Use your actual package name

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.luxevistaresort.R; // Import your R file
import com.luxevistaresort.Adapters.NotificationsAdapter; // Import adapter
import com.luxevistaresort.Models.NotificationItem; // Import model

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<NotificationItem> notificationItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the RecyclerView in the inflated view
        recyclerView = view.findViewById(R.id.notificationsRecyclerView);

        // Setup Layout Manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Prepare data (replace with your actual data fetching)
        prepareNotificationData();

        // Create and set the adapter
        adapter = new NotificationsAdapter(notificationItems);
        recyclerView.setAdapter(adapter);

    }

    // Example method to create sample data
    private void prepareNotificationData() {
        notificationItems = new ArrayList<>();

        notificationItems.add(new NotificationItem("Your room booking in King Deluxe has been successful", "20 April 2025"));
        notificationItems.add(new NotificationItem("Reminder: Check-out is tomorrow at 11 AM.", "22 April 2025"));
        notificationItems.add(new NotificationItem("Special offer unlocked for your next stay!", "23 April 2025"));
        notificationItems.add(new NotificationItem("System maintenance scheduled tonight.", "24 April 2025"));
    }

}