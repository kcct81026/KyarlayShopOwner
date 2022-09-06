package com.techmyanmar.kcct.kyarlaysupplier.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cloudinary.Search;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ConstanceVariable, Constant {

    private static final String TAG = "MainActivity";

    private MyPreference prefs;
    private AppCompatActivity activity;
    private DatabaseAdapter databaseAdapter;
    private NetworkAgent networkAgent;
    private Resources resources;

    private TextView title, txtSearch, txtNoProduct;
    private ImageView imgNotification, imgProfile;
    private LinearLayout layoutNoProduct;
    private RelativeLayout relativeCreateProduct;
    private Button btnAdd;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private UniversalAdapter adapter;
    ArrayList<UniversalPost> universalPosts = new ArrayList<>();
    boolean loading = false;
    RecyclerView.LayoutManager manager;

    List<Category> categoryList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        prefs = new MyPreference(activity);
        databaseAdapter = new DatabaseAdapter(activity);
        networkAgent = new NetworkAgent(activity);
        prefs.saveIntPerferences(SP_CATEGORY_CLICK_ID, -1);
        prefs.saveStringPreferences(SP_CATEGORY__CLICK_NAME, "All Your Products");

        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();

        Log.e(TAG, "onCreate:  "  + prefs.getStringPreferences(ConstanceVariable.SP_TOKEN) );

        networkAgent.allgetCategoryList();


        title = findViewById(R.id.title);
        imgNotification = findViewById(R.id.imgNotification);
        imgProfile = findViewById(R.id.imgProfile);
        txtSearch = findViewById(R.id.txtSearch);
        layoutNoProduct = findViewById(R.id.layoutNoProduct);
        txtNoProduct = findViewById(R.id.txtNoProduct);
        relativeCreateProduct = findViewById(R.id.relativeCreateProduct);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        title.setText(prefs.getStringPreferences(SP_BRAND_NAME));
        txtSearch.setText(resources.getString(R.string.search_product));
        txtNoProduct.setText(resources.getString(R.string.no_product_add_product));
        btnAdd.setText(resources.getString(R.string.add_product));

        manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        adapter = new UniversalAdapter(activity, universalPosts);
        recyclerView.setAdapter(adapter);
        prefs.saveBooleanPreference(PRODUCT_CHANGED, false);

        UniversalPost noitem = new UniversalPost();
        noitem.setPostType(CART_DETAIL_FOOTER);
        universalPosts.add(noitem);

        adapter.notifyDataSetChanged();
        prefs.saveIntPerferences(PAGENATION , 1);
        Log.e(TAG, "onResume: --------------------------"   );
        getProducts();
        loading = true;

        imgNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, NotificationActivity.class);
                startActivity(intent);
            }
        });
        imgNotification.setVisibility(View.GONE);

        txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SearchActivity.class);
                startActivity(intent);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, AddNewProductActivity.class);
                startActivity(intent);
            }
        });


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @SuppressLint("RestrictedApi")
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if(newState == RecyclerView.SCROLL_STATE_IDLE ) {
                    fab.setVisibility(View.VISIBLE);

                }
                else{
                    fab.setVisibility(View.GONE);
                }

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



        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                chooseCategory();
            }
        });

        JsonArrayRequest request = getCategoryList();
        AppController.getInstance().addToRequestQueue(request);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(prefs.getBooleanPreference(PRODUCT_CHANGED)){
            prefs.saveBooleanPreference(PRODUCT_CHANGED, false);
            universalPosts.clear();
            UniversalPost noitem = new UniversalPost();
            noitem.setPostType(CART_DETAIL_FOOTER);
            universalPosts.add(noitem);

            adapter.notifyDataSetChanged();

            prefs.saveIntPerferences(PAGENATION , 1);
            loading = true;
            prefs.saveIntPerferences(SP_CATEGORY_CLICK_ID, -1);
            prefs.saveStringPreferences(SP_CATEGORY__CLICK_NAME, "All Your Products");
            getProducts();

            JsonArrayRequest request = getCategoryList();
            AppController.getInstance().addToRequestQueue(request);
        }

    }

    private JsonArrayRequest getCategoryList(){

        final JsonArrayRequest jsonObjReq = new JsonArrayRequest(constSupplierCategoryList,


                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        if(response.length() > 0) {

                            try {
                                GsonBuilder builder = new GsonBuilder();
                                Gson mGson = builder.create();
                                Type listType = new TypeToken<List<Category>>() {
                                }.getType();
                                categoryList = mGson.fromJson(response.toString(), listType);
                                //databaseAdapter.insertCategories(categoryList);




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

    private void chooseCategory(){

        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialog);

        mBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBottomSheetDialog.setContentView(R.layout.layout_dialog_category);
        mBottomSheetDialog.setCanceledOnTouchOutside(true);
        mBottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        final Window window = mBottomSheetDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imgClose = mBottomSheetDialog.findViewById(R.id.imgClose);
        TextView textView = mBottomSheetDialog.findViewById(R.id.txtWatch);
        LinearLayout linearAdd = mBottomSheetDialog.findViewById(R.id.linearAdd);

        textView.setVisibility(View.VISIBLE);
        textView.setText(resources.getString(R.string.your_category));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
                prefs.saveIntPerferences(SP_CATEGORY_CLICK_ID, -1);
                prefs.saveStringPreferences(SP_CATEGORY__CLICK_NAME, "All Category");
                prefs.saveBooleanPreference(PRODUCT_CHANGED, false);
                universalPosts.clear();
                UniversalPost noitem = new UniversalPost();
                noitem.setPostType(CART_DETAIL_FOOTER);
                universalPosts.add(noitem);

                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                layoutNoProduct.setVisibility(View.GONE);

                prefs.saveIntPerferences(PAGENATION , 1);
                loading = true;

                getProducts();
            }
        });

        for (int i = 0 ; i < categoryList.size(); i++){
            //TownShip shopLocation = townShipList.get(i);

            Category category = categoryList.get(i);
            List<SubCategory> subCategoryList = category.getCategoryArrayList();

            LinearLayout phoneLayout = new LinearLayout(activity);
            phoneLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            phoneLayout.setOrientation(LinearLayout.VERTICAL);
            phoneLayout.setPadding(0, 20, 0, 20);


            LinearLayout.LayoutParams childParam1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            childParam1.weight = 0.1f;
            TextView price = new TextView(activity);
            price.setTextSize(20);
            price.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            price.setTypeface(price.getTypeface(), Typeface.BOLD);
            price.setPadding(20, 20, 20, 20);
            price.setGravity(Gravity.LEFT);
            price.setText(category.getName());
            price.setLayoutParams(childParam1);

            phoneLayout.addView(price);
            //  phoneLayout.addView(price2);

            for (int j=0; j< subCategoryList.size(); j++){
                SubCategory subCategory = subCategoryList.get(j);
                LinearLayout.LayoutParams subParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                subParam.weight = 0.1f;
                TextView subTextView = new TextView(activity);
                subTextView.setTextSize(16);
                subTextView.setPadding(20, 30, 20, 30);
                subTextView.setGravity(Gravity.LEFT);
                subTextView.setText(subCategory.getName());
                subTextView.setLayoutParams(subParam);
                phoneLayout.addView(subTextView);

                if(j != subCategoryList.size() -1){
                    TextView price2 = new TextView(activity);
                    price2.setHeight(2);
                    price2.setBackgroundColor(activity.getResources().getColor(R.color.background));
                    phoneLayout.addView(price2);
                }


                subTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBottomSheetDialog.dismiss();


                        prefs.saveIntPerferences(SP_CATEGORY_CLICK_ID, subCategory.getId());
                        prefs.saveStringPreferences(SP_CATEGORY__CLICK_NAME, subCategory.getName());
                        prefs.saveBooleanPreference(PRODUCT_CHANGED, false);
                        universalPosts.clear();
                        UniversalPost noitem = new UniversalPost();
                        noitem.setPostType(CART_DETAIL_FOOTER);
                        universalPosts.add(noitem);

                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutNoProduct.setVisibility(View.GONE);

                        prefs.saveIntPerferences(PAGENATION , 1);
                        loading = true;

                        getProducts();


                    }
                });

            }




            phoneLayout.setWeightSum(1f);






              /*  TextView space = new TextView(activity);
                space.setHeight(6);
                space.setBackgroundResource(R.color.checked_bg);*/

            linearAdd.addView(phoneLayout);

            int finalI = i;


        }


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.show();



    }

    private void getProducts(){


        String url = constCreateProduct +"?page="+prefs.getIntPreferences(PAGENATION);
        if(prefs.getIntPreferences(SP_CATEGORY_CLICK_ID ) == -1 ){
           url = constCreateProduct +"?page="+prefs.getIntPreferences(PAGENATION);
        }
        else{
            ///suppliers/api/products/search?category_id=
            Log.e(TAG, "getProducts: -------- "   + prefs.getIntPreferences(SP_CATEGORY_CLICK_ID) );
            url = constSearch +"category_id="+ prefs.getIntPreferences(SP_CATEGORY_CLICK_ID) +"&page="+prefs.getIntPreferences(PAGENATION);
        }

        Log.e(TAG, "getProducts: "  + url );

        final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(url,

                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, "onResponse: "   +response.toString() );



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

                                    if(prefs.getIntPreferences(PAGENATION) < 2){
                                        ProductList universalPost = new ProductList();
                                        universalPost.setTitle(prefs.getStringPreferences(SP_CATEGORY__CLICK_NAME));
                                        universalPost.setPostType(RECYCLER_TITLE);
                                        universalPosts.add(universalPost);
                                    }


                                    for (int i =0 ;i < productLists.size(); i++){
                                        productLists.get(i).setPostType(PRODUCT);
                                        Log.e(TAG, "onResponse: "  + productLists.get(i).getTitle() +  "  "  + productLists.get(i).getId()  );
                                        universalPosts.add(productLists.get(i));
                                    }


                                }

                                adapter.notifyItemInserted(universalPosts.size());
                                prefs.saveIntPerferences(PAGENATION, prefs.getIntPreferences(PAGENATION) + 1);

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


                            if(universalPosts.size() ==0){
                                recyclerView.setVisibility(View.GONE);
                                layoutNoProduct.setVisibility(View.VISIBLE);
                            }
                            else{
                                UniversalPost noitem = new UniversalPost();
                                noitem.setPostType(CART_DETAIL_NO_ITEM);
                                universalPosts.add(noitem);

                                adapter.notifyItemInserted(universalPosts.size());

                            }
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

                if(universalPosts.size() ==0){
                    recyclerView.setVisibility(View.GONE);
                    layoutNoProduct.setVisibility(View.VISIBLE);
                }
                else{
                    UniversalPost noitem = new UniversalPost();
                    noitem.setPostType(CART_DETAIL_NO_ITEM);
                    universalPosts.add(noitem);

                    adapter.notifyItemInserted(universalPosts.size());

                }
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
        final JsonArrayRequest jsonObjReq = new JsonArrayRequest(constCreateProduct + "?page=" + prefs.getIntPreferences(PAGENATION) ,


                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e(TAG, "onResponse:  " + constCreateProduct + "?page=" + prefs.getIntPreferences(PAGENATION));
                        Log.e(TAG, "onResponse:  "  + response.toString() );

                        if(universalPosts.size() != 0 && universalPosts.get(universalPosts.size() - 1).getPostType().equals(CART_DETAIL_FOOTER)){
                            universalPosts.remove(universalPosts.size() - 1);
                        }

                        if(response.length() > 0) {
                            loading = true;
                            prefs.saveIntPerferences(PAGENATION,prefs.getIntPreferences(PAGENATION) + 1);
                            recyclerView.setVisibility(View.VISIBLE);
                            layoutNoProduct.setVisibility(View.GONE);

                            try {
                                GsonBuilder builder = new GsonBuilder();
                                Gson mGson = builder.create();
                                Type listType = new TypeToken<List<ProductList>>() {
                                }.getType();
                                List<ProductList> productLists = mGson.fromJson(response.toString(), listType);
                                if (productLists.size() > 0){

                                    if(prefs.getIntPreferences(PAGENATION) < 2){
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
                            recyclerView.setVisibility(View.GONE);
                            layoutNoProduct.setVisibility(View.VISIBLE);


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

                if(universalPosts.size() ==0){
                    recyclerView.setVisibility(View.GONE);
                    layoutNoProduct.setVisibility(View.VISIBLE);
                }
                else{
                    UniversalPost noitem = new UniversalPost();
                    noitem.setPostType(CART_DETAIL_NO_ITEM);
                    universalPosts.add(noitem);

                    adapter.notifyItemInserted(universalPosts.size());

                }


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