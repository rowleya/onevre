package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.HashMap;
import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ui.Icons;
import com.googlecode.onevre.gwt.client.ui.VoAttributeRow;
import com.googlecode.onevre.gwt.client.ui.buttons.AddVOButton;

import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;
import com.googlecode.onevre.gwt.common.client.ModalPopup;

public class CreateVenuePanel extends ModalPopup<DockPanel> implements ClickHandler {

    // private static final String DATE_FORMAT = "EEE, MMM d, yyyy, HH:mm:ss";

    private Vector<HashMap<String, String>> voAttributesMap  = new Vector<HashMap<String, String>>();

    private TextBox nameInput = new TextBox();
    private TextArea descriptionInput = new TextArea();
    private DateBox dateInput = new DateBox();
    private TextBox locationInput = new TextBox();
    private TextBox homeInput = new TextBox();
    private ListBox profileInput = new ListBox();
    private PushButton cancel = new PushButton("Cancel", this);
    private PushButton clear = new PushButton("Clear", this);
    private PushButton ok = new PushButton("Create Venue", this);
    private FlexTable profilepanel = new FlexTable();
    private FlexTable voAttPanel = new FlexTable();
//  private NamedFrame targetFrame = new NamedFrame("victim");
//  private FormPanel form = null;
    private Hidden parent = new Hidden("parentId");
    private Hidden namespace = new Hidden("namespace", Application.getParam("pag_namespace"));
    private Hidden venueUri = new Hidden("venueUri");
    private Hidden expiry = new Hidden("expiry");
    private Hidden description = new Hidden("description");

    private MessageResponseHandler handler = null;

    private boolean changed = false;

    public boolean hasChanged() {
        return changed;
    }

    public CreateVenuePanel(MessageResponseHandler handler) {
        super(new DockPanel());
        this.handler = handler;
        DockPanel panel = getWidget();
        Image img = new Image(Icons.addVenueIcon);
        img.setHeight("64px");
        panel.add(img, DockPanel.WEST);
        panel.setCellVerticalAlignment(img, DockPanel.ALIGN_MIDDLE);
//        Grid profilepanel = new Grid (3,3);
        profilepanel.setWidget(1, 0, new Image(Icons.fileIcon));
        profilepanel.setText(1, 1, "Name: *");
        nameInput.setName("file");
        profilepanel.setWidget(1, 2, nameInput);
        profilepanel.getFlexCellFormatter().setColSpan(1, 2, 2);
        Image docInfo = new Image(Icons.docInfoIcon);
        docInfo.setHeight("22px");
        profilepanel.setWidget(2, 0, docInfo);
        profilepanel.setText(2, 1, "Description: *");
        descriptionInput.setVisibleLines(3);
        //descriptionInput.setName("description");
        profilepanel.setWidget(2, 2, descriptionInput);
        profilepanel.getFlexCellFormatter().setColSpan(2, 2, 2);
        panel.add(profilepanel, DockPanel.CENTER);

        voAttPanel = new FlexTable();
        voAttPanel.setText(0, 0, "Required VO attributes");
        voAttPanel.setTitle("one of the attributes will be required to enter the venue");
        voAttPanel.getFlexCellFormatter().setColSpan(0, 0, 3);
        voAttPanel.setText(1, 0, "VO");
        voAttPanel.setText(1, 1, "Group");
        voAttPanel.setText(1, 2, "Role");
        addVOAttribute(voAttPanel);

        AddVOButton addVOButton = new AddVOButton(this, voAttPanel);
        profilepanel.setWidget(3, 0, voAttPanel);
        profilepanel.getFlexCellFormatter().setColSpan(3, 0, 4);
        profilepanel.setWidget(3, 1, addVOButton.getButton());
        profilepanel.getFlexCellFormatter().setVerticalAlignment(3, 1, HorizontalPanel.ALIGN_MIDDLE);
        Image timeout = new Image(Icons.timeoutIcon);
        timeout.setHeight("22px");

        DockPanel buttons  = new DockPanel();
        buttons.add(ok, DockPanel.EAST);
        buttons.add(cancel, DockPanel.WEST);
        panel.add(buttons, DockPanel.SOUTH);
        panel.setWidth("100%");
    }

    public void onClick(ClickEvent event) {
        if (event.getSource().equals(clear)) {
            dateInput.setValue(null);
            return;
        }
        if (event.getSource().equals(ok)) {
            for (int i = 1; i < voAttPanel.getRowCount(); i++) {
                if (voAttPanel.getCellCount(i) > 3) {
                    VoAttributeRow voAttRow = (VoAttributeRow) voAttPanel.getWidget(i, 4);
                    if (voAttRow != null) {
                        voAttributesMap.add(voAttRow.getAttribute().toMap());
                    }
                }
            }
            changed = true;
            handler.handleResponse(new MessageResponse(MessageResponse.OK, this));
        }
        hide();
    }

    public void addVOAttribute(FlexTable voAttPanel) {
        new VoAttributeRow(voAttPanel);
    }

    public Vector<HashMap<String, String>> getAttributes() {
        return voAttributesMap;
    }

    public String getName() {
        return nameInput.getText();
    }

    public String getDescription() {
        return descriptionInput.getText();
    }

}
