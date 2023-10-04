package com.nntk.restplus.entity;


public class RestPlusResponse {
    private int httpStatus;

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String body;

    public byte[] getBodyStream() {
        return bodyStream;
    }

    public void setBodyStream(byte[] bodyStream) {
        this.bodyStream = bodyStream;
    }

    private byte[] bodyStream;
}
