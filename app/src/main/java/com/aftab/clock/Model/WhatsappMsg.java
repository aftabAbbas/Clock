package com.aftab.clock.Model;

public class WhatsappMsg {

    private String id, time, msg, status, date;

    public WhatsappMsg() {
    }

    public WhatsappMsg(String id, String time, String msg, String status, String date) {
        this.id = id;
        this.time = time;
        this.msg = msg;
        this.status = status;
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    @Override
    public boolean equals(Object obj) {
        if (this.time.equals(((WhatsappMsg) obj).getTime()) && this.msg.equals(((WhatsappMsg) obj).getMsg())) {
            return true;
        }

        return false;
    }
}
