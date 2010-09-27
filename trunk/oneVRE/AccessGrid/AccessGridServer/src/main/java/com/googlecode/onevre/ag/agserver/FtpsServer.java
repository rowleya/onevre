package com.googlecode.onevre.ag.agserver;

import java.io.File;
import java.util.HashMap;

import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;

import com.googlecode.onevre.utils.ConfigFile;
import com.googlecode.onevre.utils.Utils;

public class FtpsServer {

    FtpServer server = null;

    FtpUserManager userManager = new FtpUserManager();

    int dataPort = Integer.valueOf(VenueServerDefaults.dataPort);

    String serverDir = VenueServerDefaults.serverDir;

    DataStore dataStore = null;

    public FtpsServer(DataStore dataStore, HashMap<String, HashMap<String, String>> serverConfig){
//    int dataPort, String keyStoreFileName, String storePasswd, int dataPortStart, int dataPortEnd) {
        FtpServerFactory serverFactory = new FtpServerFactory();

        ListenerFactory factory = new ListenerFactory();

        this.dataStore = dataStore;

        dataPort = Integer.valueOf(ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_DATASTORE_SECTION,
                VenueServerConfigParameters.DATASTORE_DATA_PORT,
                VenueServerDefaults.dataPort));

        String configLocation = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.VENUE_SERVER_CONFIG_LOCATION,"");

        String keyStoreFileName = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_KEYSTORE_FILE, "");

        if (!keyStoreFileName.startsWith("/")){
            keyStoreFileName=configLocation + keyStoreFileName;
        }

        String keyStorePasswd = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_KEYSTORE_PASSWORD, "");

        String keyStoreType = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_SECTION,
                VenueServerConfigParameters.SSL_KEYSTORE_TYPE,
                VenueServerDefaults.keyStoreType);

        String portRange = ConfigFile.getParameter(serverConfig,
                    VenueServerConfigParameters.VENUE_SERVER_DATASTORE_SECTION,
                    VenueServerConfigParameters.DATASTORE_PORT_RANGE_START,
                    VenueServerDefaults.dataPortRangeStart) + "-" +
                ConfigFile.getParameter(serverConfig,
                    VenueServerConfigParameters.VENUE_SERVER_DATASTORE_SECTION,
                    VenueServerConfigParameters.DATASTORE_PORT_RANGE_END,
                    VenueServerDefaults.dataPortRangeEnd);

        serverDir = ConfigFile.getParameter(serverConfig,
                VenueServerConfigParameters.VENUE_SERVER_DATASTORE_SECTION,
                VenueServerConfigParameters.DATASTORE_DATA_LOCATION,
                VenueServerDefaults.serverDir);
        // set the port of the listener

        factory.setPort(dataPort);

        // define SSL configuration
        SslConfigurationFactory ssl = new SslConfigurationFactory();
        ssl.setKeystoreFile(new File(keyStoreFileName));
        ssl.setKeystorePassword(keyStorePasswd);
        ssl.setKeystoreType(keyStoreType);

        // set the SSL configuration for the listener
        factory.setSslConfiguration(ssl.createSslConfiguration());
        factory.setImplicitSsl(false);

        DataConnectionConfigurationFactory dataConnectionConfigFactory = new DataConnectionConfigurationFactory();
        dataConnectionConfigFactory.setPassivePorts(portRange);
        factory.setDataConnectionConfiguration(dataConnectionConfigFactory.createDataConnectionConfiguration());
        // replace the default listener
        serverFactory.addListener("default", factory.createListener());
        serverFactory.setUserManager(userManager);
        HashMap<String,Ftplet> ftplets = new HashMap<String,Ftplet>();
        ftplets.put("dataStore",new DataStoreFtplet(dataStore));
        serverFactory.setFtplets(ftplets);

        try {
            userManager.save(new FtpUser("ts23","ts23ftps","/home/venueServer",null));
        } catch (FtpException e) {
            e.printStackTrace();
        }
        server = serverFactory.createServer();

    }

    public void start() throws FtpException{
        server.start();
    }

    public void stop() throws FtpException {
        server.stop();
    }

    public void setServerDir (String serverDir){
        this.serverDir = serverDir;
    }

    public void addUser(String name, String passwd){
        try {
            userManager.save(new FtpUser(name,passwd,serverDir,null));
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public void removeUser(String name , String password){
        userManager.delete(name, password);
    }

    public File[] getFileList (String venueId) {
        File dir = new File(serverDir + "/" + venueId);
        if (!dir.exists()){
            dir.mkdirs();
        }
        return dir.listFiles();
    }

    public boolean removeFile(String venueId, String fileName){
        File file = new File(serverDir + "/" + venueId + "/" + fileName);
        return file.delete();
    }

    public String getURI(){
        String uri = "ftps://" + Utils.getLocalHost().getHostName() + ":" + dataPort + "/";
        return uri;
    }

    public File getLocalFile(String name) throws FtpException {
        String fName  = serverDir + name;
        File file = new File(fName);
        if (!file.exists()){
            throw new FtpException("File not found: " + fName);
        }
        return file;
    }
}
