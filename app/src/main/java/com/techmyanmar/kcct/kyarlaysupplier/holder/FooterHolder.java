package com.techmyanmar.kcct.kyarlaysupplier.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.techmyanmar.kcct.kyarlaysupplier.R;

public class FooterHolder extends RecyclerView.ViewHolder {

    public TextView space;
    public LinearLayout layout_first_time;
    public TextView txtFirstTime;

    public FooterHolder(View itemView) {
        super(itemView);
        space = (TextView) itemView.findViewById(R.id.cart_detail_foooter);


    }
}

