package uk.ac.bris.cs.rfideasalreadytaken.lumberjack.authentication;

public class EmailExistsException extends Exception {

    public EmailExistsException(String message) {
        super(message);
    }

}
