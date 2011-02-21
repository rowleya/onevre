package com.googlecode.onevre.ag.agserver;

import java.io.PrintWriter;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;


import com.googlecode.onevre.ag.agsecurity.Action;
import com.googlecode.onevre.ag.agsecurity.PolicyParser;
import com.googlecode.onevre.ag.agsecurity.Role;
import com.googlecode.onevre.ag.agsecurity.Subject;
import com.googlecode.onevre.ag.types.VOAttribute;
import com.googlecode.onevre.types.soap.annotation.SoapParameter;
import com.googlecode.onevre.types.soap.annotation.SoapReturn;
import com.googlecode.onevre.types.soap.interfaces.SoapServable;
import com.googlecode.onevre.utils.Utils;

public class AuthorizationManager extends SoapServable {

    private Log log = LogFactory.getLog(this.getClass());

    private String id = Utils.generateID();

    private  Vector<Role> roles = new Vector<Role>();

    private  Vector<Role> defaultRoles = new Vector<Role>();

    private  Vector<Action> actions = new Vector<Action>();

    private Vector<VOAttribute> voAttributes = new Vector<VOAttribute>();

    private Vector<Role> rolesRequired = new Vector<Role>();

    private static final String AUTH_POLICY_START = "<AuthorizationPolicy>";

    private  String parent = null;

    private PrintWriter securityLog = null;

    private boolean identificationRequired = false;

    public AuthorizationManager(PrintWriter log) {
        securityLog = log;
    }

    @SoapReturn (
            name = "authorizedFlag"
        )
    public int isAuthorized(
            @SoapParameter("subject") Subject subject,
            @SoapParameter("action") Action action) {
        securityLog.println("Authorize Subject : " + subject + " Action : " + action);
        Vector<Role> roleList = getRolesForAction(action);
        if (subject == null) {
            securityLog.println("Authorizing action " + action.getName() + " for unidentified user");
            if (identificationRequired) {
                securityLog.println("Rejecting access from unidentified user; id required");
                return 0;
            }
            if (roleList.contains(VenueServerDefaults.Everybody)) {
                securityLog.println("Accepting access from unidentified user as part of Everybody role");
                return 1;
            }
            if (roleList.contains(VenueServerDefaults.VOMSdependent)) {
                Vector<VOAttribute> voAtts = new Vector<VOAttribute>();
                securityLog.println("Authorizing based on VO attributes: " + voAtts.toString());
                return checkVOMS(voAtts);
            }
            securityLog.println("Rejecting access from unidentified user as part of Everybody role");
            return 0;
        }
        securityLog.println("Authorizing action " + action.getName()
                + " for subject " + subject.getName());
        if (roleList.contains(VenueServerDefaults.Everybody)) {
            securityLog.println("Accepting access from " + subject.getName() + " as part of Everybody role");
            return 1;
        }
        if (roleList.contains(VenueServerDefaults.VOMSdependent)) {
            Vector<VOAttribute> subjectAttributes = subject.getVoAttributes();
            if (subjectAttributes.isEmpty()) {
                subjectAttributes.add(new VOAttribute());
            }
            securityLog.println("Subject VO attributes: " + subjectAttributes.toString());
            securityLog.println("Authorizing based on VO attributes: " + voAttributes.toString());
            if (voAttributes.size() == 0) {
                securityLog.println("Accepting access based on VO attributes - no VO attributes required");
                return 1;
            }
            return checkVOMS(subjectAttributes);
        }

        for (Role role : roleList) {
            if (role.hasSubject(subject)) {
                securityLog.println("Accepting access from " + subject.getName() + " as part of " + role.getName());
                return 1;
            }
        }
        securityLog.println("Rejecting access from " + subject.getName());
        return 0;
    }

    private int checkVOMS(Vector<VOAttribute> subjectAttributes) {
        if (subjectAttributes.isEmpty()) {
            subjectAttributes.add(new VOAttribute());
        }
        securityLog.println("in checkVOMS: " + voAttributes.toString() + " matches " + subjectAttributes.toString());
        for (VOAttribute voAttribute : voAttributes) {
            for (VOAttribute voAtt : subjectAttributes) {
                voAtt.setLogger(securityLog);
                if (voAtt.matches(voAttribute)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    public void setVOattributes(Vector<VOAttribute> voAttributes) {
        if (voAttributes != null) {
            this.voAttributes = voAttributes;
        }
    }


    public void addAction(@SoapParameter("action") Action action) {
        if (!actions.contains(action)) {
            actions.add(action);
        }
    }

    public void addAction(@SoapParameter("actionList") Action [] actionList) {
        for (Action action : actionList) {
            addAction(action);
        }
    }

    public void removeAction(@SoapParameter("action") Action action) {
        actions.remove(action);
    }

    public void addRole(@SoapParameter("role") Role role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    @SoapReturn(
            name = "actionList"
            )
    public Action[] getActions() {
        return actions.toArray(new Action[0]);
    }

    @SoapReturn(
            name = "actionList"
            )
    public Action[] listActions() {
        return getActions();
    }

    @SoapReturn(
            name = "actionList"
            )
    public Action[] getActionsForSubject(@SoapParameter("subject") Subject subject) {
        Vector<Action> actionList = new Vector<Action>();
        for (Role role : getRolesForSubject(subject)) {
            for (Action action : actions) {
                if (action.hasRole(role)) {
                    if (!actionList.contains(action)) {
                        actionList.add(action);
                    }
                }
            }
        }
        return actionList.toArray(new Action[0]);
    }

    @SoapReturn(
            name = "actionList"
            )
    public Action[] listActionsForSubject(@SoapParameter("subject") Subject subject) {
        return getActionsForSubject(subject);
    }

    @SoapReturn(
            name = "actionList"
            )
    public Action[] getActionsForRole(@SoapParameter("role") Role role) {
        Vector<Action> actionList = new Vector<Action>();
        for (Action action : actions) {
            if (action.hasRole(role)) {
                if (!actionList.contains(action)) {
                    actionList.add(action);
                }
            }
        }
        return actionList.toArray(new Action[0]);
    }

    @SoapReturn(
            name = "actionList"
            )
    public Action[] listActionsForRole(@SoapParameter("role") Role role) {
        return getActionsForRole(role);
    }

    @SoapReturn(
            name = "role"
            )
    public Role findRole(@SoapParameter("name") String name) {
        for (Role role : roles) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    public void removeRole(@SoapParameter("role") Role role) {
        roles.remove(role);
    }

    @SoapReturn(
            name = "roleList"
            )
    public Role[] getRoles() {
        return roles.toArray(new Role[0]);
    }

    @SoapReturn(
            name = "roleList"
            )
    public Role[] listRoles() {
        return getRoles();
    }

    public void addRoleToAction(
            @SoapParameter("action") Action action,
            @SoapParameter("role") Role role) {
        if (!roles.contains(role)) {
            securityLog.println("Couldn't find role: " + role.getName());
            return;
        }
        if (!actions.contains(action)) {
            securityLog.println("Couldn't find action: " + action.getName());
            addAction(action);
        }
        Action a = findAction(action.getName());
        if (!a.hasRole(role)) {
            a.setRoles(role);
        } else {
            securityLog.println("Role " + role.getName() + " already in action " + action.getName());
        }
    }

    public  void addRolesToAction(
            @SoapParameter("role") Role [] roleList,
            @SoapParameter("action") Action action) {
        for (Role role : roleList) {
            addRoleToAction(action, role);
        }
    }

    public void removeRoleFromAction(
            @SoapParameter("role") Role role,
            @SoapParameter("action") Action action) {

        Action a = findAction(action.getName());
        if (a == null) {
            return;
        }

        Role r = findRole(role.getName());
        if (r == null) {
            return;
        }
        a.removeRole(r);
    }

    public void addSubjectToRole(
            @SoapParameter("subject") Subject subject,
            @SoapParameter("role") Role role) {
        securityLog.println("Adding Subject: " + subject.getName() + " to Role: " + role.getName());
        if (!roles.contains(role)) {
            securityLog.println("Couldn't find role: " + role.getName());
            return;
        }
        if (!role.hasSubject(subject)) {
            role.setSubjects(subject);
        } else {
            securityLog.println("Subject " + subject.getName() + " already in Role " + role.getName());
        }
    }

    public void  addSubjectsToRole(
            @SoapParameter("subjectList") Subject [] subjectList,
            @SoapParameter("role") Role role) {
        securityLog.println("Adding Subjects to Role: " + role.getName());
        if (!roles.contains(role)) {
            securityLog.println("Couldn't find role: " + role.getName());
            return;
        }
        for (Subject subject : subjectList) {
            if (!role.hasSubject(subject)) {
                securityLog.println("Adding Subject: " + subject.getName());
                role.setSubjects(subject);
            } else {
                securityLog.println("Subject " + subject.getName() + " already in Role " + role.getName());
            }
        }
    }

    @SoapReturn(
            name = "subjectList"
            )
    public Subject[] getSubjects(@SoapParameter("role") Role role) {
        Vector<Subject> subjectList = new Vector<Subject>();
        if (role != null) {
            Role r = findRole(role.getName());
            if (r != null) {
                subjectList = r.getSubjects();
            }
        } else {
            for (Role r : roles) {
                for (Subject s : r.getSubjects()) {
                    if (!subjectList.contains(s)) {
                        subjectList.add(s);
                    }
                }
            }
        }
        return subjectList.toArray(new Subject[0]);
    }

    @SoapReturn(
            name = "subjectList"
            )
    public Subject[] listSubjects(@SoapParameter("role") Role role) {
        return getSubjects(role);
    }


    @SoapReturn(
            name = "roleList"
            )
    public Role[] getRolesForSubject(@SoapParameter("subject") Subject subject) {
        Vector<Role> roleList = new Vector<Role>();
        for (Role role : roles) {
            if (role.hasSubject(subject)) {
                roleList.add(role);
            }
        }
        return roleList.toArray(new Role[0]);
    }

    @SoapReturn(
            name = "roleList"
            )
    public Role[] listtRolesForSubject(@SoapParameter("subject") Subject subject) {
        return getRolesForSubject(subject);
    }


    public void removeSubjectFromRole(
            @SoapParameter("subject") Subject subject,
            @SoapParameter("role") Role role) {
        Role r = findRole(role.getName());
        if (r != null) {
            r.removeSubject(subject);
        }
    }

    public void removeSubjectsFromRole(
            @SoapParameter("subjectList") Subject[] subjectList,
            @SoapParameter("role") Role role) {
        Role r = findRole(role.getName());
        if (r != null) {
            for (Subject subject : subjectList) {
                r.removeSubject(subject);

            }
        }

    }

    public void requireIdentification(@SoapParameter("inputarg") int inputarg) {
        if (inputarg == 0) {
            identificationRequired = false;
        }
        identificationRequired = true;
    }

    @SoapReturn(name = "outputarg")
    public int isIdentificationRequired() {
        if (identificationRequired) {
            return 1;
        }
        return 0;
    }

    private Vector<Role> getRolesForAction(Action action) {
        if (actions.contains(action)) {
            return actions.get(actions.indexOf(action)).getRoles();
        }
        return new Vector<Role>();
    }

    @SoapReturn(
            name = "roleList"
            )
    public Role[] listRolesInAction(@SoapParameter("action") Action action) {
        if (action != null) {
            Vector<Role> roleList = getRolesForAction(action);
            return roleList.toArray(new Role[0]);
        }
        return getRoles();
    }

    private Action findAction(String name) {
        for (Action a : actions) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    @SoapReturn(
            name = "argname"
            )
    public String getPolicy() {
        return toXml();
    }

    @SoapReturn(
            name = "argname"
            )
    public String exportPolicy() {
        return toXml();
    }

    public void importPolicy(
            @SoapParameter("policy") String policy) {

        if (policy.contains(AUTH_POLICY_START)) {
            policy = policy.substring(policy.indexOf(AUTH_POLICY_START));
        }
        try {
            PolicyParser parser = new PolicyParser(policy);
            parser.parse(parser.getSubElements().firstElement());
            for (Node node : parser.getSubElements()) {
                if (node.getNodeName().equals(new Role().getSoapType())) {
                    Role role = new Role();
                    role.parseXml(parser, node);
                    int roleIndex = roles.indexOf(role);
                    if (roleIndex != -1) {
                        Role oldr = roles.get(roleIndex);
                        for (Subject subject : role.getSubjects()) {
                            if (!oldr.hasSubject(subject)) {
                                oldr.setSubjects(subject);
                            }
                        }
                    } else {
                        roles.add(role);
                    }
                }
                if (node.getNodeName().equals(new Action().getSoapType())) {
                    Action action = new Action();
                    action.parseXml(parser, node);
                    addAction(action);
                }
            }
/*            // final check
            for (Action action: actions){
                for (Role role : action.getRoles()){
                    action.removeRole(role);
                    Role r = findRole(role.getName());
                    if (r!=null) {
                        action.setRoles(r);
                    } else {
                        int index = defaultRoles.indexOf(role);
                        if (index!=-1){
                            action.setRoles(defaultRoles.get(index));
                        }else{
                            securityLog.println("Action "
                                    + action.getName() + " contained undefined Role "
                                    + role.getName() + " ROLE REMOVED");
                        }
                    }
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String toXml() {
        String xml = "<?xml version=\"1.0\" ?>\n";
        xml += "<AuthorizationPolicy>";
        for (Role role : roles) {
            xml += role.toXml();
        }
        for (Action action : actions) {
            xml += action.toXml();
        }
        xml += "</AuthorizationPolicy>";
        return xml;
    }

}
