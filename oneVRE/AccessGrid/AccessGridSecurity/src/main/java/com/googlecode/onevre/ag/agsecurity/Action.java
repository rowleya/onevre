package com.googlecode.onevre.ag.agsecurity;

import java.util.Vector;

import org.w3c.dom.Node;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

public class Action implements SoapSerializable {

/*
    <xs:complexType name=\"Action\">
      <xs:sequence>
        <xs:element name=\"name\" type=\"xs:string\"/>
          <xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\"roles\" type=\"tns:Role\"/>
        <xs:any maxOccurs=\"unbounded\" minOccurs=\"0\"/>
      </xs:sequence>
    </xs:complexType>
*/
    private static final long serialVersionUID = 1L;

    // The name of the action
    private String name = null;

    // The roles required to take this action
    private Vector<Role> roles = new Vector<Role>();

    private static final String[] SOAP_FIELDS =
        new String[]{"name",
                     "roles"
                     };

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     null
                     };

    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(Vector<Role> roles) {
        this.roles = roles;
    }

    public boolean hasRole(Role role){
        return roles.contains(role);
    }

    public void setRoles(Role role) {
        if (!roles.contains(role)){
            roles.add(role);
        }
    }

    public boolean removeRole(Role role){
        return roles.remove(role);
    }


    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Action)) {
            return false;
        }
        Action c = (Action) o;
        if ((c.name == null) || (name == null)) {
            return false;
        }
        if (!name.equals(c.name)) {
            return false;
        }
        return true;
    }

    public Role findRole(String roleName){
        if (roleName==null) {
            return null;
        }
        for (Role role : roles){
            if (roleName.equals(role.getName())){
                return role;
            }
        }
        return null;
    }

    public Vector<Role> getRoles() {
        return roles;
    }

    public String getNameSpace() {
        return "http://www.accessgrid.org/v3.0";
    }

    public String getSoapType() {
        // TODO Auto-generated method stub
        return "Action";
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
            if (childNode.getNodeName().equals(new Role().getSoapType())){
                Role role = new Role();
                role.parseXml(parser,childNode);
                roles.add(role);
            }
        }
    }

    public String toXml(){
        String xml = "<" +getSoapType() + " name=\"" + name+ "\">";
        for (Role role: roles){
            xml += "<" + role.getSoapType() + " name=\"" + role.getName() +"\"/>";
        }
        xml += "</" + getSoapType() + ">";
        return xml;
    }

}
