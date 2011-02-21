package com.googlecode.onevre.gwt.client.ui.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.onevre.gwt.client.xmlrpc.AddTrustedServer;
import com.googlecode.onevre.gwt.common.client.XmlResponseHandler;

public class NewTrustedServerPanel extends DockPanel implements ClickHandler {

    private AddTrustedServer addTrustedServer = null;

    private TextBox nameInput = new TextBox();
    private TextBox urlInput = new TextBox();
    private PushButton add = new PushButton("add", this);
    private PushButton clear = new PushButton("clear", this);

    public NewTrustedServerPanel(XmlResponseHandler handler) {
        Grid newpanel = new Grid(2, 3);
        Image server = new Image(GWT.getModuleBaseURL() + "images/server.png");
        server.setHeight("16px");
        newpanel.setWidget(0, 0, server);
        newpanel.setText(0, 1, "Name: ");
        newpanel.setWidget(0, 2, nameInput);
        newpanel.setWidget(1, 0, new Image(GWT.getModuleBaseURL() + "images/icons/location.png"));
        newpanel.setText(1, 1, "Url: ");
        newpanel.setWidget(1, 2, urlInput);
        this.add(newpanel, DockPanel.CENTER);
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(add);
        buttons.add(clear);
        this.add(buttons, DockPanel.EAST);
        this.setCellVerticalAlignment(buttons, DockPanel.ALIGN_MIDDLE);
        addTrustedServer = new AddTrustedServer(handler);
    }

    public void clearPanel() {
        nameInput.setText("");
        urlInput.setText("");
    }

    public void onClick(ClickEvent event) {
        if (event.getSource().equals(add)) {
            addTrustedServer.addTrustedServers(nameInput.getText(), urlInput.getText());
        }
        if (event.getSource().equals(clear)) {
            clearPanel();
        }
    }

}
