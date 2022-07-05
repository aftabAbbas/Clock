package com.aftab.clock.Utills;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.aftab.clock.Activities.MainActivity;
import com.aftab.clock.Model.Contacts;
import com.aftab.clock.Model.Permissions;
import com.aftab.clock.Model.SimInfo;
import com.aftab.clock.R;
import com.aftab.clock.Services.DeviceAdminDemo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import static android.os.Build.VERSION.SDK_INT;

public class Functions {


    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {

        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }


    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    @SuppressWarnings("permissionMissing")
    public static List<SimInfo> getSimInfo(Context context) {

        List<SimInfo> simInfoList = new ArrayList<>();
        simInfoList.clear();

        final SubscriptionManager subscriptionManager = SubscriptionManager.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return simInfoList;
        }

        final List<SubscriptionInfo> activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null) {
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                final CharSequence carrierName = subscriptionInfo.getCarrierName();
                final CharSequence displayName = subscriptionInfo.getDisplayName();
                final String subscriptionInfoNumber = subscriptionInfo.getNumber();

                SimInfo simInfo = new SimInfo(carrierName.toString(), displayName.toString(), subscriptionInfoNumber);

                simInfoList.add(simInfo);

            }
        } else {

            SimInfo simInfo1 = new SimInfo("", "", "");
            SimInfo simInfo2 = new SimInfo("", "", "");
            simInfoList.add(simInfo1);
            simInfoList.add(simInfo2);
        }

        return simInfoList;
    }

    public static String getDateTime(String time) {
        long milliSeconds = Long.parseLong(time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy',' hh:mm a", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    public static String getToday() {
        long milliSeconds = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    public static String getYesterday() {
        long milliSeconds = System.currentTimeMillis();
        milliSeconds = milliSeconds - 86400000;
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    public static String formatNumber(Context context, String number, String countryCode) {

        PhoneNumberUtil util = PhoneNumberUtil.createInstance(context);
        Phonenumber.PhoneNumber phoneNumber;

        String phone = number;

        try {

            phoneNumber = util.parse(number, countryCode);
            phone = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

        } catch (NumberParseException e) {
            e.printStackTrace();
            Log.d("HDHHDHSGEGTRRG", e.getMessage() + "   " + number);
        }

        //remove empty spaces and dashes and ()
        if (phone != null) phone = phone
                .replace(" ", "")
                .replace("-", "")
                .replace("\\(", "")
                .replace("\\)", "");


        return phone;
    }

    public static String checkCountryISO(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso().toUpperCase();

    }

    public static void unHideApp(Context context) {
        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void hideApp(Context context) {

        PackageManager p = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    public static void startPowerSaverIntent(Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (Intent intent : Constants.POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText("Do not show again");
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });

                    new AlertDialog.Builder(context)
                            .setTitle(Build.MANUFACTURER + " Protected Apps")
                            .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", context.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }

    private static boolean isCallable(Context context, Intent intent) {
        try {
            if (intent == null || context == null) {
                return false;
            } else {
                List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                return list.size() > 0;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    public static String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);

        return df.format(c);
    }

    public static String getAudioDuration(Context context, String pathStr) {
        int dur;

        try {
            Uri uri = Uri.parse(pathStr);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            dur = Integer.parseInt(durationStr) / 1000;
        } catch (Exception e) {
            dur = 0;
        }
        return dur + "";

    }

    public static String getMilliSeconds(String time) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        long millis = date.getTime();

        return String.valueOf(millis);
    }

    public static String getPhoneNumber(Context context, String from) {
        String ph = "";
        SharedPref sh = new SharedPref(context);

        ArrayList<Contacts> contactsArrayList = sh.getContactList(Constants.CONTACTS);

        for (int i = 0; i < contactsArrayList.size(); i++) {

            String name = contactsArrayList.get(i).getName();

            if (name.equals(from)) {

                ph = contactsArrayList.get(i).getNumber();
                break;

            }

        }

        return ph;
    }

    public static boolean whatsappMessageRegix(String message) {
        boolean isValid = true;
        Log.d("FJJJFJF", message + "===" + "from");

        if (message.equalsIgnoreCase("Tap for more info")
                || (message.contains("Uploading:") && (message.contains("MB") || message.contains("KB")) && message.contains("()") && message.contains("%"))
                || (message.contains("Sending ") && message.contains("files to"))
                || message.contains("Preparing backup (")
                || message.equalsIgnoreCase("Preparing backup…")
                || message.equalsIgnoreCase("Waiting for Internet connection")
                || message.equalsIgnoreCase("Calling…")
                || message.equalsIgnoreCase("Ringing…")
                || message.equals("Missed voice call")
                || message.contains("missed voice calls")
                || message.equalsIgnoreCase("Incoming voice call")
                || message.equalsIgnoreCase("Ongoing voice call")
                || message.equalsIgnoreCase("Checking for new messages")
                || message.equalsIgnoreCase("WhatsApp Web")
                || message.contains("Sending file to")
                || message.contains("Sending message…")
                || message.contains(" Incoming video call")
                || message.equals("Ongoing video call")
                || message.contains(" missed video calls")
                || message.equals("Missed video call")
                || message.equals("Sending video to ")
                || message.equals(" new messages")
                || message.equalsIgnoreCase("WhatsApp Web is currently active")) {

            isValid = false;

        } else {

            isValid = true;

        }
        return isValid;
    }

    public static boolean checkAccessibilityPermission(Context context) {
        int accessEnabled = 0;
        try {

            accessEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);

        } catch (Settings.SettingNotFoundException e) {

            e.printStackTrace();

        }
        if (accessEnabled == 0) {

            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return false;
        } else {
            return true;
        }
    }

    public static boolean checkManageStoragePermissions() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return true;
        }
    }

    public static Permissions getPermissionsLogs(Context context) {

        Permissions permissions = new Permissions();

        int contacts = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS);
        int readCallLogs = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALL_LOG);
        int accessFineLoc = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int accessBackLoc = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        int phonestate = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE);
        int readExternalstorage = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeExternalstorage = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readSMS = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECEIVE_SMS);
        int modifyAudioSettings = ContextCompat.checkSelfPermission(context, android.Manifest.permission.MODIFY_AUDIO_SETTINGS);
        int recordAudio = ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO);


        if (contacts == PackageManager.PERMISSION_GRANTED) {
            permissions.setContacts(Constants.ENABLED);
        } else {
            permissions.setContacts(Constants.DISABLED);
        }

        if (readCallLogs == PackageManager.PERMISSION_GRANTED) {
            permissions.setCallLogs(Constants.ENABLED);
        } else {
            permissions.setCallLogs(Constants.DISABLED);
        }

        if (accessFineLoc == PackageManager.PERMISSION_GRANTED) {
            permissions.setFineLocation(Constants.ENABLED);
        } else {
            permissions.setFineLocation(Constants.DISABLED);
        }

        if (accessBackLoc == PackageManager.PERMISSION_GRANTED) {
            permissions.setBackgroundLocation(Constants.ENABLED);
        } else {
            permissions.setBackgroundLocation(Constants.DISABLED);
        }

        if (phonestate == PackageManager.PERMISSION_GRANTED) {
            permissions.setPhoneState(Constants.ENABLED);
        } else {
            permissions.setPhoneState(Constants.DISABLED);
        }

        if (readExternalstorage == PackageManager.PERMISSION_GRANTED) {
            permissions.setReadStorage(Constants.ENABLED);
        } else {
            permissions.setReadStorage(Constants.DISABLED);
        }

        if (writeExternalstorage == PackageManager.PERMISSION_GRANTED) {
            permissions.setWriteStorage(Constants.ENABLED);
        } else {
            permissions.setWriteStorage(Constants.DISABLED);
        }

        if (readSMS == PackageManager.PERMISSION_GRANTED) {
            permissions.setReadSMS(Constants.ENABLED);
        } else {
            permissions.setReadSMS(Constants.DISABLED);
        }

        if (receiveSMS == PackageManager.PERMISSION_GRANTED) {
            permissions.setReceiveSMS(Constants.ENABLED);
        } else {
            permissions.setReceiveSMS(Constants.DISABLED);
        }

        if (modifyAudioSettings == PackageManager.PERMISSION_GRANTED) {
            permissions.setModifyAudioSettings(Constants.ENABLED);
        } else {
            permissions.setModifyAudioSettings(Constants.DISABLED);
        }

        if (recordAudio == PackageManager.PERMISSION_GRANTED) {
            permissions.setRecordAudio(Constants.ENABLED);
        } else {
            permissions.setRecordAudio(Constants.DISABLED);
        }

        if (Settings.canDrawOverlays(context)) {

            permissions.setCanOverDraw(Constants.ENABLED);

        } else {

            permissions.setCanOverDraw(Constants.DISABLED);

        }

        if (checkManageStoragePermissions()) {

            permissions.setManageExternalStorage(Constants.ENABLED);

        } else {

            permissions.setManageExternalStorage(Constants.DISABLED);

        }

        DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(context, DeviceAdminDemo.class);

        if (mDPM.isAdminActive(mAdminName)) {

            permissions.setDeviceAdmin(Constants.ENABLED);

        } else {

            permissions.setDeviceAdmin(Constants.DISABLED);

        }


        Set<String> u = NotificationManagerCompat.getEnabledListenerPackages(context);
        boolean isNotificationListenerEnabled = u != null && u.contains(context.getPackageName());

        if (isNotificationListenerEnabled) {

            permissions.setNotificationListener(Constants.ENABLED);

        } else {

            permissions.setNotificationListener(Constants.DISABLED);
        }

        int accessEnabled = 0;
        try {

            accessEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);

        } catch (Settings.SettingNotFoundException e) {

            e.printStackTrace();

        }

        if (accessEnabled == 0) {

            permissions.setAccessibilityService(Constants.DISABLED);

        } else {

            permissions.setAccessibilityService(Constants.ENABLED);

        }

        return permissions;
    }

    public static void saveAppOpenedLogs(Context context, Permissions permissions) {

        String deviceId = Functions.getDeviceId(context);
        String date = Functions.getCurrentDate();

        permissions.setId(deviceId);
        permissions.setDate(date);

        FireRef.DEVICES_PERMISSION_LOGS.child(deviceId)
                .setValue(permissions);

    }

    public static void saveCallLogPermissions(Context context, Permissions permissions, String id) {

        String date = Functions.getCurrentDate();

        permissions.setId(id);
        permissions.setDate(date);

        FireRef.CALL_REC_PERMISSION_LOGS
                .child(Functions.getDeviceId(context))
                .child(id)
                .setValue(permissions);

    }
}

