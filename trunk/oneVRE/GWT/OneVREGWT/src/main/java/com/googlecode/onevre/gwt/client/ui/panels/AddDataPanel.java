package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ui.Icons;

import com.googlecode.onevre.gwt.common.client.ModalPopup;

public class AddDataPanel extends ModalPopup<DockPanel> implements ClickHandler {

    private static final String DATE_FORMAT = "EEE, MMM d, yyyy, HH:mm:ss";

    private FileUpload fileNameInput = new FileUpload();
    private TextArea descriptionInput = new TextArea();
    private DateBox dateInput = new DateBox();
//  private TextBox  = new TextBox();
    private TextBox locationInput = new TextBox();
    private TextBox homeInput = new TextBox();
    private ListBox profileInput = new ListBox();
    private PushButton cancel = new PushButton("Cancel", this);
    private PushButton clear = new PushButton("Clear", this);
    private PushButton ok = new PushButton("Upload Data Item", this);
    private FlexTable profilepanel = new FlexTable();
//  private NamedFrame targetFrame = new NamedFrame("victim");
    private FormPanel form = null;
    private Hidden parent = new Hidden("parentId");
    private Hidden namespace = new Hidden("namespace", Application.getParam("pag_namespace"));
    private Hidden venueUri = new Hidden("venueUri");
    private Hidden expiry = new Hidden("expiry");
    private Hidden description = new Hidden("description");

    private boolean changed = false;

    public boolean hasChanged() {
        return changed;
    }

    public AddDataPanel(String parentId, String venueURI, NamedFrame targetFrame) {
        super(new DockPanel());
        DockPanel panel = getWidget();
        Image img = new Image(Icons.addDocumentIcon);
        img.setHeight("64px");

        parent.setValue(parentId);
        venueUri.setValue(venueURI);

        form = new FormPanel(targetFrame);

        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);
        form.setWidth("100%");

        String baseurl = GWT.getModuleBaseURL();
        if (baseurl.endsWith("/")) {
            baseurl = baseurl.substring(0, baseurl.length() - 1);
        }
        form.setAction(baseurl + Application.getParam("pag_dataUploadUrl"));

        HorizontalPanel hiddenElements = new HorizontalPanel();
        profilepanel.setWidget(0, 0, hiddenElements);
        hiddenElements.add(parent);
        hiddenElements.add(namespace);
        hiddenElements.add(venueUri);
        hiddenElements.add(expiry);
        hiddenElements.add(description);
        //hiddenElements.add(targetFrame);
//        targetFrame.setVisible(false);


        panel.add(img, DockPanel.WEST);
        panel.setCellVerticalAlignment(img, DockPanel.ALIGN_MIDDLE);
//        Grid profilepanel = new Grid (3,3);
        profilepanel.setWidget(1, 0, new Image(Icons.fileIcon));
        profilepanel.setText(1, 1, "Name: *");
        fileNameInput.setName("file");
        profilepanel.setWidget(1, 2, fileNameInput);
        profilepanel.getFlexCellFormatter().setColSpan(1, 2, 2);
        Image docInfo = new Image(Icons.docInfoIcon);
        docInfo.setHeight("22px");
        profilepanel.setWidget(2, 0, docInfo);
        profilepanel.setText(2, 1, "Description: *");
        descriptionInput.setVisibleLines(3);
        //descriptionInput.setName("description");
        profilepanel.setWidget(2, 2, descriptionInput);
        profilepanel.getFlexCellFormatter().setColSpan(2, 2, 2);
        form.add(profilepanel);
        panel.add(form, DockPanel.CENTER);

        Image timeout = new Image(Icons.timeoutIcon);
        timeout.setHeight("22px");
        profilepanel.setWidget(3, 0, timeout);
        profilepanel.setText(3, 1, "expires:");
        profilepanel.setWidget(3, 2, dateInput);
        profilepanel.setWidget(3, 3, clear);
        dateInput.setValue(null);

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
            expiry.setValue(expString);
            description.setValue(descriptionInput.getValue());
            if (fileNameInput.getFilename().equals("")) {
                return;
            }
            form.submit();
        }
        hide();
    }

}
