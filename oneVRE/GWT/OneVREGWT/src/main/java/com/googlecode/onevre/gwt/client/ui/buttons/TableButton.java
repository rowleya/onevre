package com.googlecode.onevre.gwt.client.ui.buttons;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;

public class TableButton extends PushButton {

	private int value = -1;

	public TableButton(String text, int value, ClickHandler handler){
		super(text,handler);
		this.value = value;
	}

	public int getValue(){
		return value;
	}
}
