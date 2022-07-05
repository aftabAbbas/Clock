package com.aftab.clock.Model;

import java.io.Serializable;

public class WhatsappCallLogs implements Serializable {

    private String id, name, phoneNumber, callType, callDate, callDuration, location,url;
    private boolean isRead;

    public WhatsappCallLogs() {
    }

    public WhatsappCallLogs(String id, String name, String phoneNumber, String callType, String callDate, String callDuration, String location, String url, boolean isRead) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callDuration = callDuration;
        this.location = location;
        this.url = url;
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
