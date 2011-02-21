package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;


import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.VenueServerType;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;
import com.googlecode.onevre.gwt.common.client.ModalPopup;
import com.googlecode.onevre.gwt.common.client.XmlResponse;
import com.googlecode.onevre.gwt.common.client.XmlResponseHandler;

public class AddTrustedServerPanel extends ModalPopup<DockPanel> implements ClickHandler, XmlResponseHandler {

    private PushButton cancel = new PushButton("Cancel", this);
    private PushButton ok = new PushButton("Set Venue Servers", this);
    private FlexTable serverPanel = new FlexTable();
    private NewTrustedServerPanel inputPanel = new NewTrustedServerPanel(this);

    private Vector<VenueServerType> trustedServers = new Vector<VenueServerType>();

    private MessageResponseHandler handler = null;

    private boolean changed = false;


    public boolean hasChanged() {
        return changed;
    }

    private Widget getVersionImage(VenueServerType trustedServer) {
        Image out = trustedServer.getImage();
        out.setHeight("20px");
        return out;
    }

    public void addTrustedServer(VenueServerType trustedServer, int pos) {
        GWT.log("panel ts:" + trustedServer);
        this.trustedServers.add(trustedServer);
        serverPanel.setText(pos, 0, trustedServer.getName());
        serverPanel.setWidget(pos, 1, getVersionImage(trustedServer));
        serverPanel.setText(pos, 2, trustedServer.toString());
        serverPanel.setWidget(pos, 3, new PushButton("remove", this));
    }

    public void addTrustedServer(VenueServerType trustedServer) {
        int pos = serverPanel.getRowCount();
        addTrustedServer(trustedServer, pos);
    }

    public void setTrustedServers(Vector<VenueServerType> trustedServers) {
        int i = 1;
        for (VenueServerType server : trustedServers) {
            addTrustedServer(server, i);
            i++;
        }
    }

    public Vector<VenueServerType> getTrustedServers() {
        return trustedServers;
    }


    public AddTrustedServerPanel(MessageResponseHandler handler) {
        super(new DockPanel());
        DockPanel panel = getWidget();
        this.handler = handler;
        Image img = new Image(GWT.getModuleBaseURL() + "images/server.png");
        panel.add(img, DockPanel.WEST);
        panel.setCellVerticalAlignment(img, DockPanel.ALIGN_MIDDLE);
        serverPanel.setTitle("Trusted OneVRE Venue Servers");
        serverPanel.setText(0, 0, "Server");
        serverPanel.setCellPadding(20);
        serverPanel.setCellSpacing(20);


        DockPanel addServer = new DockPanel();
        addServer.add(new Label("New Trusted Server:"), DockPanel.NORTH);

        addServer.add(inputPanel, DockPanel.CENTER);

        DockPanel buttons  = new DockPanel();
        buttons.add(addServer, DockPanel.NORTH);
        buttons.add(ok, DockPanel.EAST);
        buttons.add(cancel, DockPanel.WEST);

        panel.add(serverPanel, DockPanel.CENTER);
        panel.add(buttons, DockPanel.SOUTH);
        panel.setWidth("100%");
    }

    public void onClick(ClickEvent event) {
        if (event.getSource().equals(ok)) {
            handler.handleResponse(new MessageResponse(MessageResponse.OK, this));
            hide();
            return;
        }
        if (event.getSource().equals(cancel)) {
            handler.handleResponse(new MessageResponse(MessageResponse.CANCEL, this));
            hide();
            return;
        }
        if (event.getSource().getClass().equals(PushButton.class)) {
            int row = serverPanel.getCellForEvent(event).getRowIndex();
            String server = serverPanel.getText(row, 0);
            serverPanel.removeRow(row);
            trustedServers.remove(server);
            return;
        }

    }

    public void handleResponse(XmlResponse response) {
        if (response.getResponseCode() == XmlResponse.OK) {
            VenueServerType venueServerType = (VenueServerType) response.getResponseObject();
            Application.getServerManager().addServer(venueServerType);
            addTrustedServer(venueServerType);
            inputPanel.clearPanel();
        }
        if (response.getResponseCode() == XmlResponse.ERROR) {
            String server = (String) response.getResponseObject();
            String msg = "The Server \n" + server + "\ndoes not respond to Access Grid SOAP calls.";
             MessagePopup message = new MessagePopup(msg, null, MessagePopup.ERROR, MessageResponse.OK);
             message.show();
        }
    }

}
