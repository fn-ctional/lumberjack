package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

public class Rule {

    private String id;
    private java.sql.Time maximumRemovalTime;

    public Rule(){};

    public Rule(String id, java.sql.Time maximumRemovalTime){
        this.id = id;
        this.maximumRemovalTime = maximumRemovalTime;
    }

    public String getId() {
        return id;
    }

    public java.sql.Time getMaximumRemovalTime() {
        return maximumRemovalTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMaximumRemovalTime(java.sql.Time maximumRemovalTime) { this.maximumRemovalTime = maximumRemovalTime; }
}
