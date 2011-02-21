package com.googlecode.onevre.ag.agserver;

import java.util.HashMap;
import java.util.Vector;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.Md5PasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

public class FtpUserManager implements UserManager {

    private HashMap<String, FtpUser> users = new HashMap<String, FtpUser>();

    private Vector<String> admins = new Vector<String>();

    private PasswordEncryptor passwordEncryptor = new Md5PasswordEncryptor();

    public PasswordEncryptor getPasswordEncryptor() {
        return passwordEncryptor;
    }

    public User authenticate(Authentication authentication)
            throws AuthenticationFailedException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

            String user = upauth.getUsername();
            String password = upauth.getPassword();

            if (user == null) {
                throw new AuthenticationFailedException("Authentication failed");
            }

            if (password == null) {
                password = "";
            }
            FtpUser ftpUser = users.get(user);
            if (ftpUser == null) {
                // user does not exist
                throw new AuthenticationFailedException("Authentication failed");
            }
            Vector<String> storedPasswords = ftpUser.getPasswords();
            if (storedPasswords == null) {
                // user does not exist
                throw new AuthenticationFailedException("Authentication failed");
            }

            for (String storedPwd : storedPasswords) {
                if (passwordEncryptor.matches(password, storedPwd)) {
                    return ftpUser;
                }
            }
            throw new AuthenticationFailedException("Authentication failed");
        } else {
            throw new IllegalArgumentException(
                "Authentication not supported by this user manager");
        }
    }

    public void delete(String username, String password) {
        FtpUser ftpUser = users.get(username);
        if (ftpUser != null) {
            Vector<String> storedPasswords = ftpUser.getPasswords();
            ftpUser.clearPasswords();
            for (String storedPwd : storedPasswords) {
                if (!passwordEncryptor.matches(password, storedPwd)) {
                    ftpUser.setPassword(storedPwd);
                }
            }
            if (ftpUser.getPasswords().isEmpty()) {
                users.remove(username);
            }
        }
    }

    public void delete(String username) throws FtpException {
        users.remove(username);
    }

    public boolean doesExist(String username) throws FtpException {
        return users.containsKey(username);
    }

    public String getAdminName() throws FtpException {
        return null;
    }

    public String[] getAllUserNames() throws FtpException {
        return users.keySet().toArray(new String[0]);
    }

    public User getUserByName(String username) throws FtpException {
        return users.get(username);
    }

    public boolean isAdmin(String username) throws FtpException {
        FtpUser user = users.get(username);
        if (user == null) {
            throw new FtpException("user " + username + " does not exist");
        }
        for (String adm : admins) {
            if (adm.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void save(User user) throws FtpException {
        if (user.getName() == null) {
            throw new NullPointerException("User name is null.");
        }
        FtpUser ftpUser = users.get(user.getName());
        if (ftpUser == null) {
            ftpUser = new FtpUser(user);
        } else {
            ftpUser.setPassword(user.getPassword());
        }
        users.put(user.getName(), (FtpUser) user);
    }

}
