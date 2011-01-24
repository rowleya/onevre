package com.googlecode.onevre.ag.agsecurity;

import java.util.Vector;

import org.w3c.dom.Node;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

public class Role implements SoapSerializable {

/*
    <xs:complexType name=\"Role\">
      <xs:sequence>
        <xs:element name=\"name\" type=\"xs:string\"/>
        <xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"subjects\" type=\"xs:any\"/>
        <xs:element name=\"requireDefaultId\" type=\"xs:int\"/>
        <xs:any maxOccurs=\"unbounded\" minOccurs=\"0\"/>
      </xs:sequence>
    </xs:complexType>
*/
    private static final long serialVersionUID = 1L;

    // The name of the role
    private String name = null;

    // The subjects with this role
    private Vector<Subject> subjects = new Vector<Subject>();

    private int requireDefaultId = 0;

    private static final String[] SOAP_FIELDS =
        new String[]{"name",
                     "subjects",
                     "requireDefaultId"
                     };

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     null,
                     INT_TYPE
                     };

    public Role(){}

    public Role(String name,int requireDefaultId){
    	this.name = name;
    	this.requireDefaultId = requireDefaultId;
    }

    /**
     * Returns the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a subject
     * @param subject The subject to add
     */
    public void setSubjects(Subject subject) {
        if (!subjects.contains(subject)){
            subjects.add(subject);
        }
    }

    public Vector<Subject> getSubjects() {
        return subjects;
    }

    public boolean removeSubject(Subject subject){
        return subjects.remove(subject);
    }

    /**
     * Sets the subjects
     * @param subjects the subjects
     */
    public void setSubjects(Vector<Subject> subjects) {
        this.subjects = subjects;
    }

    /**
     * @return 1 if this role requires default subject, otherwise 0.
     */
    public int getRequiredDefault(){
        return requireDefaultId;
    }

    /**
     * @param requireDefaultId flag set to 1 if this role requires default subject, otherwise 0.
     */
    public void setRequiredDefault(int requireDefaultId){
        this.requireDefaultId = requireDefaultId;
    }

    public String getNameSpace() {
        return "http://www.accessgrid.org/v3.0";
    }

    public boolean hasSubject(Subject subject){
        return subjects.contains(subject);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Role)) {
            return false;
        }
        Role c = (Role) o;
        if ((c.name == null) || (name == null)) {
            return false;
        }
        if (!name.equals(c.name)) {
            return false;
        }
        return true;
    }

    public int hashCode(){
        return name.hashCode();
    }

    public String getSoapType() {
        return "Role";
    }

    public String[] getFields() {
        return SOAP_FIELDS;
    }

    public Object[] getTypes() {
         return SOAP_TYPES;
    }

    public void parseXml(PolicyParser parser, Node node) {
        parser.parse(node);
        name = parser.getAttributes().get("name");
        for (Node childNode : parser.getSubElements()){
            if (childNode.getNodeName().equals(new Subject().getSoapType())){
                Subject subject = new Subject();
                subject.parseXml(parser,childNode);
                subjects.add(subject);
            }
        }
    }

    public String toString(){
    	String out = getName();
    	return out;
    }


    public String toXml(){
        String xml = "<" +getSoapType() + " name=\"" + name+ "\">";
        for (Subject subject: subjects){
            xml += subject.toXml();
        }
        xml += "</" + getSoapType() + ">";
        return xml;
    }

}
