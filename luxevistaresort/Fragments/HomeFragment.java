package com.luxevistaresort.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.luxevistaresort.Adapters.HomeAdapter;
import com.luxevistaresort.Adapters.ImageSliderAdapter;
import com.luxevistaresort.Adapters.OfferAdapter;
import com.luxevistaresort.Database.DBHelper;
import com.luxevistaresort.Models.Offer;
import com.luxevistaresort.Models.Room;
import com.luxevistaresort.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private List<Room> roomList;
    LinearLayout checkinDateLayout, checkoutDateLayout;
    TextView checkinDateText, checkoutDateText;
    Calendar checkinCalendar, checkoutCalendar;
    RecyclerView topDealRv;


    private CircleImageView profileImage;
    private TextView userName;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadUserProfile();
        setupRoomRecyclerView();

        RecyclerView offerRv = view.findViewById(R.id.offer_Rv);
        offerRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Offer> offerList = new ArrayList<>();
        offerList.add(new Offer(R.drawable.offer2, "Breakfast Included", "Fuel your day with breakfast included for every registered guest, each night of your stay."));
        offerList.add(new Offer(R.drawable.offer1, "Honors Discount Advance Purchase", "Save up to 17% off our Best Available Rate* by booking in advance. It pays to plan ahead!"));
        offerList.add(new Offer(R.drawable.offer3, "Experience the Stay", "Book our Experience the Stay package and indulge with an on-property credit and additional perks, like premium WiFi and early check-in."));

        OfferAdapter adapter = new OfferAdapter(offerList);
        offerRv.setAdapter(adapter);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabIndicator);

        List<Integer> imageList = Arrays.asList(R.drawable.pic2, R.drawable.pic3, R.drawable.pic4,R.drawable.pic1);

        ImageSliderAdapter adapters = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapters);

        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(0.25f + (1 - Math.abs(position)));
        });

        // Attach dot indicator
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {}
        ).attach();

        Handler handler = new Handler();
        int[] currentPosition = {0}; // Using array to access inside Runnable

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPosition[0] >= imageList.size()) {
                    currentPosition[0] = 0;
                }
                viewPager.setCurrentItem(currentPosition[0]++, true);
                handler.postDelayed(this, 3000); // 3 seconds
            }
        };
        handler.postDelayed(runnable, 3000);

        checkinDateLayout.setOnClickListener(v -> {
            showDatePicker(checkinCalendar, checkinDateText);
        });

        checkoutDateLayout.setOnClickListener(v -> {
            showDatePicker(checkoutCalendar, checkoutDateText);
        });

    }

    private void initViews(View view) {
        topDealRv  = view.findViewById(R.id.top_deal_Rv);
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);

        checkinDateLayout = view.findViewById(R.id.checkinDateLayout);
        checkoutDateLayout = view.findViewById(R.id.checkoutDateLayout);
        checkinDateText = view.findViewById(R.id.checkinDateText);
        checkoutDateText = view.findViewById(R.id.checkoutDateText);

        checkinCalendar = Calendar.getInstance();
        checkoutCalendar = Calendar.getInstance();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", null);

        if (userEmail != null) {
            DBHelper dbHelper = new DBHelper(requireContext());
            Cursor cursor = dbHelper.getUserByEmail(userEmail);

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                userName.setText("Hi " + name);

                if (imagePath != null && !imagePath.isEmpty()) {
                    Glide.with(this)
                            .load(imagePath)
                            .placeholder(R.drawable.profile)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.profile);
                }

                cursor.close();
            }
        }
    }

    private void setupRoomRecyclerView() {
        DBHelper dbHelper = new DBHelper(requireContext());

        List<Room> topRooms = dbHelper.getFeaturedRooms();

        HomeAdapter homeAdapter = new HomeAdapter(requireContext(),topRooms);
        topDealRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        topDealRv.setAdapter(homeAdapter);
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void showDatePicker(Calendar calendar, TextView targetTextView) {
        Context context = getContext();
        if (context == null) return;

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    targetTextView.setText(formatDate(calendar));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


}
