package com.luxevistaresort.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luxevistaresort.Activities.RoomDetailActivity;
import com.luxevistaresort.Models.Room; 
import com.luxevistaresort.R;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<Room> roomList;
    private Context context;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rooms_card, parent, false);
        return new RoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {

        Room room = roomList.get(position);
        holder.name.setText(room.getRoomTitle());
        holder.price.setText("$" + room.getPrice());
        holder.description.setText(room.getShortDescription());

        int imageId = context.getResources().getIdentifier(room.getImageUrl().replace(".png", ""), "drawable", context.getPackageName());
        holder.image.setImageResource(imageId);

        // GRAY OUT if unavailable
        if ("0".equals(room.getAvailability())) {
            holder.itemView.setAlpha(0.5f); // Makes it visually faded
            holder.itemView.setEnabled(true); // Disable click if needed
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setEnabled(true);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!"0".equals(room.getAvailability())) {
                // Only navigate if room is available
                Intent intent = new Intent(context, RoomDetailActivity.class);
                intent.putExtra("room_id", room.getId());
                context.startActivity(intent);
            }
        });

        if ("0".equals(room.getAvailability())) {
            holder.itemView.setAlpha(0.5f);
            holder.itemView.setEnabled(false);
            holder.unavailableBadge.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setEnabled(true);
            holder.unavailableBadge.setVisibility(View.GONE);
        }


        // Set onClickListener for the room item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra("room_id", room.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void updateRoomList(List<Room> newRoomList) {
        this.roomList = new ArrayList<>(newRoomList);
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description, unavailableBadge;
        ImageView image;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.room_title);
            price = itemView.findViewById(R.id.room_price);
            image = itemView.findViewById(R.id.room_image);
            description = itemView.findViewById(R.id.room_short_description);
            unavailableBadge = itemView.findViewById(R.id.unavailable_badge);

        }
    }



}