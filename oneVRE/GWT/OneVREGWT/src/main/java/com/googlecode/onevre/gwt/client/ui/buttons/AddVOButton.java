package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.NamedFrame;
import com.googlecode.onevre.gwt.client.ui.ActionButton;
import com.googlecode.onevre.gwt.client.ui.panels.CreateVenuePanel;
import com.googlecode.onevre.gwt.common.client.MessagePopup;
import com.googlecode.onevre.gwt.common.client.MessageResponse;

public class AddVOButton extends ActionButton implements ClickHandler {

    private CreateVenuePanel venuePanel = null;

    private FlexTable voTable = null;

    private NamedFrame targetFrame = null;

    private String parent = null;

    public AddVOButton(CreateVenuePanel venuePanel, FlexTable voTable) {
        this.venuePanel = venuePanel;
        this.voTable = voTable;
        setImageUrl("images/icons/add.png");
        setName("Add VO Attribute");
        setImageHeight("20px");
        getButton().setSize("20px", "20px");
        getButton().addClickHandler(this);
    }

    @Override
    public void action() {
        MessagePopup mp = new MessagePopup("test", null, MessagePopup.INFO, MessageResponse.OK);
    }

    public void onClick(ClickEvent paramClickEvent) {
        venuePanel.addVOAttribute(voTable);
    }


}
