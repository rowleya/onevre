package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ag.types.VenueState;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.ui.panels.AddDirectoryPanel;
import com.googlecode.onevre.gwt.client.xmlrpc.UpdateData;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;

public class CreateFolderButton extends ActionButton implements ClickHandler, MessageResponseHandler {

    private VenueState venueState = null;
    private String parentId = null;
    private int hierarchyLevel = 0;

    public CreateFolderButton(VenueState state, String parentId , int hierarchyLevel) {
        this.venueState = state;
        this.parentId = parentId;
        this.hierarchyLevel = hierarchyLevel;
        setImageUrl("images/icons/folder-new.png");
        setName("Create Folder");
        setImageHeight("20px");
        getButton().addClickHandler(this);
    }

    @Override
    public void action() {
        new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
    }

    public void onClick(ClickEvent paramClickEvent) {
        AddDirectoryPanel eddp = new AddDirectoryPanel(this, parentId, venueState.getUri(), hierarchyLevel);
        eddp.show();
    }

    public void handleResponse(MessageResponse response) {
        if (response.getResponseCode() == MessageResponse.OK) {
            DataDescription dataDescription = ((AddDirectoryPanel) response.getSource()).getDataDescription();
            GWT.log("DATA: " + dataDescription.toString());
            UpdateData.createDirectory(venueState.getUri(), dataDescription);
//            Application.getDataManager().updateData(venueState, dataDescription);
        }
    }

}
