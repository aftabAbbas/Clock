package com.aftab.clock.Model;

public class WhatsAppMessage {

    private String id, address, msg, type, time,location;
    private boolean isRead;

    public WhatsAppMessage() {

    }

    public WhatsAppMessage(String id, String address, String msg, String type, String time, String location, boolean isRead) {
        this.id = id;
        this.address = address;
        this.msg = msg;
        this.type = type;
        this.time = time;
        this.location = location;
        this.isRead = isRead;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}
