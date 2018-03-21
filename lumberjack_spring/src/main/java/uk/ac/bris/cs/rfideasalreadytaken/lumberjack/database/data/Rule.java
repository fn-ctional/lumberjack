package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data;

import java.util.Objects;

public class Rule {

    private String id;
    private int maximumRemovalTime;

    public Rule(){};

    public Rule(String id, int maximumRemovalTime){
        this.id = id;
        this.maximumRemovalTime = maximumRemovalTime;
    }

    public String getId() {
        return id;
    }

    public int getMaximumRemovalTime() {
        return maximumRemovalTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMaximumRemovalTime(int maximumRemovalTime) { this.maximumRemovalTime = maximumRemovalTime; }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        Rule r = (Rule) o;
        return Objects.equals(id, r.id) &&
                maximumRemovalTime == r.maximumRemovalTime;
    }
}
