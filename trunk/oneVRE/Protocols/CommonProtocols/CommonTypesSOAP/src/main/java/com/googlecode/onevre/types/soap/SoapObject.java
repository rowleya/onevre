package com.googlecode.onevre.types.soap;

import java.util.HashMap;
import java.util.Vector;

import com.googlecode.onevre.types.soap.exceptions.SoapException;

/**
 * @author Tobias M Schiebeck
 *
 */
public class SoapObject {

    private String name = null;

    private String soapXML = null;

    private String value = null;

    private String soapType = null;

    private Vector<String> subObjects = new Vector<String>();

    private String nameSpace = null;

    private HashMap<String, Vector<SoapObject>> subObjectMap = new HashMap<String, Vector<SoapObject>>();

    private boolean nil = false;

    /**
     * returns the names of all sub-objects
     * @return the names of all sub-objects
     */
    public Vector<String> getSubObjectNames() {
        return subObjects;
    }

    /**
     * returns the sub-object-vector of a given name
     * @param name the name of the sub-object to return
     * @return The SoapObject vector of the name
     */
    public Vector<SoapObject> getSubObject(String name) {
        return subObjectMap.get(name);
    }
    /**
     * @param name the name of the soap entity
     * @param soapType the soap type of the entity
     * @param nameSpace the soap name-space of the entity
     */
    public SoapObject(String name, String soapType, String nameSpace){
        this.name=name;
        this.soapType=soapType;
        this.value="";
        this.nameSpace=nameSpace;
    }

    /**
     * Adds a soapObject as a subObject of the current object.
     * If the last item in the subObjects has the same name as
     * the added soapObject, the object is added as component in
     * the object vector.
     *
     * @param soapObject soapObject to add
     * @throws SoapException if a component of the name of the soapObject
     * exists in the objectMap, but other objects have been added since
     */
    public void addSubObject(SoapObject soapObject) throws SoapException{
        if (!subObjects.isEmpty()){
            String lastObjectName = subObjects.lastElement();
            if (soapObject.name.equals(lastObjectName)){
                subObjectMap.get(soapObject.name).add(soapObject);
                return;
            }
            if (subObjectMap.get(soapObject.name)!=null){
                throw new SoapException("soapObject "+ soapObject.name +" already in parent ("+ name +") hierarchy" );
            }
        }
        Vector<SoapObject> soapObjectVector = new Vector<SoapObject>();
        soapObjectVector.add(soapObject);
        subObjectMap.put(soapObject.name, soapObjectVector);
        subObjects.add(soapObject.name);
    }
    /**
     * Sets the name of the SoapObject
     * @param name the name
     */
    public void setName(String name){
        this.name=name;
    }
    /**
     * Sets the name of the SoapObject
     * @param nameSpace the nameSpace
     */
    public void setNameSpace(String nameSpace){
        this.nameSpace=nameSpace;
    }
   /**
     * Sets the SoapObject to nil
     * @param nil
     */
    public void setNil(boolean nil){
        this.nil=nil;
    }

    /**
     * checks if the SoapObject is nil
     * @return flag if the SoapObject is nil
     */
    public boolean isNil(){
        return nil;
    }

    /**
     * @return the name
     */
    public String getName(){
        return name;
    }

    /**
     * @return the name
     */
    public String getNameSpace(){
        return nameSpace;
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

    /**
     * @param soapType the soapType to set
     */
    public void setSoapType(String soapType) {
        this.soapType = soapType;
    }

    /**
     * @return the soapType
     */
    public String getSoapType() {
        return soapType;
    }

    /**
     * @param soapXML the soapXML to set
     */
    public void setSoapXML(String soapXML) {
        this.soapXML = soapXML;
    }

    /**
     * @return the soapXML
     */
    public String getSoapXML() {
        return soapXML;
    }

    private String printObject(String prefix){
        String out = "";
        out += prefix + "Name: " + name + " Type: " +  soapType + " Value:" + value + "\n";
        for (String n : subObjects){
            for (SoapObject o :subObjectMap.get(n)){
                out+=o.printObject(prefix+"    ");
            }
        }
        return out;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return printObject("");
    }
}
