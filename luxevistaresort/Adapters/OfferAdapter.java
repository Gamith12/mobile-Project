package com.luxevistaresort.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luxevistaresort.Models.Offer;
import com.luxevistaresort.R;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {
    private List<Offer> offerList;

    public OfferAdapter(List<Offer> offerList) {
        this.offerList = offerList;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_offers, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        holder.image.setImageResource(offer.getImageResId());
        holder.title.setText(offer.getTitle());
        holder.description.setText(offer.getDescription());
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description;

        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_of);
            title = itemView.findViewById(R.id.title_of);
            description = itemView.findViewById(R.id.description_of);
        }
    }
}

