package com.techmyanmar.kcct.kyarlaysupplier.operation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.techmyanmar.kcct.kyarlaysupplier.R;
import com.techmyanmar.kcct.kyarlaysupplier.VO.ProductList;
import com.techmyanmar.kcct.kyarlaysupplier.VO.UniversalPost;
import com.techmyanmar.kcct.kyarlaysupplier.activity.EditProduct;
import com.techmyanmar.kcct.kyarlaysupplier.activity.ProductDetail;
import com.techmyanmar.kcct.kyarlaysupplier.holder.FooterHolder;
import com.techmyanmar.kcct.kyarlaysupplier.holder.NoItemHolder;
import com.techmyanmar.kcct.kyarlaysupplier.holder.ProductHolder;
import com.techmyanmar.kcct.kyarlaysupplier.holder.RecyclerTitleHolder;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UniversalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ConstanceVariable, Constant {

    private static final String TAG = "UniversalAdapter";

    ArrayList<UniversalPost> universalList;
    AppCompatActivity activity;
    ImageLoader imageLoader;
    Resources resources ;
    MyPreference prefs;
    Display display;
    DecimalFormat formatter = new DecimalFormat("#,###,###");



    RecyclerView.LayoutManager layoutManager;


    public UniversalAdapter(AppCompatActivity activity1, ArrayList<UniversalPost> universalList) {
        this.universalList = universalList;
        this.activity = activity1;
        imageLoader = AppController.getInstance().getImageLoader();
        prefs = new MyPreference(activity);
        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();
        display = activity.getWindowManager().getDefaultDisplay();

    }

    @Override
    public int getItemCount() {
        return universalList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(universalList.get(position).getPostType().equals(CART_DETAIL_FOOTER)){
            return VIEW_TYPE_CART_DETAIL_FOOTER;
        }
        else if(universalList.get(position).getPostType().equals(CART_DETAIL_NO_ITEM)){
            return VIEW_TYPE_CART_DETAIL_NO_ITEM;
        }
        else if(universalList.get(position).getPostType().equals(PRODUCT)){
            return VIEW_TYPE_PRODUCT;
        }
        else if(universalList.get(position).getPostType().equals(RECYCLER_TITLE)){
            return VIEW_TYPE_RECYCLER_TITLE;
        }

        return VIEW_TYPE_CART_DETAIL_FOOTER;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        if(viewType == VIEW_TYPE_CART_DETAIL_FOOTER){
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_card_detail_footer, parent, false);
            viewHolder = new FooterHolder(viewItem);
        }
        else if(viewType == VIEW_TYPE_CART_DETAIL_NO_ITEM){
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_card_detail_no_item, parent, false);
            viewHolder = new NoItemHolder(viewItem);
        }
        else if(viewType == VIEW_TYPE_PRODUCT){
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_product_item, parent, false);
            viewHolder = new ProductHolder(viewItem);
        }
        else if(viewType == VIEW_TYPE_RECYCLER_TITLE){
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_recycler_title, parent, false);
            viewHolder = new RecyclerTitleHolder(viewItem);
        }




        return viewHolder;
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"WrongConstant", "StringFormatInvalid"})
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder parentHolder, final int position) {

        int type = getItemViewType(position);

        switch (type) {

            case VIEW_TYPE_PRODUCT:{
                Boolean checkSwitchValue = false;
                ProductList product = (ProductList) universalList.get(position);
                ProductHolder productHolder = (ProductHolder) parentHolder;

                if (product.getTitle() != null && product.getTitle().trim().length() > 0){
                    productHolder.txtTitle.setText(product.getTitle());
                }

                if(product.getReviewer_status().equals("REJECTED")){
                    productHolder.txtStatus.setText("Reject");
                    productHolder.txtState.setText("Retry");
                    productHolder.txtStatus.setTextColor(activity.getResources().getColor(R.color.red));
                    productHolder.txtState.setTextColor(activity.getResources().getColor(R.color.blue));
                    productHolder.txtState.setPaintFlags(productHolder.txtState.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    productHolder.switchCompat.setVisibility(View.GONE);
                }
                else if(product.getReviewer_status().equals("PENDING")){
                    productHolder.txtStatus.setText("Pending");
                    productHolder.txtState.setText("Edit");
                    productHolder.txtStatus.setTextColor(activity.getResources().getColor(R.color.blue));
                    productHolder.txtState.setTextColor(activity.getResources().getColor(R.color.text));
                    productHolder.switchCompat.setVisibility(View.GONE);

                }
                else{
                    productHolder.switchCompat.setVisibility(View.VISIBLE);
                    productHolder.txtStatus.setVisibility(View.GONE);
                    productHolder.txtState.setText("Edit");
                    productHolder.txtState.setTextColor(activity.getResources().getColor(R.color.text));
                    if (product.getStatus().equals("available")){
                        productHolder.switchCompat.setChecked(true);
                        checkSwitchValue = true;

                        // productHolder.txtAvailable.setBackground(activity.getResources().getDrawable(R.drawable.border_orange));
                    }else {
                        productHolder.txtStatus.setText("Stock Out");
                        productHolder.switchCompat.setChecked(false);
                        checkSwitchValue = false;
                      /*  productHolder.txtAvailable.setText("Stock Out");
                        productHolder.txtAvailable.setBackground(activity.getResources().getDrawable(R.drawable.border_red));
                        productHolder.txtEditProduct.setVisibility(View.GONE);*/
                    }
                }



                productHolder.txtState.setTypeface(productHolder.txtState.getTypeface(), Typeface.BOLD);
                productHolder.txtStatus.setTypeface(productHolder.txtStatus.getTypeface(), Typeface.BOLD);

                productHolder.txtPrice.setText(formatter.format(product.getPrice()) +" "+activity.getResources().getString(R.string.currency));

                String[] strPhotos = product.getPhotos();
                if (strPhotos.length == 0){
                    //productHolder.img.setDefaultImageResId(R.drawable.ic_baseline_image_24);
                    Glide.with(activity).load(R.mipmap.ic_icon).into(productHolder.img);
                }
                else{
                    //productHolder.img.setImageUrl(strPhotos[0], imageLoader);
                    Glide.with(activity).load(strPhotos[0]).into(productHolder.img);
                }
//
//                productHolder.img.getLayoutParams().width  = display.getWidth()  / 4 ;
//                productHolder.img.getLayoutParams().height  = display.getWidth() /4  ;


                productHolder.txtState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(activity, EditProduct.class);
                        intent.putExtra("product" , product);
                        activity.startActivity(intent);
                    }
                });

                productHolder.layoutMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent  = new Intent(activity, ProductDetail.class);
                        intent.putExtra("product" , product);
                        activity.startActivity(intent);
                    }
                });


                Boolean finalCheckSwitchValue = checkSwitchValue;
                productHolder.switchCompat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);


                        View sheetView = activity.getLayoutInflater().inflate(R.layout.layout_bottomsheet_delete, null);
                        mBottomSheetDialog.setContentView(sheetView);
                        mBottomSheetDialog.setCancelable(true);


                        TextView title = mBottomSheetDialog.findViewById(R.id.title);
                        ImageView imgClose = mBottomSheetDialog.findViewById(R.id.imgClose);
                        Button btnCancel = mBottomSheetDialog.findViewById(R.id.dialog_delete_cancel);
                        Button btnConfirm = mBottomSheetDialog.findViewById(R.id.dialog_delete_confirm);


                        title.setText("Change your product status?");
                        btnCancel.setText(resources.getString(R.string.cancel_delete));
                        btnConfirm.setText(resources.getString(R.string.confirm_delete));

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBottomSheetDialog.dismiss();
                                productHolder.switchCompat.setChecked(finalCheckSwitchValue);
                            }
                        });


                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendChangeStatusToServer(product, mBottomSheetDialog,position, productHolder);
                            }
                        });

                        imgClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                productHolder.switchCompat.setChecked(finalCheckSwitchValue);
                                mBottomSheetDialog.dismiss();
                            }
                        });
                        mBottomSheetDialog.show();
                    }
                });

                productHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);


                        View sheetView = activity.getLayoutInflater().inflate(R.layout.layout_bottomsheet_delete, null);
                        mBottomSheetDialog.setContentView(sheetView);
                        mBottomSheetDialog.setCancelable(true);


                        TextView title = mBottomSheetDialog.findViewById(R.id.title);
                        ImageView imgClose = mBottomSheetDialog.findViewById(R.id.imgClose);
                        Button btnCancel = mBottomSheetDialog.findViewById(R.id.dialog_delete_cancel);
                        Button btnConfirm = mBottomSheetDialog.findViewById(R.id.dialog_delete_confirm);


                        title.setText(resources.getString(R.string.sure));
                        btnCancel.setText(resources.getString(R.string.cancel_delete));
                        btnConfirm.setText(resources.getString(R.string.confirm_delete));

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBottomSheetDialog.dismiss();
                            }
                        });


                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendDelete(product.getId(), position, mBottomSheetDialog);
                            }
                        });

                        imgClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mBottomSheetDialog.dismiss();
                            }
                        });
                        mBottomSheetDialog.show();
                    }
                });

                break;
            }
            case VIEW_TYPE_CART_DETAIL_FOOTER:{

                final FooterHolder footerHoloder = (FooterHolder) parentHolder;
                break;
            }

            case VIEW_TYPE_CART_DETAIL_NO_ITEM:{
                NoItemHolder noItemHolder = (NoItemHolder) parentHolder;
                noItemHolder.textView.setText(resources.getString(R.string.noitem));
                break;
            }
            case VIEW_TYPE_RECYCLER_TITLE:{
                RecyclerTitleHolder recyclerTitleHolder = (RecyclerTitleHolder) parentHolder;
                ProductList pList = (ProductList) universalList.get(position);
                recyclerTitleHolder.title.setText(pList.getTitle());
                break;
            }

        }
    }

    private void sendDelete(int id, int index, BottomSheetDialog mBottomSheetDialog){
      /*  JSONObject uploadMessage = new JSONObject();
        try {
            uploadMessage.put("id",  id);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/




        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT ,constDelete + "?id=" + id , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "onResponse: "  +  constDelete + "?id=" + id );
                        Log.e(TAG, "onResponse: " + response.toString());


                        try{
                            ToastHelper.showToast(activity, resources.getString(R.string.deleted_status));
                            universalList.remove(index);
                            notifyDataSetChanged();
                            mBottomSheetDialog.dismiss();


                        }catch (Exception e){
                            Log.e(TAG, "onResponse: "  + e.getMessage() );
                            ToastHelper.showToast(activity, resources.getString(R.string.error_message));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e(TAG, "onErrorResponse: "  + error.getMessage() );

                ToastHelper.showToast(activity, resources.getString(R.string.error_message));
                mBottomSheetDialog.dismiss();

            }
        }) {

            /*
            Passing some request headers*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", prefs.getStringPreferences(SP_TOKEN));
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq,"sign_in");
    }

    private void sendChangeStatusToServer(ProductList product, BottomSheetDialog dialog, int index, ProductHolder productHolder) {

        String url = "";
        Boolean finalCheckSwitchValue = false;
        if (product.getStatus().equals("available")){
            url = constDisable;
            finalCheckSwitchValue = true;
        }
        else{
            url = constEnable;
            finalCheckSwitchValue = false;
        }

        JSONObject uploadMessage = new JSONObject();
        try {
            uploadMessage.put("id",  product.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "sendChangeStatusToServer: "  + url );


        Boolean finalCheckSwitchValue1 = finalCheckSwitchValue;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT ,url, uploadMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "onResponse: " + response.toString());
                        dialog.dismiss();

                        try{
                            ToastHelper.showToast(activity, resources.getString(R.string.changed_status));
                            if (product.getStatus().equals("available")){
                                product.setStatus("sold");
                            }
                            else{
                                product.setStatus("available");
                            }

                            universalList.remove(index);
                            universalList.add(index,product);
                            notifyDataSetChanged();

                        }catch (Exception e){
                            Log.e(TAG, "onResponse: "  + e.getMessage() );
                            ToastHelper.showToast(activity, resources.getString(R.string.error_message));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                productHolder.switchCompat.setChecked(finalCheckSwitchValue1);
                dialog.dismiss();
                Log.e(TAG, "onErrorResponse: "  + error.getMessage() );

                ToastHelper.showToast(activity, resources.getString(R.string.error_message));


            }
        }) {

            /*
            Passing some request headers*/
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Auth-Token", prefs.getStringPreferences(SP_TOKEN));
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjReq,"sign_in");


    }




}