package com.luxevistaresort.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.luxevistaresort.Models.NotificationItem;
import com.luxevistaresort.R;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<NotificationItem> notificationList;

    // Constructor to receive the data list
    public NotificationsAdapter(List<NotificationItem> notificationList) {
        this.notificationList = notificationList;
    }

    // Inflates the item layout and creates the ViewHolder
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_notification, parent, false);
        return new NotificationViewHolder(itemView);
    }

    // Binds the data from the list to the views in the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        NotificationItem currentItem = notificationList.get(position);
        holder.bind(currentItem);
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        return notificationList == null ? 0 : notificationList.size();
    }

    // Updates the list data and notifies the adapter
    public void updateData(List<NotificationItem> newNotificationList) {
        this.notificationList = newNotificationList;
        notifyDataSetChanged();
    }


    // --- ViewHolder Inner Class ---
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views by their IDs from list_item_notification.xml
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDate = itemView.findViewById(R.id.textViewDate);

            // Optional: Set an OnClickListener for the item view
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                }
            });
        }

        // Helper method to bind data to the views
        public void bind(NotificationItem notificationItem) {
            textViewMessage.setText(notificationItem.getMessage());
            textViewDate.setText(notificationItem.getDate());
        }
    }
}