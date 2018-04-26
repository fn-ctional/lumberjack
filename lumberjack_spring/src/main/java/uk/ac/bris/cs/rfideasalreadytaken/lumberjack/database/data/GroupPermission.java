package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data;

import java.util.Objects;

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

    public void setId(String id) {
        this.id = id;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    public void setUserGroupID(String userGroupID) {
        this.userGroupID = userGroupID;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        GroupPermission p = (GroupPermission) o;
        return Objects.equals(ruleID, p.ruleID) &&
                Objects.equals(userGroupID, p.userGroupID);
    }

}
