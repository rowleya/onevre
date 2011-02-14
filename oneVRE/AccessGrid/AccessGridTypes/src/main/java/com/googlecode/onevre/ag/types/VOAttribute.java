package com.googlecode.onevre.ag.types;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.onevre.types.soap.interfaces.SoapSerializable;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class VOAttribute implements SoapSerializable {

	Log log = LogFactory.getLog(this.getClass());

	private static final long serialVersionUID = 1L;

    private static final String[] SOAP_FIELDS =
        new String[]{"vo",
                     "group",
                     "role",
                     "cap"};

    private static final String[] SOAP_TYPES =
        new String[]{STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE,
                     STRING_TYPE};


    // The scheme of the profile (e.g. "user" or "service")
    private String vo = "";

    // The name of the venue server
    private String group = "";

    private PrintWriter securityLog = null;

    // The url address of the venue server
    private String role = "";

    // The external id of the client (e.g. public key)
    private String cap = "";

    public VOAttribute(){
    }

    public VOAttribute(String vo, String group, String role, String cap){
    	this.vo = vo;
    	this.group = group;
    	this.role = role;
    	this.cap = cap;
    }

    public VOAttribute(String voText){
    	if (voText.startsWith("/")){
			voText = voText.substring(1);
		}
		String fqaText [] = voText.split("/");
		vo = fqaText[0];
		String sep = "";
		int length = fqaText.length;
		if (fqaText[length-1].startsWith("Capability=")){
			cap = fqaText[length-1].substring(11);
			length--;
		}
		if (fqaText[length-1].startsWith("Role=")){
			role = fqaText[length-1].substring(5);
			length--;
		}
		for (int i = 1; i<length ;i++){
			String it = fqaText[i];
			if (it.equals("")){
				continue;
			}
			group += sep + it;
			sep="/";
		}
		if (group.equals("")){
			group="/";
		}
    }

    public VOAttribute(HashMap<String, String> voMap){
    	this.vo = voMap.get("vo");
    	this.group = voMap.get("group");
    	this.role = voMap.get("role");
    	this.cap = voMap.get("cap");
    }

	/**
	 * @param name the name to set
	 */
	public void setVo(String vo) {
		this.vo = vo;
	}

	/**
	 * @return the name
	 */
	@XmlElement
	public String getVo() {
		return vo;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return the protocol
	 */
	@XmlElement
	public String getGroup() {
		return group;
	}

	/**
	 * @param url the url to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the url
	 */
	@XmlElement
	public String getRole() {
		return role;
	}

	/**
	 * @param portNumber the portNumber to set
	 */
	public void setCap(String cap) {
		this.cap = cap;
	}


	/**
	 * @return the defaultVenue
	 */
	@XmlElement
	public String getCap() {
		return cap;
	}

	public String toString(){
		String out = vo +" G="+ group + " R=" + role + " C=" + cap;
		return out;
	}

	public String toFile(){
		String out = "/"+ vo ;
		if (!group.equals("/")){
			out += "/"+ group;
		}
		out += "/Role=" + role + "/Capability=" + cap;
		return out;
	}

    public int hashCode(){
        return toString().hashCode();
    }

    public boolean equals(Object o){
        return this.toString().equals(((VOAttribute)o).toString());
    }

    public boolean matches(VOAttribute other){
    	if (securityLog!=null){
    		securityLog.println(toString() + " matches " +other);
    	}
    	log.info(toString() + " matches " +other);
    	if (other.vo.equals("")){
        	log.info(" no VO required access permitted to member of : " + vo );
        	if (securityLog!=null){
        		securityLog.println(" no VO required access permitted to member of : " + vo );
        	}
        	return true;
    	}
    	if (!other.vo.equals(vo)){
    		log.info("VO doesn't match");
        	if (securityLog!=null){
        		securityLog.println("VO doesn't match");
        	}
        	return false;
    	}
   /* 	if (other.group.equals("")||group.equals("")){
        	log.info("group not provided");
        	if (securityLog!=null){
        		securityLog.println("group not provided");
        	}
        	return true;
    	}*/
    	String gr = "";
    	String ogr = "";
    	if (!group.startsWith("/")){
    		gr="/";
    	}
    	if (!other.group.startsWith("/")){
    		ogr="/";
    	}
    	gr += group;
    	ogr += other.group;
    	if (gr.startsWith(ogr)){
        	log.info("group " + group +" allowed as subgroup of "+ other.group );
        	if (securityLog!=null){
        		securityLog.println("group " + group +" allowed as subgroup of "+ other.group );
        	}
        	return true;
    	}

    	if (!other.group.equals(group)){
    		log.info("group doesn't match");
        	if (securityLog!=null){
        		securityLog.println("group doesn't match");
        	}
    		return false;
    	}
    	if (other.role.equals("")||role.equals("")){
        	log.info("role not provided");
        	if (securityLog!=null){
        		securityLog.println("role not provided");
        	}
    		return true;
    	}
    	if (!other.role.equals(role)){
    		log.info("role doesn't match");
        	if (securityLog!=null){
        		securityLog.println("role doesn't match");
        	}
    		return false;
    	}
    	if (other.cap.equals("")||cap.equals("")){
        	log.info("cap not provided");
        	if (securityLog!=null){
        		securityLog.println("cap not provided");
        	}
    		return true;
    	}
    	if (!other.cap.equals(cap)){
    		log.info("cap doesn't match");
        	if (securityLog!=null){
        		securityLog.println("cap doesn't match");
        	}
    		return false;
    	}
    	return true;
    }

    /*
    public String toLog(){
        return name+" = "+ getDefaultVenueUrl() + " (" + version +")";
    }
*/

	public String getSoapType() {
        return "VOAttribute";
	}

	public String getNameSpace() {
        return "http://www.accessgrid.org/v3.0";
	}

	public String[] getFields() {
        return SOAP_FIELDS;
	}

	public Object[] getTypes() {
        return SOAP_TYPES;
	}

	public void setLogger(PrintWriter securityLog) {
		this.securityLog = securityLog;

	}



}
