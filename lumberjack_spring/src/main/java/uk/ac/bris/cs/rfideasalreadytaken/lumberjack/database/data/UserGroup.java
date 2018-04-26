package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data;

import java.util.Objects;

public class UserGroup {

    private String id;

    public UserGroup(){
    };

    public UserGroup(String groupName){
        this.id = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        UserGroup ug = (UserGroup) o;
        return Objects.equals(id, ug.id);
    }
}
