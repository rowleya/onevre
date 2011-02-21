package com.googlecode.onevre.gwt.client.ag.types;

public class UploadStatus {

    // Who the message is from
    private String fileName = "";

    // The message
    private long size = 0;

    // The date of the message
    private long done = 0;

    public UploadStatus(UploadStatusJSO jso) {
        fileName = jso.getFileName();
        size = Long.parseLong(jso.getSize());
        done = Long.parseLong(jso.getDone());
    };


    public String getFileName() {
        return fileName;
    }

    public long getSize() {
        return size;
    };

    public long getCurrent() {
        return done;
    };


    public String toString() {
        String out = fileName + " ( " + done + " / " + size + " )";
        return out;
    }
}
