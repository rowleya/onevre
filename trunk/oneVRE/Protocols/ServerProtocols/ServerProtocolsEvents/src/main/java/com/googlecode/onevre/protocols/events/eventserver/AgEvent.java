package com.googlecode.onevre.protocols.events.eventserver;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AgEvent {

	String eventSource = null;

	String eventName = null;

	Object eventObject = null;

	public AgEvent() {
	}

	@XmlElement
	public String getEventSource() {
		return eventSource;
	}

	@XmlElement
	public String getEventName() {
		return eventName;
	}

	@XmlElement
	public Object getEventObject() {
		return eventObject;
	}

	public AgEvent(String eventSource, String eventName, Object eventObject) {
		super();
		this.eventSource = eventSource;
		this.eventName = eventName;
		this.eventObject = eventObject;
	}
}
