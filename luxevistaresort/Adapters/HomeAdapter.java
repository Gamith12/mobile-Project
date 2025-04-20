package com.luxevistaresort.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luxevistaresort.Models.Room;
import com.luxevistaresort.R;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<Room> roomList;

    public HomeAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.featured_rooms,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomTitle.setText(room.getRoomTitle());
        holder.price.setText(room.getPrice());
        holder.description.setText(room.getShortDescription());

        int imageResId = context.getResources().getIdentifier(room.getImageUrl().replace(".png", ""), "drawable", context.getPackageName());

        Glide.with(context)
                .load(imageResId)
                .placeholder(R.drawable.suites)
                .into(holder.roomImage);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView price;
        private TextView roomTitle;
        private TextView description;
        private ImageView roomImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            price = itemView.findViewById(R.id.price_fe);
            roomTitle = itemView.findViewById(R.id.room_title_fe);
            description = itemView.findViewById(R.id.description_fe);
            roomImage = itemView.findViewById(R.id.room_image_fe);
        }
    }
}