package com.googlecode.onevre.gwt.client.ag.types;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.onevre.gwt.client.net.URL;

public class VenueServerType {

    private static final int DEFAULT_VENUESERVER_PORT = 8000;
    // The scheme of the profile (e.g. "user" or "service")
    private String protocol = "https";

    // The name of the venue server
    private String name = "";

    // The url address of the venue server
    private String url = "";

    // The port number of the venue server
    private int portNumber = DEFAULT_VENUESERVER_PORT;

    // The external id of the client (e.g. public key)
    private String defaultVenue = "/Venues/default";

    private String serverInterface = "/VenueServer";

    private String defaultVenueId = "";

    // The location of the client on the earth
    private String version = "";

    public VenueServerType(VenueServerTypeJSO jso) {
        protocol = jso.getProtocol();
        name = jso.getName();
        url = jso.getUrl();
        portNumber = jso.getPortNumber();
        defaultVenue = jso.getDefaultVenue();
        defaultVenueId = jso.getDefaultVenueId();
        version = jso.getVersion();
    }

    public VenueServerType(String urlString) {
            URL localurl = new URL(urlString);
            this.url = localurl.getHost();
            this.portNumber = localurl.getPort();
            this.protocol = localurl.getProtocol();
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param portNumber the portNumber to set
     */
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * @return the portNumber
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * @param defaultVenue the defaultVenue to set
     */
    public void setDefaultVenue(String defaultVenue) {
        this.defaultVenue = defaultVenue;
    }

    /**
     * @param defaultVenue the defaultVenue to set
     */
    public void setDefaultVenueId(String defaultVenueId) {
        this.defaultVenueId = defaultVenueId;
    }

    /**
     * @return the defaultVenue
     */
    public String getDefaultVenue() {
        return defaultVenue;
    }
    /**
     * @return the defaultVenueId
     */
    public String getDefaultVenueId() {
        return defaultVenueId;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    };

    public String toString() {
        String out = protocol + "://" + url + ":" + portNumber;
        return out;
    }

    public boolean isManagable() {
        return version.contains("OneVRE");
    }

    public String toUrl() {
        return toString() + defaultVenue;

    }
    public String toServerUrl() {
        return toString() + serverInterface;
    }

    public boolean equals(Object o) {
        return this.toString().equals(((VenueServerType) o).toString());
    }

    public boolean equals(String url) {
        return this.toString().equals(url);
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public String getDescription() {
        if (!name.equals("")) {
            return name;
        }
        return toString();
    }

    public Image getImage() {
        Image image = new Image(GWT.getModuleBaseURL() + "images/icons/agtk.png");
        String title = "AccessGrid Toolkit " + version + " Server";
        if (version.contains("OneVRE")) {
            image.setUrl(GWT.getModuleBaseURL() + "images/icons/oneVRE.png");
            String [] oneVREtitle = version.split("OneVRE");
            title = "OneVRE " + oneVREtitle[1] + " Server (AGTk v. " + oneVREtitle[0] + ")";
        }
        GWT.log(title);
        image.setTitle(title);
        return image;
    }

}
