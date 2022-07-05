package com.aftab.clock.Model;

import java.io.Serializable;

public class DeviceInfo implements Serializable {

    private String deviceId;
    private String deviceName;
    private String androidVersion;
    private String sim1Display;
    private String sim2Display;
    private String sim1Num;
    private String sim2Num;
    private String firstName;
    private String lastName;
    private String password;

    public DeviceInfo(String deviceId, String deviceName, String androidVersion, String sim1Display, String sim2Display, String sim1Num, String sim2Num, String firstName, String lastName, String password) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.androidVersion = androidVersion;
        this.sim1Display = sim1Display;
        this.sim2Display = sim2Display;
        this.sim1Num = sim1Num;
        this.sim2Num = sim2Num;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public DeviceInfo() {
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSim1Display() {
        return sim1Display;
    }

    public void setSim1Display(String sim1Display) {
        this.sim1Display = sim1Display;
    }

    public String getSim2Display() {
        return sim2Display;
    }

    public void setSim2Display(String sim2Display) {
        this.sim2Display = sim2Display;
    }

    public String getSim1Num() {
        return sim1Num;
    }

    public void setSim1Num(String sim1Num) {
        this.sim1Num = sim1Num;
    }

    public String getSim2Num() {
        return sim2Num;
    }

    public void setSim2Num(String sim2Num) {
        this.sim2Num = sim2Num;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }
}
