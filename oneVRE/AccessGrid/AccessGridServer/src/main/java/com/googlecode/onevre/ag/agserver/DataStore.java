package com.googlecode.onevre.ag.agserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.ftpserver.ftplet.FtpException;

import com.googlecode.onevre.ag.types.DataDescription;
import com.googlecode.onevre.ag.types.VenueState;
import com.googlecode.onevre.utils.Utils;
import com.sun.media.Log;


public class DataStore {

    public static String STATUS_INVALID = "invalid";
    public static String STATUS_REFERENCE = "reference";
    public static String STATUS_PRESENT = "present";
    public static String STATUS_PENDING = "pending";
    public static String STATUS_UPLOADING = "uploading";

    public static String TYPE_DIR = "Directory";
    public static String TYPE_FILE = "File";
    public static String TYPE_COMMON = "Common undefined";
    public static String EXPIRY_FORMAT =  "MMM d, yyyy, HH:mm:ss";

    public static String DESC_FILE_NAME = "00INDEX";

    private static final String[] DESC_FILE_CONTENT =
        new String[]{"name",
                     "description",
                     "expires"};

    private HashMap<String, HashMap<String, String>> file_descriptors = new HashMap<String, HashMap<String,String>>();

    private FtpsServer ftpsServer=null;

    private String localFileStore = null;

    private String uri = null;

    private HashMap<String, Venue> venues = new HashMap<String, Venue>();

    public DataDescription createDataDescription(String venueId, File file, HashMap<String, String> descriptions){
        VenueState venueState = venues.get(venueId).getState();
        String fileName = file.getName();
        String uri = getDataLocation(venueId) + fileName;
        for (DataDescription dataItem : venueState.getData()){
            if (dataItem.getUri().equals(uri)) {
                return dataItem;
            }
        }
        SimpleDateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy, HH:mm:ss");
        DataDescription dataItem = new DataDescription();
        dataItem.setId(Utils.generateID());
        dataItem.setName(fileName);
        dataItem.setUri(uri);
        String buffer = descriptions.get("description");
        if ((buffer!=null)&&(!buffer.equals(""))){
        	dataItem.setDescription(buffer);
        } else {
        	dataItem.setDescription("");
        }
        buffer = descriptions.get("expires");
    	dataItem.setExpires("");
        if ((buffer!=null)&&(!buffer.equals(""))){
        	Date d;
			try {
				d = (new SimpleDateFormat(EXPIRY_FORMAT)).parse(buffer);
				dataItem.setExpires(df.format(d));
			} catch (ParseException e) {
				Log.error("Expiry date in wrong date format - will not be set");
			}
        }
        String dateStr = df.format(new Date(file.lastModified()));
        dataItem.setLastModified(dateStr);
        dataItem.setSize(new Long(file.length()).toString());
        dataItem.setStatus(STATUS_PRESENT);
        venueState.setData(dataItem);
        return dataItem;
    }

    public HashMap<String, HashMap<String, String>> readDescriptorfile(File file) throws IOException{
    	BufferedReader reader = new BufferedReader(new FileReader(file));
    	String text;
    	HashMap<String, HashMap<String, String>> descs = new HashMap<String, HashMap<String,String>>();
    	while ((text = reader.readLine()) !=null){
    		String [] parts = text.split(";");
    		HashMap<String, String> props = new HashMap<String, String>();
    		for (int i = 0; i<parts.length; i++){
    			props.put(DESC_FILE_CONTENT[i], parts[i]);
    		}
    		if (props.get("name")!=null) {
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
            if (dataUri != null){
                venueState.removeData(dataDesc);
                String fileName = dataUri.replaceFirst(getDataLocation(venueId),"");
                ftpsServer.removeFile(venueId, fileName);
                return dataItem;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("Venue.removeData tried to delete non venue data.");
        }
        return dataDesc;
    }

    public DataStore(HashMap<String, HashMap<String, String>> serverConfig){
        ftpsServer = new FtpsServer(this, serverConfig);
        try {
            ftpsServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public String getDataLocation(String venueId){
        return ftpsServer.getURI() + venueId + "/";
    }

    public DataStore(FtpsServer ftpsServer){
        this.ftpsServer = ftpsServer;

    }

    public FtpsServer getFtpsServer(){
        return ftpsServer;
    }

    public void addUser(String venueId, String connectionId){
        ftpsServer.addUser(venueId, connectionId);
    }

    public void delUser(String venueId, String connectionId){
        ftpsServer.removeUser(venueId, connectionId);
    }

    public void destroy(){
        try {
            ftpsServer.stop();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public void addFile(String venueId, String dir, String fName) {
        if (dir.endsWith("/")){
            dir = dir.substring(0,dir.length()-1);
        }
        if (fName.startsWith("/")){
            fName = fName.substring(1);
        }
        fName = dir + "/" + fName;
     //   System.out.println("DS upload : venue: "+ venueId + " file: " + fName);
        try {
            File file = ftpsServer.getLocalFile(fName);
            DataDescription dataDescription = createDataDescription(venueId,file, new HashMap<String, String>());
            venues.get(venueId).addData(dataDescription);
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public void addVenue(String venueId, Venue venue) {
        venues.put(venueId, venue);
        VenueState venueState=venue.getState();
        System.out.println("Adding Venue Id:" + venueId + " State: " + venueState.getName());
        venueState.setDataLocation(ftpsServer.getURI() + venueId + "/");
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
            createDataDescription(venueId,file, desc);
        }
    }
}
