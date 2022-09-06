package com.techmyanmar.kcct.kyarlaysupplier.network;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.techmyanmar.kcct.kyarlaysupplier.VO.Category;
import com.techmyanmar.kcct.kyarlaysupplier.operation.AppController;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ConstanceVariable;
import com.techmyanmar.kcct.kyarlaysupplier.operation.Constant;
import com.techmyanmar.kcct.kyarlaysupplier.operation.DatabaseAdapter;
import com.techmyanmar.kcct.kyarlaysupplier.operation.MyPreference;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkAgent implements Constant, ConstanceVariable {

    private static final String TAG = "NetworkAgent";

    private AppCompatActivity activity ;
    private DatabaseAdapter databaseAdapter;
    private MyPreference prefs;


    public NetworkAgent(AppCompatActivity activity){
        this.activity = activity;
        databaseAdapter = new DatabaseAdapter(activity);
        prefs = new MyPreference(activity);
    }

    public void allgetCategoryList(){
        JsonArrayRequest request = getCategoryList();
        AppController.getInstance().addToRequestQueue(request);
    }

    private JsonArrayRequest getCategoryList(){

        final JsonArrayRequest jsonObjReq = new JsonArrayRequest(constCategoryList,


                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Log.e(TAG, "onResponse: -------------  " + response.toString() );

                        if(response.length() > 0) {

                            try {
                                GsonBuilder builder = new GsonBuilder();
                                Gson mGson = builder.create();
                                Type listType = new TypeToken<List<Category>>() {
                                }.getType();
                                List<Category> categoryList = mGson.fromJson(response.toString(), listType);
                                databaseAdapter.insertCategories(categoryList);




                            }catch (Exception e){
                                Log.e(TAG, "onResponse: "  + e.getMessage() );
                            }


                        }else{
                            Log.e(TAG, "onResponse: none "   );

                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: "  + error.getMessage() );

            }
        }){

            /*
            Passing some request headers*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", prefs.getStringPreferences(SP_TOKEN));
                return headers;
            }
        };
        return jsonObjReq;
    }
}
