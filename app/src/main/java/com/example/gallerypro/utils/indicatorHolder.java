package com.example.gallerypro.utils;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerypro.R;

public class indicatorHolder extends RecyclerView.ViewHolder {

    public ImageView image;
    View positionController;

    indicatorHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.imageIndicator);
        positionController = itemView.findViewById(R.id.activeImage);
    }
}
