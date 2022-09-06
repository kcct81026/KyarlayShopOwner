package com.techmyanmar.kcct.kyarlaysupplier.activity;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class OtpActivity extends AppCompatActivity implements Constant, ConstanceVariable {

    private static final String TAG = "OtpActivity";


    TextView title, txtEnter,txt_Click, timer, txtNewAcc;
    LinearLayout back_layout, linearName;
    EditText ed1;
    Button btn;
    ProgressBar progress_bar;

    Resources resources;
    MyPreference prefs;
    String phone;
    AppCompatActivity activity ;
    Display display;
    CountDownTimer countDownTimer = null;
    long milliseconds;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_otp);

        Log.e(TAG, "onCreate: " );
        activity = OtpActivity.this;
        prefs   = new MyPreference(OtpActivity.this);
        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();
        display = activity.getWindowManager().getDefaultDisplay();


        phone = getIntent().getStringExtra("phone" );

        progress_bar = findViewById(R.id.progress_bar);
        back_layout = findViewById(R.id.back_layout);
        title = findViewById(R.id.txt_title);
        txtEnter = findViewById(R.id.txt_enter);
        ed1 = findViewById(R.id.otp1);
        txt_Click = findViewById(R.id.txt_Click);
        linearName = findViewById(R.id.linearName);
        timer = findViewById(R.id.timer);
        txtNewAcc = findViewById(R.id.txtNewAcc);


        btn = findViewById(R.id.button);

        back_layout.getLayoutParams().width = (display.getWidth() * 3 ) / 20;

        if(prefs != null && !prefs.isContains(COUNT_DOWN)){
            prefs.saveIntPerferences(COUNT_DOWN,0);
        }



        title.setText(resources.getString(R.string.verification_code));
        txtEnter.setText(resources.getString(R.string.enter_verify_code));
        txt_Click.setText(resources.getString(R.string.click_otp));
        btn.setText(resources.getString(R.string.submit));

        if (prefs.getIntPreferences(COUNT_DOWN) == 0){
            linearName.setVisibility(View.GONE);
            txt_Click.setVisibility(View.VISIBLE);
        }else {
            linearName.setVisibility(View.VISIBLE);
            txt_Click.setVisibility(View.GONE);
            timer.setVisibility(View.VISIBLE);
            int testTime = prefs.getIntPreferences(COUNT_DOWN_TIMER);

            CountDownTimer cdt = new CountDownTimer(testTime * 1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    prefs.saveIntPerferences(COUNT_DOWN_TIMER, (int)(millisUntilFinished / 1000) );
                    timer.setText(String.format(resources.getString(R.string.otp_waiting_time) , "" + (int) (millisUntilFinished / 1000) ));
                    timer.setVisibility(View.VISIBLE);




                }

                @Override
                public void onFinish() {
                    timer.setVisibility(View.GONE);
                    prefs.saveIntPerferences(COUNT_DOWN, 0);
                    txt_Click.setVisibility(View.VISIBLE);
                    linearName.setVisibility(View.VISIBLE);

                }
            };

            cdt.start();
        }

        txt_Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOptCodeFromServer();
                txt_Click.setVisibility(View.GONE);
                linearName.setVisibility(View.VISIBLE);
                countDownTimer = new CountDownTimer(60000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {


                        prefs.saveIntPerferences(COUNT_DOWN, 1 );
                        prefs.saveIntPerferences(COUNT_DOWN_TIMER, (int)(millisUntilFinished / 1000) );
                        timer.setText(String.format(resources.getString(R.string.otp_waiting_time) , "" + (int) (millisUntilFinished / 1000) ));

                        timer.setVisibility(View.VISIBLE);
                        milliseconds = millisUntilFinished / 1000;







                    }

                    @Override
                    public void onFinish() {

                        timer.setVisibility(View.GONE);
                        prefs.saveIntPerferences(COUNT_DOWN, 0);
                        txt_Click.setVisibility(View.VISIBLE);
                        linearName.setVisibility(View.VISIBLE);

                    }
                };

                countDownTimer.start();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed1.getText().toString().isEmpty() ){

                    ToastHelper.showToast(OtpActivity.this, resources.getString(R.string.otp_code));


                }else{


                    if (prefs.isNetworkAvailable()){
                        progress_bar.setVisibility(View.VISIBLE);

                        sendOtpToServer();
                    }
                }
            }
        });

        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //prefs.saveIntPerferences(SP_CHANGE, 0);
                finish();
            }
        });
    }

    private void getOptCodeFromServer(){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,constOtpGetCode, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "onResponse: "  + response.toString() );

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progress_bar.setVisibility(View.GONE);
                Log.e(TAG, "onResponse: VolleyError Excetion "  + error.getMessage() );
            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", prefs.getStringPreferences(SP_TOKEN));
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq,"sign_in");
    }

    private void sendOtpToServer(){

        JSONObject uploadMessage = new JSONObject();
        try {
            uploadMessage.put("otp", ed1.getText().toString().trim());



        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,constOtpVerifyCode, uploadMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "onResponse: "  + response.toString() );
                        progress_bar.setVisibility(View.GONE);
                        //{"msg":"Success"}
                        try{
                            if (response.getString("msg").equals("Success")){
                                Intent intent = new Intent(activity, ChangePasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }catch (Exception e){
                            Log.e(TAG, "onResponse:  "  + e.getMessage() );
                            ToastHelper.showToast(activity, resources.getString(R.string.otp_error));
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progress_bar.setVisibility(View.GONE);
                Log.e(TAG, "onResponse: VolleyError Excetion "  + error.getMessage() );
                ToastHelper.showToast(activity, resources.getString(R.string.otp_error));
            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", prefs.getStringPreferences(SP_TOKEN));
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq,"sign_in");
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}