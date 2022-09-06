package com.techmyanmar.kcct.kyarlaysupplier.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.techmyanmar.kcct.kyarlaysupplier.R;
import com.techmyanmar.kcct.kyarlaysupplier.VO.Category;
import com.techmyanmar.kcct.kyarlaysupplier.VO.ProductList;
import com.techmyanmar.kcct.kyarlaysupplier.VO.SubCategory;
import com.techmyanmar.kcct.kyarlaysupplier.VO.UniversalPost;
import com.techmyanmar.kcct.kyarlaysupplier.network.NetworkAgent;
import com.techmyanmar.kcct.kyarlaysupplier.operation.AppController;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ConstanceVariable;
import com.techmyanmar.kcct.kyarlaysupplier.operation.Constant;
import com.techmyanmar.kcct.kyarlaysupplier.operation.DatabaseAdapter;
import com.techmyanmar.kcct.kyarlaysupplier.operation.LocaleHelper;
import com.techmyanmar.kcct.kyarlaysupplier.operation.MyPreference;
import com.techmyanmar.kcct.kyarlaysupplier.operation.UniversalAdapter;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity  extends AppCompatActivity implements Constant, ConstanceVariable {

    private static final String TAG = "SearchActivity";

    private MyPreference prefs;
    private AppCompatActivity activity;

    private NetworkAgent networkAgent;
    private Resources resources;

    private RecyclerView recyclerView;
    private EditText txtSearch;
    private ImageView imgBack;

    private UniversalAdapter adapter;
    ArrayList<UniversalPost> universalPosts = new ArrayList<>();
    boolean loading = true;
    RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);

        activity = this;
        prefs = new MyPreference(activity);
        networkAgent = new NetworkAgent(activity);
      

        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();

        Log.e(TAG, "onCreate:  "  + prefs.getStringPreferences(ConstanceVariable.SP_TOKEN) );

        networkAgent.allgetCategoryList();



        txtSearch = findViewById(R.id.txtSearch);
        imgBack = findViewById(R.id.imgBack);
        recyclerView = findViewById(R.id.recyclerView);





        manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        adapter = new UniversalAdapter(activity, universalPosts);
        recyclerView.setAdapter(adapter);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {


                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);

                    universalPosts.clear();

                    UniversalPost noitem = new UniversalPost();
                    noitem.setPostType(CART_DETAIL_FOOTER);
                    universalPosts.add(noitem);

                    adapter.notifyDataSetChanged();
                    prefs.saveIntPerferences(SEARCH_PAGENATION , 1);
                    Log.e(TAG, "onResume: --------------------------"   );
                    loading = true;
                    getProducts();


                    return true;

                }
                return false;
            }
        });







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


    @Override
    protected void onResume() {
        super.onResume();

//        prefs.saveBooleanPreference(PRODUCT_CHANGED, true);
        if(prefs.getBooleanPreference(PRODUCT_CHANGED)){
            prefs.saveBooleanPreference(PRODUCT_CHANGED, false);
            universalPosts.clear();
            UniversalPost noitem = new UniversalPost();
            noitem.setPostType(CART_DETAIL_FOOTER);
            universalPosts.add(noitem);

            adapter.notifyDataSetChanged();

            prefs.saveIntPerferences(SEARCH_PAGENATION , 1);
            loading = true;
            getProducts();
        }
    }

    private void getProducts(){
        String enquery=txtSearch.getText().toString();
        try {
            enquery = URLEncoder.encode( enquery , "utf-8");
        }catch (Exception e){}

        String url = constSearch+"product=" + enquery +"&page="+prefs.getIntPreferences(SEARCH_PAGENATION);
        Log.e(TAG, "getProducts: "  + url  );
       
        final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        Log.e(TAG, "onResponse: "  + url );
                        Log.e(TAG, "onResponse: "  + response.toString() );


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
                                    String[] strPhotos = productLists.get(0).getPhotos();



                                    if(prefs.getIntPreferences(SEARCH_PAGENATION) < 2){
                                        ProductList universalPost = new ProductList();
                                        universalPost.setTitle("Search Product");
                                        universalPost.setPostType(RECYCLER_TITLE);
                                        universalPosts.add(universalPost);
                                    }


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

    private JsonArrayRequest getProductList(){

        //+ "product=100000"
        final JsonArrayRequest jsonObjReq = new JsonArrayRequest(constCreateProduct + "?page=" + prefs.getIntPreferences(SEARCH_PAGENATION) ,


                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, "onResponse:  " + constCreateProduct + "?page=" + prefs.getIntPreferences(SEARCH_PAGENATION));
                        Log.e(TAG, "onResponse:  "  + response.toString() );

                        if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_FOOTER)){
                            universalPosts.remove(universalPosts.size() - 1);
                        }

                        if(response.length() > 0) {
                            loading = true;
                            prefs.saveIntPerferences(SEARCH_PAGENATION,prefs.getIntPreferences(SEARCH_PAGENATION) + 1);
                            recyclerView.setVisibility(View.VISIBLE);


                            try {
                                GsonBuilder builder = new GsonBuilder();
                                Gson mGson = builder.create();
                                Type listType = new TypeToken<List<ProductList>>() {
                                }.getType();
                                List<ProductList> productLists = mGson.fromJson(response.toString(), listType);
                                if (productLists.size() > 0){

                                    if(prefs.getIntPreferences(SEARCH_PAGENATION) < 2){
                                        UniversalPost universalPost = new UniversalPost();
                                        universalPost.setPostType(RECYCLER_TITLE);
                                        universalPosts.add(universalPost);
                                    }


                                    for (int i =0 ;i < productLists.size(); i++){
                                        productLists.get(i).setPostType(PRODUCT);
                                        universalPosts.add(productLists.get(i));
                                    }


                                }

                                //databaseAdapter.insertCategories(categoryList);


                                UniversalPost noitem = new UniversalPost();
                                noitem.setPostType(CART_DETAIL_FOOTER);
                                universalPosts.add(noitem);

                                adapter.notifyItemInserted(universalPosts.size());


                            }catch (Exception e){
                                Log.e(TAG, "onResponse: "  + e.getMessage() );
                            }


                        }else{
                            loading = false;
                            UniversalPost noitem = new UniversalPost();
                            noitem.setPostType(CART_DETAIL_NO_ITEM);
                            universalPosts.add(noitem);

                            adapter.notifyItemInserted(universalPosts.size());



                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: "  + error.getMessage() );
                loading = false;
                if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_FOOTER)){
                    universalPosts.remove(universalPosts.size() - 1);
                }
                else if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_FOOTER)){
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
        return jsonObjReq;
    }

}
