package com.techmyanmar.kcct.kyarlaysupplier.operation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyPreference implements ConstanceVariable {

    SharedPreferences prefs;
    Activity activity;

    public MyPreference(Activity activity) {
        this.activity = activity;
        if(prefs == null) {
            prefs =  activity.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        }
    }

    public boolean isContains(String name){
        boolean check = false;
        if(prefs.contains(name))
            return true;
        return check;
    }

    public void clearAll(){
        prefs.edit().clear().commit();
    }
    public void saveStringPreferences(String name, String value){
        prefs.edit().putString(name, value).commit();
    }
    public void saveIntPerferences(String name, int value){
        prefs.edit().putInt(name, value).commit();
    }

    public void saveFloatPerferences(String name, float value){
        prefs.edit().putFloat(name, value).commit();
    }

    public  float getFloatPreferences(String name){
        float result = 0.0f;
        result = prefs.getFloat(name, 0);
        return result;
    }

    public  int getIntPreferences(String name){
        int result = 0;
        result = prefs.getInt(name, 0);
        return result;
    }

    public String getStringPreferences(String name){
        String result = "";
        result =  prefs.getString(name, result);
        return result;
    }

    public boolean getBooleanPreference(String name){
        boolean result  = false;
        result  = prefs.getBoolean(name, result);
        return result;
    }

    public void saveBooleanPreference(String name, boolean value){
        prefs.edit().putBoolean(name, value).commit();
    }

    public void remove(String name){
        prefs.edit().remove(name).commit();
    }

    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }



}
