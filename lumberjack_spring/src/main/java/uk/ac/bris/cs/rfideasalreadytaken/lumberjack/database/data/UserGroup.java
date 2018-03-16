package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data;

public class UserGroup {

    private String id;

    public UserGroup(){};

    public UserGroup(String groupName){
        this.id = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
