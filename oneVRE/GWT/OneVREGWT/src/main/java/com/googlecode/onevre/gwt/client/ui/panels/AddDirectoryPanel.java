package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import com.googlecode.onevre.gwt.client.ag.types.DataDescription;
import com.googlecode.onevre.gwt.client.ui.Icons;

import com.googlecode.onevre.gwt.common.client.MessageResponse;
import com.googlecode.onevre.gwt.common.client.MessageResponseHandler;
import com.googlecode.onevre.gwt.common.client.ModalPopup;

public class AddDirectoryPanel extends ModalPopup<DockPanel> implements ClickHandler {

    private static final String DATE_FORMAT = "EEE, MMM d, yyyy, HH:mm:ss";

    private String parentId = null;
    private String venueUri = null;

    private MessageResponseHandler handler = null;
    private DataDescription dataDescription = null;

    private TextBox directoryNameInput = new TextBox();
    private TextArea descriptionInput = new TextArea();
    private DateBox dateInput = new DateBox();

    private TextBox locationInput = new TextBox();
    private TextBox homeInput = new TextBox();
    private ListBox profileInput = new ListBox();
    private PushButton cancel = new PushButton("Cancel", this);
    private PushButton clear = new PushButton("Clear", this);
    private PushButton ok = new PushButton("Create", this);
    private FlexTable profilepanel = new FlexTable();
//  private NamedFrame targetFrame = new NamedFrame("victim");
    private int hierarchyLevel;

    private boolean changed = false;

    public boolean hasChanged() {
        return changed;
    }

    public AddDirectoryPanel(MessageResponseHandler handler, String parentId, String venueURI, int hierarchyLevel) {
        super(new DockPanel());
        DockPanel panel = getWidget();
        Image img = new Image(Icons.addDirectoryIcon);
        img.setHeight("64px");

        this.parentId = parentId;
        this.venueUri = venueURI;
        this.handler = handler;

        String baseurl = GWT.getModuleBaseURL();
        if (baseurl.endsWith("/")) {
            baseurl = baseurl.substring(0, baseurl.length() - 1);
        }
        panel.add(img, DockPanel.WEST);
        panel.setCellVerticalAlignment(img, DockPanel.ALIGN_MIDDLE);
        profilepanel.setWidget(1, 0, new Image(Icons.folderIcon));
        profilepanel.setText(1, 1, "Name: *");
        profilepanel.setWidget(1, 2, directoryNameInput);
        profilepanel.getFlexCellFormatter().setColSpan(1, 2, 2);
        Image docInfo = new Image(Icons.docInfoIcon);
        docInfo.setHeight("22px");
        profilepanel.setWidget(2, 0, docInfo);
        profilepanel.setText(2, 1, "Description: *");
        descriptionInput.setVisibleLines(3);
        profilepanel.setWidget(2, 2, descriptionInput);
        profilepanel.getFlexCellFormatter().setColSpan(2, 2, 2);

        Image timeout = new Image(Icons.timeoutIcon);
        timeout.setHeight("22px");
        profilepanel.setWidget(3, 0, timeout);
        profilepanel.setText(3, 1, "expires:");
        profilepanel.setWidget(3, 2, dateInput);
        profilepanel.setWidget(3, 3, clear);
        dateInput.setValue(null);
        panel.add(profilepanel, DockPanel.CENTER);
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
            Date date = dateInput.getValue();
            String expString = "";
            if (date != null) {
                expString = DateTimeFormat.getFormat(DATE_FORMAT).format(date);
            }
            String name = directoryNameInput.getText();
            String desc = descriptionInput.getValue();
            dataDescription = new DataDescription(name, desc, expString);
            dataDescription.setObjectType(DataDescription.TYPE_DIR);
            dataDescription.setParentId(parentId);
            dataDescription.setHierarchyLevel(hierarchyLevel);
            handler.handleResponse(new MessageResponse(MessageResponse.OK, this));
        }
        hide();
    }

    public DataDescription getDataDescription() {
        return dataDescription;
    }

}
