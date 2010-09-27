package com.googlecode.onevre.ag.agserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;

public class FtpUser implements User {

    private PasswordEncryptor passwordEncryptor = new Md5PasswordEncryptor();

    private String name = null;

    private Vector<String> passwords = new Vector<String>();

    private int maxIdleTimeSec = 0; // no limit

    private String homeDir = null;

    private boolean isEnabled = true;

    private List<? extends Authority> authorities = new ArrayList<Authority>();

    /**
     * Default constructor.
     */
    public FtpUser() {
    }

    /**
     * Copy constructor.
     */
    public FtpUser(User user) {
        name = user.getName();
        passwords.add(user.getPassword());
        authorities = user.getAuthorities();
        maxIdleTimeSec = user.getMaxIdleTime();
        homeDir = user.getHomeDirectory();
        isEnabled = user.getEnabled();
    }

    public FtpUser(String name, String password, String homeDir, PasswordEncryptor pwdEncryptor){
        if (pwdEncryptor!=null){
            passwordEncryptor=pwdEncryptor;
        }
        this.name = name;
        this.passwords.add(passwordEncryptor.encrypt(password));
        this.homeDir = homeDir;
        authorities = null;
        maxIdleTimeSec = 0;
        isEnabled = true;
    }

    /**
     * Get the user name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set user name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the user password.
     */
    public String getPassword() {
        return passwords.firstElement();
    }

    /**
     * Set user password.
     */
    public void setPassword(String pass) {
        passwords.add(pass);
    }

    public void clearPasswords(){
        passwords = new Vector<String>();
    }

    public List<Authority> getAuthorities() {
        if (authorities != null) {
            return Collections.unmodifiableList(authorities);
        } else {
            return null;
        }
    }

    public void setAuthorities(List<Authority> authorities) {
        if (authorities != null) {
            this.authorities = Collections.unmodifiableList(authorities);
        } else {
            this.authorities = null;
        }
    }

    /**
     * Get the maximum idle time in second.
     */
    public int getMaxIdleTime() {
        return maxIdleTimeSec;
    }

    /**
     * Set the maximum idle time in second.
     */
    public void setMaxIdleTime(int idleSec) {
        maxIdleTimeSec = idleSec;
        if (maxIdleTimeSec < 0) {
            maxIdleTimeSec = 0;
        }
    }

    /**
     * Get the user enable status.
     */
    public boolean getEnabled() {
        return isEnabled;
    }

    /**
     * Set the user enable status.
     */
    public void setEnabled(boolean enb) {
        isEnabled = enb;
    }

    /**
     * Get the user home directory.
     */
    public String getHomeDirectory() {
        return homeDir;
    }

    /**
     * Set the user home directory.
     */
    public void setHomeDirectory(String home) {
        homeDir = home;
    }

    /**
     * String representation.
     */
    public String toString() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public AuthorizationRequest authorize(AuthorizationRequest request) {
        // check for no authorities at all
        // if no authorities allow everything
        // ftp is protected by the venueClient interface the
        // user is created on enter and deleted on exit
        if(authorities == null) {
            return request;
        }

        boolean someoneCouldAuthorize = false;
        for (Authority authority : authorities) {
            if (authority.canAuthorize(request)) {
                someoneCouldAuthorize = true;

                request = authority.authorize(request);

                // authorization failed, return null
                if (request == null) {
                    return null;
                }
            }

        }

        if (someoneCouldAuthorize) {
            return request;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Authority> getAuthorities(Class<? extends Authority> clazz) {
        List<Authority> selected = new ArrayList<Authority>();

        for (Authority authority : authorities) {
            if (authority.getClass().equals(clazz)) {
                selected.add(authority);
            }
        }

        return selected;
    }

    public Vector<String> getPasswords() {
        return passwords;
    }

}
