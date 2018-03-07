package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data;

public class Search {
    private String term;

    public Search(String s){
        term = s;
    }

    public Search(){
        term = "";
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
