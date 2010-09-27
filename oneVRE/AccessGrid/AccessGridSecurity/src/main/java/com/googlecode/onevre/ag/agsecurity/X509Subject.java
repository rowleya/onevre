package com.googlecode.onevre.ag.agsecurity;


public class X509Subject extends Subject {

    public X509Subject() {
        super.setAuth_type("x509");
    }

    public String getSoapType() {
        return "X509Subject";
    }

}
