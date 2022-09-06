package com.techmyanmar.kcct.kyarlaysupplier.operation;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techmyanmar.kcct.kyarlaysupplier.R;

public class ToastHelper implements ConstanceVariable {

    public static void showToast(AppCompatActivity activity, String message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View toastRoot = inflater.inflate(R.layout.layout_custom_toast, null);
        TextView textView = toastRoot.findViewById(R.id.toast_text);
        textView.setText(message);
        Toast toast = new Toast(activity);
        toast.setView(toastRoot);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }
}
