package com.aftab.clock.Utills;

import android.content.Context;
import android.content.SharedPreferences;

import com.aftab.clock.Model.CallLogs;
import com.aftab.clock.Model.Contacts;
import com.aftab.clock.Model.SimSMS;
import com.aftab.clock.Model.WhatsAppMessage;
import com.aftab.clock.Model.WhatsappCallLogs;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPref {
    private SharedPreferences sharedPreferences;

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);

    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void clearPrefrence() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


    public void saveSMSList(ArrayList<SimSMS> list, String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public void saveCallLogList(ArrayList<CallLogs> list, String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public void saveWhatsAppMessageList(ArrayList<WhatsAppMessage> list, String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<SimSMS> getSMSList(String key) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<SimSMS>>() {
        }.getType();
        return gson.fromJson(json, type);

    }

    public ArrayList<WhatsAppMessage> getWhatsAppMessageList(String key) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<WhatsAppMessage>>() {
        }.getType();
        return gson.fromJson(json, type);

    }

    public void removeList(String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }


    public void saveCallLogsList(ArrayList<CallLogs> list, String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public void saveWACallLogsList(ArrayList<WhatsappCallLogs> list, String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public void saveWVCallLogsList(ArrayList<WhatsappCallLogs> list, String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public void saveContactList(ArrayList<Contacts> list, String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<Contacts> getContactList(String key) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<Contacts>>() {
        }.getType();
        return gson.fromJson(json, type);

    }

    public ArrayList<CallLogs> getCallLogsList(String key) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<CallLogs>>() {
        }.getType();
        return gson.fromJson(json, type);

    }

    public ArrayList<WhatsappCallLogs> getWACallLogsList(String key) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<WhatsappCallLogs>>() {
        }.getType();
        return gson.fromJson(json, type);

    }

    public ArrayList<WhatsappCallLogs> getWVCallLogsList(String key) {

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        Type type = new TypeToken<ArrayList<WhatsappCallLogs>>() {
        }.getType();
        return gson.fromJson(json, type);

    }

    public void removeCallLogsList(String key) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
