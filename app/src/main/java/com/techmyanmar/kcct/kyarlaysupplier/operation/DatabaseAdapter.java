package com.techmyanmar.kcct.kyarlaysupplier.operation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.techmyanmar.kcct.kyarlaysupplier.VO.Category;
import com.techmyanmar.kcct.kyarlaysupplier.VO.SubCategory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter implements ConstantDB, ConstanceVariable {


    private static final String DATABASE_NAME = "KL_SHOPOWNER_DB";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private DBHelper dBHelper;
    private static SQLiteDatabase db = null;

    SharedPreferences prefs;
    String SP_NAME = "KyarlayShopOwner";
    private static final String TAG = "DatabaseAdapter";

    public DatabaseAdapter(Context context) {
        this.context = context;
        if (prefs == null) {
            prefs = context.getSharedPreferences(SP_NAME, Activity.MODE_PRIVATE);
        }
        dBHelper = new DBHelper(context);
        open();
    }

    public DatabaseAdapter open() {
        try {
            if (db == null || !db.isOpen()) {
                db = dBHelper.get_database();
            }
        } catch (SQLException e) {
        }
        return this;
    }

    // close the database
    public void close() throws SQLException {
        if (db == null && db.isOpen())
            dBHelper.close();
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            db = getWritableDatabase();
        }

        public SQLiteDatabase get_database() {
            return db;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                String category = "create table " + CATEGORY_TABLE +
                        " (" + pId + " integer primary key autoincrement," + ID + " integer not null ," +
                        NAME + " text not null );";
                db.execSQL(category);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String category = "create table " + CATEGORY_SUB_TABLE +
                        " (" + pId + " integer primary key autoincrement," + ID + " integer not null ," +
                        C_ID + " integer not null ," +
                        NAME + " text not null );";
                db.execSQL(category);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //super.onDowngrade(db, oldVersion, newVersion);
            try {
                db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);
            } catch (Exception e) {
                Log.e(TAG, "onDowngrade: " + e.getMessage());
            }

            try {
                db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_SUB_TABLE);
            } catch (Exception e) {
                Log.e(TAG, "onDowngrade: " + e.getMessage());
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE);

            } catch (Exception e) {
                Log.e(TAG, "onUpgrade: " + e.getMessage());
            }

            try {
                db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_SUB_TABLE);
            } catch (Exception e) {
                Log.e(TAG, "onDowngrade: " + e.getMessage());
            }

        }

    }

    public void deleteAllColumn(String table) {
        db.delete(table, null, null);
    }


    @SuppressLint("Range")
    public ArrayList<Category> getCategories() {
        ArrayList<Category> categoryList = new ArrayList<>();
        Cursor cursor = db.query(CATEGORY_TABLE, null, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                List<SubCategory> subCategoryList = new ArrayList<>();

                Cursor cursor1 = db.query(CATEGORY_SUB_TABLE, null, C_ID + "=?", new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex(ID)))}, null, null, null);
                if (cursor1 != null && cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    do {
                        SubCategory subCategory = new SubCategory();
                        subCategory.setId(cursor1.getInt(cursor1.getColumnIndex(C_ID)));
                        subCategory.setId(cursor1.getInt(cursor1.getColumnIndex(ID)));
                        subCategory.setName(cursor1.getString(cursor1.getColumnIndex(NAME)));
                        subCategoryList.add(subCategory);
                    } while (cursor1.moveToNext());
                }

                category.setCategoryArrayList(subCategoryList);

                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        return categoryList;
    }

    public  void insertCategories(List<Category> categoryList) {

        deleteAllColumn(CATEGORY_TABLE);
        deleteAllColumn(CATEGORY_SUB_TABLE);

        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);

            try{
                ContentValues pro = new ContentValues();
                pro.put(ID, category.getId());
                pro.put(NAME, category.getName());

                for(int j = 0 ; j < category.getCategoryArrayList().size(); j++){
                    SubCategory subCategory = category.getCategoryArrayList().get(j);

                    ContentValues to = new ContentValues();
                    to.put(C_ID,        category.getId());
                    to.put(ID,      subCategory.getId());
                    to.put(NAME,       subCategory.getName());
                    long imageReturn = db.insert(CATEGORY_SUB_TABLE, null, to);
                }
                long cityReturn = db.insert(CATEGORY_TABLE, null, pro);


            }catch (Exception e){
            }

        }
    }

}