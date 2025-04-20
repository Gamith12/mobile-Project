package com.luxevistaresort.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.luxevistaresort.Adapters.RoomAdapter;
import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.Models.Room;
import com.luxevistaresort.R;
import com.luxevistaresort.Utils.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoomsFragment extends Fragment {

    private RecyclerView rvRooms;
    private RoomAdapter roomAdapter;
    private List<Room> fullRoomList = new ArrayList<>();
    private List<Room> filteredRoomList = new ArrayList<>();

    private Button btnFilter, btnSort;

    public RoomsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        initViews(view);
        setupRecyclerView();
        setupButtonListeners();

        RecyclerView rvRooms = view.findViewById(R.id.rv_rooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(getContext()));

        DBHelper dbHelper = new DBHelper(getContext());
        fullRoomList = dbHelper.getAllRooms(); // Save full list for filtering
        filteredRoomList = new ArrayList<>(fullRoomList); // Initialize filtered list

        roomAdapter = new RoomAdapter(getContext(), fullRoomList);
        rvRooms.setAdapter(roomAdapter);


        return view;
    }

    private void initViews(View view) {
        rvRooms = view.findViewById(R.id.rv_rooms);
        btnFilter = view.findViewById(R.id.btn_filter);
        btnSort = view.findViewById(R.id.btn_sort);
    }

    private void setupRecyclerView() {
        rvRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        int dividerColor = getResources().getColor(R.color.divider_color);
        int dividerHeight = 2;
        int margin = getResources().getDimensionPixelOffset(R.dimen.card_line_margin);
        rvRooms.addItemDecoration(new DividerItemDecoration(dividerColor, dividerHeight, margin));
    }

    private void setupButtonListeners() {
        btnFilter.setOnClickListener(v -> showFilterDialog());
        btnSort.setOnClickListener(v -> showSortDialog());
    }

    private void showFilterDialog() {
        String[] filters = {"All", "Suite", "Deluxe", "Executive","Superior"};

        new AlertDialog.Builder(getContext())
                .setTitle("Filter by Room Type")
                .setItems(filters, (dialog, which) -> applyFilter(filters[which]))
                .show();
    }

    private void applyFilter(String type) {
        if (type.equals("All")) {
            filteredRoomList = new ArrayList<>(fullRoomList);
        } else {
            filteredRoomList = new ArrayList<>();
            for (Room room : fullRoomList) {
                if (room.getRoomTitle().toLowerCase().contains(type.toLowerCase())) {
                    filteredRoomList.add(room);
                }
            }
        }
        roomAdapter.updateRoomList(filteredRoomList);
    }

    private void showSortDialog() {
        String[] options = {
                "Price: Low to High",
                "Price: High to Low",
                "Availability: Available First",
                "Availability: Unavailable First"
        };

        new AlertDialog.Builder(getContext())
                .setTitle("Sort by")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            sortRoomsByPrice(true);
                            break;
                        case 1:
                            sortRoomsByPrice(false);
                            break;
                        case 2:
                            sortRoomsByAvailability(true);
                            break;
                        case 3:
                            sortRoomsByAvailability(false);
                            break;
                    }
                })
                .show();
    }
    private void sortRoomsByAvailability(boolean availableFirst) {
        Collections.sort(filteredRoomList, (room1, room2) -> {
            if (availableFirst) {
                return room1.getAvailability().compareTo(room2.getAvailability());
            } else {
                return room2.getAvailability().compareTo(room1.getAvailability());
            }
        });

        roomAdapter.notifyDataSetChanged();
    }

    private void sortRoomsByPrice(boolean ascending) {
        Collections.sort(filteredRoomList, (r1, r2) -> {
            double price1 = r1.getNumericPrice();
            double price2 = r2.getNumericPrice();
            return ascending ? Double.compare(price1, price2) : Double.compare(price2, price1);
        });

        roomAdapter.updateRoomList(filteredRoomList);
    }

}
