package com.techmyanmar.kcct.kyarlaysupplier.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.techmyanmar.kcct.kyarlaysupplier.R;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ConstanceVariable;
import com.techmyanmar.kcct.kyarlaysupplier.operation.LocaleHelper;
import com.techmyanmar.kcct.kyarlaysupplier.operation.MyPreference;


public class EditProfileActivity extends AppCompatActivity implements ConstanceVariable {

    private static final String TAG = "EditProfileActivity";

    MyPreference prefs;
    AppCompatActivity activity;
    Resources resources;
    Display display;


    LinearLayout  layoutLanguage, layoutLogout, layoutBack, layoutPassword;
    TextView title, txtLanguage, txtLogout,txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_profile);



        activity = EditProfileActivity.this;
        prefs = new MyPreference(activity);
        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();
        display = activity.getWindowManager().getDefaultDisplay();

        layoutLanguage = findViewById(R.id.layoutLanguage);
        layoutLogout = findViewById(R.id.layoutLogout);
        layoutBack = findViewById(R.id.layoutBack);
        layoutPassword = findViewById(R.id.layoutPassword);
        title = findViewById(R.id.title);

        txtLanguage = findViewById(R.id.txtLanguage);
        txtLogout = findViewById(R.id.txtLogout);
        txtPassword = findViewById(R.id.txtPassword);

        title.setText(resources.getString(R.string.profile));
        txtLogout.setText(resources.getString(R.string.logout));
        txtLanguage.setText(resources.getString(R.string.change_language));
        txtPassword.setText(resources.getString(R.string.change_password));


        layoutLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage();
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prefs.getBooleanPreference(SP_LANGUAGE_CHANGE)){
                    finish();
                }

                else{
                    Intent intent = new Intent(activity,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        layoutPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, OtpActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!prefs.getBooleanPreference(SP_LANGUAGE_CHANGE)){
            finish();
        }

        else{
            Intent intent = new Intent(activity,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void logout(){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width   = display.getWidth();
        window.setAttributes(wlp);

        TextView title = dialog.findViewById(R.id.title);
        TextView txtBody = dialog.findViewById(R.id.txtBody);
        Button dialog_delete_cancel = dialog.findViewById(R.id.dialog_delete_cancel);
        Button dialog_delete_confirm = dialog.findViewById(R.id.dialog_delete_confirm);

        title.setText(resources.getString(R.string.logout));
        txtBody.setText(resources.getString(R.string.logout_confirm_title));
        dialog_delete_cancel.setText(resources.getString(R.string.cancel));
        dialog_delete_confirm.setText(resources.getString(R.string.confirm));

        dialog_delete_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog_delete_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


                activity.finishAffinity();
                prefs.clearAll();
                Intent intent = new Intent(activity,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    private void changeLanguage(){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_language_dialog);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width   = display.getWidth();
        window.setAttributes(wlp);

        TextView myanmar      = (TextView) dialog.findViewById(R.id.myanmar);
        TextView engilsh    = (TextView) dialog.findViewById(R.id.english);
        RadioButton rmyanmar         = (RadioButton) dialog.findViewById(R.id.radiomyanmar);
        RadioButton renglish       = (RadioButton) dialog.findViewById(R.id.radioenglish);
        Button next           = (Button) dialog.findViewById(R.id.next);
        next.setVisibility(View.GONE);

        if(prefs.getStringPreferences(LANGUAGE).equals(LANGUAGE_MYANMAR)) {
            rmyanmar.setChecked(true);
            renglish.setChecked(false);
        }else if(prefs.getStringPreferences(LANGUAGE).equals(LANGUAGE_ENGLISH)) {
            rmyanmar.setChecked(false);
            renglish.setChecked(true);
        }else  {
            rmyanmar.setChecked(true);
            renglish.setChecked(false);
        }

        myanmar.setText(resources.getString(R.string.setting_myanmar));
        engilsh.setText(resources.getString(R.string.setting_english));

        myanmar.setOnClickListener(new EditProfileActivity.dismissDialog(dialog, LANGUAGE_MYANMAR));
        engilsh.setOnClickListener(new EditProfileActivity.dismissDialog(dialog, LANGUAGE_ENGLISH));
        rmyanmar.setOnClickListener(new EditProfileActivity.dismissDialog(dialog,  LANGUAGE_MYANMAR));
        renglish.setOnClickListener(new EditProfileActivity.dismissDialog(dialog, LANGUAGE_ENGLISH));


        dialog.show();
    }

    public  class dismissDialog implements View.OnClickListener{
        Dialog dialog;
        String result;

        public dismissDialog(Dialog dialog, String result) {
            this.result = result;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {

            dialog.dismiss();
            prefs.saveStringPreferences(LANGUAGE, result);


            if (prefs.getStringPreferences(LANGUAGE).equals(LANGUAGE_ENGLISH)){
                prefs.saveStringPreferences(LANGUAGE, LANGUAGE_ENGLISH);


            }
            else{
                prefs.saveStringPreferences(LANGUAGE, LANGUAGE_MYANMAR);
            }

            prefs.saveBooleanPreference(SP_LANGUAGE_CHANGE,true);

            activity.recreate();

        }
    }
}
