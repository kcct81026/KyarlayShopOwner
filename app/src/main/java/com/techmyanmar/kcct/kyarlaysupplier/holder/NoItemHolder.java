package com.techmyanmar.kcct.kyarlaysupplier.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techmyanmar.kcct.kyarlaysupplier.R;

public class NoItemHolder  extends RecyclerView.ViewHolder {

    public TextView textView;

    public NoItemHolder(@NonNull View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.cart_detail_foooter);
    }
}
