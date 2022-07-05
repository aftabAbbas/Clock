package com.aftab.clock.Model;

import java.io.Serializable;

public class Recordings implements Serializable {

    private String name, date, filePath,duration;

    public Recordings() {
    }

    public Recordings(String name, String date, String filePath, String duration) {
        this.name = name;
        this.date = date;
        this.filePath = filePath;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
