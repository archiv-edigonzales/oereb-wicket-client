package ch.so.agi.oereb.wicketclient;

import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private String forename;
    private String surname;
    
    public Person(String forename, String surname) {
        this.forename = forename;
        this.surname = surname;
    }
    
    public String fullName() {
        return surname + " " + forename;
    }

}
