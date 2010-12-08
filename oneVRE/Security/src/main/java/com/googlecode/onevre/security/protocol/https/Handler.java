package com.googlecode.onevre.security.protocol.https;

import java.net.URL;
import java.net.URLConnection;

import org.globus.net.GSIURLConnection;
import org.ietf.jgss.GSSCredential;

public class Handler extends org.globus.net.protocol.https.Handler {

    private static GSSCredential credential = null;

    /**
     * Sets the credential to be used on subsequent calls
     * @param credential The credential to set
     */
    public static void setCredential(GSSCredential credential) {
        Handler.credential = credential;
    }

    protected URLConnection openConnection(URL u) {
        System.err.println("Creating connection");
        URLConnection connection = super.openConnection(u);
        if ((credential != null) && (connection instanceof GSIURLConnection)) {
            System.err.println("Setting credential");
            ((GSIURLConnection) connection).setCredentials(credential);
        }
        return connection;
    }


}
