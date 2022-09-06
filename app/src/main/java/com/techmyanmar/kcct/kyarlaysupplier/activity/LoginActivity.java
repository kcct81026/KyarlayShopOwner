package com.techmyanmar.kcct.kyarlaysupplier.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techmyanmar.kcct.kyarlaysupplier.R;
import com.techmyanmar.kcct.kyarlaysupplier.operation.AppController;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ConstanceVariable;
import com.techmyanmar.kcct.kyarlaysupplier.operation.Constant;
import com.techmyanmar.kcct.kyarlaysupplier.operation.LocaleHelper;
import com.techmyanmar.kcct.kyarlaysupplier.operation.MyPreference;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements Constant, ConstanceVariable {

    private static final String TAG = "LoginActivity";

    private AppCompatActivity activity;
    private Resources resources;
    private MyPreference prefs;
    private Boolean isPasswordVisbile = false;

    private TextView txtTitlePhNo,txtPhNoReminder,txtTitlePassword,txtPasswordReminder, txtLogin;
    private EditText edPhoneNumber,edPassword;
    private ImageView imgPasswordInvisible;
    private LinearLayout layoutPhoneNumber, layoutPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        activity = this;
        prefs = new MyPreference(activity);
        if(prefs.getStringPreferences(LANGUAGE) == ""){
            prefs.saveStringPreferences(LANGUAGE, LANGUAGE_MYANMAR);
        }

        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();

        txtTitlePhNo = findViewById(R.id.txtTitlePhNo);
        txtPhNoReminder = findViewById(R.id.txtPhNoReminder);
        txtTitlePassword = findViewById(R.id.txtTitlePassword);
        txtPasswordReminder = findViewById(R.id.txtPasswordReminder);
        txtLogin = findViewById(R.id.txtLogin);
        edPhoneNumber = findViewById(R.id.edPhoneNumber);
        edPassword = findViewById(R.id.edPassword);
        imgPasswordInvisible = findViewById(R.id.imgPasswordInvisible);
        layoutPhoneNumber = findViewById(R.id.layoutPhoneNumber);
        layoutPassword = findViewById(R.id.layoutPassword);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edPhoneNumber.getText().toString().trim().isEmpty()){
                    layoutPhoneNumber.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtPhNoReminder.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else if(edPassword.getText().toString().trim().isEmpty()){
                    layoutPassword.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtPasswordReminder.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else{
                    layoutPassword.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                    txtPasswordReminder.setTextColor(activity.getResources().getColor(R.color.text_gray));
                    layoutPhoneNumber.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                    txtPhNoReminder.setTextColor(activity.getResources().getColor(R.color.text_gray));
                    txtLogin.setEnabled(false);
                    sendServer();
                }
            }
        });

        imgPasswordInvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPasswordVisbile){
                    isPasswordVisbile = false;
                    imgPasswordInvisible.setImageDrawable(activity.getResources().getDrawable(R.drawable.password_invisible));
                    edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                }
                else{
                    edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isPasswordVisbile = true;
                    imgPasswordInvisible.setImageDrawable(activity.getResources().getDrawable(R.drawable.visibility));
                }
            }
        });

        edPhoneNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    layoutPhoneNumber.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtPhNoReminder.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else{
                    layoutPhoneNumber.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                    txtPhNoReminder.setTextColor(activity.getResources().getColor(R.color.text_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    layoutPassword.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtPasswordReminder.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else{
                    layoutPassword.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                    txtPasswordReminder.setTextColor(activity.getResources().getColor(R.color.text_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void sendServer(){

        Log.e(TAG, "sendServer:  "  + constLoginUrl + "phone=" + edPhoneNumber.getText().toString() + "&password=" + edPassword.getText().toString() );

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,constLoginUrl + "phone=" + edPhoneNumber.getText().toString() + "&password=" + edPassword.getText().toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "onResponse: " + response.toString());

                        txtLogin.setEnabled(true);
                        try{

                            if(response.getInt("status") == 1){
                                prefs.saveStringPreferences(SP_TOKEN, response.getString("auth_token"));
                                JSONObject jsonObject = response.getJSONObject("supplier");
                                prefs.saveIntPerferences(SP_ID, jsonObject.getInt("id"));
                                prefs.saveStringPreferences(SP_NAME, jsonObject.getString("name"));
                                prefs.saveStringPreferences(SP_PHONE, jsonObject.getString("phone"));
                                prefs.saveStringPreferences(SP_CONTACT_US, jsonObject.getString("contact_us"));
                                prefs.saveStringPreferences(SP_EMAIL, jsonObject.getString("email"));
                                prefs.saveStringPreferences(SP_PASSWORD, edPassword.getText().toString().trim());
                                prefs.saveFloatPerferences(SP_COMMISSION, (float) jsonObject.getDouble("commission"));

                                JSONObject jsonObject1 = jsonObject.getJSONObject("brands");
                                prefs.saveIntPerferences(SP_BRAND_ID ,jsonObject1.getInt("id"));
                                prefs.saveStringPreferences(SP_BRAND_NAME, jsonObject1.getString("name"));
                                prefs.saveStringPreferences(SP_BRAND_LOGO, jsonObject1.getString("logo"));

                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                if(response.getInt("error_type") == 0){
                                    layoutPhoneNumber.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                                    txtPhNoReminder.setTextColor(activity.getResources().getColor(R.color.red));
                                    txtPhNoReminder.setText(response.getString("msg"));
                                }
                                else{
                                    layoutPassword.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                                    txtPasswordReminder.setTextColor(activity.getResources().getColor(R.color.red));
                                    txtPasswordReminder.setText(response.getString("msg"));
                                }
                            }

                        }catch (Exception e){
                            Log.e(TAG, "onResponse: "  + e.getMessage() );
                            ToastHelper.showToast(activity, resources.getString(R.string.error_login));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "onErrorResponse: "  + error.getMessage() );
                txtLogin.setEnabled(true);
                ToastHelper.showToast(activity, resources.getString(R.string.error_login));


            }
        }) {

            /*
            Passing some request headers*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq,"sign_in");
    }
}
