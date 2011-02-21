package com.googlecode.onevre.ag.agserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.ftpserver.ftplet.FtpException;

import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.utils.Utils;


public class DataStore {

    private Log log = LogFactory.getLog(this.getClass());

    public static final String STATUS_INVALID = "invalid";
    public static final String STATUS_REFERENCE = "reference";
    public static final String STATUS_PRESENT = "present";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_UPLOADING = "uploading";

    public static final String EXPIRY_FORMAT =  "MMM d, yyyy, HH:mm:ss";

    public static final String DESC_FILE_NAME = "00INDEX";

    private static final String[] DESC_FILE_CONTENT =
        new String[]{"name",
                     "description",
                     "expires"};

    private HashMap<String, HashMap<String, String>> fileDescriptors = new HashMap<String, HashMap<String, String>>();

    private FtpsServer ftpsServer = null;

    private String localFileStore = null;

    private String uri = null;

    private HashMap<String, Venue> venues = new HashMap<String, Venue>();

    public DataDescription createDataDescription(String venueId, String path,
            File file, HashMap<String, String> descriptions) {
        VenueState venueState = venues.get(venueId).getState(venueId);
        String fileName = file.getName();
        log.info("createDataDescription p: " + path + " f: " + fileName);
        String url = getDataLocation(venueId) + path + "/" + fileName;
        for (DataDescription dataItem : venueState.getData()) {
            if (dataItem.getUri().equals(url)) {
                return dataItem;
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy, HH:mm:ss");
        DataDescription dataItem = new DataDescription();
        dataItem.setId(Utils.generateID());
        dataItem.setName(fileName);
        dataItem.setUri(url);
        String buffer = descriptions.get("description");
        if ((buffer != null) && (!buffer.equals(""))) {
            dataItem.setDescription(buffer);
        } else {
            dataItem.setDescription("");
        }
        buffer = descriptions.get("expires");
        dataItem.setExpires("");
        if ((buffer != null) && (!buffer.equals(""))) {
            Date d;
            try {
                d = (new SimpleDateFormat(EXPIRY_FORMAT)).parse(buffer);
                dataItem.setExpires(df.format(d));
            } catch (ParseException e) {
                log.error("Expiry date in wrong date format - will not be set");
            }
        }
        String dateStr = df.format(new Date(file.lastModified()));
        dataItem.setLastModified(dateStr);
        dataItem.setSize(new Long(file.length()).toString());
        dataItem.setStatus(STATUS_PRESENT);
        venueState.setData(dataItem);
        return dataItem;
    }

    public HashMap<String, HashMap<String, String>> readDescriptorfile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String text;
        HashMap<String, HashMap<String, String>> descs = new HashMap<String, HashMap<String, String>>();
        while ((text = reader.readLine()) != null) {
            String [] parts = text.split(";");
            HashMap<String, String> props = new HashMap<String, String>();
            for (int i = 0; i < parts.length; i++) {
                props.put(DESC_FILE_CONTENT[i], parts[i]);
            }
            if (props.get("name") != null) {
                descs.put(props.get("name"), props);
            }
        }
        return descs;
    }

    public DataDescription removeData(String venueId, DataDescription dataDesc) throws IOException {
        VenueState venueState = venues.get(venueId).getState();
        try {
            Vector<DataDescription> dataDescriptions = venueState.getData();
            DataDescription dataItem = dataDescriptions.get(dataDescriptions.indexOf(dataDesc));
            String dataUri = dataItem.getUri();
            if (dataUri != null) {
                venueState.removeData(dataItem);
                String fileName = dataUri.replaceFirst(getDataLocation(venueId), "");
                String path = fileName;
                int idx = fileName.lastIndexOf("/");
                if (idx != -1) {
                    path = fileName.substring(0, fileName.lastIndexOf("/"));
                }
                storeDescription(venueId, path, dataItem.getName(), null);
                ftpsServer.removeFile(venueId, fileName);
                return dataItem;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Venue.removeData tried to delete non venue data.");
        }
        return dataDesc;
    }

    public DataStore(HashMap<String, HashMap<String, String>> serverConfig) {
        ftpsServer = new FtpsServer(this, serverConfig);
        try {
            ftpsServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public String getDataLocation(String venueId) {
        return ftpsServer.getURI() + venueId + "/";
    }

    public DataStore(FtpsServer ftpsServer) {
        this.ftpsServer = ftpsServer;

    }

    public FtpsServer getFtpsServer() {
        return ftpsServer;
    }

    public void addUser(String venueId, String connectionId) {
        ftpsServer.addUser(venueId, connectionId);
    }

    public void delUser(String venueId, String connectionId) {
        ftpsServer.removeUser(venueId, connectionId);
    }

    public void destroy() {
        try {
            ftpsServer.stop();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public void addFile(String venueId, String dir, String fName) {
        if (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }
        if (fName.startsWith("/")) {
            fName = fName.substring(1);
        }
        fName = dir + "/" + fName;
     //   System.out.println("DS upload : venue: "+ venueId + " file: " + fName);
        try {
            File file = ftpsServer.getLocalFile(fName);
            DataDescription dataDescription = createDataDescription(venueId, dir, file, new HashMap<String, String>());
            dataDescription.setType(DataDescription.TYPE_FILE);
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public void storeDescription(String venueId, String oldfilename , DataDescription data) {
        String path = data.getUri().replaceFirst(getDataLocation(venueId), "");
        int index = path.lastIndexOf("/");
        if (index != -1) {
            path = path.substring(0, index);
        }
        storeDescription(venueId, path, oldfilename, data);
    }

    public void storeDescription(String venueId, String path, String oldfilename , DataDescription data) {
        if ((data != null) && (!oldfilename.equals(data.getName()))) {
            File f = ftpsServer.getFile(venueId, path, oldfilename);
            if (!f.renameTo(ftpsServer.getFile(venueId, path, data.getName()))) {
                log.error("can't rename file");
                return;
            }
        }
        File descFile = ftpsServer.getFile(venueId, path, DESC_FILE_NAME);
        StringBuffer buffer = new StringBuffer();
        String line = null;
        boolean found = false;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(descFile));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(oldfilename)) {
                    if (data != null) {
                        line = data.getName() + ";" + data.getDescription() + ";" + data.getExpires() + "\n";
                        buffer.append(line);
                    }
                    found = true;
                } else {
                    buffer.append(line + "\n");
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            // do nothing
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if ((data != null) && (!found)) {
            line = data.getName() + ";" + data.getDescription() + ";" + data.getExpires() + "\n";
            buffer.append(line);
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(descFile));
            writer.write(buffer.toString());
            writer.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public void populate(String venueId, String parentId, int level, String path) {
        level++;
        File[] filelist = ftpsServer.getFileList(venueId, path);
        HashMap<String , HashMap<String, String>> descs = new HashMap<String, HashMap<String, String>>();
        for (File file : filelist) {
            if (file.getName().equals(DESC_FILE_NAME)) {
                try {
                    descs = readDescriptorfile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (File file : filelist) {
            if (file.getName().equals(DESC_FILE_NAME)) {
                continue;
            }
            System.out.println("Adding Data:" + file.getName());
            HashMap<String, String> desc =  descs.get(file.getName());
            if (desc == null) {
                desc = new HashMap<String, String>();
            }


            DataDescription data = createDataDescription(venueId, path, file, desc);
            data.setObjectType(DataDescription.TYPE_FILE);
            data.setParentId(parentId);
            data.setHierarchyLevel(level);
            if (file.isDirectory()) {
                populate(venueId, data.getId(), level, path + "/" + file.getName());
                data.setObjectType(DataDescription.TYPE_DIR);
            }
            System.out.println("Adding DataDescription: " + data.toString());
        }

    }


    public void addVenue(String venueId, Venue venue) {
        venues.put(venueId, venue);
        VenueState venueState = venue.getState(venueId);
        System.out.println("Adding Venue Id:" + venueId + " State: " + venueState.getName());
        venueState.setDataLocation(ftpsServer.getURI() + venueId + "/");
        populate(venueId, "-1", 0, "");
  /*
        File[] filelist = ftpsServer.getFileList(venueId);
        HashMap<String , HashMap<String, String>> descs = new HashMap<String, HashMap<String,String>>();
        for (File file : filelist){
            if (file.getName().equals(DESC_FILE_NAME)){
                try {
                    descs = readDescriptorfile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (File file : filelist){
            if (file.getName().equals(DESC_FILE_NAME)){
                continue;
            }
            if (file.isDirectory()){
                // ignore for the moment
                continue;
            }
            System.out.println("Adding Data:" + file.getName());
            HashMap<String, String> desc =  descs.get(file.getName());
            if (desc==null) {
                desc = new HashMap<String, String>();
            }
            createDataDescription(venueId,"",file, desc);
        }
        */
    }

    public void addDir(String venueId, DataDescription dataDescription, String parentUri) {
        dataDescription.setObjectType(DataDescription.TYPE_DIR);
        String path = "";
        String parent = dataDescription.getParentId();
        if (!parent.equals("0")) {
            path = parentUri.replaceFirst(getDataLocation(venueId), "");
        }
        storeDescription(venueId, path, dataDescription.getName(), dataDescription);
        ftpsServer.createDirectory(venueId, path, dataDescription.getName());
    }
}
