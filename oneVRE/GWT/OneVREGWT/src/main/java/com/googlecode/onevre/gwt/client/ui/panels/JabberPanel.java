package com.googlecode.onevre.gwt.client.ui.panels;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.onevre.gwt.client.Application;
import com.googlecode.onevre.gwt.client.ag.types.ClientProfile;
import com.googlecode.onevre.gwt.client.ag.types.JabberMessage;

public class JabberPanel extends FlexTable implements ClickHandler , KeyPressHandler {

    private static final int KEY_CODE_RETURN = 13;

    private HashMap<String, String> participantColour = new HashMap<String, String>();

    private String jabberRoomColour = "#00CC00";
    private String[] jabberColours = new String[] {"#FF0000", "#00FF00", "#0000FF", "#FF00FF", "#00FFFF"};

    private String venueUri = "";

    private HTML jabberText = new HTML();

    private TextBox jabberInput = new TextBox();
    private ScrollPanel jabberTextPanel = new ScrollPanel();

    public JabberPanel(String venueUri) {
        this.venueUri = venueUri;
        this.setWidth("100%");
        this.setHeight("100%");

        jabberText.setWidth("100%");
        jabberInput.setWidth("100%");

        jabberTextPanel.setStyleName("jabberPanel");
        jabberTextPanel.setWidth("100%");
        jabberTextPanel.setHeight("80px");
        jabberTextPanel.add(jabberText);



        PushButton submit = new PushButton("Send");
        submit.setWidth("80px");
        submit.addClickHandler(this);
        jabberInput.addKeyPressHandler(this);

        this.setWidget(0, 0, jabberTextPanel);
        this.setWidget(1, 0, jabberInput);
        this.setWidget(1, 1, submit);
        this.getFlexCellFormatter().setColSpan(0, 0, 2);
        this.getFlexCellFormatter().setStyleName(0, 0, "textarea");
    }

    public void addMessage(JabberMessage message) {
        String colour = "";
        String from = message.getFrom();
        if (from.equals("")) {
            colour = jabberRoomColour;
            from = "***";
        } else {
            colour = participantColour.get(from);
            if (colour == null) {
                colour = jabberColours[participantColour.size() % jabberColours.length];
                participantColour.put(from, colour);
            }
            from = "&lt;" + from + "&gt; ";
        }
        String content = jabberText.getHTML();
        content += "<span style='color: " + colour + "';>"
            + "[" + message.getDate() + "] " + from + " "
            + message.getMessage() + "</span><br/>";
        GWT.log(content);
        jabberText.setHTML(content);
        jabberTextPanel.scrollToBottom();

    }

    public void clear() {
        jabberText.setText("");
    }

    public void updateClient(ClientProfile user) {
    }

    public void onClick(ClickEvent paramClickEvent) {
        submit();
    }

    public void onKeyPress(KeyPressEvent event) {
        if (event.getCharCode() == KEY_CODE_RETURN) {
            submit();
        }

    }

    private void submit() {
        Application.getJabberManager().submit(venueUri, jabberInput.getText());
        jabberInput.setText("");
    }

}
