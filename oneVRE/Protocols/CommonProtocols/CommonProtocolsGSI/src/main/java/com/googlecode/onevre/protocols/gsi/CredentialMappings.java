
package com.googlecode.onevre.protocols.gsi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.glite.voms.VOMSAttribute;
import org.glite.voms.VOMSValidator;

import com.googlecode.onevre.ag.types.VOAttribute;

import java.util.Vector;

public class CredentialMappings {

    private GSSCredential credential = null;

    private String credentialDN = null;

    private Vector<VOAttribute> voAttributes = new Vector<VOAttribute>();

    private Log log = LogFactory.getLog(this.getClass());

    public CredentialMappings(GSSCredential cred) {
        credential = cred;
        try {
            credentialDN = cred.getName().toString();
        } catch (GSSException e) {
            log.error("credential.getName threw:", e);
        }
        GlobusGSSCredentialImpl globuscred = (GlobusGSSCredentialImpl) credential;
        VOMSValidator validator = new VOMSValidator(globuscred.getCertificateChain()).validate();
        @SuppressWarnings("unchecked")
        Vector<Object> attribute = new Vector<Object>(validator.getVOMSAttributes());
        for (Object attrobj : attribute) {
            VOMSAttribute vomsAttribute = (VOMSAttribute) attrobj;
            String voText = (String) vomsAttribute.getFullyQualifiedAttributes().get(0);
            log.info("analyzing VO attribute: " + voText);
            VOAttribute voAttribute = new VOAttribute(voText);
            voAttributes.add(voAttribute);
        }
    }

    public CredentialMappings(String credDN, Vector<String> voAttributeStrings) {
        credentialDN = credDN;
        for (String voText : voAttributeStrings) {
            log.info("analyzing VO attribute: " + voText);
            VOAttribute voAttribute = new VOAttribute(voText);
            voAttributes.add(voAttribute);
        }
    }

    public GSSCredential getCredential() {
        return credential;
    }

    public String getDN() {
        if (credential != null) {
            try {
                return credential.getName().toString();
            } catch (GSSException e) {
                log.error("credential.getName threw:", e);
            }
        }
        return credentialDN;
    }

    public int getLifetime() {
        if (credential != null) {
            try {
                return credential.getRemainingLifetime();
            } catch (GSSException e) {
                log.error("credential.getRemainingLifetime threw:", e);
            }
        }
        return 0;
    }

    public Vector<VOAttribute> getVoAttributes() {
        return voAttributes;
    }

}
