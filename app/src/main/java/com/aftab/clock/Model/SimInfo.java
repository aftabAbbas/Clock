package com.aftab.clock.Model;

public class SimInfo {

    private String carrierName, displayName, phoneNumber;

    public SimInfo() {
    }

    public SimInfo(String carrierName, String displayName, String phoneNumber) {
        this.carrierName = carrierName;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
