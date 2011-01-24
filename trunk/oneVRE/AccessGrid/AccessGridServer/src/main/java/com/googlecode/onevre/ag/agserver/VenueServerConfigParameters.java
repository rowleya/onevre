package com.googlecode.onevre.ag.agserver;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.ag.types.Capability;

/** Pag Configuration file parameter names */

public class VenueServerConfigParameters {

    // venueServer configuration
    public static String VENUE_SERVER_SECTION = "VenueServer";

    public static String VENUE_SERVER_CONFIG_LOCATION = "ConfigLocation";

    public static String VENUE_SERVER_SECURE = "secure";

    public static String VENUE_SERVER_PORT = "venueServerPort";

    public static String VENUE_SERVER_VENUE_LIST = "venueList";

    public static String VENUE_SERVER_DEFAULT_VENUE = "defaultVenue";

    public static String VENUE_SERVER_IMPORT_HOST = "venueImport";

    public static String VENUE_SERVER_LOG_FILE = "serverLogFile";

    public static String VENUE_SERVER_DEFAULT_POLICY_FILE = "defaultPolicyFile";

    public static String SSL_KEYSTORE_FILE = "keyStore";

    public static String SSL_KEYSTORE_TYPE = "keyStoreType";

    public static String SSL_KEYSTORE_PASSWORD = "keyStorePasswd";

    public static String SSL_TRUSTSTORE_FILE = "trustStore";

    public static String SSL_TRUSTSTORE_TYPE = "trustStoreType";

    public static String SSL_TRUSTSTORE_PASSWORD = "trustStorePasswd";

    public static String PKI_CRL = "PKI-CRL";


    public static String VENUE_SERVER_DATASTORE_SECTION = "DataStore";

    public static String DATASTORE_DATA_PORT = "dataPort";

    public static String DATASTORE_DATA_LOCATION = "dataLocation";

    public static String DATASTORE_PORT_RANGE_START= "dataPortRangeStart";

    public static String DATASTORE_PORT_RANGE_END= "dataPortRangeEnd";

    public static String VENUE_SERVER_EVENTSERVER_SECTION = "EventServer";

    public static String EVENTSERVER_HOST= "eventHost";

    public static String EVENTSERVER_PORT= "eventPort";

    public static String VENUE_SERVER_TEXTSERVER_SECTION = "TextServer";

    public static String TEXTSERVER_HOST = "textHost";

    public static String TEXTSERVER_PORT = "textPort";

    public static String VENUE_SERVER_CAPABILITIES = "Capabilities";

    public static String VENUE_SERVER_CAPABILITIY_TYPES = "types";

    public static String CAPABILITIY_ROLE = "role";
    public static String CAPABILITIY_TYPE = "type";
    public static String CAPABILITIY_CODEC = "codec";
    public static String CAPABILITIY_RATE = "rate";

    public static HashMap<String, Vector<Capability>> defaultCapablities = new HashMap<String, Vector<Capability>>();

    public static void addCapabilty(String type,Capability capability){
        Vector<Capability> caps = defaultCapablities.get(type);
        if (caps==null){
            caps = new Vector<Capability>();
            defaultCapablities.put(type, caps);
        }
        caps.add(capability);
    }
}
