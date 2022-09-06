package com.techmyanmar.kcct.kyarlaysupplier.activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity implements Constant, ConstanceVariable {

    private static final String TAG = "ChangePasswordActivity";


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




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_otp);

        Log.e(TAG, "onCreate: " );
        activity = ChangePasswordActivity.this;
        prefs   = new MyPreference(ChangePasswordActivity.this);
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



        title.setText(resources.getString(R.string.change_password));
        txtEnter.setText(resources.getString(R.string.enter_new_password));

        txt_Click.setVisibility(View.GONE);

        btn.setText(resources.getString(R.string.submit));
        ed1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed1.getText().toString().isEmpty() ){

                    ToastHelper.showToast(ChangePasswordActivity.this, resources.getString(R.string.otp_code));


                }else{


                    if (prefs.isNetworkAvailable()){
                        progress_bar.setVisibility(View.VISIBLE);
                        sendServer();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    private void sendServer(){

        JSONObject uploadMessage = new JSONObject();
        try {
            // uploadMessage.put("phone",  edPhone.getText().toString().trim());
            uploadMessage.put("password",  ed1.getText().toString().trim());

        } catch (JSONException e) {
            e.printStackTrace();
        }




        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,constChangePasword, uploadMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress_bar.setVisibility(View.GONE);

                        Log.e(TAG, "onResponse: " + response.toString());

                        try{
                            if (response.getString("msg").equals("Success")){
                                ToastHelper.showToast(activity,resources.getString(R.string.change_password_successful));
                                prefs.clearAll();
                                Intent intent = new Intent(activity,LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }
                            else{
                                JSONArray array = response.getJSONArray("errors");
                                String[] arr = toStringArray(array);
                                if (arr.length > 0) {
                                    ToastHelper.showToast(activity, arr[0]);
                                }

                                //ToastHelper.showToast();
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
                progress_bar.setVisibility(View.GONE);
                ToastHelper.showToast(activity, resources.getString(R.string.error_login));


            }
        }) {

            /*
            Passing some request headers*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Auth-Token", prefs.getStringPreferences(SP_TOKEN));
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq,"sign_in");
    }

    public String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }
}