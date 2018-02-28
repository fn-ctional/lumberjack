package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

public class Rule {

    private String id;

    public Rule(){};

    public Rule(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
