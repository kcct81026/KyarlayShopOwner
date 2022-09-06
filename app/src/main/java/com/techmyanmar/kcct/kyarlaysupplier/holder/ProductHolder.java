package com.techmyanmar.kcct.kyarlaysupplier.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.techmyanmar.kcct.kyarlaysupplier.R;

public class ProductHolder extends RecyclerView.ViewHolder {

    public ImageView img;
    public TextView txtTitle, txtPrice, txtStatus, txtState;
    public LinearLayout layoutMain;
    public ImageView imgDelete;
    public SwitchCompat switchCompat;

    public ProductHolder(@NonNull View itemView) {
        super(itemView);

        img = itemView.findViewById(R.id.img);
        txtTitle = itemView.findViewById(R.id.txtTitle);
        txtPrice = itemView.findViewById(R.id.txtPrice);
        txtStatus = itemView.findViewById(R.id.txtStatus);
        txtState = itemView.findViewById(R.id.txtState);
        layoutMain = itemView.findViewById(R.id.layoutMain);
        imgDelete = itemView.findViewById(R.id.imgDelete);
        switchCompat = itemView.findViewById(R.id.switch_on_off);
    }
}
