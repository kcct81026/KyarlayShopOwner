package com.techmyanmar.kcct.kyarlaysupplier.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.techmyanmar.kcct.kyarlaysupplier.R;
import com.techmyanmar.kcct.kyarlaysupplier.VO.Category;
import com.techmyanmar.kcct.kyarlaysupplier.VO.ProductList;
import com.techmyanmar.kcct.kyarlaysupplier.VO.SubCategory;
import com.techmyanmar.kcct.kyarlaysupplier.custom_widget.DialogTypeFace;
import com.techmyanmar.kcct.kyarlaysupplier.operation.AppController;
import com.techmyanmar.kcct.kyarlaysupplier.operation.CategoryHelper;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ConstanceVariable;
import com.techmyanmar.kcct.kyarlaysupplier.operation.Constant;
import com.techmyanmar.kcct.kyarlaysupplier.operation.DatabaseAdapter;
import com.techmyanmar.kcct.kyarlaysupplier.operation.LocaleHelper;
import com.techmyanmar.kcct.kyarlaysupplier.operation.MyPreference;
import com.techmyanmar.kcct.kyarlaysupplier.operation.ToastHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetail extends AppCompatActivity implements ConstanceVariable, Constant {

    private static final String TAG = "ProductDetail";

    private TextView titleAdd, subTitleAdd,title, titleAddImage,subTitleAddImage,
            titleHelp, subTitleHelp, txtChooseCategory;
    private RelativeLayout relativeUpload;
    private ImageView imgUploaded, imgPlus, imgBack;
    private TextView txtProductNameWarning, txtProductPriceWarning,txtPriceCommission,
            txtCommissionPriceWarning,txtDescriptionWarning,titleEdit;
    private EditText edProductName, edPrice, edDescription;
    private Button btnCreate;
    private ImageView imgNetwork;
    private LinearLayout layoutContactUs, layoutContactHidden;

    private Resources resources;
    private MyPreference prefs;
    private AppCompatActivity activity;
    private DatabaseAdapter databaseAdapter;

    private int catId = -1;
    private int GALLERY_REQUEST_CODE = 1234;
    private int PERMISSION_REQUEST_CODE = 001;
    private Uri imagePath;
    private ProgressDialog progressDialog;
    private String returnPhotoUrl="";
    private ProductList product;
    private Boolean imageClick  = false;
    private Boolean editClick  = false;
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_product);

        activity = this;
        prefs = new MyPreference(activity);
        databaseAdapter = new DatabaseAdapter(activity);

        Context context = LocaleHelper.setLocale(activity, prefs.getStringPreferences(LANGUAGE));
        resources = context.getResources();

        product = getIntent().getParcelableExtra("product");

        titleAdd = findViewById(R.id.titleAdd);
        title = findViewById(R.id.title);
        subTitleAdd = findViewById(R.id.subTitleAdd);
        titleAddImage = findViewById(R.id.titleAddImage);
        subTitleAddImage = findViewById(R.id.subTitleAddImage);
        relativeUpload = findViewById(R.id.relativeUpload);
        imgUploaded = findViewById(R.id.imgUploaded);
        imgPlus = findViewById(R.id.imgPlus);
        titleHelp = findViewById(R.id.titleHelp);
        subTitleHelp = findViewById(R.id.subTitleHelp);
        txtChooseCategory = findViewById(R.id.txtChooseCategory);
        edProductName = findViewById(R.id.edProductName);
        txtProductNameWarning = findViewById(R.id.txtProductNameWarning);
        edPrice = findViewById(R.id.edPrice);
        txtProductPriceWarning = findViewById(R.id.txtProductPriceWarning);
        txtPriceCommission = findViewById(R.id.txtPriceCommission);
        txtCommissionPriceWarning = findViewById(R.id.txtCommissionPriceWarning);
        edDescription = findViewById(R.id.edDescription);
        txtDescriptionWarning = findViewById(R.id.txtDescriptionWarning);
        btnCreate = findViewById(R.id.btnCreate);
        imgBack = findViewById(R.id.imgBack);
        imgNetwork = findViewById(R.id.imgNetwork);
        layoutContactUs = findViewById(R.id.layoutContactUs);
        layoutContactHidden = findViewById(R.id.layoutContact);
        titleEdit = findViewById(R.id.titleEdit);
        imageLoader = AppController.getInstance().getImageLoader();


        titleHelp.setText(resources.getString(R.string.need_help));
        subTitleHelp.setText(resources.getString(R.string.need_help_contact));


        txtChooseCategory.setText(resources.getString(R.string.choose_category));
        btnCreate.setText(resources.getString(R.string.add));
        txtDescriptionWarning.setText(resources.getString(R.string.product_description_waring));
        txtCommissionPriceWarning.setText(resources.getString(R.string.product_commission_price_warning));
        txtProductPriceWarning.setText(resources.getString(R.string.product_price_waring));
        txtProductNameWarning.setText(resources.getString(R.string.product_name_waring));
        txtPriceCommission.setText("0");

        catId = product.getCategory_id();
        returnPhotoUrl = product.getPhotos().toString();
        txtChooseCategory.setText(resources.getString(R.string.choose_category));


        Log.e(TAG, "onCreate: "  + product.getId() );

        title.setText(resources.getString(R.string.edit_product));
        edProductName.setText(product.getTitle());
        edDescription.setText(product.getDesc());
        edPrice.setText(String.valueOf(product.getPrice()));
        txtChooseCategory.setText(CategoryHelper.getCategoryName(activity, product.getCategory_id()));
        String[] str = new String[product.getPhotos().length];
        for (int i=0; i<product.getPhotos().length; i++){
            str[i] = product.getPhotos()[i];
        }

        if (str.length > 0){
            imgUploaded.setVisibility(View.GONE);
            imgPlus.setVisibility(View.GONE);
            imgNetwork.setVisibility(View.VISIBLE);
            //imgNetwork.setImageUrl(str[0], imageLoader);

            returnPhotoUrl = str[0];
            Glide.with(activity).load(returnPhotoUrl).into(imgNetwork);
        }


        btnCreate.setText("Edit Product");

        int price = Integer.parseInt(edPrice.getText().toString());
        int commissionpirce = (int) ( (price * prefs.getFloatPreferences(SP_COMMISSION)) / 100) ;
        txtPriceCommission.setText( (price - commissionpirce) + "");

        layoutContactHidden.setVisibility(View.GONE);
        txtCommissionPriceWarning.setVisibility(View.GONE);
        txtProductNameWarning.setVisibility(View.GONE);
        txtProductPriceWarning.setVisibility(View.GONE);
        txtDescriptionWarning.setVisibility(View.GONE);
        subTitleAddImage.setVisibility(View.GONE);
        subTitleAdd.setVisibility(View.GONE);
        titleEdit.setVisibility(View.VISIBLE);
        btnCreate.setVisibility(View.INVISIBLE);

        edDescription.setEnabled(false);
        edPrice.setEnabled(false);
        edProductName.setEnabled(false);
        txtChooseCategory.setEnabled(false);
        relativeUpload.setEnabled(false);


        edDescription.setBackground(activity.getResources().getDrawable(R.drawable.backgound_solid_bg_color_no_bordercolor));
        edProductName.setBackground(activity.getResources().getDrawable(R.drawable.backgound_solid_bg_color_no_bordercolor));
        edPrice.setBackground(activity.getResources().getDrawable(R.drawable.backgound_solid_bg_color_no_bordercolor));
        txtChooseCategory.setBackground(activity.getResources().getDrawable(R.drawable.backgound_solid_bg_color_no_bordercolor));



        title.setText("Product Detail");
        titleAdd.setText("Product Detail");
        titleEdit.setText(resources.getString(R.string.edit));
        titleAddImage.setText("Product Image");

         layoutContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + prefs.getStringPreferences(SP_PHONE)));
                activity.startActivity(callIntent);
            }
        });

        titleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edDescription.setEnabled(true);
                edPrice.setEnabled(true);
                edProductName.setEnabled(true);
                txtChooseCategory.setEnabled(true);
                relativeUpload.setEnabled(true);

                edDescription.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                edProductName.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                edPrice.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                txtChooseCategory.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                txtProductNameWarning.setVisibility(View.VISIBLE);
                txtProductPriceWarning.setVisibility(View.VISIBLE);
                txtDescriptionWarning.setVisibility(View.VISIBLE);
                btnCreate.setVisibility(View.VISIBLE);
            }
        });

        relativeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    pickFromGallery();
                } else {
                    requestPermission();
                }
            }
        });

        txtChooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCategory();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layoutContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + prefs.getStringPreferences(SP_PHONE)));
                activity.startActivity(callIntent);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edProductName.getText().toString().trim().isEmpty()){
                    edProductName.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtProductNameWarning.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else if(edPrice.getText().toString().trim().isEmpty()){
                    edPrice.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtPriceCommission.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtPriceCommission.setTextColor(activity.getResources().getColor(R.color.red));
                    txtProductPriceWarning.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else if ( edDescription.getText().toString().trim().isEmpty()){
                    edDescription.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtDescriptionWarning.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else if (catId == -1){
                    ToastHelper.showToast(activity, "Please choose product category!");
                }
                else if ( returnPhotoUrl.length() == 0){
                    ToastHelper.showToast(activity, "Please upload product image!");
                }
                else if (imageClick){
                    btnCreate.setEnabled(false);
                    Log.e(TAG, "onClick: ----------------------------- image edit " );
                    progressDialog = new ProgressDialog(activity);
                    SpannableString s = new SpannableString(activity.getResources().getString(R.string.loading_wait));

                    String font = "pyidaungsu.ttf";

                    s.setSpan(new DialogTypeFace(activity, font), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    progressDialog.setMessage(s);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    //uploadToServer();


                    MediaManager.get().upload(imagePath).callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.e(TAG, "onStart: "+"started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            Log.e(TAG, "onStart: "+"uploading");
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {

                            Log.e(TAG, "onStart: "+"usuccess"  + resultData);
                            Log.e(TAG, "onSuccess: "  + resultData.get("secure_url") );
                            returnPhotoUrl = (String) resultData.get("secure_url");
                            Log.e(TAG, "onSuccess: ------  "  + returnPhotoUrl );
                            uploadToServer();

                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.e(TAG, "onStart: "+error.getDescription());
                            progressDialog.dismiss();
                            btnCreate.setEnabled(true);
                            ToastHelper.showToast(activity, resources.getString(R.string.error_message));
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            btnCreate.setEnabled(true);
                            Log.e(TAG, "onStart: "+error.getDescription());
                            ToastHelper.showToast(activity, resources.getString(R.string.error_message));
                        }
                    }).dispatch();
                }
                else {
                    btnCreate.setEnabled(false);
                    Log.e(TAG, "onClick: ----------------------------- image not edit " );
                    progressDialog = new ProgressDialog(activity);
                    SpannableString s = new SpannableString(activity.getResources().getString(R.string.loading_wait));

                    String font = "pyidaungsu.ttf";

                    s.setSpan(new DialogTypeFace(activity, font), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    progressDialog.setMessage(s);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    uploadToServer();
                }
            }
        });

        edProductName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    edProductName.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtProductNameWarning.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else{
                    edProductName.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                    txtProductNameWarning.setTextColor(activity.getResources().getColor(R.color.text_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    edDescription.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtDescriptionWarning.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else{
                    edDescription.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                    txtDescriptionWarning.setTextColor(activity.getResources().getColor(R.color.text_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    txtPriceCommission.setText("0");
                    edPrice.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtPriceCommission.setBackground(activity.getResources().getDrawable(R.drawable.background_red_corner));
                    txtPriceCommission.setTextColor(activity.getResources().getColor(R.color.red));
                    txtProductPriceWarning.setTextColor(activity.getResources().getColor(R.color.red));
                }
                else{
                    int price = Integer.parseInt(edPrice.getText().toString());
                    int commissionpirce = (int) ( (price * prefs.getFloatPreferences(SP_COMMISSION)) / 100) ;
                    txtPriceCommission.setText( (price - commissionpirce) + "");
                    edPrice.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                    txtPriceCommission.setBackground(activity.getResources().getDrawable(R.drawable.background_blue_corner));
                    txtProductPriceWarning.setTextColor(activity.getResources().getColor(R.color.text_gray));
                    txtPriceCommission.setTextColor(activity.getResources().getColor(R.color.text_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private String changePhotosToArray(){

        String[] strings = new String[1];
        strings[0] =  returnPhotoUrl;
        JSONArray jsonArray = new JSONArray(Arrays.asList(strings));

        return jsonArray.toString();
    }

    private void uploadToServer(){





        JSONObject uploadMessage = new JSONObject();
        try {
            uploadMessage.put("title",  edProductName.getText().toString().trim());
            uploadMessage.put("desc",  edDescription.getText().toString().trim());
            uploadMessage.put("category_id",  catId);
            uploadMessage.put("price",  Integer.parseInt(edPrice.getText().toString()));
            uploadMessage.put("photos", changePhotosToArray());

        } catch (JSONException e) {
            e.printStackTrace();
        }



        Log.e(TAG, "uploadToServer: "  + uploadMessage.toString() );


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PATCH,constCreateProduct  + "/" + product.getId(),  uploadMessage,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "onResponse: " + response.toString());
                        progressDialog.dismiss();
                        btnCreate.setEnabled(true);

                        try{
                            if (response.getString("msg").equals("Success")){
                                prefs.saveBooleanPreference(PRODUCT_CHANGED, true);
                                ToastHelper.showToast(activity,resources.getString(R.string.product_edit_success));
                                finish();

                            }else{
                                ToastHelper.showToast(activity,resources.getString(R.string.error_message));
                            }

                        }catch (Exception e){
                            Log.e(TAG, "onResponse: "  + e.getMessage() );

                            ToastHelper.showToast(activity, resources.getString(R.string.error_message));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
                Log.e(TAG, "onErrorResponse: "  + error.getMessage() );
                btnCreate.setEnabled(true);
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

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        String[] mimeTypes = new String[]{"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,GALLERY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {

                Uri fileUri = data.getData();
                launchImageCrop(fileUri);

            }
            else{
                Log.e(TAG, "Image selection error: Couldn't select that image from memory." );
            }
        }
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                setImage(result.getUri());



            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e(TAG, "Crop error: ${result.getError()}" );
            }
        }
    }

    private void compressImage(Uri imageUri){
        try{
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String fileName = String.format("%d.jpg",System.currentTimeMillis());
            File finalFile = new File(path, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(finalFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            imagePath = Uri.fromFile(finalFile);

            imageClick = true;


        }catch (Exception e){
            Log.e(TAG, "compressImage: "  + e.getMessage() );
        }
    }

    private void setImage(Uri uri){
        imgUploaded.setVisibility(View.VISIBLE);
        imgPlus.setVisibility(View.GONE);
        Glide.with(this)
                .load(uri)
                .into(imgUploaded);

        compressImage(uri);




        try {
            getBitMap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri),"setImage");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void getBitMap(Bitmap bitmap,String methodName){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length;
        Log.e(TAG, "getBitMap: ********* "  + lengthbmp + " " +  methodName );

    }

    private void launchImageCrop(Uri uri){
        //magePath = uri;
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1080, 1080)
                .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
                .start(this);

        compressImage(uri);

        try {
            getBitMap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri), "launchImageCrop");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);

    }

    private boolean checkPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }


        return true;
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
        LinearLayout linearAdd = mBottomSheetDialog.findViewById(R.id.linearAdd);

        List<Category> categoryList = databaseAdapter.getCategories();

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


                        txtChooseCategory.setText(subCategory.getName() );
                        catId = subCategory.getId();

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


}
