package com.googlecode.onevre.gwt.client;

import java.io.Serializable;

public class Parameter implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String value;


    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
