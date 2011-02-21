package com.googlecode.onevre.gwt.client.ag.types;

import java.util.Vector;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TreeItem;
import com.googlecode.onevre.gwt.client.ui.buttons.MonitorVenueButton;

public class VenueTreeItem extends TreeItem {

    private static int treeInstances = 0;

    private TreeItem dummy = new TreeItem();

    private boolean hasDummy = true;

    public Panel getItemPanel() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(new Label(venueName));
        MonitorVenueButton mvb = new MonitorVenueButton(venueUrl);
        panel.add(mvb.getButton());
        panel.setWidth("100%");
        panel.setCellHorizontalAlignment(mvb.getButton(), HorizontalPanel.ALIGN_RIGHT);
        return panel;
    }

    public VenueTreeItem(VenueTreeItemJSO jso) {
        super();
        uniqueId = jso.getId();
        venueName = jso.getName();
        venueUrl = jso.getUri();
        isExpanded = jso.isExpanded();
        isSelected = jso.isSelected();

        if (jso.hasConnections()) {
            VectorJSO<VenueTreeItemJSO> vtijso = jso.getConnections();
            connections = new Vector<VenueTreeItem>();
            for (int i = 0; i < vtijso.size(); i++) {
                connections.add(new VenueTreeItem(vtijso.get(i)));
            }
        }
        this.setWidget(getItemPanel());
        this.addItem(dummy);
    }

    public void removeDummy() {
        if (hasDummy) {
            this.removeItem(dummy);
            hasDummy = false;
        }
    }

    public boolean hasDummy() {
        return hasDummy;
    }

    private static synchronized String getUniqueId() {
        treeInstances++;
        return String.valueOf(System.currentTimeMillis())
            + String.valueOf(treeInstances);
    }

    private String uniqueId = getUniqueId();

    // The name of the venue
    private String venueName = null;

    // The venue being addressed
    private String venueUrl = null;

    // True if the venue item is expanded
    private boolean isExpanded = false;

    // True if the venue item is selected
    private boolean isSelected = false;

    // The connections from this venue to other venues
    private Vector<VenueTreeItem> connections = null;

    public boolean hasConnections() {
        return (connections != null);
    }
    public Vector<VenueTreeItem> getConnections() {
        return connections;
    }

    public String getName() {
        return venueName;
    }

    public String getVenueUrl() {
        return venueUrl;
    }


}
