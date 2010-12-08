
package com.googlecode.onevre.protocols.gsi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.glite.voms.VOMSAttribute;
import org.glite.voms.VOMSValidator;

import com.googlecode.onevre.ag.types.VOAttribute;

import java.util.HashMap;
import java.util.Vector;

public class CredentialMappings {

	GSSCredential credential = null;

	String credentialDN = null;

	Vector<VOAttribute> voAttributes = new Vector<VOAttribute>();

	Log log = LogFactory.getLog(this.getClass());

	public CredentialMappings(GSSCredential cred){
		credential = cred;
		try {
			credentialDN = cred.getName().toString();
		} catch (GSSException e) {
			log.error("credential.getName threw:", e);
		}
		GlobusGSSCredentialImpl globuscred = (GlobusGSSCredentialImpl)credential;
		VOMSValidator validator = new VOMSValidator(globuscred.getCertificateChain()).validate();
		Vector attribute = new Vector(validator.getVOMSAttributes());
		for (Object attrobj : attribute){
			VOMSAttribute vomsAttribute = (VOMSAttribute)attrobj;
			String voText = (String)vomsAttribute.getFullyQualifiedAttributes().get(0);
			log.info("analyzing VO attribute: " + voText);
			if (voText.startsWith("/")){
				voText = voText.substring(1);
			}
			String fqaText [] = voText.split("/");
			String vo = fqaText[0];
			String group = "";
			String capability = "";
			String sep = "";
			int length = fqaText.length;
			if (fqaText[length-1].startsWith("Capability=")){
				capability = fqaText[length-1].substring(11);
				length--;
			}
			String role="";
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
			VOAttribute voAttribute = new VOAttribute(vo,group,role,capability);
			voAttributes.add(voAttribute);
		}
	}

	public CredentialMappings(String credDN, Vector<String> voAttributeStrings){
		credentialDN = credDN;
		for (String voText : voAttributeStrings){
			log.info("analyzing VO attribute: " + voText);
			if (voText.startsWith("/")){
				voText = voText.substring(1);
			}
			String fqaText [] = voText.split("/");
			String vo = fqaText[0];
			String group = "";
			String capability = "";
			String sep = "";
			int length = fqaText.length;
			if (fqaText[length-1].startsWith("Capability=")){
				capability = fqaText[length-1].substring(11);
				length--;
			}
			String role="";
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
			VOAttribute voAttribute = new VOAttribute(vo,group,role,capability);
			voAttributes.add(voAttribute);
		}
	}

	public String getDN(){
		if (credential !=null){
			try {
				return credential.getName().toString();
			} catch (GSSException e) {
				log.error("credential.getName threw:", e);
			}
		}
		return credentialDN;
	}

	public int getLifetime(){
		if (credential!=null){
			try {
				return credential.getRemainingLifetime();
			} catch (GSSException e) {
				log.error("credential.getRemainingLifetime threw:", e);
			}
		}
		return 0;
	}

	public Vector<VOAttribute> getVoAttributes(){
		return voAttributes;
	}

}
