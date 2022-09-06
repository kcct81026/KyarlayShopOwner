package com.techmyanmar.kcct.kyarlaysupplier.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techmyanmar.kcct.kyarlaysupplier.R;

public class RecyclerTitleHolder extends RecyclerView.ViewHolder {

    public TextView title;

    public RecyclerTitleHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
    }
}
