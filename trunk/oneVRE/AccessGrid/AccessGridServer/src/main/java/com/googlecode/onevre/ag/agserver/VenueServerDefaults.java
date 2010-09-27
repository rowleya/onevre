package com.googlecode.onevre.ag.agserver;

import com.googlecode.onevre.ag.agsecurity.Role;

/**
 * Defaults for the Venue Server
 *
 * @author Tobias M Schiebeck
 *
 */
public class VenueServerDefaults {

    public static String venueServerPort = "8000";

    public static String keyStoreType="JKS";

    public static String trustStoreType="PKCS12";

    public static String venueServerLogFile = "PAGVenueServer.log";

    /** <h2> Data - Connection </h2> */
    /** public Port for the VenueServers FTPS server */
    public static String dataPort = "8006";

    /** Start Port for the FTPS data connections */
    public static String dataPortRangeStart = "50000";

    /** End Port for the FTPS data connections */
    public static String dataPortRangeEnd = "50020";

    /** Local dirctory for the dataStore */
    public static String serverDir =  "/home/venueServer";

    /** the Version of the VenueServer
     * <ul>
     * <li>version Number of AGTk we are compatible with</li>
     * <li>identifier for OneVRE extensions</li>
     * <li>version Number of OneVRE</li>
     * </ul>
     */
    public static String serverVersion =  "3.1.0 OneVRE 1.0";

    /** <h2> Jabber - Connection </h2> */
    /** Host for the Jabber Text - Chat */
    public static String textHost = "jabber.mcs.anl.gov";

    /** Port for the Jabber Text - Chat */
    public static String textPort = "5223";

    /** <h2> Event Server - Connection </h2> */
    /** Port for the Event Server */
    public static String eventPort = "8002";

    public static Role Everybody = new Role();
    {
        Everybody.setName("Everybody");
    }

    public static Role Administrators = new Role();
    {
        Administrators.setName("Administrators");
        Administrators.setRequiredDefault(1);
    }
}
