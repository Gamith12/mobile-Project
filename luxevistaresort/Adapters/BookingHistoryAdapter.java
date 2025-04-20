package com.luxevistaresort.Adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luxevistaresort.Models.Booking;
import com.luxevistaresort.R;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private Context context;

    public BookingHistoryAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.roomTitle.setText(booking.getRoomTitle());
        holder.numNights.setText(booking.getNights() + " night(s)");
        holder.bookedDate.setText("Booked on: " + booking.getBookedDate());
        holder.roomPrice.setText(booking.getRoomPrice());

        int imageResId = holder.itemView.getContext().getResources()
                .getIdentifier(booking.getRoomImageUrl().replace(".png", ""), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(imageResId)
                .placeholder(R.drawable.suites)
                .into(holder.roomImage);



        holder.cardView.setOnClickListener(v -> {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.95f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.95f, 1f);
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(300);
            animatorSet.start();

            // Optional: open detail activity after animation
            v.postDelayed(() -> {
                // Intent logic if needed
            }, 150);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImage;
        TextView roomTitle, bookedDate, roomPrice, numNights;
//        Button btnBookAgain;
        CardView cardView;

        public BookingViewHolder(View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.room_history_image);
            roomTitle = itemView.findViewById(R.id.room_history_title);
            bookedDate = itemView.findViewById(R.id.booked_date);
            roomPrice = itemView.findViewById(R.id.room_history_price);
            cardView = itemView.findViewById(R.id.booking_card);
//            btnBookAgain = itemView.findViewById(R.id.btn_view_details);
            numNights = itemView.findViewById(R.id.room_nights);
        }
    }
}
