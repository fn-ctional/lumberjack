package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data;

public class GroupPermission {

    private String id;
    private String ruleID;
    private String userGroupID;

    public GroupPermission(){};

    public GroupPermission(String ruleID, String userGroupID){
        this.id = id;
        this.ruleID = ruleID;
        this.userGroupID = userGroupID;
    }

    public String getId() { return id; }

    public String getRuleID() { return ruleID; }

    public String getUserGroupID() {
        return userGroupID;
    }

    public void setAssignmentID(String id) {
        this.id = id;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    public void setUserGroupID(String userGroupID) {
        this.userGroupID = userGroupID;
    }

}
