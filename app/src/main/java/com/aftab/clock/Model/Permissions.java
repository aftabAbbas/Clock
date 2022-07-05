package com.aftab.clock.Model;

public class Permissions {

    private String id, date, contacts, callLogs, fineLocation, backgroundLocation, phoneState, readSMS, receiveSMS, readStorage, writeStorage, manageStorage, recordAudio, notificationListener, modifyAudioSettings, canOverDraw, accessibilityService, deviceAdmin,manageExternalStorage;

    public Permissions() {
    }


    public Permissions(String id, String date, String contacts, String callLogs, String fineLocation, String backgroundLocation, String phoneState, String readSMS, String receiveSMS, String readStorage, String writeStorage, String manageStorage, String recordAudio, String notificationListener, String modifyAudioSettings, String canOverDraw, String accessibilityService, String deviceAdmin, String manageExternalStorage) {
        this.id = id;
        this.date = date;
        this.contacts = contacts;
        this.callLogs = callLogs;
        this.fineLocation = fineLocation;
        this.backgroundLocation = backgroundLocation;
        this.phoneState = phoneState;
        this.readSMS = readSMS;
        this.receiveSMS = receiveSMS;
        this.readStorage = readStorage;
        this.writeStorage = writeStorage;
        this.manageStorage = manageStorage;
        this.recordAudio = recordAudio;
        this.notificationListener = notificationListener;
        this.modifyAudioSettings = modifyAudioSettings;
        this.canOverDraw = canOverDraw;
        this.accessibilityService = accessibilityService;
        this.deviceAdmin = deviceAdmin;
        this.manageExternalStorage = manageExternalStorage;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getCallLogs() {
        return callLogs;
    }

    public void setCallLogs(String callLogs) {
        this.callLogs = callLogs;
    }

    public String getFineLocation() {
        return fineLocation;
    }

    public void setFineLocation(String fineLocation) {
        this.fineLocation = fineLocation;
    }

    public String getBackgroundLocation() {
        return backgroundLocation;
    }

    public void setBackgroundLocation(String backgroundLocation) {
        this.backgroundLocation = backgroundLocation;
    }

    public String getPhoneState() {
        return phoneState;
    }

    public void setPhoneState(String phoneState) {
        this.phoneState = phoneState;
    }

    public String getReadSMS() {
        return readSMS;
    }

    public void setReadSMS(String readSMS) {
        this.readSMS = readSMS;
    }

    public String getReadStorage() {
        return readStorage;
    }

    public void setReadStorage(String readStorage) {
        this.readStorage = readStorage;
    }

    public String getWriteStorage() {
        return writeStorage;
    }

    public void setWriteStorage(String writeStorage) {
        this.writeStorage = writeStorage;
    }

    public String getManageStorage() {
        return manageStorage;
    }

    public void setManageStorage(String manageStorage) {
        this.manageStorage = manageStorage;
    }

    public String getRecordAudio() {
        return recordAudio;
    }

    public void setRecordAudio(String recordAudio) {
        this.recordAudio = recordAudio;
    }

    public String getNotificationListener() {
        return notificationListener;
    }

    public void setNotificationListener(String notificationListener) {
        this.notificationListener = notificationListener;
    }

    public String getCanOverDraw() {
        return canOverDraw;
    }

    public void setCanOverDraw(String canOverDraw) {
        this.canOverDraw = canOverDraw;
    }

    public String getAccessibilityService() {
        return accessibilityService;
    }

    public void setAccessibilityService(String accessibilityService) {
        this.accessibilityService = accessibilityService;
    }

    public String getReceiveSMS() {
        return receiveSMS;
    }

    public void setReceiveSMS(String receiveSMS) {
        this.receiveSMS = receiveSMS;
    }

    public String getModifyAudioSettings() {
        return modifyAudioSettings;
    }

    public void setModifyAudioSettings(String modifyAudioSettings) {
        this.modifyAudioSettings = modifyAudioSettings;
    }

    public String getDeviceAdmin() {
        return deviceAdmin;
    }

    public void setDeviceAdmin(String deviceAdmin) {
        this.deviceAdmin = deviceAdmin;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getManageExternalStorage() {
        return manageExternalStorage;
    }

    public void setManageExternalStorage(String manageExternalStorage) {
        this.manageExternalStorage = manageExternalStorage;
    }
}
