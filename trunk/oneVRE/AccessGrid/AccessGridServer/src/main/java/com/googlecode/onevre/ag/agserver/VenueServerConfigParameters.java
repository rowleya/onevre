package com.googlecode.onevre.ag.agserver;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.ag.types.Capability;

/** Pag Configuration file parameter names */

public class VenueServerConfigParameters {

    // venueServer configuration
    public static final String VENUE_SERVER_SECTION = "VenueServer";

    public static final String VENUE_SERVER_CONFIG_LOCATION = "ConfigLocation";

    public static final String VENUE_SERVER_SECURE = "secure";

    public static final String VENUE_SERVER_PORT = "venueServerPort";

    public static final String VENUE_SERVER_VENUE_LIST = "venueList";

    public static final String VENUE_SERVER_DEFAULT_VENUE = "defaultVenue";

    public static final String VENUE_SERVER_IMPORT_HOST = "venueImport";

    public static final String VENUE_SERVER_LOG_FILE = "serverLogFile";

    public static final String VENUE_SERVER_DEFAULT_POLICY_FILE = "defaultPolicyFile";

    public static final String SSL_KEYSTORE_FILE = "keyStore";

    public static final String SSL_KEYSTORE_TYPE = "keyStoreType";

    public static final String SSL_KEYSTORE_PASSWORD = "keyStorePasswd";

    public static final String SSL_TRUSTSTORE_FILE = "trustStore";

    public static final String SSL_TRUSTSTORE_TYPE = "trustStoreType";

    public static final String SSL_TRUSTSTORE_PASSWORD = "trustStorePasswd";

    public static final String PKI_CRL = "PKI-CRL";


    public static final String VENUE_SERVER_DATASTORE_SECTION = "DataStore";

    public static final String DATASTORE_DATA_PORT = "dataPort";

    public static final String DATASTORE_DATA_LOCATION = "dataLocation";

    public static final String DATASTORE_PORT_RANGE_START = "dataPortRangeStart";

    public static final String DATASTORE_PORT_RANGE_END = "dataPortRangeEnd";

    public static final String VENUE_SERVER_EVENTSERVER_SECTION = "EventServer";

    public static final String EVENTSERVER_HOST = "eventHost";

    public static final String EVENTSERVER_PORT = "eventPort";

    public static final String VENUE_SERVER_TEXTSERVER_SECTION = "TextServer";

    public static final String TEXTSERVER_HOST = "textHost";

    public static final String TEXTSERVER_PORT = "textPort";

    public static final String VENUE_SERVER_CAPABILITIES = "Capabilities";

    public static final String VENUE_SERVER_CAPABILITIY_TYPES = "types";

    public static final String CAPABILITIY_ROLE = "role";
    public static final String CAPABILITIY_TYPE = "type";
    public static final String CAPABILITIY_CODEC = "codec";
    public static final String CAPABILITIY_RATE = "rate";

    public static HashMap<String, Vector<Capability>> defaultCapablities = new HashMap<String, Vector<Capability>>();

    public static void addCapabilty(String type, Capability capability) {
        Vector<Capability> caps = defaultCapablities.get(type);
        if (caps == null) {
            caps = new Vector<Capability>();
            defaultCapablities.put(type, caps);
        }
        caps.add(capability);
    }

    private VenueServerConfigParameters() {
    }

}
