package com.techmyanmar.kcct.kyarlaysupplier.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techmyanmar.kcct.kyarlaysupplier.R;
import com.techmyanmar.kcct.kyarlaysupplier.operation.AppController;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ConstanceVariable;
import com.techmyanmar.kcct.kyarlaysupplier.operation.Constant;
import com.techmyanmar.kcct.kyarlaysupplier.operation.LocaleHelper;
import com.techmyanmar.kcct.kyarlaysupplier.operation.MyPreference;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ToastHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity implements ConstanceVariable, Constant {

    private static final String TAG = "SplashScreenActivity";

    private AppCompatActivity activity;
    private MyPreference prefs;
    private Resources resources;
    private TextView  textView;
    private ProgressBar progress_bar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        activity = SplashScreenActivity.this;
        prefs = new MyPreference(activity);


        if(prefs.getStringPreferences(LANGUAGE) == ""){
            prefs.saveStringPreferences(LANGUAGE, LANGUAGE_MYANMAR);
        }


        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();

        textView = findViewById(R.id.textView);
        textView.setVisibility(View.GONE);
        progress_bar = findViewById(R.id.progress_bar);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_bar.setVisibility(View.VISIBLE);
                sendServer();
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                if (prefs.getStringPreferences(SP_PHONE) == "" && prefs.getStringPreferences(SP_PASSWORD) == ""){
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{

                    //prefs.saveStringPreferences(SP_FCM_TOEKN, FirebaseInstanceId.getInstance().getToken());
                    //Log.e(TAG, "run: ----------------- MyFirebaseMessagingServ  "   + prefs.getStringPreferences(SP_FCM_TOEKN) );

                    sendServer();
                }

            }
        }, 1500);

    }

    private void sendServer(){


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,constLoginUrl + "phone=" + prefs.getStringPreferences(ConstanceVariable.SP_PHONE)+ "&password=" + prefs.getStringPreferences(ConstanceVariable.SP_PASSWORD), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "onResponse: " + response.toString());
                        progress_bar.setVisibility(View.GONE);

                        try{

                            if(response.getInt("status") == 1){
                                prefs.saveStringPreferences(SP_TOKEN, response.getString("auth_token"));
                                JSONObject jsonObject = response.getJSONObject("supplier");
                                prefs.saveIntPerferences(SP_ID, jsonObject.getInt("id"));
                                prefs.saveStringPreferences(SP_NAME, jsonObject.getString("name"));
                                prefs.saveStringPreferences(SP_PHONE, jsonObject.getString("phone"));
                                prefs.saveStringPreferences(SP_CONTACT_US, jsonObject.getString("contact_us"));
                                prefs.saveStringPreferences(SP_EMAIL, jsonObject.getString("email"));
                                prefs.saveStringPreferences(SP_PASSWORD, prefs.getStringPreferences(SP_PASSWORD));
                                prefs.saveFloatPerferences(SP_COMMISSION, (float) jsonObject.getDouble("commission"));

                                JSONObject jsonObject1 = jsonObject.getJSONObject("brands");
                                prefs.saveIntPerferences(SP_BRAND_ID ,jsonObject1.getInt("id"));
                                prefs.saveStringPreferences(SP_BRAND_NAME, jsonObject1.getString("name"));
                                prefs.saveStringPreferences(SP_BRAND_LOGO, jsonObject1.getString("logo"));

                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                               // textView.setVisibility(View.VISIBLE);
                                ToastHelper.showToast(activity, resources.getString(R.string.error_login));
                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }catch (Exception e){
                            Log.e(TAG, "onResponse: "  + e.getMessage() );
                            ToastHelper.showToast(activity, resources.getString(R.string.error_message));
                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //textView.setVisibility(View.VISIBLE);
                progress_bar.setVisibility(View.GONE);
                Log.e(TAG, "onErrorResponse: "  + error.getMessage() );
                ToastHelper.showToast(activity, resources.getString(R.string.error_login));
                ToastHelper.showToast(activity, resources.getString(R.string.error_message));
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                finish();


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
