package com.luxevistaresort.Adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.luxevistaresort.Models.ServiceReservation;
import com.luxevistaresort.R;

import java.util.List;

public class ServiceHistoryAdapter extends RecyclerView.Adapter<ServiceHistoryAdapter.ViewHolder> {

    private List<ServiceReservation> serviceList;
    CardView cardView;

    public ServiceHistoryAdapter(List<ServiceReservation> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_service_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHistoryAdapter.ViewHolder holder, int position) {
        ServiceReservation reservation = serviceList.get(position);
        holder.serviceType.setText("Service Type: "+reservation.getServiceType());
        holder.serviceDate.setText("Date and Time: "+reservation.getDate() + " at " + reservation.getTime());
        holder.guestName.setText("Guest: " + reservation.getGuestName());

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
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceType, serviceDate, guestName;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceType = itemView.findViewById(R.id.text_service_type);
            serviceDate = itemView.findViewById(R.id.text_service_date);
            guestName = itemView.findViewById(R.id.text_guest_name);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
