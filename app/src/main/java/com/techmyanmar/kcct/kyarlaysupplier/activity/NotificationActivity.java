package com.techmyanmar.kcct.kyarlaysupplier.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.techmyanmar.kcct.kyarlaysupplier.R;
import com.techmyanmar.kcct.kyarlaysupplier.VO.ProductList;
import com.techmyanmar.kcct.kyarlaysupplier.VO.UniversalPost;
import com.techmyanmar.kcct.kyarlaysupplier.network.NetworkAgent;
import com.techmyanmar.kcct.kyarlaysupplier.operation.AppController;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ConstanceVariable;
import com.techmyanmar.kcct.kyarlaysupplier.operation.Constant;
import com.techmyanmar.kcct.kyarlaysupplier.operation.LocaleHelper;
import com.techmyanmar.kcct.kyarlaysupplier.operation.MyPreference;
import com.techmyanmar.kcct.kyarlaysupplier.operation.UniversalAdapter;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//title
public class NotificationActivity  extends AppCompatActivity implements ConstanceVariable, Constant {

    private static final String TAG = "SearchActivity";

    private MyPreference prefs;
    private AppCompatActivity activity;

    private NetworkAgent networkAgent;
    private Resources resources;

    private RecyclerView recyclerView;
    private TextView title;
    private ImageView imgBack;

    private UniversalAdapter adapter;
    ArrayList<UniversalPost> universalPosts = new ArrayList<>();
    boolean loading = false;
    RecyclerView.LayoutManager manager;

    String fromCalss = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notification);

        activity = this;
        prefs = new MyPreference(activity);
        networkAgent = new NetworkAgent(activity);


        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();

        Log.e(TAG, "onCreate:  "  + prefs.getStringPreferences(ConstanceVariable.SP_TOKEN) );

        networkAgent.allgetCategoryList();



        title = findViewById(R.id.title);
        imgBack = findViewById(R.id.imgBack);
        recyclerView = findViewById(R.id.recyclerView);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle != null) {
            fromCalss = bundle.getString("fromCalss");
        }


        manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        adapter = new UniversalAdapter(activity, universalPosts);
        recyclerView.setAdapter(adapter);

        title.setText("Notification");


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        UniversalPost noitem = new UniversalPost();
        noitem.setPostType(CART_DETAIL_FOOTER);
        universalPosts.add(noitem);

        adapter.notifyDataSetChanged();
        prefs.saveIntPerferences(SEARCH_PAGENATION , 1);
        Log.e(TAG, "onResume: --------------------------"   );
        loading = true;
        getProducts();








        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {



            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if ((lastVisibleItem + 1) == totalItemCount && loading == false) {
                    loading = true;
                   /* JsonArrayRequest request = getProductList();
                    AppController.getInstance().addToRequestQueue(request);*/
                    getProducts();
                }

            }
        });





    }




    private void getProducts(){


        String url = constCreateProduct +"?page="+prefs.getIntPreferences(SEARCH_PAGENATION);

        final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {




                        if(response.length() > 0) {


                            if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_FOOTER)){
                                universalPosts.remove(universalPosts.size() - 1);
                            }
                            loading = false;

                            try {
                                GsonBuilder builder = new GsonBuilder();
                                Gson mGson = builder.create();
                                Type listType = new TypeToken<List<ProductList>>() {
                                }.getType();
                                List<ProductList> productLists = mGson.fromJson(response.toString(), listType);

                                if (productLists.size() > 0){

                                 
                                    for (int i =0 ;i < productLists.size(); i++){
                                        productLists.get(i).setPostType(PRODUCT);
                                        universalPosts.add(productLists.get(i));
                                    }


                                }

                                adapter.notifyItemInserted(universalPosts.size());
                                prefs.saveIntPerferences(SEARCH_PAGENATION, prefs.getIntPreferences(SEARCH_PAGENATION) + 1);

                            }catch (Exception e){
                                Log.e(TAG, "onResponse: getPromoteProduct Exception : "  + e.getMessage() );
                            }

                        }else{
                            if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_FOOTER)){
                                universalPosts.remove(universalPosts.size() - 1);
                            }

                            if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_NO_ITEM)){
                                universalPosts.remove(universalPosts.size() - 1);
                            }


                            UniversalPost noitem = new UniversalPost();
                            noitem.setPostType(CART_DETAIL_NO_ITEM);
                            universalPosts.add(noitem);

                            adapter.notifyItemInserted(universalPosts.size());

                        }



                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loading = false;
                if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_FOOTER)){
                    universalPosts.remove(universalPosts.size() - 1);
                }

                if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_NO_ITEM)){
                    universalPosts.remove(universalPosts.size() - 1);
                }

                UniversalPost noitem = new UniversalPost();
                noitem.setPostType(CART_DETAIL_NO_ITEM);
                universalPosts.add(noitem);

                adapter.notifyItemInserted(universalPosts.size());

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
        AppController.getInstance().addToRequestQueue(jsonArrayReq, "memberchecking");


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (fromCalss.equals(FROM_FIREBASE)) {

            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
            finish();

        } else
            finish();
    }
}


