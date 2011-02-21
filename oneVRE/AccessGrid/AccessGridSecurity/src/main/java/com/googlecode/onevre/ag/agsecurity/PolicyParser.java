package com.googlecode.onevre.ag.agsecurity;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PolicyParser {

    private String name = "";

    private String value = "";

    private HashMap<String, String> attributes = null;

    private Vector<Node> subElements =  null;

  //  HashMap<String, Class<XmlParsable>> registeredClasses = new HashMap<String, Class<XmlParsable>>();

    public PolicyParser(String xml) throws  IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));
        subElements =  new Vector<Node>();
        subElements.add(document.getFirstChild());
    }

    public PolicyParser(InputStream xmlStream) throws  IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlStream);
        subElements =  new Vector<Node>();
        subElements.add(document.getFirstChild());
    }

    public void parse(Node node) {
        attributes = new HashMap<String, String>();
        subElements =  new Vector<Node>();
        name = node.getNodeName();
        value = node.getNodeValue();
        NamedNodeMap attributeMap = node.getAttributes();
        for (int i = 0; i < attributeMap.getLength(); i++) {
            Node attr = attributeMap.item(i);
            attributes.put(attr.getNodeName(), attr.getNodeValue());
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            subElements.add(children.item(i));
        }
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }


/*    public Class<XmlParsable> getParsableClass(Node node){
        return registeredClasses.get(node.getNodeName());
    }
*/
    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public Vector<Node> getSubElements() {
        return subElements;
    }

    public static void main(String[] args) {
        PolicyParser parser;
        try {
            parser = new PolicyParser("<AuthorizationPolicy><Role name=\"VenueUsers\"/><Role name=\"Administrators\"><Subject auth_data=\"\" auth_type=\"x509\" name=\"/O=Access Grid/OU=agdev-ca.mcs.anl.gov/CN=VenueServer/sam.ag.manchester.ac.uk\"/><Subject auth_data=\"\" auth_type=\"x509\" name=\"/O=Access Grid/O=Argonne National Laboratory/OU=Futures Lab Anonymous Authority/CN=Anonymous User 46196aa809cbdec1670ee62177f263d2\"/><Subject auth_data=\"\" auth_type=\"x509\" name=\"/O=Access Grid/OU=agdev-ca.mcs.anl.gov/CN=VenueServer/fraser.ag.manchester.ac.uk\"/></Role><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Action name=\"Enter\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"Exit\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetStreams\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"UpdateLifetime\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"NegotiateCapabilities\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetStaticStreams\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetUploadDescriptor\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetRolesForSubject\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"CreateApplication\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"UpdateApplication\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"DestroyApplication\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"UpdateClientProfile\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"AddService\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"RemoveService\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"UpdateService\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetServices\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"AddData\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"UpdateData\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"RemoveData\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetDataStoreInformation\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetDataDescriptions\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"AddNetworkService\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"RemoveNetworkService\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetNetworkServices\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetClients\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetCachedProfiles\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"AddConnection\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"RemoveConnection\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetConnections\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"SetConnections\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetEncryptMedia\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetDescription\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetName\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetApplication\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetApplications\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"AddNetworkLocationToStream\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"RemoveNetworkLocationFromStream\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetEventServiceLocation\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"DetermineSubjectRoles\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"AddNetService\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"RemoveNetService\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"IsAuthorized\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"IsValid\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"AllocateMulticastLocation\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"RecycleMulticastLocation\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetState\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action><Action name=\"GetVersion\"><Role name=\"AllowedEntry\"/><Role name=\"Everybody\"/><Role name=\"Administrators\"/></Action></AuthorizationPolicy>");
            parser.parse(parser.subElements.firstElement());
            System.out.println(parser.getName());
            System.out.println(parser.getAttributes());
            System.out.println(parser.getSubElements());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

