package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

public class GroupPermission {

    private int id;
    private String ruleID;
    private String userGroupID;

    public GroupPermission(){};

    public GroupPermission(String ruleID, String userGroupID){
        this.id = id;
        this.ruleID = ruleID;
        this.userGroupID = userGroupID;
    }

    public int getId() { return id; }

    public String getRuleID() { return ruleID; }

    public String getUserGroupID() {
        return userGroupID;
    }

    public void setAssignmentID(int id) {
        this.id = id;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    public void setUserGroupID(String userGroupID) {
        this.userGroupID = userGroupID;
    }

}
