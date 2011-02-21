package com.googlecode.onevre.gwt.client.ui;

import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;

public class ButtonPanel implements ClickHandler {

    private static final int ICON_SIZE = 16;
    private static final int SPACER_SIZE = 4;

    public static final HorizontalAlignmentConstant LEFT = HorizontalPanel.ALIGN_LEFT;
    public static final HorizontalAlignmentConstant RIGHT = HorizontalPanel.ALIGN_RIGHT;

    private HorizontalPanel panel = new HorizontalPanel();

    private HashMap<PushButton, ActionButton> buttons =
        new HashMap<PushButton, ActionButton>();

    public ButtonPanel(String id, HorizontalAlignmentConstant align) {
        panel.setHorizontalAlignment(align);
   }

    public void addButton(ActionButton butt) {
        addButton(butt, ICON_SIZE, ICON_SIZE);
    }

    public void addButton(ActionButton butt , int height , int width) {
        PushButton button = butt.getButton();
        button.setWidth(width + "px");
        button.setHeight(height + "px");
        panel.add(button);
        buttons.put(button, butt);
        button.addClickHandler(this);
    }

    public void onClick(ClickEvent event) {
        buttons.get(event.getSource()).onClick();
    }

    public Panel getPanel() {
        return panel;
    }

    public void addSpacer() {
        addSpacer(SPACER_SIZE);
    }

    public void addSpacer(int size) {
        SimplePanel p = new SimplePanel();
        p.setWidth(size + "px");
        p.setHeight(ICON_SIZE + "px");
        panel.add(p);
    }

}
